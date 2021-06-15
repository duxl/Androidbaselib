package com.duxl.baselib.ui.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.duxl.baselib.R;
import com.duxl.baselib.ui.status.IStatusView;
import com.duxl.baselib.ui.status.IStatusViewContainer;
import com.duxl.baselib.ui.status.SimpleStatusView;
import com.duxl.baselib.utils.DisplayUtil;
import com.duxl.baselib.widget.ActionBarView;
import com.duxl.baselib.widget.XSmartRefreshLayout;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * BaseFragment
 * create by duxl 2020/8/16
 */
public abstract class BaseFragment extends RefreshFragment implements IStatusViewContainer {


    protected Unbinder mUnbinder;

    protected View mRootContentView;
    protected View mStateBar;
    protected ActionBarView mActionBarView;
    protected XSmartRefreshLayout mXSmartRefreshLayout;
    protected View mContentView;
    protected FrameLayout mFlContainer;
    protected IStatusView mStatusView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams(getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootContentView = inflater.inflate(R.layout.layout_page, container, false);
        mRootContentView.setBackgroundColor(0x00000000); // Fragment的背景色是基于Activity的，所以这里设置为透明
        mStateBar = mRootContentView.findViewById(R.id.v_state_bar);
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

        mUnbinder = ButterKnife.bind(this, mContentView);
        initView(mContentView);
        return mRootContentView;
    }

    protected abstract int getLayoutResId();

    protected void initParams(Bundle args) {
    }

    protected void initView(View v) {
    }

    protected void initStateBar() {
        //如果Android版本大于4.4，说明状态栏就可以被透明，我们自己的布局就可以放到状态栏之下
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            /* // 布局内容是否放到状态栏之下取决于Fragment依附的Activity，这里不需要设置，注释掉多余的代码
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                View decorView = getActivity().getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

                decorView.setSystemUiVisibility(option);
                getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
            } else {
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
             */

            // 设置状态栏高度
            int stateBarHeight = DisplayUtil.getBarHeight(getActivity().getApplicationContext());
            mStateBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, stateBarHeight));
        }
    }

    public void hideStateBar() {
        mStateBar.setVisibility(View.GONE);
    }

    public void showStateBar() {
        mStateBar.setVisibility(View.VISIBLE);
    }

    public void setStateBarColor(int color) {
        mStateBar.setBackgroundColor(color);
    }

    public void setStateBarResource(int resId) {
        mStateBar.setBackgroundResource(resId);
    }

    protected void initActionBar() {
        if (mActionBarView != null) {
            mActionBarView.setOnClickBackListener(this::onClickActionBack);
            mActionBarView.setOnClickCloseListener(this::onClickActionClose);
            mActionBarView.setOnClickRightTextViewListener(this::onClickActionTvRight);
            mActionBarView.setOnClickRightImageViewListener(this::onClickActionIvRight);
        }
    }

    public ActionBarView getActionBarView() {
        return mActionBarView;
    }

    public void hideActionBar() {
        if (mActionBarView != null) {
            mActionBarView.setVisibility(View.GONE);
        }
    }

    public void showActionBar() {
        if (mActionBarView != null) {
            mActionBarView.setVisibility(View.VISIBLE);
        }
    }

    public void setActionBarColor(int color) {
        if (mActionBarView != null) {
            mActionBarView.getChildAt(0).setBackgroundColor(color);
        }
    }

    public void setActionBarResource(int resId) {
        if (mActionBarView != null) {
            mActionBarView.getChildAt(0).setBackgroundResource(resId);
        }
    }

    /**
     * 设置ActionBar悬浮，也就是ActionBar透下去可已看到后面的内容
     *
     * @param isFloat
     */
    public void setActionBarFloat(boolean isFloat) {
        if (mActionBarView != null) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mXSmartRefreshLayout.getLayoutParams();
            if (isFloat) {
                layoutParams.topToTop = R.id.page_top;
                layoutParams.topToBottom = -1;
            } else {
                layoutParams.topToTop = -1;
                layoutParams.topToBottom = R.id.page_top;
            }
            mXSmartRefreshLayout.setLayoutParams(layoutParams);
        }
    }

    public boolean isActionBarFloat() {
        if (mActionBarView != null) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mXSmartRefreshLayout.getLayoutParams();
            return layoutParams.topToTop != -1;
        }
        return false;
    }

    public void setTitle(int titleId) {
        setTitle(getText(titleId));
    }

    public void setTitle(CharSequence title) {
        if (mActionBarView != null) {
            mActionBarView.setTitle(title);
        }
    }

    protected void onClickActionBack(View v) {
        if (v.getId() == R.id.iv_action_back) {
            getActivity().finish();
        }
    }

    protected void onClickActionClose(View v) {
        if (v.getId() == R.id.iv_action_close) {
            getActivity().finish();
        }
    }

    protected void onClickActionTvRight(View v) {
    }

    protected void onClickActionIvRight(View v) {
    }

    protected IStatusView initStatusView() {
        return new SimpleStatusView(this);
    }

    @Override
    public View getRootContentView() {
        return mRootContentView;
    }

    @Override
    public View getContentView() {
        return mContentView;
    }

    @Override
    public IStatusView getStatusView() {
        return mStatusView;
    }

    public View getStateBar() {
        return mStateBar;
    }

    @Override
    public void onDestroyView() {
        mRootContentView = null;
        mStateBar = null;
        mActionBarView = null;
        mXSmartRefreshLayout = null;
        mContentView = null;
        mFlContainer = null;
        mStatusView = null;

        if (mUnbinder != null) {
            try {
                mUnbinder.unbind();
            } catch (Exception e) {
            }
        }
        super.onDestroyView();
    }
}
