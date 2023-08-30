package com.duxl.baselib.widget.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 时间线装饰器
 * create by duxl 2023/8/30
 */
public class TimelineDecoration extends RecyclerView.ItemDecoration {

    private int mOutLeft; // 左边留空的间距（不包涵recycler自己item的margin和padding值）
    private int mRadius; // 圆点半径
    private int mCenterX; // 圆点中心x坐标
    private int mCenterY; // 圆点中心y坐标，相对于每个item而言
    private boolean mShowLine = true; // 是否显示竖线
    private Paint mDotPaint;
    private Paint mLinePaint;
    private int mExcludeStartCount; // 排除前面N个item不绘制圆点
    private int mExcludeEndCount; // 排除最后N个item不绘制圆点

    /**
     * @param context
     * @param outLeft 需要列表左边留空的间距
     * @param radius  圆点半径
     * @param centerX 圆点中心x坐标
     */
    public TimelineDecoration(Context context, int outLeft, int radius, int centerX) {
        this(context, outLeft, radius, centerX, -1);
    }

    /**
     * @param context
     * @param outLeft 需要列表左边留空的间距
     * @param radius  圆点半径
     * @param centerX 圆点中心x坐标
     * @param centerY 原点中心y坐标，相对于每个item而言，-1表示居中
     */
    public TimelineDecoration(Context context, int outLeft, int radius, int centerX, int centerY) {
        mOutLeft = outLeft;
        mRadius = radius;
        mCenterX = centerX;
        mCenterY = centerY;

        mDotPaint = new Paint();
        initDotPaint(mDotPaint);

        mLinePaint = new Paint();
        initLinePaint(mLinePaint);
    }

    /**
     * 设置会否显示竖线，默认true
     *
     * @param show
     */
    public void setShowLine(boolean show) {
        this.mShowLine = show;
    }

    /**
     * 排除前面N个item不会只圆点和线条
     *
     * @param count
     */
    public void setExcludeStartCount(int count) {
        this.mExcludeStartCount = count;
    }

    /**
     * 排除最后N个item不会只圆点和线条
     *
     * @param count
     */
    public void setExcludeEndCount(int count) {
        this.mExcludeEndCount = count;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left = mOutLeft;
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int childCount = parent.getChildCount();
        if (childCount == 0) {
            return;
        }

        // 画竖线
        if (mShowLine) {
            int startY = Integer.MIN_VALUE;
            int endY = Integer.MIN_VALUE;
            for (int i = 0; i < childCount; i++) {
                View childView = parent.getChildAt(i);
                int childAdapterPosition = parent.getChildAdapterPosition(childView);

                if (childAdapterPosition + 1 > mExcludeStartCount && startY == Integer.MIN_VALUE) {
                    startY = childView.getTop();
                    if (childAdapterPosition == mExcludeStartCount) {
                        if (mCenterY < 0) {
                            startY += childView.getHeight() / 2;
                        } else {
                            startY += mCenterY;
                        }
                    }
                    continue;
                }

                if (childAdapterPosition > parent.getAdapter().getItemCount() - mExcludeEndCount - 1) {
                    break;
                }

                if (startY != Integer.MIN_VALUE) {
                    endY = childView.getBottom();
                    if (parent.getChildAdapterPosition(childView) == parent.getAdapter().getItemCount() - mExcludeEndCount - 1) {
                        if (mCenterY < 0) {
                            endY -= childView.getHeight() / 2;
                        } else {
                            endY = childView.getTop() + mCenterY;
                        }
                    }
                }

            }
            if (startY != Integer.MIN_VALUE && endY != Integer.MIN_VALUE) {
                c.drawLine(mCenterX, startY, mCenterX, endY, mLinePaint);
            }
        }

        // 画圆点
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            int childAdapterPosition = parent.getChildAdapterPosition(childView);
            if (childAdapterPosition < mExcludeStartCount) {
                continue;
            }
            if (childAdapterPosition > parent.getAdapter().getItemCount() - mExcludeEndCount - 1) {
                continue;
            }

            int centerY;
            if (mCenterY >= 0) {
                centerY = childView.getTop() + mCenterY;
            } else {
                centerY = childView.getTop() + childView.getHeight() / 2;
            }

            // 画圆点
            Drawable dot = getDot(childAdapterPosition);
            Rect rect = new Rect();
            rect.left = mCenterX - dot.getIntrinsicWidth() / 2;
            rect.top = centerY - dot.getIntrinsicHeight() / 2;
            rect.right = mCenterX + dot.getIntrinsicWidth() / 2;
            rect.bottom = centerY + dot.getIntrinsicHeight() / 2;
            dot.setBounds(rect);
            dot.draw(c);
        }
    }

    /**
     * 重写该方法可对圆点画笔初始化设置
     *
     * @param paint
     */
    protected void initDotPaint(Paint paint) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
    }

    /**
     * 重写该方法可对线条画笔初始化设置
     *
     * @param paint
     */
    protected void initLinePaint(Paint paint) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
    }

    /**
     * 获取圆点，重写此方法可自定义圆点样式
     *
     * @param position
     * @return
     */
    public Drawable getDot(int position) {
        GradientDrawable dot = new GradientDrawable();
        dot.setColor(mDotPaint.getColor());
        dot.setCornerRadius(mRadius);
        dot.setSize(mRadius * 2, mRadius * 2);
        return dot;
    }

    /**
     * 设置圆点画笔颜色
     *
     * @param color
     */
    public void setDotColor(@ColorInt int color) {
        mDotPaint.setColor(color);
    }

    /**
     * 设置线条画笔颜色
     *
     * @param color
     */
    public void setLineColor(@ColorInt int color) {
        mLinePaint.setColor(color);
    }

}
