package com.duxl.baselib.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

    private OnShowListener mOnShowListener;
    private OnDismissListener mOnDismissListener;

    @LayoutRes
    public abstract int getResId();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
    }

    public Bundle getSafeArguments() {
        Bundle args = super.getArguments();
        if (args != null) {
            return args;
        }
        args = new Bundle();
        setArguments(args);

        return args;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = createView(inflater, container);
        setSlideEnabled(getSafeArguments().getBoolean("slideEnabled", true));
        if (getSafeArguments().containsKey("navigationBarColor")) {
            setNavigationBarColor(getSafeArguments().getInt("navigationBarColor"));
        }

        if (getSafeArguments().containsKey("navigationContrastEnforced")) {
            setNavigationBarContrastEnforced(getSafeArguments().getBoolean("navigationContrastEnforced"));
        }

        if (getSafeArguments().containsKey("windowBackgroundColor")) {
            setWindowBackgroundColor(getSafeArguments().getInt("windowBackgroundColor"));
        }

        if (getSafeArguments().containsKey("dialogHeight")) {
            setDialogHeight(getSafeArguments().getInt("dialogHeight"));
        }

        if (getSafeArguments().containsKey("dimAmount")) {
            setDimAmount(getSafeArguments().getFloat("dimAmount"));
        }


        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(getCancelOutside());
            dialog.setOnShowListener(dialog2 -> {
                // 设置背景透明有助于圆角正常显示
                dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet).setBackgroundColor(Color.TRANSPARENT);

                if (mOnShowListener != null) {
                    mOnShowListener.onShowListener(BaseBottomSheetDialogFragment.this, dialog);
                }
            });

        }
        return view;
    }

    protected View createView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(getResId(), container, false);
    }

    /**
     * 设置Dialog的固定高度
     *
     * @param height
     */
    protected BaseBottomSheetDialogFragment setDialogHeight(int height) {
        try {
            getSafeArguments().putInt("dialogHeight", height);
            View view = getView();
            if (view != null) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
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
        } catch (Exception e) {
            Log.e("BottomSheetDialog", e.getMessage(), e);
        }

        return this;
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
    protected BottomSheetBehavior<?> getBehavior() {
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
            BottomSheetBehavior<?> behavior = getBehavior();
            if (behavior != null) {
                behavior.setHideable(enabled);
            }
        } catch (Exception e) {
            Log.e("BottomSheetDialog", e.getMessage(), e);
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
        BottomSheetBehavior<?> behavior = getBehavior();
        if (behavior != null) {
            behavior.setState(state);
        }
        return this;
    }

    /**
     * 设置是否开启NavigationBar的对比度增强<br/>
     * 当系统底部为三点导航时，设置为透明还是有一个颜色遮罩，如果要彻底取消遮罩，调用此方法将对比度设置为false<br/><br/>
     *
     * 注意：要使此方法生效，需要使用EdgeToEdge.enable()，也就是Activity内容扩展到系统栏，
     * 详见文档 https://developer.android.google.cn/design/ui/mobile/guides/foundations/system-bars?hl=zh-cn
     *
     * @param contrastEnforced 是否启用对比度强度
     * @return
     */
    public BaseBottomSheetDialogFragment setNavigationBarContrastEnforced(boolean contrastEnforced) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getSafeArguments().putBoolean("navigationContrastEnforced", contrastEnforced);
                Dialog dialog = getDialog();
                if (dialog != null && dialog.getWindow() != null) {
                    dialog.getWindow().setNavigationBarContrastEnforced(contrastEnforced);
                }
            }
        } catch (Exception e) {
            Log.e("BottomSheetDialog", e.getMessage(), e);
        }
        return this;
    }

    /**
     * 设置底部虚拟导航bar颜色，当系统底部为三点导航时，设置为透明还是有一个颜色遮罩，
     * 这时还需要设置setNavigationBarContrastEnforced(false)
     *
     * @param color 颜色值
     * @return
     */
    public BaseBottomSheetDialogFragment setNavigationBarColor(@ColorInt int color) {
        try {
            getSafeArguments().putInt("navigationBarColor", color);
            Dialog dialog = getDialog();
            if (dialog != null && dialog.getWindow() != null) {
                dialog.getWindow().setNavigationBarColor(color);
            }
        } catch (Exception e) {
            Log.e("BottomSheetDialog", e.getMessage(), e);
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
            if (dialog != null && dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(color));
            }
        } catch (Exception e) {
            Log.e("BottomSheetDialog", e.getMessage(), e);
        }
        return this;
    }

    /**
     * 设置dialog遮罩暗淡值
     *
     * @param amount 新的调光量，从0表示无调光到1表示完全调光。
     * @return
     */
    public BaseBottomSheetDialogFragment setDimAmount(float amount) {
        try {
            getSafeArguments().putFloat("dimAmount", amount);
            Dialog dialog = getDialog();
            if (dialog != null && dialog.getWindow() != null) {
                dialog.getWindow().setDimAmount(amount);
            }
        } catch (Exception e) {
            Log.e("BottomSheetDialog", e.getMessage(), e);
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
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(this);
        }
    }

    public interface OnShowListener {
        void onShowListener(BaseBottomSheetDialogFragment dialogFragment, Dialog dialog);
    }

    public BaseBottomSheetDialogFragment setOnShowListener(OnShowListener listener) {
        this.mOnShowListener = listener;
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
        super.onDestroyView();
    }
}
