package com.duxl.baselib.widget;

import static com.duxl.baselib.utils.DisplayUtil.getMeasureSize;

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

    /**
     * 测量高宽，并计算总的行数和每行child个数
     *
     * @param widthMeasureSpec  horizontal space requirements as imposed by the parent.
     *                          The requirements are encoded with
     *                          {@link android.view.View.MeasureSpec}.
     * @param heightMeasureSpec vertical space requirements as imposed by the parent.
     *                          The requirements are encoded with
     *                          {@link android.view.View.MeasureSpec}.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mLineChildCount.clear();

        int childCount = getChildCount();
        int width = getMeasureSize(widthMeasureSpec, 0);
        int height = 0;
        if (childCount > 0) {
            int currentLine = 0; // 当前行
            int currentLineMaxHeight = 0; // 当前行的maxHeight
            int currentLineTotalWidth = 0; // 当前行的总child宽度
            for (int i = 0; i < childCount; i++) {
                if (i == 0) {
                    currentLineTotalWidth = mFirstLineMarginLeft + mFirstLineMarginRight;
                    if (EmptyUtils.isNotNull(mLeftView)) {
                        currentLineMaxHeight = Math.max(currentLineMaxHeight, mLeftView.getMeasuredHeight());
                    }
                    if (EmptyUtils.isNotNull(mRightView)) {
                        currentLineMaxHeight = Math.max(currentLineMaxHeight, mRightView.getMeasuredHeight());
                    }
                }

                View childView = getChildAt(i);
                // 测量子view
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);

                int childWidth = childView.getMeasuredWidth();
                int childHeight = childView.getMeasuredHeight();
                if (mLineChildCount.size() == currentLine) {
                    currentLineTotalWidth += childWidth;
                } else {
                    currentLineTotalWidth += mItemHorizontalSpace + childWidth;
                }

                currentLineMaxHeight = Math.max(currentLineMaxHeight, childHeight);

                if (currentLineTotalWidth > width) {
                    currentLineTotalWidth = childWidth;
                    if (currentLine == 0) {
                        height = currentLineMaxHeight;
                    } else {
                        height += currentLineMaxHeight + mItemVerticalSpace;
                    }

                    currentLineMaxHeight = childHeight;
                    currentLine++;
                    // 新增一行，并初始化有1个child(第一个child的长度就已经超过一行的情况)
                    mLineChildCount.add(1);
                } else {
                    // 累加当前行child的个数
                    if (mLineChildCount.size() == currentLine) {
                        mLineChildCount.add(1);
                    } else {
                        mLineChildCount.set(currentLine, mLineChildCount.get(currentLine) + 1);
                    }
                }

                // 累加当前行child的个数
                /*if (mLineChildCount.size() == currentLine) {
                    mLineChildCount.add(1);
                } else {
                    mLineChildCount.set(currentLine, mLineChildCount.get(currentLine) + 1);
                }*/
            }

            if (currentLineMaxHeight > 0) {
                if (currentLine == 0) {
                    height = currentLineMaxHeight;
                } else {
                    height += currentLineMaxHeight + mItemVerticalSpace;
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
        for (int currentLine = 0; currentLine < mLineChildCount.size(); currentLine++) {
            int currentLineMaxHeight = 0; // 当前行的最大高度
            if (currentLine == 0) {
                if (EmptyUtils.isNotNull(mLeftView)) {
                    currentLineMaxHeight = Math.max(currentLineMaxHeight, mLeftView.getMeasuredHeight());
                }
                if (EmptyUtils.isNotNull(mRightView)) {
                    currentLineMaxHeight = Math.max(currentLineMaxHeight, mRightView.getMeasuredHeight());
                }
            }

            // 计算当前行的最大高度view
            int tempLineChildIndex = 0;
            for (int lineChildIndex = 0; lineChildIndex < mLineChildCount.get(currentLine); lineChildIndex++) {
                View childView = getChildAt(childIndex + tempLineChildIndex);
                currentLineMaxHeight = Math.max(currentLineMaxHeight, childView.getMeasuredHeight());
                tempLineChildIndex++;
            }
            // 当前行的最大右边位置
            int maxRight = getMeasuredWidth();
            if (currentLine == 0) {
                maxRight -= mFirstLineMarginRight;
            }
            // 摆放当前行view
            int beforeChildX = 0;
            for (int lineChildIndex = 0; lineChildIndex < mLineChildCount.get(currentLine); lineChildIndex++) {
                View childView = getChildAt(childIndex);
                if (lineChildIndex == 0) {
                    if (currentLine == 0) {
                        left = mFirstLineMarginLeft;
                    } else {
                        left = 0;
                    }
                } else {
                    left = mItemHorizontalSpace + beforeChildX;
                }

                top = beforeLineY + (currentLineMaxHeight - childView.getMeasuredHeight()) / 2;
                if (currentLine > 0) {
                    top += mItemVerticalSpace;
                }

                right = left + childView.getMeasuredWidth();
                bottom = top + childView.getMeasuredHeight();
                right = Math.min(right, maxRight);
                childView.layout(left, top, right, bottom);
                //System.out.println("childView.layout."+childIndex+"("+left+", "+top+", "+right+", "+bottom+")");
                beforeChildX = right;
                childIndex++;
            }
            beforeLineY += currentLineMaxHeight;
            if (currentLine > 0) {
                beforeLineY += mItemVerticalSpace;
            }
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
