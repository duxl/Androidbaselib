package com.duxl.baselib.widget;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * <pre>
 * 横向列表点击item滚动居中
 * 使用方式：
 * 1、recyclerview.layoutManager = centerLayoutManager
 * 2、centerLayoutManager.smoothScrollToPositionCenter(position)
 *
 * ps：滑动自动居中可使用 new LinearSnapHelper().attachToRecyclerView(recyclerView)
 * </pre>
 */
public class CenterLayoutManager extends LinearLayoutManager {
    private int lastPosition = 0;
    private RecyclerView mRecyclerView;

    public CenterLayoutManager(RecyclerView recyclerView, int orientation, boolean reverseLayout) {
        super(recyclerView.getContext(), orientation, reverseLayout);
        mRecyclerView = recyclerView;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
    }

    public void smoothScrollToPositionCenter(int position, long speedPer) {
        CenterSmoothScroller smoothScroller = new CenterSmoothScroller(mRecyclerView.getContext(), speedPer, lastPosition - position);
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
        this.lastPosition = position;
    }

    public void smoothScrollToPositionCenter(int position) {
        smoothScrollToPositionCenter(position, 100);
    }

    public static class CenterSmoothScroller extends LinearSmoothScroller {

        private long speedPer;
        private int deltaPosition;

        public CenterSmoothScroller(Context context, long speedPer, int deltaPosition) {
            super(context);
            this.speedPer = speedPer;
            this.deltaPosition = deltaPosition;
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            if (deltaPosition == 0) {
                return super.calculateSpeedPerPixel(displayMetrics);
            }
            float newDuration = speedPer / 1f / (Math.abs(deltaPosition));//重新计算相近两个位置的滚动间隔
            return newDuration / displayMetrics.densityDpi;
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            return super.calculateTimeForScrolling(dx);
        }
    }
}
