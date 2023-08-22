package com.duxl.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duxl.baselib.R;
import com.duxl.baselib.utils.EmptyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 第一行可设置margin属性的FlowLayout
 * create by duxl 2023/8/13
 */
public class FirstLineMarginFlowLayout extends ViewGroup {

    private int mFirstLineMarginLeft = 0;
    private int mFirstLineMarginRight = 0;
    private int mItemHorizontalSpace = 0;
    private int mItemVerticalSpace = 0;
    private View mLeftView;
    private View mRightView;

    // 记录每行child的个数
    private List<Integer> mLineChildCount = new ArrayList<>();

    public FirstLineMarginFlowLayout(Context context) {
        super(context);
    }

    public FirstLineMarginFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FirstLineMarginFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FirstLineMarginFlowLayout);
        mFirstLineMarginLeft = typedArray.getDimensionPixelOffset(R.styleable.FirstLineMarginFlowLayout_flm_margin_left, 0);
        mFirstLineMarginRight = typedArray.getDimensionPixelOffset(R.styleable.FirstLineMarginFlowLayout_flm_margin_right, 0);
        mItemHorizontalSpace = typedArray.getDimensionPixelOffset(R.styleable.FirstLineMarginFlowLayout_flm_item_horizontal_space, 0);
        mItemVerticalSpace = typedArray.getDimensionPixelOffset(R.styleable.FirstLineMarginFlowLayout_flm_item_vertical_space, 0);
        int leftViewLayout = typedArray.getResourceId(R.styleable.FirstLineMarginFlowLayout_flm_left_view, View.NO_ID);
        int rightViewLayout = typedArray.getResourceId(R.styleable.FirstLineMarginFlowLayout_flm_right_view, View.NO_ID);
        typedArray.recycle();

        if (leftViewLayout != View.NO_ID) {
            mLeftView = LayoutInflater.from(context).inflate(leftViewLayout, null);
            measureView(mLeftView);
            mFirstLineMarginLeft = mLeftView.getMeasuredWidth();
            addView(mLeftView);

        }
        if (rightViewLayout != View.NO_ID) {
            mRightView = LayoutInflater.from(context).inflate(rightViewLayout, null);
            measureView(mRightView);
            mFirstLineMarginRight = mRightView.getMeasuredWidth();
            addView(mRightView);
        }
    }

    private void measureView(View v) {
        int size = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        v.measure(size, size);
    }

    @Override
    public int getChildCount() {
        int childCount = super.getChildCount();
        if (mLeftView != null) {
            childCount--;
        }
        if (mRightView != null) {
            childCount--;
        }
        return childCount;
    }

    @Override
    public View getChildAt(int index) {
        if (mLeftView != null) {
            index++;
        }
        if (mRightView != null) {
            index++;
        }
        return super.getChildAt(index);
    }

    /**
     * 设置第一行左边的margin值，如果设置了LeftView，marginLeft将无效
     *
     * @param marginLeft
     */
    public void setFirstLineMarginLeft(int marginLeft) {
        this.mFirstLineMarginLeft = marginLeft;
        requestLayout();
    }

    /**
     * 设置第一行右边的margin值，如果设置了RightView，marginRight将无效
     *
     * @param marginRight
     */
    public void setFirstLineMarginRight(int marginRight) {
        this.mFirstLineMarginRight = marginRight;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mLineChildCount.clear();

        int childCount = getChildCount();
        int width = getMeasureSize(widthMeasureSpec, 0);
        int height = 0;
        if (childCount > 0) {
            int currentLine = 0; // 当前行
            int lineMaxHeight = 0; // 当前行的maxHeight
            int lineTotalWidth = 0; // 当前行的总child宽度
            for (int i = 0; i < childCount; i++) {
                if (i == 0) {
                    lineTotalWidth = mFirstLineMarginLeft + mFirstLineMarginRight;
                    if (EmptyUtils.isNotNull(mLeftView)) {
                        lineMaxHeight = Math.max(lineMaxHeight, mLeftView.getMeasuredHeight());
                    }
                    if (EmptyUtils.isNotNull(mRightView)) {
                        lineMaxHeight = Math.max(lineMaxHeight, mRightView.getMeasuredHeight());
                    }
                }

                View childView = getChildAt(i);
                // 测量子view
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);

                int childWidth = childView.getMeasuredWidth();
                int childHeight = childView.getMeasuredHeight();
                if (mLineChildCount.size() == currentLine) {
                    lineTotalWidth += childWidth;
                } else {
                    lineTotalWidth += mItemHorizontalSpace + childWidth;
                }

                lineMaxHeight = Math.max(lineMaxHeight, childHeight);

                if (lineTotalWidth > width) {
                    lineTotalWidth = childWidth;
                    if (currentLine == 0) {
                        height = lineMaxHeight;
                    } else {
                        height += mItemVerticalSpace + lineMaxHeight;
                    }

                    lineMaxHeight = childHeight;
                    currentLine++;
                }
                if (mLineChildCount.size() == currentLine) {
                    mLineChildCount.add(1);
                } else {
                    mLineChildCount.set(currentLine, mLineChildCount.get(currentLine) + 1);
                }
            }

            if (lineMaxHeight > 0) {
                if (currentLine == 0) {
                    height = lineMaxHeight;
                } else {
                    height += mItemVerticalSpace + lineMaxHeight;
                }
            }
        }

        height = getMeasureSize(heightMeasureSpec, height);
        if (mLeftView != null) {
            height = Math.max(height, mLeftView.getMeasuredHeight());
        }
        if (mRightView != null) {
            height = Math.max(height, mRightView.getMeasuredHeight());
        }
        setMeasuredDimension(width, height);
    }

    /**
     * 获取测量尺寸
     *
     * @param measureSpace 尺寸和模式
     * @param contentSize  内容的尺寸
     * @return
     */
    private int getMeasureSize(int measureSpace, int contentSize) {
        int mode = MeasureSpec.getMode(measureSpace);
        if (mode == MeasureSpec.EXACTLY) { // 确切的尺寸（match_parent或100dp）
            return MeasureSpec.getSize(measureSpace);
        } else if (mode == MeasureSpec.AT_MOST) { // 需要根据内容测量得到尺寸（wrap_content）
            return contentSize;
        }
        return 0;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mLeftView != null) {
            mLeftView.layout(0, 0, mLeftView.getMeasuredWidth(), mLeftView.getMeasuredHeight());
        }

        if (mRightView != null) {
            mRightView.layout(getMeasuredWidth() - mRightView.getMeasuredWidth(), 0, getMeasuredWidth(), mRightView.getMeasuredHeight());
        }

        int beforeLineY = 0;
        int childIndex = 0;
        int left, top, right, bottom; // 定义上下左右摆放的位置
        for (int line = 0; line < mLineChildCount.size(); line++) {
            int beforeLineMaxHeight = 0;
            if (line == 0) {
                if(EmptyUtils.isNotNull(mLeftView)) {
                    beforeLineMaxHeight = Math.max(beforeLineMaxHeight, mLeftView.getMeasuredHeight());
                }
                if(EmptyUtils.isNotNull(mRightView)) {
                    beforeLineMaxHeight = Math.max(beforeLineMaxHeight, mRightView.getMeasuredHeight());
                }
            }

            int tempChildIndex = 0;
            for (int lineChildIndex = 0; lineChildIndex < mLineChildCount.get(line); lineChildIndex++) {
                View childView = getChildAt(tempChildIndex);
                beforeLineMaxHeight = Math.max(beforeLineMaxHeight, childView.getMeasuredHeight() + mItemVerticalSpace);
                tempChildIndex++;
            }

            int beforeChildX = 0;
            for (int lineChildIndex = 0; lineChildIndex < mLineChildCount.get(line); lineChildIndex++) {
                View childView = getChildAt(childIndex);
                if (lineChildIndex == 0) {
                    if (line == 0) {
                        left = mFirstLineMarginLeft;
                    } else {
                        left = 0;
                    }
                } else {
                    left = mItemHorizontalSpace + beforeChildX;
                }

                top = beforeLineY + (beforeLineMaxHeight - childView.getMeasuredHeight()) / 2;
                right = left + childView.getMeasuredWidth();
                bottom = top + childView.getMeasuredHeight();
                childView.layout(left, top, right, bottom);

                beforeChildX = right;
                childIndex++;
            }
            beforeLineY += beforeLineMaxHeight;
        }
    }

    public void setOnLeftViewClickListener(OnClickListener listener) {
        if (mLeftView != null) {
            mLeftView.setOnClickListener(listener);
        }
    }

    public void setOnRightViewClickListener(OnClickListener listener) {
        if (mRightView != null) {
            mRightView.setOnClickListener(listener);
        }
    }

    /**
     * 移除所有的子view，LeftView和RightView不会被移除
     */
    public void removeAllItemViews() {
        int childCount = super.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View childView = super.getChildAt(i);
            if (childView != mLeftView && childView != mRightView) {
                super.removeView(childView);
            }
        }
    }
}
