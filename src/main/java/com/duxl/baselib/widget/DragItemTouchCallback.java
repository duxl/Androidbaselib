package com.duxl.baselib.widget;

import android.graphics.Canvas;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView拖拽
 * create by duxl 2023/8/19
 */
public abstract class DragItemTouchCallback extends ItemTouchHelper.Callback {

    private final String TAG = "DragItemTouchCallback";
    protected MovementFlags mMovementFlags;
    protected boolean mIsLongPressDragEnabled = true;
    protected boolean mIsDraging = false; // 是否正在拖动

    public DragItemTouchCallback() {
        this(MovementFlags.ALL);
    }

    public DragItemTouchCallback(MovementFlags flags) {
        mMovementFlags = flags;
    }

    public enum MovementFlags {
        ALL("所有方向", ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT),
        H("横向", ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT),
        V("纵向", ItemTouchHelper.UP | ItemTouchHelper.DOWN),
        LEFT("向左", ItemTouchHelper.LEFT),
        RIGHT("向右", ItemTouchHelper.RIGHT),
        UP("向上", ItemTouchHelper.UP),
        DOWN("向下", ItemTouchHelper.DOWN);

        public final String desc;
        public final int flag;

        MovementFlags(String desc, int flag) {
            this.desc = desc;
            this.flag = flag;
        }
    }

    /**
     * 设置是否长按才进入拖动
     *
     * @param enabled
     */
    public void setLongPressDragEnabled(boolean enabled) {
        this.mIsLongPressDragEnabled = enabled;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(mMovementFlags.flag, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
        return onMove(source.getBindingAdapterPosition(), target.getBindingAdapterPosition());
    }

    @Override
    public boolean canDropOver(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder current, @NonNull RecyclerView.ViewHolder target) {
        return super.canDropOver(recyclerView, current, target);
    }

    /**
     * eg:
     * <pre>
     * // 交换两个item
     * Collections.swap(listData, fromPosition, toPosition)
     * adapter.notifyItemMoved(fromPosition, toPosition)
     * return true
     * </pre>
     *
     * @param fromPosition
     * @param toPosition
     * @return
     */
    public abstract boolean onMove(int fromPosition, int toPosition);

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    /**
     * 是否长按才进入拖动
     *
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return mIsLongPressDragEnabled;
    }

    /**
     * 设置是否支持滑动操作
     *
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        mIsDraging = false;
        onFinishDrag(viewHolder);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG && mIsLongPressDragEnabled) {
            if(!mIsDraging) {
                mIsDraging = true;
                onBeginDrag(viewHolder);
            }
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && !mIsLongPressDragEnabled) {
            if (!mIsDraging && (dX != 0 || dY != 0)) {
                mIsDraging = true;
                onBeginDrag(viewHolder);
            }
        }
    }

    /**
     * 开始拖拽
     *
     * @param viewHolder
     */
    public void onBeginDrag(@Nullable RecyclerView.ViewHolder viewHolder) {
        Log.i(TAG, "开始拖拽");
    }

    /**
     * 完成拖拽
     *
     * @param viewHolder
     */
    public void onFinishDrag(RecyclerView.ViewHolder viewHolder) {
        Log.i(TAG, "完成拖拽");
    }
}
