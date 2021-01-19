package com.duxl.baselib.http;

import android.os.Looper;

import com.duxl.baselib.BuildConfig;
import com.duxl.baselib.http.interceptor.NoNetworkException;
import com.duxl.baselib.utils.ToastUtils;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.concurrent.TimeoutException;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.HttpException;

/**
 * HttpObserver
 * <p>
 * create by duxl 2021/1/15
 */
public abstract class BaseHttpObserver<T extends BaseRoot> implements Observer<T> {

    public BaseHttpObserver() {
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull T root) {
        if (root.isSuccess()) {
            try {
                onSuccess(root);
            } catch (Exception e) {
                onError(e);
            }
        } else {
            onError(root.getCode(), root.getMsg(), root);
        }

    }

    @Override
    public void onError(@NonNull Throwable e) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
        HttpExceptionReason exceptionReason = getReasonByException(e);
        onError(exceptionReason.getCode(), exceptionReason.getMsg(), null);
    }

    @Override
    public void onComplete() {

    }

    public abstract void onSuccess(T root);

    public void onError(int code, String msg, T root) {
        if (isMainThread()) {
            ToastUtils.show(msg);
        }
    }

    protected boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    protected HttpExceptionReason getReasonByException(Throwable e) {
        if (e instanceof HttpException) {
            return HttpExceptionReasons.BAD_NETWORK;

        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {
            return HttpExceptionReasons.CONNECT_ERROR;

        } else if (e instanceof InterruptedIOException
                || e instanceof TimeoutException) {
            return HttpExceptionReasons.CONNECT_TIMEOUT;

        } else if (e instanceof NoNetworkException) {
            return HttpExceptionReasons.CONNECT_NO;

        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof JsonSyntaxException
                || e instanceof MalformedJsonException
                || e instanceof ParseException) {
            return HttpExceptionReasons.PARSE_ERROR;

        } else {
            return HttpExceptionReasons.UNKNOWN_ERROR;
        }
    }
}
