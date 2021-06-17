package com.duxl.baselib.ui.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.duxl.baselib.R;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseDialogFragment extends AppCompatDialogFragment {


    private static final String TAG = "BaseDialogFragment";

    private static final float DEFAULT_DIM = 0.5f;
    private Unbinder mUnbinder;
    private OnDismissListener mOnDismissListener;


    @LayoutRes
    public abstract int getResId();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.dialog_fragment_style);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(getCancelOutside());
        View view = inflater.inflate(getResId(), container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        super.onActivityCreated(savedInstanceState);
        //设置背景为透明
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            //设置弹窗大小为会屏
            window.setLayout(getWidth(), getHeight());
            //去除阴影
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.dimAmount = getDimAmount();
            layoutParams.gravity = getGravity();
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(layoutParams);
        }
    }

    @Override
    public void onDestroyView() {
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(this);
        }
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroyView();
    }

    public int getGravity() {
        return Gravity.CENTER;
    }

    public int getWidth() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public int getHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public float getDimAmount() {
        return DEFAULT_DIM;
    }

    public boolean getCancelOutside() {
        return true;
    }

    public String getFragmentTag() {
        return TAG;
    }

    public void showDialog(FragmentManager manager) {
        show(manager, getFragmentTag());
    }

    public void showDialog(FragmentManager manager, String tag) {
        show(manager, tag);
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        manager
                .beginTransaction()
                .add(this, tag)
                .commitAllowingStateLoss();
    }

    public void dismissDialog() {
        if (getDialog() != null && getDialog().isShowing()) {
            getDialog().dismiss();
        }
    }

    public interface OnDismissListener {
        void onDismiss(BaseDialogFragment dialogFragment);
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.mOnDismissListener = listener;
    }
}
