package com.duxl.baselib.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsAnimationCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import java.util.Objects;

/**
 * Android全屏／沉浸式状态栏下，键盘挡住输入框解决办法
 * 参考文章：https://www.jianshu.com/p/eeb23a4ca91d
 * create by duxl 2021/2/24
 */
public class WindowSoftInputCompat {

    /**
     * 在Activity初始化中调用此方法，解决键盘挡住输入框的问题，
     * activity在Manifest注册时需要设置windowSoftInputMode="adjustResize"
     * <br/><br/>
     * <font color='yellow'>推荐使用更加丝滑的assistV2方法(activity)</font>
     *
     * @param activity 有输入框的activity，如果是fragment传所在的activity
     */
    public static void assist(Activity activity) {
        assist(activity, null);
    }

    /**
     * 在Activity初始化中调用此方法，解决键盘挡住输入框的问题，
     * activity在Manifest注册时需要设置windowSoftInputMode="adjustResize"
     * <br/><br/>
     * <font color='yellow'>推荐使用更加丝滑的assistV2(activity, listener)方法</font>
     *
     * @param activity 有输入框的activity，如果是fragment传所在的activity
     * @param listener 键盘高度发生变化监听
     */
    public static void assist(Activity activity, OnHeightChangeListener listener) {
        new WindowSoftInputCompat(activity, listener, false);
    }

    public static void assistV2(Activity activity) {
        assistV2(activity, null);
    }

    public static void assistV2(Activity activity, OnHeightChangeListener listener) {
        new WindowSoftInputCompat(activity, listener, true);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    //为适应华为小米等手机键盘上方出现黑条或不适配
    private int contentHeight;//获取setContentView本来view的高度
    private boolean isfirst = true;//只用获取一次
    private int statusBarHeight;//状态栏高度
    private int currentIme; // 当前软键盘的高度（软键盘开始动画前获取到的值）
    private OnHeightChangeListener mOnHeightChangeListener;

    private WindowSoftInputCompat(Activity activity, OnHeightChangeListener listener, boolean v2) {
        mOnHeightChangeListener = listener;
        statusBarHeight = DisplayUtil.getBarHeight(activity);
        //1､找到Activity的最外层布局控件，它其实是一个DecorView,它所用的控件就是FrameLayout
        FrameLayout content = activity.findViewById(android.R.id.content);
        //2､获取到setContentView放进去的View
        mChildOfContent = content.getChildAt(0);
        if (v2) {
            final int initPaddingBottom = mChildOfContent.getPaddingBottom();

            ViewCompat.setWindowInsetsAnimationCallback(mChildOfContent, new WindowInsetsAnimationCompat.Callback(
                    // DISPATCH_MODE_CONTINUE_ON_SUBTREE 表示继续将键盘动画事件传递给子 View，让整个 UI 树都能同步响应 insets 动画。
                    // DISPATCH_MODE_STOP 回调只在当前 View 停止，不传递下去, 隔绝子 View 的处理
                    WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_CONTINUE_ON_SUBTREE
            ) {

                @Override
                public void onPrepare(@NonNull WindowInsetsAnimationCompat animation) {
                    super.onPrepare(animation);
                    // 获取当前键盘高度（动画开始时刻的真实高度）
                    currentIme = Objects.requireNonNull(ViewCompat.getRootWindowInsets(mChildOfContent)).getInsets(WindowInsetsCompat.Type.ime()).bottom;
                }

                @NonNull
                @Override
                public WindowInsetsAnimationCompat.BoundsCompat onStart(@NonNull WindowInsetsAnimationCompat animation, @NonNull WindowInsetsAnimationCompat.BoundsCompat bounds) {
                    if (mOnHeightChangeListener != null) {
                        try {
                            // 软件键盘折叠状态的高度
                            int lowerIme = bounds.getLowerBound().bottom;
                            // 软件键盘展开后的的高度
                            int upperIme = bounds.getUpperBound().bottom;
                            //System.out.println("duxl.log: 软件键盘折叠状态的高度=" + lowerIme + ", 软件键盘展开后的的高度=" + upperIme + ", 获取当前键盘高度=" + currentIme);
                            mOnHeightChangeListener.onStart(currentIme < upperIme, upperIme);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return super.onStart(animation, bounds);
                }

                @Override
                public void onEnd(@NonNull WindowInsetsAnimationCompat animation) {
                    super.onEnd(animation);
                    if (mOnHeightChangeListener != null) {
                        WindowInsetsCompat controller = ViewCompat.getRootWindowInsets(content);
                        if (controller != null) {
                            mOnHeightChangeListener.onEnd(controller.isVisible(WindowInsetsCompat.Type.ime()));
                        }
                    }
                }

                @NonNull
                @Override
                public WindowInsetsCompat onProgress(@NonNull WindowInsetsCompat insets,
                                                     @NonNull List<WindowInsetsAnimationCompat> runningAnimations) {
                    Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
                    int bottom = imeInsets.bottom;

                    if (mOnHeightChangeListener != null) {
                        mOnHeightChangeListener.onHeightChanged(bottom);
                    }

                    if (mOnHeightChangeListener == null || mOnHeightChangeListener.resizeContentHeight()) {
                        mChildOfContent.setPadding(
                                mChildOfContent.getPaddingLeft(),
                                mChildOfContent.getPaddingTop(),
                                mChildOfContent.getPaddingRight(),
                                initPaddingBottom + bottom
                        );
                    }

                    // 可以修改了insets的参数 使 后续View看到修改后的insets
                    return insets;
                }
            });
        } else {
            // 注册监听前先移除避免多次注册
            mChildOfContent.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
            //3､给Activity的xml布局设置View树监听，当布局有变化，如键盘弹出或收起时，都会回调此监听
            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
        }
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


        /**
         * 软键盘变化开始
         *
         * @param targetVisible true表示开始显示软键盘，false表示开始隐藏软键盘
         * @param fullHeight    软键盘完全显示时的高度
         */
        public void onStart(boolean targetVisible, int fullHeight) {
        }

        /**
         * 软键盘变化结束
         *
         * @param isVisible 结束后软键盘是否显示
         */
        public void onEnd(boolean isVisible) {
        }
    }
}
