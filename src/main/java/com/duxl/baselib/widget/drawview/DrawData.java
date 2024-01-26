package com.duxl.baselib.widget.drawview;

import java.io.Serializable;
import java.util.List;

public class DrawData implements Serializable {

    /**
     * 画布宽度
     */
    public int canvasWidth;

    /**
     * 画布高度
     */
    public int canvasHeight;

    /**
     * 画笔大小
     */
    public float paintWidth;

    /**
     * 画笔颜色
     */
    public int paintColor;

    /**
     * 绘制数据
     */
    public List<TrackPoint> trackPoints;
}
