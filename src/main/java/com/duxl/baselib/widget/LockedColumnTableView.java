package com.duxl.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duxl.baselib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 横向和纵向都可滑动，并可锁定列不可横向滑动
 * 参考下面链接并做了代码封装：
 * https://blog.csdn.net/mojo001/article/details/126014355
 */
public class LockedColumnTableView extends LinearLayout {

    protected View mLockedHeaderView;
    protected View mHeaderView;
    protected int mLockedDataLayoutId;
    protected int mDataLayoutId;
    protected RecyclerView mLockedDataRecyclerView;
    protected RecyclerView mDataRecyclerView;

    public LockedColumnTableView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LockedColumnTableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LockedColumnTableView);
        if (typedArray != null) {
            int lockedHeaderLayout = typedArray.getResourceId(R.styleable.LockedColumnTableView_locked_header_layout, View.NO_ID);
            if (lockedHeaderLayout != View.NO_ID) {
                mLockedHeaderView = LayoutInflater.from(context).inflate(lockedHeaderLayout, null);
            }

            int headerLayout = typedArray.getResourceId(R.styleable.LockedColumnTableView_header_layout, View.NO_ID);
            if (headerLayout != View.NO_ID) {
                mHeaderView = LayoutInflater.from(context).inflate(headerLayout, null);
            }
            mLockedDataLayoutId = typedArray.getResourceId(R.styleable.LockedColumnTableView_locked_data_layout, View.NO_ID);
            mDataLayoutId = typedArray.getResourceId(R.styleable.LockedColumnTableView_data_layout, View.NO_ID);
            typedArray.recycle();
        }
        init();
    }

    protected void init() {
        setOrientation(LinearLayout.HORIZONTAL);
        addView(createLockView());
        addView(createDataView());
        if (mLockedDataRecyclerView != null && mDataRecyclerView != null) {
            mLockedDataRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                        mDataRecyclerView.scrollBy(dx, dy);
                    }
                }
            });
            mDataRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                        mLockedDataRecyclerView.scrollBy(dx, dy);
                    }
                }
            });
        }
    }

    protected View createLockView() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(VERTICAL);
        if (mLockedHeaderView != null) {
            layout.addView(mLockedHeaderView);
        }
        if (mLockedDataLayoutId != View.NO_ID) {
            mLockedDataRecyclerView = new RecyclerView(getContext());
            mLockedDataRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            layout.addView(mLockedDataRecyclerView);
        }

        return layout;
    }

    protected View createDataView() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(VERTICAL);
        layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        if (mHeaderView != null) {
            layout.addView(mHeaderView);
        }
        if (mDataLayoutId != View.NO_ID) {
            mDataRecyclerView = new RecyclerView(getContext());
            mDataRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            layout.addView(mDataRecyclerView);
        }

        HorizontalScrollView scrollView = new HorizontalScrollView(getContext());
        scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        scrollView.addView(layout);
        scrollView.setFillViewport(true);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);//取消滑到顶端的阴影
        scrollView.setHorizontalScrollBarEnabled(false);
        return scrollView;
    }

    public View getLockedHeaderView() {
        return mLockedHeaderView;
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public RecyclerView getLockedDataRecyclerView() {
        return mLockedDataRecyclerView;
    }

    public RecyclerView getDataRecyclerView() {
        return mDataRecyclerView;
    }

    public void setAdapter(Adapter adapter) {
        if (adapter != null) {
            if (mLockedDataRecyclerView != null) {
                BaseRealAdapter realAdapter = new BaseRealAdapter(adapter, mLockedDataLayoutId, 0);
                adapter.addRealAdapter(realAdapter);
                mLockedDataRecyclerView.setAdapter(realAdapter);
            }
            if (mDataRecyclerView != null) {
                BaseRealAdapter realAdapter = new BaseRealAdapter(adapter, mDataLayoutId, 1);
                adapter.addRealAdapter(realAdapter);
                mDataRecyclerView.setAdapter(realAdapter);
            }

        }
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder {
        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * 数据适配器，这里自定义适配器，以便外层使用时只需要一个适配器就可以是同时对lock和unlock两个recyclerview进行设置
     */
    public static abstract class Adapter {
        private List<BaseRealAdapter> mBaseRealAdapters = new ArrayList<>();

        private void addRealAdapter(BaseRealAdapter adapter) {
            this.mBaseRealAdapters.add(adapter);
        }

        public abstract int getItemCount();

        /**
         * 绑定锁定的列
         *
         * @param holder
         * @param position
         */
        public abstract void onBindLockedViewHolder(@NonNull RecyclerView.ViewHolder holder, int position);

        /**
         * 绑定锁定的列
         *
         * @param holder
         * @param position
         * @param payloads
         */
        public void onBindLockedViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
            onBindLockedViewHolder(holder, position);
        }

        /**
         * 绑定非锁定的列
         *
         * @param holder
         * @param position
         */
        public abstract void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position);

        /**
         * 绑定非锁定的列
         *
         * @param holder
         * @param position
         * @param payloads
         */
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
            onBindViewHolder(holder, position);
        }

        public final void notifyDataSetChanged() {
            mBaseRealAdapters.stream().forEach(it -> it.notifyDataSetChanged());
        }

        public final void notifyItemChanged(int position) {
            mBaseRealAdapters.stream().forEach(it -> it.notifyItemChanged(position));
        }

        public final void notifyItemChanged(int position, @Nullable Object payload) {
            mBaseRealAdapters.stream().forEach(it -> it.notifyItemChanged(position, payload));
        }

        public final void notifyItemRangeChanged(int positionStart, int itemCount) {
            mBaseRealAdapters.stream().forEach(it -> it.notifyItemRangeChanged(positionStart, itemCount));
        }

        public final void notifyItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            mBaseRealAdapters.stream().forEach(it -> it.notifyItemRangeChanged(positionStart, itemCount, payload));
        }

        public final void notifyItemInserted(int position) {
            mBaseRealAdapters.stream().forEach(it -> it.notifyItemRangeInserted(position, 1));
        }

        public final void notifyItemRangeInserted(int positionStart, int itemCount) {
            mBaseRealAdapters.stream().forEach(it -> it.notifyItemRangeInserted(positionStart, itemCount));
        }

        public final void notifyItemMoved(int fromPosition, int toPosition) {
            mBaseRealAdapters.stream().forEach(it -> it.notifyItemMoved(fromPosition, toPosition));
        }

        public final void notifyItemRemoved(int position) {
            mBaseRealAdapters.stream().forEach(it -> it.notifyItemRemoved(position));
        }

        public final void notifyItemRangeRemoved(int positionStart, int itemCount) {
            mBaseRealAdapters.stream().forEach(it -> notifyItemRangeRemoved(positionStart, itemCount));
        }
    }

    /**
     * 真正的数据适配器
     */
    public static class BaseRealAdapter extends RecyclerView.Adapter {

        private Adapter mAdapter;
        private int mType; // 0：locked、1：unlocked
        private int mLayoutId;


        /**
         * @param adapter  外层使用的数据适配器
         * @param layoutId 布局文件，锁定列或非锁定列
         * @param type     类型：0锁定、1非锁定
         */
        public BaseRealAdapter(Adapter adapter, int layoutId, int type) {
            this.mAdapter = adapter;
            this.mLayoutId = layoutId;
            this.mType = type;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
            return new BaseViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (mType == 0) {
                mAdapter.onBindLockedViewHolder(holder, position);
            } else {
                mAdapter.onBindViewHolder(holder, position);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
            if (mType == 0) {
                mAdapter.onBindLockedViewHolder(holder, position, payloads);
            } else {
                mAdapter.onBindViewHolder(holder, position, payloads);
            }
        }

        @Override
        public int getItemCount() {
            return mAdapter.getItemCount();
        }
    }
}
