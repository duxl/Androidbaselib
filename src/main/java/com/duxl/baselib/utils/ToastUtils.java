package com.duxl.baselib.utils;

import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.duxl.baselib.rx.SimpleObserver;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;


/**
 * ToastUtils
 */

public class ToastUtils {

    private static Toast toast;
    private static Disposable mDisposable;

    public static void show(int resId) {
        show(Utils.getString(resId), Toast.LENGTH_SHORT);
    }

    public static void show(int resId, int duration) {
        show(Utils.getString(resId), duration);
    }

    public static void show(CharSequence text) {
        show(text, Toast.LENGTH_SHORT);
    }

    public static void show(CharSequence text, int duration) {
        show(text, duration, DisplayUtil.getScreenHeight(Utils.getActContextOrApp()) / 4);
    }

    public static void show(CharSequence text, int duration, int yOffset) {
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

        toast = Toast.makeText(Utils.getActContextOrApp(), text, duration);
        toast.setGravity(Gravity.CENTER, 0, yOffset);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            toast.addCallback(new Toast.Callback() {
                @Override
                public void onToastHidden() {
                    if (EmptyUtils.isNotNull(toast)) {
                        toast.removeCallback(this);
                        toast = null;
                    }
                    mDisposable = null;
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
                            toast = null;
                            mDisposable = null;
                        }
                    });
        }
        toast.show();
    }

    public static void showCenter(CharSequence text, int duration) {
        show(text, duration, 0);
    }

    public static void showCenter(int resId, int duration) {
        show(Utils.getString(resId), duration, 0);
    }

    public static void showCenter(CharSequence text) {
        showCenter(text, Toast.LENGTH_SHORT);
    }

    public static void showCenter(int resId) {
        showCenter(Utils.getString(resId), Toast.LENGTH_SHORT);
    }

    public static void show(int resId, Object... args) {
        show(String.format(Utils.getString(resId), args), Toast.LENGTH_SHORT);
    }

    public static void show(String format, Object... args) {
        show(String.format(format, args), Toast.LENGTH_SHORT);
    }

    public static void show(int resId, int duration, Object... args) {
        show(String.format(Utils.getString(resId), args), duration);
    }

    public static void show(String format, int duration, Object... args) {
        show(String.format(format, args), duration);
    }

    public static void showLong(CharSequence text) {
        show(text, Toast.LENGTH_LONG);
    }


    public static void showLongWhenDebug(CharSequence text) {
        if (Utils.getApp().getGlobalHttpConfig().isDEBUG()) {
            show(text, Toast.LENGTH_LONG);
        }
    }
}
