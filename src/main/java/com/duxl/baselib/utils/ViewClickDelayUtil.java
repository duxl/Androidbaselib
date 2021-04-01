package com.duxl.baselib.utils;

import android.view.View;

/**
 * 延迟控件的点击响应（避免快速连续点击）
 *
 * @author duxl
 */
public class ViewClickDelayUtil {

    /**
     * 延迟控件的点击响应,1000毫秒内不可重复点击（避免快速连续点击）
     *
     * @param v
     */
    public static void clickDelay(final View v) {
        clickDelay(v, 1000);
    }

    /**
     * 延迟控件的点击响应（避免快速连续点击）
     *
     * @param v
     * @param delayMillis 延迟时间，单位毫秒
     */
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
}
