package com.duxl.baselib.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.duxl.baselib.utils.DisplayUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.trello.rxlifecycle4.LifecycleProvider;
import com.trello.rxlifecycle4.LifecycleTransformer;
import com.trello.rxlifecycle4.RxLifecycle;
import com.trello.rxlifecycle4.android.FragmentEvent;
import com.trello.rxlifecycle4.android.RxLifecycleAndroid;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

/**
 * BottomSheetDialog
 */
public abstract class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment implements LifecycleProvider<FragmentEvent> {

    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();
    private static final String TAG = "BaseBottomSheetDialogFragment";

    private OnDismissListener mOnDismissListener;

    @LayoutRes
    public abstract int getResId();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, DialogFragment.STYLE_NO_FRAME);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
    }

    public Bundle getSafeArguments() {
        Bundle args = super.getArguments();
        if (args != null) {
            return args;
        }
        return new Bundle();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = createView(inflater, container);
        setSlideEnabled(getSafeArguments().getBoolean("slideEnabled", true));
        int navigationBarColor = getSafeArguments().getInt("navigationBarColor", -1);
        if (navigationBarColor != -1) {
            setNavigationBarColor(navigationBarColor);
        }
        setWindowBackgroundColor(getSafeArguments().getInt("windowBackgroundColor", Color.parseColor("#80000000")));
        getDialog().setCanceledOnTouchOutside(getCancelOutside());
        if (useHalfHeight()) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            int height = DisplayUtil.getScreenHeight(getContext()) * 2 / 3;
            if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        height
                );
            } else {
                layoutParams.height = height;
            }
            view.setLayoutParams(layoutParams);

        }
        return view;
    }

    protected View createView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(getResId(), container, false);
    }

    /**
     * 使用屏幕的一半高度
     * 弹框的高度不管内容多少，这里启用后都设置成屏幕额2/3高度
     *
     * @return
     */
    protected boolean useHalfHeight() {
        return true;
    }

    /**
     * <pre>
     * // 设置滑动后直接关闭，不需要折叠一半效果
     * behavior.setSkipCollapsed(true);
     *
     * // 设置完全展开
     * behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
     *
     * // 设置不可滑动关闭
     * behavior.setHideable(false);
     * </pre>
     *
     * @return
     */
    protected BottomSheetBehavior getBehavior() {
        Dialog baseDialog = getDialog();
        if (baseDialog instanceof BottomSheetDialog) {
            BottomSheetDialog dialog = (BottomSheetDialog) baseDialog;
            BottomSheetBehavior<?> behavior = dialog.getBehavior();
            return behavior;
        }
        return null;
    }

    /**
     * 设置是滑动关闭是否可用
     *
     * @param enabled 是否可滑动关闭，默认true
     * @return
     */
    public BaseBottomSheetDialogFragment setSlideEnabled(boolean enabled) {
        try {
            getSafeArguments().putBoolean("slideEnabled", enabled);
            BottomSheetBehavior behavior = getBehavior();
            if (behavior != null) {
                behavior.setHideable(enabled);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 设置bottom sheet状态（完全展开、半展开、收起）
     *
     * @param state One of {@link BottomSheetBehavior#STATE_COLLAPSED }, {@link BottomSheetBehavior#STATE_EXPANDED}, {@link BottomSheetBehavior#STATE_HIDDEN},
     *              or {@link BottomSheetBehavior#STATE_HALF_EXPANDED}.
     */
    public BaseBottomSheetDialogFragment setSheetState(int state) {
        getSafeArguments().putInt("sheetState", state);
        BottomSheetBehavior behavior = getBehavior();
        if (behavior != null) {
            behavior.setState(state);
        }
        return this;
    }

    /**
     * 设置底部虚拟导航bar颜色
     *
     * @param color
     * @return
     */
    public BaseBottomSheetDialogFragment setNavigationBarColor(@ColorInt int color) {
        try {
            getSafeArguments().putInt("navigationBarColor", color);
            Dialog dialog = getDialog();
            if (dialog != null) {
                dialog.getWindow().setNavigationBarColor(color);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 设置window背景色，默认半透明
     *
     * @param color
     * @return
     */
    public BaseBottomSheetDialogFragment setWindowBackgroundColor(@ColorInt int color) {
        try {
            getSafeArguments().putInt("windowBackgroundColor", color);
            Dialog dialog = getDialog();
            if (dialog != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public int show(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        return super.show(transaction, tag);
    }

    public boolean getCancelOutside() {
        return true;
    }

    public String getFragmentTag() {
        return TAG;
    }

    /*public int getWidth() {
        //return WindowManager.LayoutParams.WRAP_CONTENT;
        return DisplayUtil.getScreenWidth(requireContext()) - DisplayUtil.dip2px(requireContext(), 100);
    }

    public int getHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }*/

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
        void onDismiss(BaseBottomSheetDialogFragment dialogFragment);
    }

    public BaseBottomSheetDialogFragment setOnDismissListener(OnDismissListener listener) {
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

    @Override
    public void onDestroyView() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(this);
        }
        super.onDestroyView();
    }
}
