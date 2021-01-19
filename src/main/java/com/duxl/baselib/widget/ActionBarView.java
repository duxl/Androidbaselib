package com.duxl.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.duxl.baselib.R;

/**
 * ActionBarView
 * create by duxl 2020/8/15
 */
public class ActionBarView extends LinearLayout {

    protected RelativeLayout mRlBar;
    protected LinearLayout mLlLeft;
    protected LinearLayout mLlCenter;
    protected LinearLayout mLlRight;

    protected AppCompatImageView mIvBack;
    protected AppCompatImageView mIvClose;

    protected TextView mTvTitle;

    protected TextView mTvRight;
    protected ImageView mIvRight;

    protected View mBottomLine;

    public ActionBarView(Context context) {
        this(context, null);
    }

    public ActionBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);
        initView(context);

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ActionBarView, 0, 0);

            try {
                int barColor = a.getColor(R.styleable.ActionBarView_abv_bar_background_color, getResources().getColor(R.color.action_bar_bg_color));
                setBarBackgroundColor(barColor);

                int barResId = a.getResourceId(R.styleable.ActionBarView_abv_bar_background_resource, -1);
                if (barResId != -1) {
                    setBarBackgroundResource(barResId);
                }

                int backIcon = a.getResourceId(R.styleable.ActionBarView_abv_back_icon, R.drawable.action_bar_back);
                boolean backVisible = a.getBoolean(R.styleable.ActionBarView_abv_back_visible, getResources().getBoolean(R.bool.action_bar_back_visible));
                setBackImage(backIcon, backVisible ? View.VISIBLE : View.GONE);

                int closeIcon = a.getResourceId(R.styleable.ActionBarView_abv_close_icon, R.drawable.action_bar_close);
                boolean closeVisible = a.getBoolean(R.styleable.ActionBarView_abv_close_visible, getResources().getBoolean(R.bool.action_bar_close_visible));
                setCloseImage(closeIcon, closeVisible ? View.VISIBLE : View.GONE);

                String title = a.getString(R.styleable.ActionBarView_abv_title);
                boolean titleVisible = a.getBoolean(R.styleable.ActionBarView_abv_title_visible, getResources().getBoolean(R.bool.action_bar_title_visible));
                setTitle(title, titleVisible ? View.VISIBLE : View.GONE);
                int titleSize = a.getDimensionPixelSize(R.styleable.ActionBarView_abv_title_size, getResources().getDimensionPixelSize(R.dimen.action_bar_title_size));
                setTitleSizePX(titleSize);
                int titleColor = a.getColor(R.styleable.ActionBarView_abv_title_color, getResources().getColor(R.color.action_bar_title_color));
                setTitleColor(titleColor);

                String rightText = a.getString(R.styleable.ActionBarView_abv_right_text);
                boolean rightTextVisible = a.getBoolean(R.styleable.ActionBarView_abv_right_text_visible, getResources().getBoolean(R.bool.action_bar_right_text_visible));
                setRightText(rightText, rightTextVisible ? View.VISIBLE : View.GONE);
                int rightTextSize = a.getDimensionPixelSize(R.styleable.ActionBarView_abv_right_text_size, getResources().getDimensionPixelSize(R.dimen.action_bar_right_text_size));
                setRightTextSizePX(rightTextSize);
                int rightTextColor = a.getColor(R.styleable.ActionBarView_abv_right_text_color, getResources().getColor(R.color.action_bar_right_text_color));
                setRightTextColor(rightTextColor);

                int rightIcon = a.getResourceId(R.styleable.ActionBarView_abv_right_icon, R.drawable.action_bar_done);
                boolean rightIconVisible = a.getBoolean(R.styleable.ActionBarView_abv_right_icon_visible, getResources().getBoolean(R.bool.action_bar_right_image_visible));
                setRightImage(rightIcon, rightIconVisible ? View.VISIBLE : View.GONE);

                boolean bottomLineVisible = a.getBoolean(R.styleable.ActionBarView_abv_bottom_line_visible, getResources().getBoolean(R.bool.action_bar_bottom_line_visible));
                setBottomLineVisible(bottomLineVisible ? View.VISIBLE : View.GONE);
                int lineColor = a.getColor(R.styleable.ActionBarView_abv_bottom_line_color, getResources().getColor(R.color.action_bar_bottom_line_color));
                setBottomLineColor(lineColor);

            } finally {
                a.recycle();
            }
        }


    }

    protected void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_action_bar_view, this, true);
        mRlBar = findViewById(R.id.rl_bar);

        mLlLeft = findViewById(R.id.ll_action_left);
        mLlCenter = findViewById(R.id.ll_action_center);
        mLlRight = findViewById(R.id.ll_action_right);

        mIvBack = findViewById(R.id.iv_action_back);
        mIvClose = findViewById(R.id.iv_action_close);

        mTvTitle = findViewById(R.id.tv_action_title);

        mTvRight = findViewById(R.id.tv_action_right);
        mIvRight = findViewById(R.id.iv_action_right);

        mBottomLine = findViewById(R.id.bottom_action_line);
    }

    public RelativeLayout getRlBar() {
        return mRlBar;
    }

    public LinearLayout getLlLeft() {
        return mLlLeft;
    }

    public LinearLayout getLlCenter() {
        return mLlCenter;
    }

    public LinearLayout getLlRight() {
        return mLlRight;
    }

    public AppCompatImageView getIvBack() {
        return mIvBack;
    }

    public AppCompatImageView getIvClose() {
        return mIvClose;
    }

    public TextView getTvTitle() {
        return mTvTitle;
    }

    public TextView getTvRight() {
        return mTvRight;
    }

    public ImageView getIvRight() {
        return mIvRight;
    }

    public View getBottomLine() {
        return mBottomLine;
    }

    public void setBarBackgroundColor(int color) {
        if (getRlBar() != null) {
            getRlBar().setBackgroundColor(color);
        }
    }

    public void setBarBackgroundResource(int resId) {
        if (getRlBar() != null) {
            getRlBar().setBackgroundResource(resId);
        }
    }

    public void setBackImage(int resId) {
        setBackImage(resId, View.VISIBLE);
    }

    public void setBackImage(int resId, int visibility) {
        if (getIvBack() != null) {
            getIvBack().setImageResource(resId);
            getIvBack().setVisibility(visibility);
        }
    }

    public void setCloseImage(int resId) {
        setCloseImage(resId, View.VISIBLE);
    }

    public void setCloseImage(int resId, int visibility) {
        if (getIvClose() != null) {
            getIvClose().setImageResource(resId);
            getIvClose().setVisibility(visibility);
        }
    }

    public void setTitle(CharSequence title) {
        setTitle(title, View.VISIBLE);
    }

    public void setTitle(CharSequence title, int visibility) {
        if (getTvTitle() != null) {
            resetTitleToCenter(() -> {
                getTvTitle().setText(title);
                getTvTitle().setVisibility(VISIBLE);
            });
        }
    }

    public void setTitleSizePX(int size) {
        if (getTvTitle() != null) {
            getTvTitle().setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
    }

    public void setTitleSizeDP(int size) {
        if (getTvTitle() != null) {
            getTvTitle().setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        }
    }

    public void setTitleColor(int color) {
        if (getTvTitle() != null) {
            getTvTitle().setTextColor(color);
        }
    }

    public void setRightTextSizePX(int size) {
        if (getTvRight() != null) {
            getTvRight().setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
    }

    public void setRightTextSizeDP(int size) {
        if (getTvRight() != null) {
            getTvRight().setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        }
    }

    public void setRightTextColor(int color) {
        if (getTvRight() != null) {
            getTvRight().setTextColor(color);
        }
    }

    /**
     * 设置标题居中，当标题左右图标显示状态改变时，调用此方法
     */
    public void resetTitleToCenter() {
        resetTitleToCenter(null);
    }

    /**
     * 设置标题居中，当标题左右图标显示状态改变时，调用此方法
     */
    public void resetTitleToCenter(Runnable runnable) {
        if (getTvTitle() != null) {
            getTvTitle().post(() -> {
                int startWidth = getLlLeft().getWidth();
                int endWidth = getLlRight().getWidth();
                int maxWidth = Math.max(startWidth, endWidth);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLlCenter().getLayoutParams();
                params.setMargins(maxWidth, 0, maxWidth, 0);
                getTvTitle().requestLayout();
                if (runnable != null) {
                    runnable.run();
                }
            });
        }
    }

    public void setRightText(CharSequence text) {
        setRightText(text, View.VISIBLE);
    }

    public void setRightText(CharSequence text, int visibility) {
        if (getTvRight() != null) {
            getTvRight().setText(text);
            getTvRight().setVisibility(visibility);
        }
    }

    public void setRightImage(int resId) {
        setRightImage(resId, View.VISIBLE);
    }

    public void setRightImage(int resId, int visibility) {
        if (getIvRight() != null) {
            getIvRight().setImageResource(resId);
            getIvRight().setVisibility(visibility);
        }
    }

    public void setBottomLineVisible(int visibility) {
        if (getBottomLine() != null) {
            getBottomLine().setVisibility(visibility);
        }
    }

    public void setBottomLineColor(int color) {
        if (getBottomLine() != null) {
            getBottomLine().setBackgroundColor(color);
        }
    }

    /**
     * 设置返回按钮点击事件
     *
     * @param listener
     */
    public void setOnClickBackListener(OnClickListener listener) {
        if (getIvBack() != null) {
            getIvBack().setOnClickListener(listener);
        }
    }

    /**
     * 设置关闭按钮点击事件
     *
     * @param listener
     */
    public void setOnClickCloseListener(OnClickListener listener) {
        if (getIvClose() != null) {
            getIvClose().setOnClickListener(listener);
        }
    }

    /**
     * 设置右边TextView点击事件
     *
     * @param listener
     */
    public void setOnClickRightTextViewListener(OnClickListener listener) {
        if (getTvRight() != null) {
            getTvRight().setOnClickListener(listener);
        }
    }

    /**
     * 设置右边ImageView点击事件
     *
     * @param listener
     */
    public void setOnClickRightImageViewListener(OnClickListener listener) {
        if (getIvRight() != null) {
            getIvRight().setOnClickListener(listener);
        }
    }
}
