package com.duxl.baselib.utils;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.duxl.baselib.R;
import com.duxl.baselib.widget.DataRunnable;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * dp、sp 转换为 px 的工具类
 *
 * @author duxl 2012.11.12
 */
public class DisplayUtil {

    /**
     * 获取屏幕宽度
     *
     * @param activity
     * @return
     */
    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindow().getWindowManager().getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param activity
     * @return
     */
    public static int getScreenHeight(Activity activity) {
        Display display = activity.getWindow().getWindowManager().getDefaultDisplay();
        return display.getHeight();
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getBarHeight(Context context) {
        int barHeight = getBarHeight2(context);
        if (barHeight > 0) {
            return barHeight;
        }

        barHeight = dip2px(context, 38);// 默认为38，貌似大部分是这样的
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            barHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return barHeight;
    }

    private static int getBarHeight2(Context context) {
        int result = -1;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;

    }

    /**
     * 获取屏幕密度
     *
     * @param context
     * @return
     */
    public static String getDensity(Context context) {
        float d = context.getResources().getDisplayMetrics().density;
        String density = d + "";
        return density;
    }

    /**
     * 显示键盘，建议使用showKeyboard(Window window, View view)
     *
     * @param ctx
     */
    public static void showKeyboard(Activity ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * 显示软键盘
     * 不依赖 InputMethodManager 的历史 bug
     * Android 11+ 完美
     * AndroidX 兼容性更好
     *
     * @param window
     * @param view
     */
    public static void showKeyboard(Window window, View view) {
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(window, view);
        if (insetsController != null) {
            insetsController.show(WindowInsetsCompat.Type.ime());
        }
    }

    /**
     * 隐藏键盘，建议使用hideKeyboard(Window window, View view)
     *
     * @param ctx
     * @return
     */
    public static boolean hideKeyboard(Activity ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        View curFocus = ctx.getCurrentFocus();
        return curFocus != null && imm.hideSoftInputFromWindow(curFocus.getWindowToken(), 0);
    }

    /**
     * 隐藏软键盘，建议使用hideKeyboard(Window window, View view)
     * @param view
     * @param mContext
     */
    public static void hideKeyboard(View view, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }

    /**
     * 隐藏软键盘
     * 不依赖 InputMethodManager 的历史 bug
     * Android 11+ 完美
     * AndroidX 兼容性更好
     *
     * @param window
     * @param view
     */
    public static void hideKeyboard(Window window, View view) {
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(window, view);
        if (insetsController != null) {
            insetsController.hide(WindowInsetsCompat.Type.ime());
        }
    }

    /**
     * 软键盘是否显示<br/>
     * 已过时建议使用 {@link DisplayUtil#observerKeyboardVisibleChanged observerKeyboardVisibleChanged} 方法
     *
     * @param context
     * @return
     */
    public static boolean isKeyboardShow(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    /**
     * 软键盘是否作用与某个控件。<br/>
     * 已过时建议使用 {@link DisplayUtil#observerKeyboardVisibleChanged observerKeyboardVisibleChanged} 方法
     *
     * @param context
     * @param view    当前焦点所在的视图
     * @return
     */
    public static boolean isKeyboardShow(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive(view);
    }

    /**
     * 输入法（软键盘）是否正在接收输入文本，通常用来检测软键盘是否处于激活状态并准备接受用户输入
     *
     * @param context
     * @return
     */
    public static boolean isKeyboardAcceptingText(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isAcceptingText();
    }

    /**
     * 观察软键盘显示状态
     *
     * @param v
     * @param onKeyboardVisibleChanged
     */
    public static void observerKeyboardVisibleChanged(@NonNull View v, DataRunnable<Boolean> onKeyboardVisibleChanged) {
        Objects.requireNonNull(v);
        ViewCompat.setOnApplyWindowInsetsListener(v, (v1, insets) -> {
            if (onKeyboardVisibleChanged != null) {
                boolean keyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime());
                if (v1.getTag(R.id.tagKeyboardVisible) == null || ((Boolean) v1.getTag(R.id.tagKeyboardVisible) != keyboardVisible)) {
                    v1.setTag(R.id.tagKeyboardVisible, keyboardVisible);
                    onKeyboardVisibleChanged.run(keyboardVisible);
                }
            }
            return insets;
        });
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        if (pxValue == 0) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        if (dipValue == 0) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        if (pxValue == 0) {
            return 0;
        }
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        if (spValue == 0) {
            return 0;
        }
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取控件在屏幕的X坐标
     *
     * @param v
     * @return
     */
    public static int getViewOnScreenLocationX(View v) {
        return getViewOnScreenLocation(v)[0];
    }

    /**
     * 获取控件在屏幕的Y坐标
     *
     * @param v
     * @return
     */
    public static int getViewOnScreenLocationY(View v) {
        return getViewOnScreenLocation(v)[1];
    }

    /**
     * 获取控件在屏幕中的位置
     *
     * @param v
     * @return
     */
    public static int[] getViewOnScreenLocation(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        return location;
    }

    /**
     * 获取控件在父窗体中的X坐标
     *
     * @param v
     * @return
     */
    public static int getViewInWindowLocationX(View v) {
        return getViewInWindowLocation(v)[0];
    }

    /**
     * 获取控件在父窗体中的Y坐标
     *
     * @param v
     * @return
     */
    public static int getViewInWindowLocationY(View v) {
        return getViewInWindowLocation(v)[1];
    }

    /**
     * 获取控件在父窗体中的位置
     *
     * @param v
     * @return
     */
    public static int[] getViewInWindowLocation(View v) {
        int[] location = new int[2];
        v.getLocationInWindow(location);
        return location;
    }

    /**
     * 获取控件焦点，并显示软键盘
     *
     * @return
     */
    public static void showSoftInputFromWindow(Activity context, View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    /**
     * 强制显示软键盘
     *
     * @param context
     * @param view
     */
    public static void showSoftInputForced(Activity context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 强制隐藏软键盘
     *
     * @param context
     * @param view
     */
    public static void hideSoftInputForced(Activity context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 更改颜色透明度
     *
     * @param originalColor 需要更改的颜色
     * @param alpha         更改的透明度
     * @return
     */
    public static int changeAlpha(int originalColor,
                                  @FloatRange(from = 0.0, to = 1.0) float alpha) {
        return Color.argb(
                (int) (255 * alpha),
                Color.red(originalColor),
                Color.green(originalColor),
                Color.blue(originalColor)
        );
    }

    /**
     * 从一个颜色切换到另一个颜色
     *
     * @param fromColor 起始颜色
     * @param toColor   结束颜色
     * @param fraction  过度阀值，介于 0 到 1 之间的值
     * @return 返回指定阀值的颜色
     */
    public static int progressColor(int fromColor, int toColor, float fraction) {
        return (int) new ArgbEvaluator().evaluate(fraction, fromColor, toColor);
    }

    /**
     * 将int类型的color转为#AARRGGBB 字符串（带透明度）
     *
     * @param color
     * @return
     */
    public static String colorIntToARGB(int color) {
        return String.format("#%08X", color);
    }

    /**
     * 将int类型的color转为#RRGGBB 字符串(不带透明度)
     *
     * @param color
     * @return
     */
    public static String colorIntToRGB(int color) {
        return String.format("#%06X", 0xFFFFFF & color);
    }

    /**
     * 测量view，一般通过LayoutInflater.inflate()加载的布局，
     * 在没有绘制到屏幕上是拿不到view的高宽的，调用此方法测量view
     * 后即可获取高宽
     *
     * @param v
     */
    public static void measureView(View v) {
        int size = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(size, size);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
    }

    /**
     * 获取测量尺寸（自定义view时需要测量view尺寸可调用此方法）
     *
     * @param measureSpace 尺寸和模式
     * @param contentSize  内容的尺寸
     * @return
     */
    public static int getMeasureSize(int measureSpace, int contentSize) {
        int mode = View.MeasureSpec.getMode(measureSpace);
        if (mode == View.MeasureSpec.EXACTLY) { // 确切的尺寸（match_parent或100dp）
            return View.MeasureSpec.getSize(measureSpace);
        } else if (mode == View.MeasureSpec.AT_MOST) { // 需要根据内容测量得到尺寸（wrap_content）
            return contentSize;
        }
        return contentSize;
    }

    /**
     * 截图，将view转成bitmap(如果v已显示出来，直接调用{@link #cutMeasuredView(View)})
     *
     * @param v
     * @return
     */
    public static Bitmap cutView(View v) {
        measureView(v);
        return cutMeasuredView(v);
    }

    /**
     * 截图，将view转成bitmap，前提是v已经显示到UI上
     *
     * @param v
     * @return
     */
    public static Bitmap cutMeasuredView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    /**
     * 是否存在虚拟导航bar
     *
     * @param context
     * @return
     */
    public static boolean isSupportNavBar(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (wm == null) return false;
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y || realSize.x != size.x;
        }
        boolean menu = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        return !menu && !back;
    }

    /**
     * 设置虚拟导航bar的颜色
     *
     * @param activity
     * @param color
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setNavBarColor(Activity activity, final int color) {
        if (isSupportNavBar(activity)) {
            activity.getWindow().setNavigationBarColor(color);
        }
    }
}
