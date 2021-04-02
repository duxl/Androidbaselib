package com.duxl.baselib.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.duxl.baselib.BuildConfig;
import com.duxl.baselib.rx.SimpleObserver;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;


/**
 * ToastUtils
 */

public class ToastUtils {

    private static Context context = Utils.getContext();
    private static Toast toast;
    private static Disposable mDisposable;

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

        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }

        toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER, 0, DisplayUtil.getScreenHeight(context) / 4);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            toast.addCallback(new Toast.Callback() {
                @Override
                public void onToastHidden() {
                    if (EmptyUtils.isNotNull(toast)) {
                        toast.removeCallback(this);
                        toast = null;
                    }
                }
            });
        } else {
            Observable
                    .timer(3, TimeUnit.SECONDS)
                    .subscribe(new SimpleObserver<Long>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            mDisposable = d;
                        }

                        @Override
                        public void onNext(@NonNull Long aLong) {
                            if (EmptyUtils.isNotNull(toast)) {
                                toast = null;
                            }
                            mDisposable = null;
                        }
                    });
        }
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
