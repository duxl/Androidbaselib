package com.duxl.baselib.utils;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

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

    public static void startRotationAnim(View v, float from, float to) {
        startRotationAnim(v, from, to, 200, null);
    }

    /**
     * 启动旋转动画
     *
     * @param v
     * @param from         旋转的开始角度
     * @param to           旋转的结束角度
     * @param duration     动画时长
     * @param interpolator 差值器
     */
    public static void startRotationAnim(View v, float from, float to, long duration, @Nullable TimeInterpolator interpolator) {
        v.clearAnimation();
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "rotation", from, to);
        anim.setDuration(duration);
        if (interpolator != null) {
            anim.setInterpolator(interpolator);
        }
        anim.start();
    }

    /**
     * adapter.notifyItemChanged(int Position)更新单个item时会出现闪烁问题，
     * 这里可以通过取消recycler的动画来避免闪烁
     *
     * @param view RecyclerView
     */
    public static void setRecyclerAnimEnable(RecyclerView view, boolean enabled) {
        RecyclerView.ItemAnimator itemAnimator = view.getItemAnimator();
        if (itemAnimator != null) {
            // //设置更新动画duration为0，其他的你也可以设置。
            if (enabled) {
                itemAnimator.setChangeDuration(250);
                itemAnimator.setAddDuration(120);
                itemAnimator.setMoveDuration(250);
                itemAnimator.setRemoveDuration(120);
                if (itemAnimator instanceof DefaultItemAnimator) {
                    //取消动画
                    ((DefaultItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
                }
            } else {
                itemAnimator.setChangeDuration(0);
                itemAnimator.setAddDuration(0);
                itemAnimator.setMoveDuration(0);
                itemAnimator.setRemoveDuration(0);
                if (itemAnimator instanceof DefaultItemAnimator) {
                    //取消动画
                    ((DefaultItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
                }
            }
        }
    }


    /**
     * 使子View变化时产生动画，比如 setVisibility(View.GONE)。可以自定义 添加、移除、改变布局 时的动画：
     *
     * @param v
     * @param enable
     */
    public static void setLayoutAnimateChangesEnable(ViewGroup v, Boolean enable) {
        if (enable) {
            LayoutTransition layoutTransition = new LayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING); // 启用改变动画
            v.setLayoutTransition(layoutTransition);
        } else {
            v.setLayoutTransition(null);
        }
    }
}
