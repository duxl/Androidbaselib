package com.duxl.baselib.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.duxl.baselib.R;
import com.duxl.baselib.utils.EmptyUtils;

/**
 * 加载框
 * create by duxl 2021/3/25
 */
public class ProgressDialog implements IProgressDialog {

    protected Dialog mDialog;
    protected View mContentView;
    protected boolean mCancelTouchOutside;
    protected boolean mCancelable;

    private long mProgressDialogShowTime; // 加载框开始显示时间
    private long mProgressDialogMinDur = 500; // 加载框至少显示时长（设置适当的时长，避免加载框一闪就消失）

    public ProgressDialog(Context context) {
        mDialog = new Dialog(context, R.style.progress_dialog_style);
        mContentView = getContentView(context);
        if (EmptyUtils.isNotNull(mContentView)) {
            mDialog.setContentView(mContentView);
        }
    }

    protected View getContentView(Context context) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_progress_dialog, null);
        return contentView;
    }

    public void setMessage(CharSequence msg) {
        if (EmptyUtils.isNotNull(mContentView)) {
            TextView tvMsg = mContentView.findViewById(R.id.progress_msg);
            tvMsg.setText(msg);
        }
    }

    @Override
    public void show(CharSequence msg) {
        if (EmptyUtils.isNotNull(mDialog)) {
            mDialog.setCanceledOnTouchOutside(cancelTouchOutside());
            mDialog.setCancelable(cancelable());
            setMessage(msg);

            if (!mDialog.isShowing()) {
                mDialog.show();
            }

            mProgressDialogShowTime = System.currentTimeMillis();
        }
    }

    @Override
    public void dismiss() {
        if (EmptyUtils.isNotNull(mDialog)) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            mDialog = null;
        }
    }

    /**
     * 延迟隐藏dialog，延迟时间跟{@link #setShowMinDuration(long)}有关
     */
    public void dismissDelay() {
        long durTime = System.currentTimeMillis() - mProgressDialogShowTime;
        if (durTime < mProgressDialogMinDur) {
            mContentView.postDelayed(() -> dismiss(), mProgressDialogMinDur - durTime);
            return;
        }
        dismiss();
    }

    /**
     * 设置点击加载框外是否消失
     *
     * @param cancel
     * @return
     */
    public ProgressDialog setCancelTouchOutside(boolean cancel) {
        this.mCancelTouchOutside = cancel;
        return this;
    }

    @Override
    public boolean cancelTouchOutside() {
        return mCancelTouchOutside;
    }

    /**
     * 设置弹框是否可取消（指的是用户点击手机的返回键）
     *
     * @param cancel
     * @return
     */
    public ProgressDialog setCancelable(boolean cancel) {
        this.mCancelable = cancel;
        return this;
    }

    @Override
    public boolean cancelable() {
        return mCancelable;
    }

    /**
     * 设置dialog显示的至少时长（设置适当的时长，避免加载框一闪就消失）
     *
     * @param duration 时长，单位毫秒
     * @return
     */
    public ProgressDialog setShowMinDuration(long duration) {
        this.mProgressDialogMinDur = duration;
        return this;
    }

    /**
     * 获取dialog显示的至少时长
     *
     * @return 时间，单位毫秒
     */
    public long getShowMinDuration() {
        return mProgressDialogMinDur;
    }
}
