package com.duxl.baselib.ui.status;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.duxl.baselib.R;
import com.duxl.baselib.widget.OnLoadListener;
import com.scwang.smart.refresh.layout.constant.RefreshState;

/**
 * SimpleStatusView
 * create by duxl 2020/8/15
 */
public class SimpleStatusView extends LinearLayout implements IStatusView {

    protected Context context;
    protected OnLoadListener mOnLoadListener;

    protected Status mStatus = Status.None;
    protected int mErrCode;
    protected ImageView mIvStatus;
    protected TextView mTvStatus;
    protected TextView mBtnStatus;

    private IRefreshContainer mRefreshContainer;

    protected int mLayoutResId = R.layout.layout_simple_status_view;

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

    protected String emptyText = "什么都没有";
    protected int emptyImgRes;
    protected String emptyBtnText = "再试一次";
    protected int emptyTextVisibility;
    protected int emptyImgVisibility;
    protected int emptyBtnVisibility;

    public SimpleStatusView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleStatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public void setRefreshContainer(IRefreshContainer refreshContainer) {
        this.mRefreshContainer = refreshContainer;
        if (refreshContainer != null && refreshContainer.getRefreshLayout() != null) {
            mOnLoadListener = refreshContainer.getRefreshLayout().getOnLoadListener();
        }
    }

    public void setLayoutResId(int mLayoutResId) {
        this.mLayoutResId = mLayoutResId;
        removeAllViews();
        LayoutInflater.from(context).inflate(mLayoutResId, this, true);
        mIvStatus = findViewById(R.id.iv_status_view);
        mTvStatus = findViewById(R.id.tv_status_view);
        mBtnStatus = findViewById(R.id.btn_status_view);
    }

    public void setOnLoadListener(OnLoadListener listener) {
        this.mOnLoadListener = listener;
        if (getRefreshContainer() != null && getRefreshContainer().getRefreshLayout() != null) {
            getRefreshContainer().getRefreshLayout().setOnLoadListener(mOnLoadListener);
        }
    }

    protected void initView(Context context, AttributeSet attrs) {
        this.context = context;
        setBackground(ContextCompat.getDrawable(context, R.drawable.style_simple_status_view_bg));
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setVisibility(View.GONE);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleStatusView);
        // 确定布局文件
        mLayoutResId = typedArray.getResourceId(R.styleable.SimpleStatusView_sv_layoutResId, mLayoutResId);
        setLayoutResId(mLayoutResId);

        // loading设置
        loadingText = typedArray.getString(R.styleable.SimpleStatusView_sv_loading_text);
        if (loadingText == null) {
            loadingText = context.getString(R.string.style_simple_status_loading_text);
        }
        loadingImgRes = typedArray.getResourceId(R.styleable.SimpleStatusView_sv_loading_img_res, R.drawable.style_simple_status_loading_img_res);
        loadingBtnText = typedArray.getString(R.styleable.SimpleStatusView_sv_loading_button_text);
        if (loadingBtnText == null) {
            loadingBtnText = context.getString(R.string.style_simple_status_loading_button_text);
        }
        loadingTextVisibility = typedArray.getInteger(R.styleable.SimpleStatusView_sv_loading_text_visibility, getResources().getInteger(R.integer.loadingTextVisibility));
        loadingImgVisibility = typedArray.getInteger(R.styleable.SimpleStatusView_sv_loading_img_visibility, getResources().getInteger(R.integer.loadingImgVisibility));
        loadingBtnVisibility = typedArray.getInteger(R.styleable.SimpleStatusView_sv_loading_btn_visibility, getResources().getInteger(R.integer.loadingBtnVisibility));

        // error设置
        errorText = typedArray.getString(R.styleable.SimpleStatusView_sv_error_text);
        if (errorText == null) {
            errorText = context.getString(R.string.style_simple_status_error_text);
        }
        errorImgRes = typedArray.getResourceId(R.styleable.SimpleStatusView_sv_loading_img_res, R.drawable.style_simple_status_error_res);
        errorBtnText = typedArray.getString(R.styleable.SimpleStatusView_sv_error_button_text);
        if (errorBtnText == null) {
            errorBtnText = context.getString(R.string.style_simple_status_error_button_text);
        }
        errorTextVisibility = typedArray.getInteger(R.styleable.SimpleStatusView_sv_error_text_visibility, getResources().getInteger(R.integer.errorTextVisibility));
        errorImgVisibility = typedArray.getInteger(R.styleable.SimpleStatusView_sv_error_img_visibility, getResources().getInteger(R.integer.errorImgVisibility));
        errorBtnVisibility = typedArray.getInteger(R.styleable.SimpleStatusView_sv_error_btn_visibility, getResources().getInteger(R.integer.errorBtnVisibility));

