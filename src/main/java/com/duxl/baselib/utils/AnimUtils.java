package com.duxl.baselib.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

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

    /**
     * adapter.notifyItemChanged(int Position)更新单个item时会出现闪烁问题，
     * 这里可以通过取消recycler的动画来避免闪烁
     *
     * @param view RecyclerView
     */
    public static void clearRecyclerAnim(RecyclerView view) {
        RecyclerView.ItemAnimator itemAnimator = view.getItemAnimator();
        if (itemAnimator != null) {
            // //设置更新动画duration为0，其他的你也可以设置。
            itemAnimator.setChangeDuration(0);
            if (itemAnimator instanceof DefaultItemAnimator) {
                //取消动画
                ((DefaultItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
            }
        }
    }
}
