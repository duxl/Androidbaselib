package com.duxl.baselib.widget.intercept.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.duxl.baselib.widget.intercept.InterceptEventHelper;

/**
 * 不处理横向滑动的SwipeRefreshLayout，将横向滑动交给子View处理
 * create by duxl 2021/3/30
 */
public class InterceptSwipeRefreshLayout extends SwipeRefreshLayout {

    private InterceptEventHelper mInterceptHelper;

    public InterceptSwipeRefreshLayout(@NonNull Context context) {
        this(context, null);
    }

    public InterceptSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInterceptHelper = new InterceptEventHelper(context, false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mInterceptHelper.interceptTouchEvent(ev)) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }
}