        // empty设置
        emptyText = typedArray.getString(R.styleable.SimpleStatusView_sv_empty_text);
        if (emptyText == null) {
            emptyText = context.getString(R.string.style_simple_status_empty_text);
        }
        emptyImgRes = typedArray.getResourceId(R.styleable.SimpleStatusView_sv_empty_img_res, R.drawable.style_simple_status_empty_res);
        emptyBtnText = typedArray.getString(R.styleable.SimpleStatusView_sv_empty_button_text);
        if (emptyBtnText == null) {
            emptyBtnText = context.getString(R.string.style_simple_status_empty_button_text);
        }
        emptyTextVisibility = typedArray.getInteger(R.styleable.SimpleStatusView_sv_empty_text_visibility, getResources().getInteger(R.integer.emptyTextVisibility));
        emptyImgVisibility = typedArray.getInteger(R.styleable.SimpleStatusView_sv_empty_img_visibility, getResources().getInteger(R.integer.emptyImgVisibility));
        emptyBtnVisibility = typedArray.getInteger(R.styleable.SimpleStatusView_sv_empty_btn_visibility, getResources().getInteger(R.integer.emptyBtnVisibility));

        typedArray.recycle();

        /*LayoutInflater.from(context).inflate(mLayoutResId, this, true);
        mIvStatus = findViewById(R.id.iv_status_view);
        mTvStatus = findViewById(R.id.tv_status_view);
        mBtnStatus = findViewById(R.id.btn_status_view);

        loadingText = context.getString(R.string.style_simple_status_loading_text);
        loadingImgRes = R.drawable.style_simple_status_loading_img_res;
        loadingBtnText = context.getString(R.string.style_simple_status_loading_button_text);
        loadingTextVisibility = getResources().getInteger(R.integer.loadingTextVisibility);
        loadingImgVisibility = getResources().getInteger(R.integer.loadingImgVisibility);
        loadingBtnVisibility = getResources().getInteger(R.integer.loadingBtnVisibility);

        errorText = context.getString(R.string.style_simple_status_error_text);
        errorImgRes = R.drawable.style_simple_status_error_res;
        errorBtnText = context.getString(R.string.style_simple_status_error_button_text);
        errorTextVisibility = getResources().getInteger(R.integer.errorTextVisibility);
        errorImgVisibility = getResources().getInteger(R.integer.errorImgVisibility);
        errorBtnVisibility = getResources().getInteger(R.integer.errorBtnVisibility);

        emptyText = context.getString(R.string.style_simple_status_empty_text);
        emptyImgRes = R.drawable.style_simple_status_empty_res;
        emptyBtnText = context.getString(R.string.style_simple_status_empty_button_text);
        emptyTextVisibility = getResources().getInteger(R.integer.emptyTextVisibility);
        emptyImgVisibility = getResources().getInteger(R.integer.emptyImgVisibility);
        emptyBtnVisibility = getResources().getInteger(R.integer.emptyBtnVisibility);*/
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
        if (getRefreshContainer() != null) {
            getRefreshContainer().getContentView().setVisibility(View.VISIBLE);
        }
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

        if (getRefreshContainer() != null) {
            getRefreshContainer().getContentView().setVisibility(View.GONE);
        }
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

        if (getRefreshContainer() != null) {
            getRefreshContainer().getContentView().setVisibility(View.GONE);
        }
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

        loadComplete();
        if (getRefreshContainer() != null) {
            getRefreshContainer().getContentView().setVisibility(View.GONE);
            getRefreshContainer().finishLoadMoreWithNoMoreData();
        }
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
            if (mOnLoadListener == null) {
                if (getRefreshContainer() != null && getRefreshContainer().getRefreshLayout() != null) {
                    mOnLoadListener = getRefreshContainer().getRefreshLayout().getOnLoadListener();
                }

                if (mOnLoadListener == null) {
                    return;
                }
            }

            if (getStatus() == Status.Loading) {
                mOnLoadListener.onLoadingClick();
            } else if (getStatus() == Status.Empty) {
                mOnLoadListener.onEmptyClick();
            } else if (getStatus() == Status.Error) {
                mOnLoadListener.onErrorClick(mErrCode);
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
        if (getRefreshContainer() != null) {
            if (getRefreshContainer().getRefreshLayout().getState() == RefreshState.Refreshing) {
                getRefreshContainer().finishRefresh();
            }

            if (getRefreshContainer().getRefreshLayout().getState() == RefreshState.Loading) {
                getRefreshContainer().finishLoadMore();
            }
        }
    }

    @Override
    public Status getStatus() {
        return mStatus;
    }
}
