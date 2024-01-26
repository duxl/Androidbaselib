package com.duxl.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.duxl.baselib.R;
import com.google.android.material.appbar.AppBarLayout;

/**
 * 跟随布局
 * ps：参考AppBarLayout代码
 */
public class CoordinatorFollowLayout extends FrameLayout implements CoordinatorLayout.AttachedBehavior {

    public enum Gravity {
        Left, Top, Right, Bottom,
        AlignLeft, AlignTop, AlignRight, AlignBottom
    }

    private Gravity mGravity = Gravity.Bottom;

    public void setFlowGravity(Gravity gravity) {
        this.mGravity = gravity;
    }

    public Gravity getFlowGravity() {
        return mGravity;
    }

    private boolean mFlowCenter;

    public boolean isFlowCenter() {
        return mFlowCenter;
    }

    public void setFlowCenter(boolean center) {
        this.mFlowCenter = center;
    }

    private int mFlowAnchor;

    public int getFlowAnchor() {
        return mFlowAnchor;
    }

    public void setFlowAnchor(int anchor) {
        this.mFlowAnchor = anchor;
    }

    public CoordinatorFollowLayout(@NonNull Context context) {
        this(context, null);
    }

    public CoordinatorFollowLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoordinatorFollowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CoordinatorFollowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CoordinatorFollowLayout);
        if (typedArray != null) {
            int gravity = typedArray.getInt(R.styleable.CoordinatorFollowLayout_follow_gravity, -1);
            if (gravity != -1) {
                setFlowGravity(Gravity.values()[gravity]);
            }
            setFlowCenter(typedArray.getBoolean(R.styleable.CoordinatorFollowLayout_follow_center, false));
            setFlowAnchor(typedArray.getResourceId(R.styleable.CoordinatorFollowLayout_follow_anchor, View.NO_ID));
            typedArray.recycle();
        }
    }

    @NonNull
    @Override
    public CoordinatorLayout.Behavior getBehavior() {
        return new Behavior();
    }

    /**
     * 跟随Behavior实现
     * 给view设置FlowBehavior，并指定一个锚点
     * create by duxl 2023/8/27
     */
    public static class Behavior extends CoordinatorLayout.Behavior<CoordinatorFollowLayout> {

        public Behavior() {

        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull CoordinatorFollowLayout child, @NonNull View dependency) {
            if (dependency instanceof AppBarLayout) {
                return true;
            }
            return false;
        }

        @Override
        public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull CoordinatorFollowLayout child, @NonNull View dependency) {
            if (child.getFlowAnchor() == View.NO_ID) {
                return super.onDependentViewChanged(parent, child, dependency);
            }

            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) child.getLayoutParams();
            View anchorView = dependency.findViewById(child.getFlowAnchor());
            if (anchorView != null) {
                switch (child.getFlowGravity()) {
                    case Left:
                        child.setTranslationX(anchorView.getLeft() - child.getWidth() - marginLayoutParams.rightMargin);
                        break;
                    case Top:
                        child.setTranslationY(anchorView.getTop() - child.getHeight() - marginLayoutParams.bottomMargin);
                        break;
                    case Right:
                        child.setTranslationX(anchorView.getRight());
                        break;
                    case Bottom:
                        child.setTranslationY(anchorView.getBottom());
                    case AlignLeft:
                        child.setTranslationX(anchorView.getLeft());
                        break;
                    case AlignTop:
                        child.setTranslationY(anchorView.getTop());
                        break;
                    case AlignRight:
                        child.setTranslationX(anchorView.getRight() - child.getWidth() - marginLayoutParams.rightMargin);
                        break;
                    case AlignBottom:
                        child.setTranslationY(anchorView.getBottom() - child.getHeight() - marginLayoutParams.bottomMargin);
                        break;
                }

                if (child.isFlowCenter()) {
                    switch (child.getFlowGravity()) {
                        case Left:
                        case AlignLeft:
                        case Right:
                        case AlignRight:
                            child.setTranslationY(anchorView.getTop() + anchorView.getHeight() / 2 - child.getHeight() / 2);
                            break;
                        case Top:
                        case AlignTop:
                        case Bottom:
                        case AlignBottom:
                            child.setTranslationX(anchorView.getLeft() + anchorView.getWidth() / 2 - child.getWidth() / 2);
                    }
                }
            }

            return true;
        }
    }

}
