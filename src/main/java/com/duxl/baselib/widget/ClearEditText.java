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
public class ClearEditText extends AppCompatEditText implements View.OnClickListener {

    private int mClearViewId = -1; // 清空View的id
    private View mClearView; // 清空View
    private boolean mClearRequestFocus; // 清空输入框后是否获取焦点
    private boolean mClearInitVisible; // 清空View初始是否可见，默认false
    private boolean mClearClickVisible; // 清空View点击后是否可见，默认false

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
            typedArray.recycle();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mClearViewId != -1 && mClearView == null) {
            mClearView = ((ViewGroup) getParent()).findViewById(mClearViewId);
            if (mClearView != null) {
                if (!mClearInitVisible) {
                    mClearView.setVisibility(View.INVISIBLE);
                }
                mClearView.setOnClickListener(this);
                setTextChangedListener();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mClearView) {
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
                if (s.length() > 0) {
                    mClearView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
