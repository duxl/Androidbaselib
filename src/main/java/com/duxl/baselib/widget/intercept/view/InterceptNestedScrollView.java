package com.duxl.baselib.widget.intercept.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.duxl.baselib.widget.intercept.InterceptEventHelper;

/**
 * 不处理横向滑动的NestedScrollView，将横向滑动交给子View处理
 * create by duxl 2021/3/30
 */
public class InterceptNestedScrollView extends NestedScrollView {

    private InterceptEventHelper mInterceptHelper;

    public InterceptNestedScrollView(@NonNull Context context) {
        this(context, null);
    }

    public InterceptNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mInterceptHelper = new InterceptEventHelper(context, true);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mInterceptHelper.interceptTouchEvent(ev)) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }
}
