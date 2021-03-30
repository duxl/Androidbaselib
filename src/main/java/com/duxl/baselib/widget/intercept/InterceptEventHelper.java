package com.duxl.baselib.widget.intercept;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * <pre>
 * 事件分发帮助类：
 * 处理可纵向滑动容器（如NestedScrollView、SwipeRefreshLayout）的事件。
 * 如果横向滑动大于纵向滑动，则表示容器不处理此事件交给子view处理，否则容
 * 器需要处理此事件。
 * </pre>
 * create by duxl 2021/3/30
 */
public class InterceptEventHelper {

    private float startX;
    private float startY;
    private boolean mIsXMove;// 是否横向拖拽
    private final int mTouchSlop;

    /**
     * @param context  上下文对象
     * @param minMovie 是否启用最小滑动距离
     */
    public InterceptEventHelper(Context context, boolean minMovie) {
        if (minMovie) {
            // getScaledTouchSlop()得来的一个距离，表示滑动的时候，手势移动要大于这个距离才开始移动控件
            mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        } else {
            mTouchSlop = -1;
        }
    }

    /**
     * 处理事件分发
     *
     * @param ev 事件
     * @return false表示事件不处理交给子view处理，true表示需要处理此事件
     */
    public boolean interceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsXMove = false;
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 如果横向移动则不拦截，直接return false；
                if (mIsXMove) {
                    return false;
                }
                float endX = ev.getX();
                float endY = ev.getY();
                float distanceX = Math.abs(endX - startX);
                float distanceY = Math.abs(endY - startY);
                // 如果dx>xy，则认定为左右滑动，将事件交给子view处理，return false
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsXMove = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsXMove = false;
                break;
        }
        // 如果dy>dx，则认定为下拉事件，需处理并拦截
        return true;
    }
}
