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

    private Dialog mDialog;
    private View mContentView;
    private boolean mCancelTouchOutside;
    private boolean mCancelable;

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

    public void setCancelTouchOutside(boolean cancel) {
        this.mCancelTouchOutside = cancel;
    }

    @Override
    public boolean cancelTouchOutside() {
        return mCancelTouchOutside;
    }

    public void setCancelable(boolean cancel) {
        this.mCancelable = cancel;
    }

    @Override
    public boolean cancelable() {
        return mCancelable;
    }
}
