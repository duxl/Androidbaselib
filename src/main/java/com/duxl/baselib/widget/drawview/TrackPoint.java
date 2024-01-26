package com.duxl.baselib.widget.drawview;

import android.graphics.PointF;

import java.io.Serializable;

/**
 * 绘制轨迹点
 */
public class TrackPoint implements Serializable {

    public PointF point;

    public boolean isStart;

    public TrackPoint(PointF point, boolean isStart) {
        this.point = point;
        this.isStart = isStart;
    }
}
