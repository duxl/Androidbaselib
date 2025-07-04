package com.duxl.baselib.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.CheckResult;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.duxl.baselib.R;
import com.trello.rxlifecycle4.LifecycleProvider;
import com.trello.rxlifecycle4.LifecycleTransformer;
import com.trello.rxlifecycle4.RxLifecycle;
import com.trello.rxlifecycle4.android.FragmentEvent;
import com.trello.rxlifecycle4.android.RxLifecycleAndroid;

import java.util.Objects;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public abstract class BaseDialogFragment extends AppCompatDialogFragment implements LifecycleProvider<FragmentEvent> {


    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();
    private static final String TAG = "BaseDialogFragment";

    private static final float DEFAULT_DIM = 0.5f;
    private OnShowListener mOnShowListener;
    private OnDismissListener mOnDismissListener;


    @LayoutRes
    public abstract int getResId();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.dialog_fragment_style);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(getCancelOutside());
        getDialog().setOnShowListener(dialog -> {
            if (mOnShowListener != null) {
                mOnShowListener.onShowListener(BaseDialogFragment.this, getDialog());
            }
        });
        return createView(inflater, container);
    }

    protected View createView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(getResId(), container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        super.onActivityCreated(savedInstanceState);
        //设置背景为透明
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            //设置弹窗大小
            window.setLayout(getWidth(), getHeight());
            //去除阴影
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.dimAmount = getDimAmount();
            layoutParams.gravity = getGravity();
            layoutParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(layoutParams);

            if (clearDimBehind()) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        }
    }

    @Override
    public void onDestroyView() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(this);
        }
    }

    /**
     * 清除dimBehind后，dialog显示时状态栏的颜色不会发生改变
     * （ps：默认dialog显示时底层被半透明遮盖住的，所以通知栏会自动变为白色）
     *
     * @return
     */
    public boolean clearDimBehind() {
        return false;
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

    public BaseDialogFragment setCancelable2(boolean cancelable) {
        super.setCancelable(cancelable);
        return this;
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

    public interface OnShowListener {
        void onShowListener(BaseDialogFragment dialogFragment, Dialog dialog);
    }

    public BaseDialogFragment setOnShowListener(BaseDialogFragment.OnShowListener listener) {
        this.mOnShowListener = listener;
        return this;
    }

    public void dismissDialog() {
        if (getDialog() != null && getDialog().isShowing()) {
            getDialog().dismiss();
        }
    }

    public interface OnDismissListener {
        void onDismiss(BaseDialogFragment dialogFragment);
    }

    public BaseDialogFragment setOnDismissListener(OnDismissListener listener) {
        this.mOnDismissListener = listener;
        return this;
    }

    @Override
    @NonNull
    @CheckResult
    public final Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(lifecycleSubject);
    }

    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
    }

    @Override
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(FragmentEvent.START);
    }

    @Override
    public void onResume() {
        super.onResume();
        lifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override
    public void onPause() {
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();
    }

    @Override
    public void onStop() {
        lifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        lifecycleSubject.onNext(FragmentEvent.DETACH);
        super.onDetach();
    }
}
