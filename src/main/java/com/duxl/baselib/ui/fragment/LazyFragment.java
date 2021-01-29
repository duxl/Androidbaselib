package com.duxl.baselib.ui.fragment;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * 支持懒加载的Fragment
 * create by duxl 2021/1/29
 */
public abstract class LazyFragment extends BaseFragment {
    private boolean isVisibleToUser = true;
    private boolean isVisible; // 是否可见
    private boolean isFirstVisible = true; // 是否第一次
    private boolean isAttach;

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
            // ViewPager中Fragment嵌套的子Fragment不会调用setUserVisibleHint方法
            // 这里手动调用子Fragment的setUserVisibleHint方法
            if (getChildFragmentManager().getFragments() != null) {
                for (Fragment childFragment : getChildFragmentManager().getFragments()) {
                    childFragment.setUserVisibleHint(isVisibleToUser);
                }
            }
        }
    }

    protected void lazyHiddenChanged(boolean visible) {
        if (isVisible != visible) {
            isVisible = visible;
            onLazyHiddenChanged(isVisible, isFirstVisible);
            isFirstVisible = false;
        }
    }

    /**
     * fragment可见状态改变的时候回调
     *
     * @param isVisible      是否可见
     * @param isFirstVisible 第一次可见的时候为true，其它为false
     */
    protected abstract void onLazyHiddenChanged(boolean isVisible, boolean isFirstVisible);
}
