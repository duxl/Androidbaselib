package com.duxl.baselib.ui.status;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duxl.baselib.R;
import com.scwang.smart.refresh.layout.constant.RefreshState;

/**
 * SimpleStatusView
 * create by duxl 2020/8/15
 */
public class SimpleStatusView extends LinearLayout implements IStatusView {

    protected Status mStatus = Status.None;
    protected int mErrCode;
    protected ImageView mIvStatus;
    protected TextView mTvStatus;
    protected Button mBtnStatus;

    private IRefreshContainer mRefreshContainer;

    protected int mLayoutResId;

    protected String loadingText;
    protected int loadingImgRes;
    protected String loadingBtnText;
    protected int loadingTextVisibility;
    protected int loadingImgVisibility;
    protected int loadingBtnVisibility;

    protected String errorText;
    protected int errorImgRes;
    protected String errorBtnText;
    protected int errorTextVisibility;
    protected int errorImgVisibility;
    protected int errorBtnVisibility;

    protected String emptyText = "空空如也";
    protected int emptyImgRes;
    protected String emptyBtnText = "再试一次";
    protected int emptyTextVisibility;
    protected int emptyImgVisibility;
    protected int emptyBtnVisibility;

    public SimpleStatusView(IRefreshContainer refreshContainer) {
        this(refreshContainer, R.layout.layout_simple_status_view);
    }

    public SimpleStatusView(IRefreshContainer refreshContainer, int layoutResId) {
        super(refreshContainer.getContext());
        this.mRefreshContainer = refreshContainer;
        this.mLayoutResId = layoutResId;

        setBackground(getResources().getDrawable(R.drawable.style_simple_status_view_bg));
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setVisibility(View.GONE);
        initView(refreshContainer.getContext());
    }

    protected void initView(Context context) {
        LayoutInflater.from(context).inflate(mLayoutResId, this, true);
        mIvStatus = findViewById(R.id.iv_status_view);
        mTvStatus = findViewById(R.id.tv_status_view);
        mBtnStatus = findViewById(R.id.btn_status_view);

        loadingText = context.getString(R.string.style_simple_status_loading_text);
        loadingImgRes = R.drawable.style_simple_status_loading_img_res;
        loadingBtnText = context.getString(R.string.style_simple_status_loading_button_text);
        loadingTextVisibility = R.integer.loadingTextVisibility;
        loadingImgVisibility = R.integer.loadingImgVisibility;
        loadingBtnVisibility = R.integer.loadingBtnVisibility;

        errorText = context.getString(R.string.style_simple_status_error_text);
        errorImgRes = R.drawable.style_simple_status_error_res;
        errorBtnText = context.getString(R.string.style_simple_status_error_button_text);
        errorTextVisibility = R.integer.errorTextVisibility;
        errorImgVisibility = R.integer.errorImgVisibility;
        errorBtnVisibility = R.integer.errorBtnVisibility;

        emptyText = context.getString(R.string.style_simple_status_empty_text);
        emptyImgRes = R.drawable.style_simple_status_empty_res;
        emptyBtnText = context.getString(R.string.style_simple_status_empty_button_text);
        emptyTextVisibility = R.integer.emptyTextVisibility;
        emptyImgVisibility = R.integer.emptyImgVisibility;
        emptyBtnVisibility = R.integer.emptyBtnVisibility;
    }

