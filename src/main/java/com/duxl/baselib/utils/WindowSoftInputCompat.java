package com.duxl.baselib.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

/**
 * Android全屏／沉浸式状态栏下，键盘挡住输入框解决办法
 * 参考文章：https://www.jianshu.com/p/eeb23a4ca91d
 * create by duxl 2021/2/24
 */
public class WindowSoftInputCompat {

    /**
     * 在Activity初始化中调用此方法，解决键盘挡住输入框的问题，
     * activity在Manifest注册时需要设置windowSoftInputMode="adjustResize"
     *
     * @param activity 有输入框的activity，如果是fragment传所在的activity
     */
    public static void assist(Activity activity) {
        assist(activity, null);
    }

    /**
     * 在Activity初始化中调用此方法，解决键盘挡住输入框的问题，
     * activity在Manifest注册时需要设置windowSoftInputMode="adjustResize"
     *
     * @param activity 有输入框的activity，如果是fragment传所在的activity
     * @param listener 键盘高度发生变化监听
     */
    public static void assist(Activity activity, OnHeightChangeListener listener) {
        new WindowSoftInputCompat(activity, listener);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    //为适应华为小米等手机键盘上方出现黑条或不适配
    private int contentHeight;//获取setContentView本来view的高度
    private boolean isfirst = true;//只用获取一次
    private int statusBarHeight;//状态栏高度
    private OnHeightChangeListener mOnHeightChangeListener;

    private WindowSoftInputCompat(Activity activity, OnHeightChangeListener listener) {
        mOnHeightChangeListener = listener;
        statusBarHeight = DisplayUtil.getBarHeight(activity);
        //1､找到Activity的最外层布局控件，它其实是一个DecorView,它所用的控件就是FrameLayout
        FrameLayout content = activity.findViewById(android.R.id.content);
        //2､获取到setContentView放进去的View
        mChildOfContent = content.getChildAt(0);
        // 注册监听前先移除避免多次注册
        mChildOfContent.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
        //3､给Activity的xml布局设置View树监听，当布局有变化，如键盘弹出或收起时，都会回调此监听
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
        //6､获取到Activity的xml布局的放置参数
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        //4､软键盘弹起会使GlobalLayout发生变化
        @Override
        public void onGlobalLayout() {
            if (isfirst) {
                contentHeight = mChildOfContent.getHeight();//兼容华为等机型
                isfirst = false;
            }
            //5､当前布局发生变化时，对Activity的xml布局进行重绘
            possiblyResizeChildOfContent();
        }
    };

    // 获取界面可用高度，如果软键盘弹起后，Activity的xml布局可用高度需要减去键盘高度
    private void possiblyResizeChildOfContent() {
        //1､获取当前界面可用高度，键盘弹起后，当前界面可用布局会减少键盘的高度
        int usableHeightNow = computeUsableHeight();
        //2､如果当前可用高度和原始值不一样
        if (usableHeightNow != usableHeightPrevious) {
            //3､获取Activity中xml中布局在当前界面显示的高度
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            //4､Activity中xml布局的高度-当前可用高度
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            //5､高度差大于屏幕1/4时，说明键盘弹出
            ValueAnimator valueAnimator = null;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // 6､键盘弹出了，Activity的xml布局高度应当减去键盘高度
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    valueAnimator = ValueAnimator.ofInt(frameLayoutParams.height, usableHeightSansKeyboard - heightDifference + statusBarHeight);
                } else {
                    valueAnimator = ValueAnimator.ofInt(frameLayoutParams.height, usableHeightSansKeyboard - heightDifference);
                }
            } else {
                valueAnimator = ValueAnimator.ofInt(frameLayoutParams.height, contentHeight);
            }
            valueAnimator.setDuration(30);
            valueAnimator.setInterpolator(new DecelerateInterpolator()); // 变化速率从快到慢
            //7､ 重绘Activity的xml布局
            valueAnimator.addUpdateListener(animation -> {
                int targetHeight = (int) animation.getAnimatedValue();
                if (mOnHeightChangeListener != null) {
                    mOnHeightChangeListener.onHeightChanged(contentHeight - targetHeight);
                }

                if (mOnHeightChangeListener == null || mOnHeightChangeListener.resizeContentHeight()) {
                    frameLayoutParams.height = targetHeight;
                    mChildOfContent.requestLayout();
                }

            });
            valueAnimator.start();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        // 全屏模式下：直接返回r.bottom，r.top其实是状态栏的高度
        return (r.bottom - r.top);
    }

    /**
     * 键盘高度发生变化监听
     */
    public static abstract class OnHeightChangeListener {

        /**
         * 当键盘高度发生变化时，是否更改Activity的内容高度，使输入框不被键盘遮挡
         *
         * @return 是否更改Activity内容高度，默认true
         */
        public boolean resizeContentHeight() {
            return true;
        }

        /**
         * 键盘高度发生变化
         *
         * @param softHeight 软键盘当前高度
         */
        public abstract void onHeightChanged(int softHeight);
    }
}
