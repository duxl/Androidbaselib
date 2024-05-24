package com.duxl.baselib.widget;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * 自定义滚动动画时长的Scroller
 */
public class CustomDurationScroller extends Scroller {

    private int fixDuration = 500;

    public CustomDurationScroller(Context context) {
        super(context);
    }

    public CustomDurationScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public CustomDurationScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    /**
     * 更改动画时长
     *
     * @param fixDuration 动画时长
     */
    public void setFixDuration(int fixDuration) {
        this.fixDuration = fixDuration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, fixDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, fixDuration);
    }
}
