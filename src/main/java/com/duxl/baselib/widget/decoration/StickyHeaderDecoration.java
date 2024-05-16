package com.duxl.baselib.widget.decoration;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duxl.baselib.utils.DisplayUtil;

/**
 * 黏性header，实现类似QQ分组的滑动悬停效果
 */
public abstract class StickyHeaderDecoration extends RecyclerView.ItemDecoration {

    private RecyclerView mRecyclerView;
    private int mHeaderHeight;
    private int mHeaderWidth;

    public StickyHeaderDecoration(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                // 禁止Header区域下面的Item被点击
                return e.getY() < mHeaderHeight;
            }
        });
    }

    /**
     * 获取黏性头View，列表中的每一项都应该提供该view
     *
     * @param position adapter中的position位置
     * @return
     */
    public abstract View getHeaderView(int position);

    /**
     * 指定的position是否是header
     *
     * @param position
     * @return
     */
    public abstract boolean isHeader(int position);

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (parent.getChildCount() == 0) {
            return;
        }
        View firstView = parent.getChildAt(0);
        int position = parent.getChildAdapterPosition(firstView);

        View headerView = getHeaderView(position);
        /**headerView.setPressed();
         headerView.setEnabled();*/
        if (isHeader(position)) {
            mHeaderHeight = firstView.getHeight();
            mHeaderWidth = firstView.getWidth();
        }

        Bitmap bitmap = getTitleBitmap(headerView);
        int top = 0;
        if (isHeader(position)) { // 列表中第一个view是头的时候，直接显示header
            top = firstView.getTop();
            if (!isHeader(position + 1)) {
                top = 0;
            }
        } else { // 列表中第一个item不是header，
            if (isHeader(position + 1)) {
                if (firstView.getTop() < 0 && firstView.getHeight() + firstView.getTop() < mHeaderHeight) {
                    top = firstView.getHeight() + firstView.getTop() - mHeaderHeight;
                }
            }
        }
        c.drawBitmap(bitmap, firstView.getLeft(), top, null);
        bitmap.recycle();
    }

    private Bitmap getTitleBitmap(View v) {
        v.setMinimumWidth(mHeaderWidth);
        v.setMinimumHeight(mHeaderHeight);
        return DisplayUtil.cutView(v);
    }
}