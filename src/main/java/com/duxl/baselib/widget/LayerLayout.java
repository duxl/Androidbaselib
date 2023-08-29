package com.duxl.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.duxl.baselib.R;

/**
 * 横向叠放view的Layout(常用显示多个图标或头像，并且有重叠的效果)
 */
public class LayerLayout extends ViewGroup {

    protected int mItemWidth; // 每个item的宽度
    protected int mItemHeight; // 每个item的高度
    protected int mItemOffset; // item与item之间的偏移量
    protected boolean mItemReverse; // 是否反转（决定完全显示是第一个还是最后一个）

    public LayerLayout(Context context) {
        this(context, null);
    }

    public LayerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LayerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LayerLayout);
        if (typedArray != null) {
            mItemWidth = typedArray.getDimensionPixelOffset(R.styleable.LayerLayout_layer_item_width, 0);
            mItemHeight = typedArray.getDimensionPixelOffset(R.styleable.LayerLayout_layer_item_height, 0);
            mItemOffset = typedArray.getDimensionPixelOffset(R.styleable.LayerLayout_layer_item_offset, 0);
            mItemReverse = typedArray.getBoolean(R.styleable.LayerLayout_layer_item_reverse, false);
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = mItemWidth;
        int height = mItemHeight;
        int childCount = getChildCount();
        for (int i = 1; i < childCount; i++) {
            width += mItemOffset;
        }
        width += getPaddingLeft() + getPaddingRight();
        height += getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int left = getPaddingLeft();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            childView.setTranslationZ(mItemReverse ? childCount - i : i);
            childView.layout(left, getPaddingTop(), left + mItemWidth, getPaddingTop() + mItemHeight);
            left += mItemOffset;
        }
    }

    /**
     * 获取item的高度
     *
     * @return
     */
    public int getItemWidth() {
        return mItemWidth;
    }

    /**
     * 设置item的高度
     *
     * @param width
     */
    public void setItemWidth(int width) {
        this.mItemWidth = width;
        invalidate();
    }

    /**
     * 获取item的宽度
     *
     * @return
     */
    public int getItemHeight() {
        return mItemHeight;
    }

    /**
     * 设置item的高度
     *
     * @param height
     */
    public void setItemHeight(int height) {
        this.mItemHeight = height;
        invalidate();
    }

    /**
     * 获取item与item之间偏移量
     *
     * @return
     */
    public int getItemOffset() {
        return mItemOffset;
    }

    /**
     * 设置item与item之间的偏移量
     *
     * @param offset
     */
    public void setItemOffset(int offset) {
        this.mItemOffset = offset;
        invalidate();
    }

    /**
     * 是否反转
     *
     * @return true表示第一个item在最顶上，即完全显示。false刚好相反
     */
    public boolean isItemReverse() {
        return mItemReverse;
    }

    /**
     * 设置是否反转
     *
     * @param reverse 是否反转：true表示第一个item在最顶上，false表示最后一个item在最顶上
     */
    public void setItemReverse(boolean reverse) {
        this.mItemReverse = reverse;
        requestLayout();
    }
}
