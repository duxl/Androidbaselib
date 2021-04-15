package com.duxl.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.duxl.baselib.R;

/**
 * 监听输入内容是否为空，不为空显示清除按钮，点击清除按钮清空文本内容
 * create by duxl 2021/2/24
 */
public class ClearEditText extends AppCompatEditText {

    private int mClearViewId = -1; // 清空View的id
    private View mClearView; // 清空View
    private boolean mClearRequestFocus; // 清空输入框后是否获取焦点
    private boolean mClearInitVisible; // 清空View初始是否可见，默认false
    private boolean mClearClickVisible; // 清空View点击后是否可见，默认false
    private boolean mClearEnable; // 是否启用清空功能

    public ClearEditText(@NonNull Context context) {
        this(context, null);
    }

    public ClearEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClearEditText);
        if (typedArray != null) {
            mClearViewId = typedArray.getResourceId(R.styleable.ClearEditText_clear_viewId, -1);
            mClearRequestFocus = typedArray.getBoolean(R.styleable.ClearEditText_clear_requestFocus, true);
            mClearInitVisible = typedArray.getBoolean(R.styleable.ClearEditText_clear_initVisible, false);
            mClearClickVisible = typedArray.getBoolean(R.styleable.ClearEditText_clear_clickVisible, false);
            mClearEnable = typedArray.getBoolean(R.styleable.ClearEditText_clear_clickVisible, true);
            typedArray.recycle();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mClearViewId != -1 && mClearView == null) {
            mClearView = ((ViewGroup) getParent()).findViewById(mClearViewId);
            if (mClearView != null) {
                if (!mClearInitVisible && getText().length() == 0) {
                    mClearView.setVisibility(View.INVISIBLE);
                }
                mClearView.setOnClickListener(this::onClickView);
                setTextChangedListener();
            }
        }
    }

    private void onClickView(View v) {
        if (v == mClearView) {
            if (!mClearEnable) {
                return;
            }

            if (mOnClearClickListener != null && mOnClearClickListener.onClearClick(mClearView)) {
                return;
            }

            getText().clear();
            if (!mClearClickVisible) {
                mClearView.setVisibility(View.INVISIBLE);
            }
            if (mClearRequestFocus) {
                requestFocus();
            }
        }
    }

    private void setTextChangedListener() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mClearEnable) {
                    return;
                }
                mClearView.setVisibility(s.length() > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    public interface OnClearClickListener {
        /**
         * clear点击事件回调
         *
         * @param clearView
         * @return 如果返回true表示已处理次事件，控件不再处理（清空内容），如果返回false会清空输入框内容
         */
        boolean onClearClick(View clearView);
    }

    private OnClearClickListener mOnClearClickListener;

    /**
     * 设置清除View被点击事件回调
     *
     * @param listener
     */
    public void setOnClearClickListener(OnClearClickListener listener) {
        this.mOnClearClickListener = listener;
    }

    /**
     * 是否启用清空功能
     *
     * @return 返回是否启用清空功能
     */
    public boolean isClearEnable() {
        return mClearEnable;
    }

    /**
     * 设置是否启用清空功能
     *
     * @param clearEnable 是否启用清空功能
     */
    public void setClearEnable(boolean clearEnable) {
        this.mClearEnable = clearEnable;
    }

    /**
     * 获取清空输入框后是否获取焦点
     *
     * @return 返回清空输入框后是否获取焦点
     */
    public boolean isClearRequestFocus() {
        return mClearRequestFocus;
    }

    /**
     * 设置清空输入框后是否获取焦点
     *
     * @param requestFocus 清空输入框后是否获取焦点
     */
    public void setClearRequestFocus(boolean requestFocus) {
        this.mClearRequestFocus = requestFocus;
    }

    /**
     * 获取清空View初始是否可见，默认false
     *
     * @return 返回清空View初始是否可见，默认false
     */
    public boolean isClearInitVisible() {
        return mClearInitVisible;
    }

    /**
     * 设置清空View初始是否可见，默认false
     *
     * @param visible 清空View初始是否可见
     */
    public void setClearInitVisible(boolean visible) {
        this.mClearInitVisible = visible;
    }

    /**
     * 获取清空View点击后是否可见，默认false
     *
     * @return 返回清空View点击后是否可见，默认false
     */
    public boolean isClearClickVisible() {
        return mClearClickVisible;
    }

    /**
     * 设置清空View点击后是否可见，默认false
     *
     * @param visible 清空View点击后是否可见
     */
    public void setClearClickVisible(boolean visible) {
        this.mClearClickVisible = visible;
    }
}
