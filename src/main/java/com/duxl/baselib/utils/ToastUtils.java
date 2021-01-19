package com.duxl.baselib.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.duxl.baselib.BuildConfig;


/**
 * ToastUtils
 */

public class ToastUtils {

    private static Context context = Utils.getContext();
    private static Toast toast;

    public static void show(int resId) {
        show(context.getResources().getText(resId), Toast.LENGTH_SHORT);
    }

    public static void show(int resId, int duration) {
        show(context.getResources().getText(resId), duration);
    }

    public static void show(CharSequence text) {
        show(text, Toast.LENGTH_SHORT);
    }


    public static void show(CharSequence text, int duration) {
        if (TextUtils.isEmpty(String.valueOf(text))) {
            return;
        }
        if (toast != null) {
            toast.cancel();
            toast = null;
        }

        toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER, 0, DisplayUtil.getScreenHeight(context) / 4);
        toast.show();
    }

    public static void showCenter(CharSequence text) {
        if (TextUtils.isEmpty(String.valueOf(text))) {
            return;
        }
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void show(int resId, Object... args) {
        show(String.format(context.getResources().getString(resId), args),
                Toast.LENGTH_SHORT);
    }

    public static void show(String format, Object... args) {
        show(String.format(format, args), Toast.LENGTH_SHORT);
    }

    public static void show(int resId, int duration, Object... args) {
        show(String.format(context.getResources().getString(resId), args),
                duration);
    }

    public static void show(String format, int duration, Object... args) {
        show(String.format(format, args), duration);
    }

    public static void showLong(CharSequence text) {
        show(text, Toast.LENGTH_LONG);
    }


    public static void showLongWhenDebug(CharSequence text) {
        if (BuildConfig.DEBUG) {
            show(text, Toast.LENGTH_LONG);
        }
    }
}
