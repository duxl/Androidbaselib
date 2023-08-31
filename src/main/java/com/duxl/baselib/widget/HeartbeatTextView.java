package com.duxl.baselib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.duxl.baselib.rx.SimpleObserver;
import com.duxl.baselib.utils.EmptyUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * 心跳TextView（可用于倒计时等功能）
 * create by duxl 2021/10/28
 */
public class HeartbeatTextView extends androidx.appcompat.widget.AppCompatTextView {

    private long mTimeInterval = 1; // 心跳时间间隔
    private TimeUnit mTimeUnit = TimeUnit.SECONDS; // 心跳间隔单位
    private Disposable mDisposable;

    public HeartbeatTextView(Context context) {
        this(context, null);
    }

    public HeartbeatTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {

    }

    private void stop() {
        if (EmptyUtils.isNotNull(mDisposable)) {
            if (!mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
            mDisposable = null;
        }
    }

    /**
     * 设置心跳间隔，默认1秒钟心跳一次
     *
     * @param time 心跳间隔时间
     * @param unit 心跳间隔时间单位
     */
    public void setInterval(long time, TimeUnit unit) {
        mTimeInterval = time;
        mTimeUnit = unit;
        start();
    }

    private void start() {
        stop();
        Observable
                .interval(0, mTimeInterval, mTimeUnit)
                .take(Long.MAX_VALUE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        super.onSubscribe(d);
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        super.onNext(aLong);
                        if (EmptyUtils.isNotNull(mOnHeartbeatListener)) {
                            mOnHeartbeatListener.onHeartbeat(HeartbeatTextView.this);
                        }
                        if (aLong == Long.MAX_VALUE - 1) {
                            start();
                        }
                    }
                });
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            start();
        } else {
            stop();
        }
    }

    /**
     * 心跳监听
     */
    public interface OnHeartbeatListener {
        void onHeartbeat(HeartbeatTextView v);
    }

    private OnHeartbeatListener mOnHeartbeatListener;

    public void setOnHeartbeatListener(OnHeartbeatListener listener) {
        this.mOnHeartbeatListener = listener;
    }
}
