package com.duxl.baselib.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * 动画工具类
 */
public class AnimUtils {

    /**
     * 启动动画
     *
     * @param v         需要执行动画的控件view
     * @param animResId 动画资源id
     */
    public static void startAnimation(View v, int animResId) {
        startAnimation(v, animResId, null);
    }

    /**
     * 启动动画
     *
     * @param v         需要执行动画的控件view
     * @param animResId 动画资源id
     * @param listener  动画listener
     */
    public static void startAnimation(View v, int animResId, Animation.AnimationListener listener) {
        Animation animation = AnimationUtils.loadAnimation(v.getContext(), animResId);
        if (listener != null) {
            animation.setAnimationListener(listener);
        }
        v.clearAnimation();
        v.startAnimation(animation);
    }
}
