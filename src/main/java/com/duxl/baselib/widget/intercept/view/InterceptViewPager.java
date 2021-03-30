package com.duxl.baselib.widget.intercept.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.duxl.baselib.widget.intercept.InterceptEventHelper;

/**
 * 处理其它可纵向滑动的容器包含ViewPager滑动冲突的情况
 * create by duxl 2021/1/26
 */
public class InterceptViewPager extends ViewPager {

    private InterceptEventHelper mInterceptEventHelper;

    public InterceptViewPager(@NonNull Context context) {
        this(context, null);
    }

    public InterceptViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mInterceptEventHelper = new InterceptEventHelper(context, true);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = mInterceptEventHelper.interceptTouchEvent(ev);
        getParent().requestDisallowInterceptTouchEvent(!intercept);
        return super.onInterceptTouchEvent(ev);
    }
}
