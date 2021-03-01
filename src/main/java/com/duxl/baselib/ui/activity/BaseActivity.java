package com.duxl.baselib.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.duxl.baselib.BuildConfig;
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
 * BaseActivity
 * create by duxl 2020/8/15
 */
public abstract class BaseActivity extends RefreshActivity implements IStatusViewContainer {

    protected Unbinder mUnbinder;

    protected View mRootContentView;
    protected View mStateBar;
    protected ActionBarView mActionBarView;
    protected XSmartRefreshLayout mXSmartRefreshLayout;
    protected View mContentView;
    protected FrameLayout mFlContainer;
    protected IStatusView mStatusView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams(getIntent());
        setContentView(R.layout.activity_base);
        mStateBar = findViewById(R.id.v_state_bar);
        initStateBar();

        mActionBarView = findViewById(R.id.action_bar_view);
        initActionBar();

        mXSmartRefreshLayout = findViewById(R.id.x_smart_refresh_layout);
        initSmartRefreshLayout(mXSmartRefreshLayout);

        mFlContainer = findViewById(R.id.fl_container_base_activity);
        mContentView = getLayoutInflater().inflate(getLayoutResId(), null);
        mFlContainer.addView(mContentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mStatusView = initStatusView();
        mFlContainer.addView(mStatusView.getView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mUnbinder = ButterKnife.bind(this, mContentView);
        initView(mContentView);
    }

    @Override
    public void setContentView(int layoutResID) {
        View rootView = getLayoutInflater().inflate(layoutResID, null);
        setContentView(rootView);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        mRootContentView = view;
    }

    protected abstract int getLayoutResId();

    protected void initParams(Intent args) {
    }

    protected void initView(View v) {
    }

    protected void initStateBar() {
        //如果Android版本大于4.4，说明状态栏就可以被透明，我们自己的布局就可以放到状态栏之下
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                View decorView = getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

                decorView.setSystemUiVisibility(option);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            } else {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }

            int stateBarHeight = DisplayUtil.getBarHeight(getApplicationContext());
            mStateBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, stateBarHeight));
        }
    }

    /**
     * 隐藏状态栏，默认显示
     */
    public void hideStateBar() {
        mStateBar.setVisibility(View.GONE);
    }

    /**
     * 显示状态栏，默认显示
     */
    public void showStateBar() {
        mStateBar.setVisibility(View.VISIBLE);
    }

    /**
     * 设置状态栏颜色
     *
     * @param color
     */
    public void setStateBarColor(int color) {
        mStateBar.setBackgroundColor(color);
    }

    /**
     * 设置状态栏背景
     *
     * @param resId
     */
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

    @Override
    public void setTitle(int titleId) {
        setTitle(getText(titleId));
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mActionBarView != null) {
            mActionBarView.setTitle(title);
        }
    }

    protected void onClickActionBack(View v) {
        if (v.getId() == R.id.iv_action_back) {
            finish();
        }
    }

    protected void onClickActionClose(View v) {
        if (v.getId() == R.id.iv_action_close) {
            finish();
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
    public Context getContext() {
        return this;
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
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    /**
     * 设置状态栏字体和图标为深色
     * Android版本在6.0以上时可以调用此方法来改变状态栏字体图标颜色
     */
    public void setStateBarDarkMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置状态栏字体和图标为浅色
     */
    public void setStateBarLightMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
