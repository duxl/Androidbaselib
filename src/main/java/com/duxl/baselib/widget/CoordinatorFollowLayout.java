package com.duxl.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.duxl.baselib.R;

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

    private int offsetX;

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    private int offsetY;

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
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
            setOffsetX(typedArray.getDimensionPixelOffset(R.styleable.CoordinatorFollowLayout_follow_offsetX, 0));
            setOffsetY(typedArray.getDimensionPixelOffset(R.styleable.CoordinatorFollowLayout_follow_offsetY, 0));
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
            return dependency.getId() == child.getFlowAnchor();
        }

        /**
         * 锚点发生变化会调用此方法
         */
        @Override
        public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull CoordinatorFollowLayout child, @NonNull View dependency) {
            if (child.getFlowAnchor() == View.NO_ID) {
                return super.onDependentViewChanged(parent, child, dependency);
            }

            PointF targetLocation = computerChildLocation(dependency, child);
            child.setX(targetLocation.x);
            child.setY(targetLocation.y);
            return true;
        }

        /**
         * 重写此方法，「预览看到的位置」和「预览点击位置」才一致。不重写此次方法预览时有问题，运行时正常
         */
        @Override
        public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull CoordinatorFollowLayout child, int layoutDirection) {
            View anchorView = parent.findViewById(child.getFlowAnchor());
            if (anchorView == null) {
                return super.onLayoutChild(parent, child, layoutDirection);
            }
            PointF targetLocation = computerChildLocation(anchorView, child);
            child.layout((int) targetLocation.x, (int) targetLocation.y, (int) targetLocation.x + child.getMeasuredWidth(), (int) targetLocation.y + child.getMeasuredHeight());

            return true;
        }

        /**
         * 根据锚点View的计算child的位置。<br/>
         * 几个方法的含义解释：<br/>
         * 1、getLeft()：返回相对于父容器的“布局位置”	在 layout(left, top, right, bottom) 中写死的 left<br/>
         * 2、getTranslationX()：返回平移偏移量，在原本left的基础上，视觉平移的像素，不变改getLeft的值<br/>
         * 3、getX()：返回 布局位置 + 平移偏移，即 getLeft() + getTranslationX()，也就是视觉看到的的位置<br/>
         *
         * @param dependency 锚点view
         * @param child      childView
         * @return 返回child需要显示的位置
         */
        protected PointF computerChildLocation(View dependency, CoordinatorFollowLayout child) {
            PointF result = new PointF();
            float anchorViewX = dependency.getX();
            float anchorViewY = dependency.getY();

            switch (child.getFlowGravity()) {
                case Left:
                    result.x = anchorViewX - child.getMeasuredWidth() + child.getOffsetX();
                    result.y = anchorViewY + child.getOffsetY();
                    break;
                case Top:
                    result.x = anchorViewX + child.getOffsetX();
                    result.y = anchorViewY - child.getMeasuredHeight() + child.getOffsetY();
                    break;
                case Right:
                    result.x = anchorViewX + dependency.getMeasuredWidth() + child.getOffsetX();
                    result.y = anchorViewY + child.getOffsetY();
                    break;
                case Bottom:
                    result.x = anchorViewX + child.getOffsetX();
                    result.y = anchorViewY + dependency.getMeasuredHeight() + child.getOffsetY();
                    break;
                case AlignLeft:
                    result.x = anchorViewX + child.offsetX;
                    result.y = anchorViewY + child.getOffsetY();
                    break;
                case AlignTop:
                    result.x = anchorViewX + child.getOffsetX();
                    result.y = anchorViewY + child.getOffsetY();
                    break;
                case AlignRight:
                    result.x = anchorViewX + dependency.getMeasuredWidth() - child.getMeasuredWidth() + child.getOffsetX();
                    result.y = anchorViewY + child.getOffsetY();
                    break;
                case AlignBottom:
                    result.x = anchorViewX + child.getOffsetX();
                    result.y = anchorViewY + dependency.getMeasuredHeight() - child.getMeasuredHeight() + child.getOffsetY();
                    break;
            }

            if (child.isFlowCenter()) {
                switch (child.getFlowGravity()) {
                    case Left:
                    case AlignLeft:
                    case Right:
                    case AlignRight:
                        result.y = anchorViewY + (dependency.getMeasuredHeight() - child.getMeasuredHeight()) / 2f + child.getOffsetY();
                        break;
                    case Top:
                    case AlignTop:
                    case Bottom:
                    case AlignBottom:
                        result.x = anchorViewX + (dependency.getMeasuredWidth() - child.getMeasuredWidth()) / 2f + child.getOffsetX();
                }
            }
            return result;
        }
    }

}
