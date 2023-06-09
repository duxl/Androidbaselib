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
    private RecyclerView mRecyclerView;
    private CenterSmoothScroller mSmoothScroller;

    public CenterLayoutManager(RecyclerView recyclerView, int orientation, boolean reverseLayout) {
        super(recyclerView.getContext(), orientation, reverseLayout);
        mRecyclerView = recyclerView;
        mSmoothScroller = new CenterSmoothScroller(mRecyclerView.getContext(), 100, 0);
        mSmoothScroller.setTargetPosition(0);
    }

    public void smoothScrollToPositionCenter(int position, float speedPer) {
        mSmoothScroller.deltaPosition = mSmoothScroller.getTargetPosition() - position;
        mSmoothScroller.speedPer = speedPer;
        mSmoothScroller.setTargetPosition(position);
        startSmoothScroll(mSmoothScroller);
    }

    public void smoothScrollToPositionCenter(int position) {
        smoothScrollToPositionCenter(position, 100);
    }

    public static class CenterSmoothScroller extends LinearSmoothScroller {

        private float speedPer;
        private int deltaPosition;

        public CenterSmoothScroller(Context context, long speedPer, int deltaPosition) {
            super(context);
            this.speedPer = speedPer;
            this.deltaPosition = deltaPosition;
        }

        @Override
        public void setTargetPosition(int targetPosition) {
            super.setTargetPosition(targetPosition);
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
            return speedPer / (Math.abs(deltaPosition)) / displayMetrics.densityDpi;
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            return super.calculateTimeForScrolling(dx);
        }
    }
}
