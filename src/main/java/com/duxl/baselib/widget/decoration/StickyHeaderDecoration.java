package com.duxl.baselib.widget.decoration;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duxl.baselib.utils.DisplayUtil;

/**
 * 黏性header，实现类似QQ分组的滑动悬停效果
 */
public abstract class StickyHeaderDecoration extends RecyclerView.ItemDecoration {

    private RecyclerView mRecyclerView;
    protected int mHeaderHeight;
    protected int mHeaderWidth;
    protected StickyMode mStickyMode = StickyMode.PUSH;
    private MotionEvent mMotionEvent;

    public StickyHeaderDecoration(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                // 禁止Header区域下面的Item被点击
                // todo 目前还不知道怎么把事件传递到悬停的组上面
                //System.out.println("StickyHeaderDecoration: onInterceptTouchEvent");
                return e.getY() < mHeaderHeight;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                super.onTouchEvent(rv, e);
                //mMotionEvent = MotionEvent.obtain(e);
                //rv.invalidate();
                //System.out.println("StickyHeaderDecoration: onTouchEvent");
            }
        });
    }

    public enum StickyMode {
        PUSH, COVER
    }

    public void setStickyMode(StickyMode mode) {
        this.mStickyMode = mode;
    }

    /**
     * 获取下一行第一个childView的position偏移数
     *
     * @param position recycler的child位置（可见位置）
     * @return 返回下一行第一个childView的位置
     */
    protected int getNextRowFirstChildOffset(int position) {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            // 必须在设置LayoutManager之后添加StickyHeaderDecoration
            throw new IllegalArgumentException("add StickyHeaderDecoration must after LayoutManager be set");
        }

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int spanCount = gridLayoutManager.getSpanCount();
            View currentView = mRecyclerView.getChildAt(position);
            for (int i = 1; i < spanCount + 1; i++) {
                View nextView = mRecyclerView.getChildAt(position + i);
                if (nextView != null && nextView.getTop() >= currentView.getBottom()) {
                    return i;
                }
            }
        }

        return 1;
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

    public boolean isDrawOver() {
        return true;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (!isDrawOver()) {
            onDrawHeader(c, parent, state);
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (isDrawOver()) {
            onDrawHeader(c, parent, state);
        }
    }

    public void onDrawHeader(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (parent.getChildCount() == 0) {
            return;
        }
        View firstView = parent.getChildAt(0);
        int position = parent.getChildAdapterPosition(firstView);

        if (isHeader(position)) {
            mHeaderHeight = firstView.getHeight();
            mHeaderWidth = firstView.getWidth();
        }


        int top = 0;
        if (isHeader(position)) { // 列表中第一个view是头的时候，直接显示header
            top = firstView.getTop();
            if (!isHeader(position + 1)) {
                top = 0;
            }
        } else if (mStickyMode == StickyMode.PUSH) {
            // 列表中第一个item不是header,判断下一行的第一个item是否是header
            if (isHeader(position + getNextRowFirstChildOffset(0))) {
                if (firstView.getTop() < 0 && firstView.getHeight() + firstView.getTop() < mHeaderHeight) {
                    top = firstView.getHeight() + firstView.getTop() - mHeaderHeight;
                }
            }
        }

        drawHeader(c, position, top);
    }

    View headerView;

    protected void drawHeader(Canvas canvas, int position, int top) {
        /*if (headerView == null) {
            headerView = getHeaderView(position);
        }
        if (mMotionEvent != null) {
            headerView.dispatchTouchEvent(mMotionEvent);
            //mMotionEvent.recycle();
            if (mMotionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                System.out.println("StickyHeaderDecoration: ACTION_DOWN");
            } else if (mMotionEvent.getAction() == MotionEvent.ACTION_UP) {
                System.out.println("StickyHeaderDecoration: ACTION_UP");
            } else if (mMotionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                System.out.println("StickyHeaderDecoration: ACTION_MOVE");
            }
        }*/

        headerView = getHeaderView(position);
        /**headerView.setPressed();
         headerView.setEnabled();*/

        //c.drawBitmap(bitmap, firstView.getLeft(), top, null);
        if (headerView != null) {
            Bitmap bitmap = getTitleBitmap(headerView);
            canvas.drawBitmap(bitmap, 0, top, null);
            bitmap.recycle();
        }
    }

    protected Bitmap getTitleBitmap(View v) {
        v.setMinimumWidth(mHeaderWidth);
        v.setMinimumHeight(mHeaderHeight);
        return DisplayUtil.cutView(v);
    }
}