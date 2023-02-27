package com.duxl.baselib.rx;

import android.content.Context;

import com.duxl.baselib.R;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.widget.dialog.ProgressDialog;

import java.lang.ref.WeakReference;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;

/**
 * create by duxl 2021/3/25
 */
public class ProgressTransformer<T> implements ObservableTransformer<T, T> {

    protected ProgressDialog mProgressDialog;
    private WeakReference<Context> mContextReference;
    private boolean mAutoDismiss = true;

    public ProgressTransformer(Context context) {
        this(context, false, true);
    }

    public ProgressTransformer(Context context, boolean cancelTouchOutside, boolean cancel) {
        this(context, context.getString(R.string.data_loading), cancelTouchOutside, cancel);
    }

    public ProgressTransformer(Context context, CharSequence msg, boolean cancelTouchOutside, boolean cancel) {
        this.mContextReference = new WeakReference(context);
        this.mProgressDialog = getProgressDialog();
        if (EmptyUtils.isNotNull(mProgressDialog)) {
            mProgressDialog.setCancelTouchOutside(cancelTouchOutside);
            mProgressDialog.setCancelable(cancel);
            mProgressDialog.show(msg);
        }
    }

    /**
     * 设置dialog显示的至少时长（设置适当的时长，避免加载框一闪就消失）<br/>
     * 注意：如果dialog因为延迟还未消失就finish页面，android11上有可能会闪退，
     * finish页面需要自行确保已经调用{@link #dismiss()}已经关闭dialog
     *
     * @param duration 时长，单位毫秒
     * @return
     */
    public ProgressTransformer setShowMinDuration(long duration) {
        if (EmptyUtils.isNotNull(mProgressDialog)) {
            mProgressDialog.setShowMinDuration(duration);
        }
        return this;
    }

    public ProgressTransformer setMsg(CharSequence msg) {
        if (EmptyUtils.isNotNull(mProgressDialog)) {
            mProgressDialog.setMessage(msg);
        }
        return this;
    }

    /**
     * 设置对话框是否自动消失
     *
     * @param dismiss 是否自动消失，默认true
     */
    public ProgressTransformer setAutoDismiss(boolean dismiss) {
        this.mAutoDismiss = dismiss;
        return this;
    }

    protected ProgressDialog getProgressDialog() {
        if (EmptyUtils.isNotNull(mContextReference.get())) {
            return new ProgressDialog(mContextReference.get());
        }
        return null;
    }

    @Override
    public @NonNull ObservableSource<T> apply(@NonNull Observable<T> upstream) {
        return upstream
                .doOnSubscribe(disposable -> {
                })
                .doOnNext(t -> {
                    dismissDelay();
                })
                .doOnTerminate(() -> {
                    dismissDelay();
                })
                .doOnDispose(() -> {
                    dismissDelay();
                });
    }

    /**
     * 隐藏dialog
     */
    public void dismiss() {
        if (EmptyUtils.isNotNull(mProgressDialog)) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 延迟隐藏dialog
     */
    public void dismissDelay() {
        if (mAutoDismiss && EmptyUtils.isNotNull(mProgressDialog)) {
            mProgressDialog.dismissDelay();
        }
    }
}
