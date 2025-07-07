package com.duxl.baselib.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duxl.baselib.R;

/**
 * <pre>
 *     支持懒加载的Fragment
 *
 *     特点：
 *     1、Fragment第一次出现在前台时才回调{@link #initView(View)}
 *     2、Fragment可见状态变化时，回调{@link #onLazyHiddenChanged(boolean, boolean)}
 *     3、可调用 {@link #inflateLayout()} 方法触发立即加载布局文件，从而触发{@link #initView(View)}方法，
 *      {@link #inflateLayout()}方法在ViewPager中预加载下一个页面特别有用
 *     4、使用ViewPager的PagerAdapter时，构造函数需传入behavior参数才能达到懒加载在Android低版本兼容
 *     例如 new FragmentPagerAdapter(manager, BEHAVIOR_SET_USER_VISIBLE_HINT)
 *     5、将 {@link androidx.viewpager.widget.ViewPager#setOffscreenPageLimit(int) ViewPager#setOffscreenPageLimit(int)}
 *     设置到最大值效果更佳
 * </pre>
 * create by duxl 2021/1/29
 */
public abstract class LazyFragment extends BaseFragment {
    private boolean isVisibleToUser = true;
    private boolean isVisible; // 是否可见
    private int _isFirstVisible = -1; // -1表示未设置值，0表示不是第一次不可见，1表示第一次可见
    private boolean isAttach;

    private ViewStub mViewStub;
    private boolean mHasInflated; // 是否已经加载了真实的布局文件
    private boolean mInflatedImmediately; // 是否立即加载布局文件

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootContentView = inflater.inflate(R.layout.fragment_lazy, container, false);
        mViewStub = mRootContentView.findViewById(R.id.view_stub);
        if (mInflatedImmediately) {
            innerInflateLayout();
        }
        return mRootContentView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        isAttach = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isAttach = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean parentIsVisibleToUser = true;
        if (getParentFragment() != null) {
            // 如果是ViewPager中嵌套的子Fragment，需要判断父Fragment是否可见，
            // 因为嵌套的子Fragment的生命周期是跟外面的Activity相关，即使是用
            // getChildFragmentManager添加进来的
            parentIsVisibleToUser = getParentFragment().getUserVisibleHint();
        }
        // 判断isVisibleToUser也是因为ViewPager的原因，在viewpager中非当前
        // fragment也会执行onResume，并且!isHidden()也是true
        lazyHiddenChanged(!isHidden() && isVisibleToUser && parentIsVisibleToUser);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        lazyHiddenChanged(!hidden);
    }

    @Override
    public void onPause() {
        super.onPause();
        lazyHiddenChanged(false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isAttach) {
            lazyHiddenChanged(isVisibleToUser);
            // ViewPager中Fragment嵌套的子Fragment不会调用setUserVisibleHint方法（注意：Android低版本有可
            // 能会调setUserVisibleHint，为了达到统一效果，构造PagerAdapter的时候一定要传
            // FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT 这个参数）
            // 这里手动调用子Fragment的setUserVisibleHint方法
            if (getChildFragmentManager().getFragments() != null) {
                for (Fragment childFragment : getChildFragmentManager().getFragments()) {
                    childFragment.setUserVisibleHint(isVisibleToUser);
                }
            }
        }
    }

    /**
     * 加载布局文件，调用了此方法的Fragment被创建后布局文件会被立即加载
     *
     * @return
     */
    public LazyFragment inflateLayout() {
        mInflatedImmediately = true;
        innerInflateLayout();
        return this;
    }

    /**
     * 加载布局文件
     */
    protected void innerInflateLayout() {
        if (mViewStub != null && !mHasInflated) {
            //mViewStub.setVisibility(View.VISIBLE);
            mViewStub.inflate();
            // Fragment的背景色是基于Activity的，所以这里设置为透明
            mRootContentView.findViewById(R.id.cl_root_content).setBackgroundColor(0x00000000);

            mStateBar = mRootContentView.findViewById(R.id.v_state_bar);
            initStateBar();
            hideStateBar();

            mActionBarView = mRootContentView.findViewById(R.id.action_bar_view);
            initActionBar();
            hideActionBar();

            mXSmartRefreshLayout = mRootContentView.findViewById(R.id.x_smart_refresh_layout);
            initSmartRefreshLayout(mXSmartRefreshLayout);

            mFlContainer = mRootContentView.findViewById(R.id.fl_container_base);
            mContentView = getLayoutInflater().inflate(getLayoutResId(), null);

            mFlContainer.addView(mContentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            mStatusView = initStatusView();
            mFlContainer.addView(mStatusView.getView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            initView(mContentView);

            mHasInflated = true;
        }
    }

    /**
     * 是否可见
     *
     * @return
     */
    public boolean isLazyVisible() {
        return isVisible;
    }

    /**
     * 是否第一次可见
     *
     * @return
     */
    public boolean isLazyFirstVisible() {
        return _isFirstVisible == 1;
    }

    /**
     * 可见状态改变处理函数
     *
     * @param visible
     */
    protected void lazyHiddenChanged(boolean visible) {
        if (isVisible != visible) {
            isVisible = visible;
            if (isVisible && _isFirstVisible == -1) {
                _isFirstVisible = 1;
                innerInflateLayout();
            } else {
                _isFirstVisible = 0;
            }
            resetOnBackPressedDispatcher(isVisible);
            onLazyHiddenChanged(isVisible, _isFirstVisible == 1);
        }
    }

    protected void resetOnBackPressedDispatcher(boolean visible) {
        if (mOnBackPressedCallback != null) {
            mOnBackPressedCallback.setEnabled(false);
        }
        if (visible) {
            initOnBackPressedDispatcher();
        }
    }

    /**
     * fragment可见状态改变的时候回调
     *
     * @param isVisible      是否可见
     * @param isFirstVisible 第一次可见的时候为true，其它为false
     */
    protected abstract void onLazyHiddenChanged(boolean isVisible, boolean isFirstVisible);

    /**
     * 布局文件
     *
     * @return
     */
    protected abstract int getLayoutResId();
}
