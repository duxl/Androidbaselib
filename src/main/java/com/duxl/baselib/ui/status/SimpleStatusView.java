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

    protected ImageView mIvStatus;
    protected TextView mTvStatus;
    protected Button mBtnStatus;

    private IRefreshContainer mRefreshContainer;

    protected int mLayoutResId;

    protected String loadingText;
    protected int loadingImgRes;

    protected String errorText;
    protected int errorImgRes;
    protected String errorBtnText;

    protected String emptyText = "空空如也";
    protected int emptyImgRes;
    protected String emptyBtnText = "再试一次";

    public SimpleStatusView(IRefreshContainer refreshContainer) {
        this(refreshContainer, R.layout.layout_simple_status_view);
    }

    public SimpleStatusView(IRefreshContainer refreshContainer, int layoutResId) {
        super(refreshContainer.getContext());
        this.mRefreshContainer = refreshContainer;
        this.mLayoutResId = layoutResId;

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

        errorText = context.getString(R.string.style_simple_status_error_text);
        errorImgRes = R.drawable.style_simple_status_error_res;
        errorBtnText = context.getString(R.string.style_simple_status_error_button_text);

        emptyText = context.getString(R.string.style_simple_status_empty_text);
        emptyImgRes = R.drawable.style_simple_status_empty_res;
        emptyBtnText = context.getString(R.string.style_simple_status_empty_button_text);
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
    public void showLoading() {
        setImageIfNotNull(loadingImgRes);
        setMessageIfNotNull(loadingText);
        setBtnVisibilityIfNotNull(View.GONE);

        setVisibility(View.VISIBLE);
        getRefreshContainer().getContentView().setVisibility(View.GONE);
        requestImageViewLayout();
    }

    @Override
    public void showError() {
        setImageIfNotNull(errorImgRes);
        setMessageIfNotNull(errorText);
        setBtnIfNotNull(errorBtnText);
        setBtnVisibilityIfNotNull(View.VISIBLE);
        setBtnClickListenerIfNotNull(1);

        setVisibility(View.VISIBLE);
        getRefreshContainer().getContentView().setVisibility(View.GONE);
        loadComplete();
        requestImageViewLayout();
    }

    @Override
    public void showEmpty() {
        setImageIfNotNull(emptyImgRes);
        setMessageIfNotNull(emptyText);
        setBtnIfNotNull(emptyBtnText);
        setBtnVisibilityIfNotNull(View.VISIBLE);
        setBtnClickListenerIfNotNull(0);

        setVisibility(View.VISIBLE);
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

    protected void setBtnVisibilityIfNotNull(int visibility) {
        if (mBtnStatus != null) {
            mBtnStatus.setVisibility(visibility);
        }
    }

    /**
     * 设置按钮点击事件
     *
     * @param type 类型：0为空、1错误
     */
    protected void setBtnClickListenerIfNotNull(int type) {
        if (mBtnStatus == null) {
            return;
        }

        mBtnStatus.setOnClickListener(v -> {
            if (mRefreshContainer.getRefreshLayout().getOnLoadListener() == null) {
                return;
            }

            if (type == 0) {
                mRefreshContainer.getRefreshLayout().getOnLoadListener().onEmptyClick();
            } else if (type == 1) {
                mRefreshContainer.getRefreshLayout().getOnLoadListener().onErrorClick();
            }
        });
    }

    @Override
    public void showContent() {
        setVisibility(View.GONE);
        getRefreshContainer().getContentView().setVisibility(View.VISIBLE);
        loadComplete();
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
}
