package com.duxl.baselib.widget.drawview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.duxl.baselib.utils.EmptyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 绘图
 */
public class DrawView extends View {

    private Paint mPaint = new Paint();
    private Path mPath = new Path();
    private List<TrackPoint> mTrackPoints = new ArrayList<>();

    private float mLastX;
    private float mLastY;

    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(15);
    }

    /**
     * 获取绘图数据,可将此数据保存下来下次恢复绘图
     *
     * @return
     */
    public DrawData getDrawData() {
        DrawData drawData = new DrawData();
        drawData.canvasWidth = getMeasuredWidth();
        drawData.canvasHeight = getMeasuredHeight();
        drawData.paintWidth = mPaint.getStrokeWidth();
        drawData.paintColor = mPaint.getColor();
        drawData.trackPoints = mTrackPoints;
        return drawData;
    }

    /**
     * 恢复绘图数据
     *
     * @param drawData 之前的绘图数据，如果画布不相等无法还原之前的绘图，
     *                 需自己把数据处理（等比例缩放）后再调用该方法恢复
     *                 绘图
     * @return 返回是否恢复成功
     */
    public boolean restoreDrawData(DrawData drawData) {
        if (drawData != null) {
            if (drawData.canvasWidth != getMeasuredWidth() || drawData.canvasHeight != getMeasuredHeight()) {
                // 画布大小相等才能恢复绘图
                return false;
            }
            mPaint.setStrokeWidth(drawData.paintWidth);
            mPaint.setColor(drawData.paintColor);
            mTrackPoints.clear();
            if (EmptyUtils.isNotEmpty(drawData.trackPoints)) {
                mPath.reset();
                for (TrackPoint trackPoint : drawData.trackPoints) {
                    if(trackPoint.isStart) {
                        touchDown(trackPoint.point.x, trackPoint.point.y);
                    } else {
                        touchMove(trackPoint.point.x, trackPoint.point.y);
                    }
                }
            }

            invalidate();
            dispatchOnChanged();
            return true;
        }
        return false;
    }

    /**
     * 设置画笔大小
     *
     * @param width
     */
    public void setPaintWidth(float width) {
        mPaint.setStrokeWidth(width);
    }

    /**
     * 设置画笔颜色
     *
     * @param color
     */
    public void setPaintColor(int color) {
        mPaint.setColor(color);
    }

    /**
     * 重置
     */
    public void reset() {
        if (canRevoke()) {
            mPath.reset();
            mTrackPoints.clear();
            invalidate();
            dispatchOnChanged();
        }
    }

    /**
     * 是否可回退
     *
     * @return
     */
    public boolean canRevoke() {
        return EmptyUtils.isNotEmpty(mTrackPoints);
    }

    /**
     * 回退
     */
    public void revoke() {
        if (canRevoke()) {
            // 查找最后一个落笔点
            int i = mTrackPoints.size() - 1;
            for (; i >= 0; i--) {
                if (mTrackPoints.get(i).isStart) {
                    break;
                }
            }

            // 删除落笔点以及之后滑动点
            for (int j = mTrackPoints.size() - 1; j >= i; j--) {
                mTrackPoints.remove(j);
            }

            mPath.reset();
            for (TrackPoint historyPoint : mTrackPoints) {
                if (historyPoint.isStart) {
                    mPath.moveTo(historyPoint.point.x, historyPoint.point.y);
                } else {
                    float cX = (historyPoint.point.x + mLastX) / 2;
                    float cY = (historyPoint.point.y + mLastY) / 2;
                    mPath.quadTo(cX, cY, historyPoint.point.x, historyPoint.point.y);
                }
                mLastX = historyPoint.point.x;
                mLastY = historyPoint.point.y;
            }

            invalidate();
            dispatchOnChanged();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchDown(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            touchMove(event.getX(), event.getY());
        }
        invalidate();
        return true;
    }

    private void touchDown(float x, float y) {
        mLastX = x;
        mLastY = y;
        mPath.moveTo(mLastX, mLastY);
        mTrackPoints.add(new TrackPoint(new PointF(mLastX, mLastY), true));
        dispatchOnChanged();
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mLastX);
        float dy = Math.abs(y - mLastY);
        // 两点之间的距离大于等于5时，生成贝塞尔绘制曲线
        if (dx >= 5 || dy >= 5) {
            // 设置贝塞尔曲线的操作点为起点和终点的一半
            float cX = (x + mLastX) / 2;
            float cY = (y + mLastY) / 2;
            mPath.quadTo(cX, cY, x, y);
            // 第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
            mLastX = x;
            mLastY = y;
            mTrackPoints.add(new TrackPoint(new PointF(mLastX, mLastY), false));
            dispatchOnChanged();
        }
    }

    public interface OnChangedListener {
        void onChanged(DrawView view);
    }

    private OnChangedListener mOnChangedListener;

    public void setOnChangedListener(OnChangedListener listener) {
        this.mOnChangedListener = listener;
    }

    private void dispatchOnChanged() {
        if (mOnChangedListener != null) {
            mOnChangedListener.onChanged(this);
        }
    }
}
