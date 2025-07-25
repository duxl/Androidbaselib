package com.duxl.baselib.ui.status;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Guideline;

import com.duxl.baselib.R;
import com.duxl.baselib.widget.OnLoadListener;
import com.scwang.smart.refresh.layout.constant.RefreshState;

/**
 * SimpleStatusView
 * create by duxl 2020/8/15
 */
public class SimpleStatusView extends FrameLayout implements IStatusView {

    protected Context context;
    protected OnLoadListener mOnLoadListener;

    protected Status mStatus = Status.None;
    protected int mErrCode;
    protected Guideline mGuideLine;
    protected ImageView mIvStatus;
    protected TextView mTvStatus;
    protected TextView mBtnStatus;

    private IRefreshContainer mRefreshContainer;

    protected int mLayoutResId = R.layout.layout_simple_status_view;
    protected float guidelinePercent = 0.4f;

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
        this.context = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleStatusView);
        // 布局文件
        mLayoutResId = typedArray.getResourceId(R.styleable.SimpleStatusView_sv_layoutResId, mLayoutResId);
        getLayoutInflater(context).inflate(mLayoutResId, this, true);

        guidelinePercent = typedArray.getFloat(R.styleable.SimpleStatusView_sv_guideline_percent, guidelinePercent);

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

        int statusOrdinal = typedArray.getInteger(R.styleable.SimpleStatusView_sv_status, Status.None.ordinal());
        mStatus = Status.values()[statusOrdinal];

        typedArray.recycle();

        initView(context);
    }

    /**
     * 设置状态View内容垂直方向的位置百分比
     */
    public void setVerticalPercent(@FloatRange(from = 0.0, to = 1.0) Float ratio) {
        if (mGuideLine != null) {
            mGuideLine.setGuidelinePercent(ratio);
        }
    }

    public void setRefreshContainer(IRefreshContainer refreshContainer) {
        this.mRefreshContainer = refreshContainer;
        if (refreshContainer != null && refreshContainer.getRefreshLayout() != null) {
            mOnLoadListener = refreshContainer.getRefreshLayout().getOnLoadListener();
        }
    }

    public void setLayoutResId(int layoutResId) {
        this.mLayoutResId = layoutResId;
        removeAllViews();
        getLayoutInflater(context).inflate(layoutResId, this, true);
        initView(context);
    }

    public void setLayout(View v) {
        mLayoutResId = 0;
        removeAllViews();
        addView(v);
        initView(context);
    }

    protected LayoutInflater getLayoutInflater(Context context) {
        return LayoutInflater.from(context);
    }

    public void setOnLoadListener(OnLoadListener listener) {
        this.mOnLoadListener = listener;
        if (getRefreshContainer() != null && getRefreshContainer().getRefreshLayout() != null) {
            getRefreshContainer().getRefreshLayout().setOnLoadListener(mOnLoadListener);
        }
    }

    protected void initView(Context context) {
        setVisibility(View.GONE);

        mGuideLine = findViewById(R.id.guide_status_line);
        mIvStatus = findViewById(R.id.iv_status_view);
        mTvStatus = findViewById(R.id.tv_status_view);
        mBtnStatus = findViewById(R.id.btn_status_view);

        setVerticalPercent(guidelinePercent);

        switch (mStatus) {
            case Loading:
                showLoading();
                break;
            case Empty:
                showEmpty();
                break;
            case Error:
                showError(30000);
                break;
            default:
                showContent();
                break;
        }
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
