package com.duxl.baselib.http;

import android.os.Looper;

import com.duxl.baselib.BaseApplication;
import com.duxl.baselib.BuildConfig;
import com.duxl.baselib.http.interceptor.NoNetworkException;
import com.duxl.baselib.utils.ToastUtils;
import com.duxl.baselib.utils.Utils;
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
 * <pre>
 * HttpObserver
 *
 *
 * create by duxl 2021/1/15
 * 根据Root定义的code、msg和isSuccess处理接口返回的数据
 * 如果成功则会回调{@link #onSuccess(BaseRoot) onSuccess(root)方法}
 * 如果失败，则会根据{@link HttpExceptionReasons 错误配置信息}回调{@link #onError(int, String, BaseRoot) onError(code, msg, root)方法}
 *
 * </pre>
 *
 * @param <R>
 */
public abstract class BaseHttpObserver<R extends BaseRoot> implements Observer<R> {

    public BaseHttpObserver() {
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull R root) {
        if (root.isSuccess()) {
            onSuccess(root);
//            try { // 放在try-catch里面的话，业务代码的异常会被catch掉
//                onSuccess(root);
//            } catch (Exception e) {
//                onError(e);
//            }
        } else {
            onError(root.getCode(), root.getMsg(), root);
        }

    }

    @Override
    public void onError(@NonNull Throwable e) {
        if (Utils.getApp().getGlobalHttpConfig().isDEBUG()) {
            e.printStackTrace();
        }
        HttpExceptionReason exceptionReason = getReasonByException(e);
        onError(exceptionReason.getCode(), exceptionReason.getMsg(), null);
    }

    @Override
    public void onComplete() {

    }

    public abstract void onSuccess(R root);

    public void onError(int code, String msg, R root) {
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