    @Override
    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }

    @Override
    public void setLoadingImgRes(int loadingImgRes) {
        this.loadingImgRes = loadingImgRes;
    }

    @Override
    public void setLoadingBtnText(String loadingBtnText) {
        this.loadingBtnText = loadingBtnText;
    }

    @Override
    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    @Override
    public void setErrorImgRes(int errorImgRes) {
        this.errorImgRes = errorImgRes;
    }

    @Override
    public void setErrorBtnText(String errorText) {
        this.errorBtnText = errorText;
    }

    @Override
    public void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
    }

    @Override
    public void setEmptyImgRes(int emptyImgRes) {
        this.emptyImgRes = emptyImgRes;
    }

    @Override
    public void setEmptyBtnText(String emptyText) {
        this.emptyBtnText = emptyText;
    }

    @Override
    public void setLoadingVisibility(int textVisibility, int imgVisibility, int btnVisibility) {
        this.loadingTextVisibility = textVisibility;
        this.loadingImgVisibility = imgVisibility;
        this.loadingBtnVisibility = btnVisibility;
    }

    @Override
    public void setErrorVisibility(int textVisibility, int imgVisibility, int btnVisibility) {
        this.errorTextVisibility = textVisibility;
        this.errorImgVisibility = imgVisibility;
        this.errorBtnVisibility = btnVisibility;
    }

    @Override
    public void setEmptyVisibility(int textVisibility, int imgVisibility, int btnVisibility) {
        this.emptyTextVisibility = textVisibility;
        this.emptyImgVisibility = imgVisibility;
        this.emptyBtnVisibility = btnVisibility;
    }

    @Override
    public void showContent() {
        mStatus = Status.None;
        setVisibility(View.GONE);
        getRefreshContainer().getContentView().setVisibility(View.VISIBLE);
        loadComplete();
    }

    @Override
    public void showLoading() {
        mStatus = Status.Loading;

        setImageIfNotNull(loadingImgRes);
        setMessageIfNotNull(loadingText);
        setBtnIfNotNull(loadingBtnText);

        setVisibilityIfNotNull(mIvStatus, loadingImgVisibility);
        setVisibilityIfNotNull(mTvStatus, loadingTextVisibility);
        setVisibilityIfNotNull(mBtnStatus, loadingBtnVisibility);
        setVisibility(View.VISIBLE);

        setClickListenerIfNotNull(mBtnStatus);

        getRefreshContainer().getContentView().setVisibility(View.GONE);
        requestImageViewLayout();
    }

    @Override
    public void showError(int errCode) {
        mStatus = Status.Error;
        mErrCode = errCode;

        setImageIfNotNull(errorImgRes);
        setMessageIfNotNull(errorText);
        setBtnIfNotNull(errorBtnText);

        setVisibilityIfNotNull(mIvStatus, errorImgVisibility);
        setVisibilityIfNotNull(mTvStatus, errorTextVisibility);
        setVisibilityIfNotNull(mBtnStatus, errorBtnVisibility);
        setVisibility(View.VISIBLE);

        setClickListenerIfNotNull(mBtnStatus);

        getRefreshContainer().getContentView().setVisibility(View.GONE);
        loadComplete();
        requestImageViewLayout();
    }

    @Override
    public void showEmpty() {
        mStatus = Status.Empty;

        setImageIfNotNull(emptyImgRes);
        setMessageIfNotNull(emptyText);
        setBtnIfNotNull(emptyBtnText);

        setVisibilityIfNotNull(mIvStatus, emptyImgVisibility);
        setVisibilityIfNotNull(mTvStatus, emptyTextVisibility);
        setVisibilityIfNotNull(mBtnStatus, emptyBtnVisibility);
        setVisibility(View.VISIBLE);

        setClickListenerIfNotNull(mBtnStatus);

        getRefreshContainer().getContentView().setVisibility(View.GONE);
        loadComplete();
        requestImageViewLayout();
    }

    protected void setImageIfNotNull(int imgResId) {
        if (mIvStatus != null) {
            mIvStatus.setImageResource(imgResId);
        }
    }

    protected void setMessageIfNotNull(String text) {
        if (mTvStatus != null) {
            mTvStatus.setText(text);
        }
    }

    protected void setBtnIfNotNull(String text) {
        if (mBtnStatus != null) {
            mBtnStatus.setText(text);
        }
    }

    protected void setVisibilityIfNotNull(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    /**
     * 设置点击事件
     *
     * @param view
     */
    protected void setClickListenerIfNotNull(View view) {
        if (view == null) {
            return;
        }

        view.setOnClickListener(v -> {
            if (mRefreshContainer.getRefreshLayout().getOnLoadListener() == null) {
                return;
            }

            if (getStatus() == Status.Loading) {
                mRefreshContainer.getRefreshLayout().getOnLoadListener().onLoadingClick();
            } else if (getStatus() == Status.Empty) {
                mRefreshContainer.getRefreshLayout().getOnLoadListener().onEmptyClick();
            } else if (getStatus() == Status.Error) {
                mRefreshContainer.getRefreshLayout().getOnLoadListener().onErrorClick(mErrCode);
            }
        });
    }

    @Override
    public IRefreshContainer getRefreshContainer() {
        return mRefreshContainer;
    }

    @Override
    public View getView() {
        return this;
    }

    private void requestImageViewLayout() {
//        ViewGroup.LayoutParams layoutParams = mIvStatus.getLayoutParams();
//        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        mIvStatus.requestLayout();
    }

    @Override
    public void loadComplete() {
        if (getRefreshContainer().getRefreshLayout().getState() == RefreshState.Refreshing) {
            getRefreshContainer().finishRefresh();
        }

        if (getRefreshContainer().getRefreshLayout().getState() == RefreshState.Loading) {
            getRefreshContainer().finishLoadMore();
        }
    }

    @Override
    public Status getStatus() {
        return mStatus;
    }
}
