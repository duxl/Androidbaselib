package com.duxl.baselib.utils;

import android.view.View;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 延迟控件的点击响应（避免快速连续点击）
 *
 * @author duxl
 */
public class ViewClickDelayUtil {

    /**
     * 延迟控件的点击响应,1000毫秒内不可重复点击（避免快速连续点击）<br/>
     * 已过时，内部控制clickable为false时点击会点击到底下的view的问题，请使用setListener
     *
     * @param v
     */
    @Deprecated
    public static void clickDelay(final View v) {
        clickDelay(v, 1000);
    }

    /**
     * 延迟控件的点击响应（避免快速连续点击）<br/>
     * 已过时，内部控制clickable为false时点击会点击到底下的view的问题，请使用setListener
     *
     * @param v
     * @param delayMillis 延迟时间，单位毫秒
     */
    @Deprecated
    public static void clickDelay(final View v, long delayMillis) {
        if (v != null && v.isClickable()) {
            v.setClickable(false);
            v.postDelayed(() -> {
                if (v != null) {
                    v.setClickable(true);
                }
            }, delayMillis);
        }
    }

    /**
     * 延迟控件的点击响应（避免快速连续点击）
     *
     * @param v        控件
     * @param listener 点击事件
     */
    public static void setListener(final View v, View.OnClickListener listener) {
        setListener(v, 1000, listener);
    }

    /**
     * 延迟控件的点击响应（避免快速连续点击）
     *
     * @param v           控件
     * @param delayMillis 延迟时间，单位毫秒
     * @param listener    点击事件
     */
    public static void setListener(final View v, long delayMillis, View.OnClickListener listener) {
        AtomicLong clickTime = new AtomicLong();
        v.setOnClickListener(v1 -> {
            if (System.currentTimeMillis() - clickTime.get() > delayMillis) {
                listener.onClick(v1);
                clickTime.set(System.currentTimeMillis());
            }
        });
    }
}
