package com.duxl.baselib.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.duxl.baselib.R;
import com.duxl.baselib.utils.AnimUtils;
import com.duxl.baselib.utils.EmptyUtils;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 可折叠分组的适配器
 *
 * @param <G> 分组数据类型
 * @param <C> 分组下的Child数据类型
 */
public abstract class BaseExpandableAdapter<G extends BaseExpandableAdapter.GroupItemEntity<C>, C> extends BaseQuickAdapter<G, BaseExpandableAdapter.ExpandViewHolder> {

    /**
     * 折叠状态改变
     */
    public static final int PAYLOAD_EXPAND_CHANGED = 3000;

    private static final class ChildChangePayload {
        private enum TYPE {
            DataSetChanged,

            ItemChanged,
            ItemRangeChanged,

            ItemInserted,
            ItemRangeInserted,

            ItemRemoved,
            ItemRangeRemoved,

            ItemMoved
        }

        TYPE type;
        int position = -1;
        int positionStart = -1;
        int itemCount = -1;
        int fromPosition = -1;
        int toPosition = -1;
        Object payload = null;

        public ChildChangePayload(TYPE type) {
            this.type = type;
        }
    }

    private int mGroupLayoutResId;
    private int mChildLayoutResId;

    // Child的item长按事件(ps:分组的item长按事件已有)
    private OnChildItemLongClickListener mOnChildItemLongClickListener;
    // Child的子view长按事件(ps:分组的item长按事件已有)
    private OnChildItemChildLongClickListener mOnChildItemChildLongClickListener;
    // Child的item点击事件(ps:分组的item点击事件已有)
    private OnChildItemClickListener mOnChildItemClickListener;
    // Child的子view点击事件(ps:分组的子view点击事件已有)
    private OnChildItemChildClickListener mOnChildItemChildClickListener;
    // 用于保存需要设置点击事件的Child的子view
    private LinkedHashSet<Integer> mChildItemChildClickViewIds = new LinkedHashSet<>();
    // 用于保存需要设置长按事件的Child的子view
    private LinkedHashSet<Integer> mChildItemChildLongClickViewIds = new LinkedHashSet<>();


    /**
     * @param groupLayoutResId 组布局
     * @param childLayoutResId 子布局
     */
    public BaseExpandableAdapter(@LayoutRes int groupLayoutResId, @LayoutRes int childLayoutResId) {
        super(R.layout.adapter_base_expandable_item);
        this.mGroupLayoutResId = groupLayoutResId;
        this.mChildLayoutResId = childLayoutResId;
    }

    /**
     * 设置Child的item长按事件
     *
     * @param listener
     */
    public void setOnChildItemLongClickListener(OnChildItemLongClickListener listener) {
        this.mOnChildItemLongClickListener = listener;
    }

    /**
     * 设置Child的子View长按事件
     *
     * @param listener
     */
    public void setOnChildItemChildLongClickListener(OnChildItemChildLongClickListener listener) {
        this.mOnChildItemChildLongClickListener = listener;
    }

    /**
     * 设置Child的item点击事件
     *
     * @param listener
     */
    public void setOnChildItemClickListener(OnChildItemClickListener listener) {
        this.mOnChildItemClickListener = listener;
    }

    /**
     * 设置Child的子view点击事件
     *
     * @param listener
     */
    public void setOnChildItemChildClickListener(OnChildItemChildClickListener listener) {
        this.mOnChildItemChildClickListener = listener;
    }

    /**
     * 设置子Child的哪些子view可点击
     *
     * @param ids
     */
    public void addChildItemChildClickViewIds(int... ids) {
        for (int id : ids) {
            mChildItemChildClickViewIds.add(id);
        }
    }

    /**
     * 设置子Child的哪些子view可长按
     *
     * @param ids
     */
    public void addChildItemChildLongClickViewIds(int... ids) {
        for (int id : ids) {
            mChildItemChildLongClickViewIds.add(id);
        }
    }

    @NonNull
    @Override
    protected ExpandViewHolder createBaseViewHolder(@NonNull View view) {
        ExpandViewHolder viewHolder = super.createBaseViewHolder(view);
        FrameLayout flGroupContainer = viewHolder.getViewOrNull(R.id.fl_group_container);
        if (flGroupContainer != null) {
            View vGroup = onCreateGroupView(flGroupContainer, mGroupLayoutResId);
            flGroupContainer.removeAllViews();
            flGroupContainer.addView(vGroup);
        }
        return viewHolder;
    }

    @Override
    protected void convert(@NonNull ExpandViewHolder holder, G dataGroup, @NonNull List<? extends Object> payloads) {
        super.convert(holder, dataGroup, payloads);
        int groupPosition = getItemPosition(dataGroup);

        if (EmptyUtils.isNotEmpty(payloads)) {
            Object payload = payloads.get(0);
            if (payload instanceof ChildChangePayload) {
                ChildChangePayload childPayload = (ChildChangePayload) payload;
                RecyclerView recyclerChildren = holder.findView(R.id.recyclerview_children);
                if (recyclerChildren != null) {
                    RecyclerView.Adapter childAdapter = recyclerChildren.getAdapter();
                    if (childAdapter != null) {
                        switch (childPayload.type) {
                            case DataSetChanged:
                                childAdapter.notifyDataSetChanged();
                                break;
                            case ItemChanged:
                                if (childPayload.payload != null) {
                                    childAdapter.notifyItemChanged(childPayload.position, childPayload.payload);
                                } else {
                                    childAdapter.notifyItemChanged(childPayload.position);
                                }
                                break;
                            case ItemRangeChanged:
                                if (childPayload.payload != null) {
                                    childAdapter.notifyItemRangeChanged(childPayload.positionStart, childPayload.itemCount, childPayload.payload);
                                } else {
                                    childAdapter.notifyItemRangeChanged(childPayload.positionStart, childPayload.itemCount);
                                }
                                break;
                            case ItemInserted:
                                childAdapter.notifyItemInserted(childPayload.position);
                                break;
                            case ItemRangeInserted:
                                childAdapter.notifyItemRangeInserted(childPayload.positionStart, childPayload.itemCount);
                                break;
                            case ItemRemoved:
                                childAdapter.notifyItemRemoved(childPayload.position);
                                break;
                            case ItemRangeRemoved:
                                childAdapter.notifyItemRangeRemoved(childPayload.positionStart, childPayload.itemCount);
                                break;
                            case ItemMoved:
                                childAdapter.notifyItemMoved(childPayload.fromPosition, childPayload.toPosition);
                                break;
                        }
                    }
                }
                return;
            } else if (payload instanceof Integer && Integer.parseInt(payload.toString()) == PAYLOAD_EXPAND_CHANGED) {
                // 处理组展开和折叠
                RecyclerView recyclerChildren = holder.findView(R.id.recyclerview_children);
                expandChildren(recyclerChildren, dataGroup);
                return;
            }
        }

        FrameLayout flGroup = holder.getView(R.id.fl_group_container);
        bindGroup(flGroup.getChildAt(0), dataGroup, groupPosition, payloads);
    }

    @Override
    protected void convert(@NonNull ExpandViewHolder holder, G dataGroup) {
        //AnimUtils.setLayoutAnimateChangesEnable((ViewGroup) holder.root, expandAnimEnabled);
        int groupPosition = getItemPosition(dataGroup);
        // 处理Group组
        FrameLayout flGroup = holder.getView(R.id.fl_group_container);
        bindGroup(flGroup.getChildAt(0), dataGroup, groupPosition);

        // 处理Children
        RecyclerView recyclerChildren = holder.findView(R.id.recyclerview_children);
        if (recyclerChildren != null) {
            onBindChildren(recyclerChildren, dataGroup, groupPosition);
        }
    }

    /**
     * 创建分组View，可重写此方法
     *
     * @param parent           分组view最终所在的容器
     * @param groupLayoutResId 构造函数传进来的组布局文件id，重写可使用其它的布局
     * @return
     */
    protected View onCreateGroupView(ViewGroup parent, int groupLayoutResId) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(groupLayoutResId, parent, false);
    }

    /**
     * 创建子View，可重写此方法
     *
     * @param parent           子view最终所在的容器
     * @param childLayoutResId 由构造函数传进来的子布局文件id
     * @return
     */
    protected View onCreateChildView(ViewGroup parent, int childLayoutResId) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(childLayoutResId, parent, false);
    }

    protected RecyclerView.LayoutManager getChildrenLayoutManger(RecyclerView childrenView, G dataGroup, int positionGroup) {
        return new LinearLayoutManager(childrenView.getContext());
    }

    protected void bindGroup(@NonNull View viewGroup, G dataGroup, int positionGroup, @NonNull List<? extends Object> payloads) {

    }

    /**
     * 绑定分组
     *
     * @param viewGroup
     * @param dataGroup
     * @param positionGroup
     */
    protected abstract void bindGroup(@NonNull View viewGroup, G dataGroup, int positionGroup);


    /**
     * 绑定分组下的子view，这里是针对该分组下的所有child，一个分组子调用一次，
     * 如果需要设置整个children的背景可以在这里处理
     *
     * @param recyclerChildren children组列表
     * @param dataGroup        组数据
     * @param positionGroup    分组所在位置
     */
    protected void onBindChildren(RecyclerView recyclerChildren, G dataGroup, int positionGroup) {
        AnimUtils.setRecyclerAnimEnable(recyclerChildren, false);
        recyclerChildren.setHasFixedSize(true);
        recyclerChildren.setNestedScrollingEnabled(false);
        recyclerChildren.setLayoutManager(getChildrenLayoutManger(recyclerChildren, dataGroup, positionGroup));
        BaseQuickAdapter<C, ExpandViewHolder> childrenAdapter = new BaseQuickAdapter<C, ExpandViewHolder>(mChildLayoutResId, dataGroup.getChildren()) {
            @Override
            protected void convert(@NonNull ExpandViewHolder viewHolder, C dataChild, @NonNull List<? extends Object> payloads) {
                bindChild(recyclerChildren, viewHolder.getRoot(), dataGroup, positionGroup, dataChild, getItemPosition(dataChild), payloads);
            }

            @Override
            protected void convert(@NonNull ExpandViewHolder viewHolder, C dataChild) {
                bindChild(recyclerChildren, viewHolder.getRoot(), dataGroup, positionGroup, dataChild, getItemPosition(dataChild));
            }

            @NonNull
            @Override
            protected ExpandViewHolder onCreateDefViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ExpandViewHolder(onCreateChildView(parent, mChildLayoutResId));
            }
        };
        recyclerChildren.setAdapter(childrenAdapter);

        // 添加哪些子view可点击
        for (Integer childChildId : mChildItemChildClickViewIds) {
            childrenAdapter.addChildClickViewIds(childChildId);
        }

        // 添加哪些子view可长按
        for (Integer childChildId : mChildItemChildLongClickViewIds) {
            childrenAdapter.addChildLongClickViewIds(childChildId);
        }

        // 设置item长按事件
        childrenAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            if (mOnChildItemLongClickListener != null) {
                return mOnChildItemLongClickListener.onChildItemLongClick(adapter, view, position, positionGroup);
            }
            return false;
        });

        // 设置item的子View长按事件
        childrenAdapter.setOnItemChildLongClickListener((adapter, view, position) -> {
            if (mOnChildItemChildLongClickListener != null) {
                return mOnChildItemChildLongClickListener.onChildItemLongClick(adapter, view, position, positionGroup);
            }
            return false;
        });

        // 设置item点击事件
        childrenAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (mOnChildItemClickListener != null) {
                mOnChildItemClickListener.onItemClick(adapter, view, position, positionGroup);
            }
        });

        // 设置item的子view点击事件
        childrenAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (mOnChildItemChildClickListener != null) {
                mOnChildItemChildClickListener.onItemChildClick(adapter, view, position, positionGroup);
            }
        });

        // 处理组展开和折叠
        expandChildren(recyclerChildren, dataGroup);
    }

    /**
     * 展开和折叠组
     *
     * @param dataGroup
     */
    protected void expandChildren(RecyclerView recyclerView, G dataGroup) {
        /*ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
        if (dataGroup.isExpand()) {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            layoutParams.height = 0;
        }
        recyclerView.setLayoutParams(layoutParams);*/
        if (dataGroup.isExpand()) {
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
    }

    protected void bindChild(@NonNull RecyclerView recyclerChildren, @NonNull View childView, G dataGroup, int positionGroup, C dataChild, int positionChild, @NonNull List<? extends Object> payloads) {

    }

    /**
     * 绑定分组下的子view，这里针对单个child
     *
     * @param recyclerChildren 分组下的子列表
     * @param childView        childView
     * @param dataGroup        组数据
     * @param positionGroup    组position
     * @param dataChild        组下面的child数据
     * @param positionChild    child所在group中的position
     */
    protected abstract void bindChild(@NonNull RecyclerView recyclerChildren, @NonNull View childView, G dataGroup, int positionGroup, C dataChild, int positionChild);

    /**
     * 通知分组折叠状态改变
     *
     * @param groupPosition
     */
    public void notifyItemExpandChanged(int groupPosition) {
        notifyItemChanged(groupPosition, PAYLOAD_EXPAND_CHANGED);
    }

    /**
     * 子列表更新
     *
     * @param groupPosition
     */
    public void notifyChildDataSetChanged(int groupPosition) {
        ChildChangePayload payload = new ChildChangePayload(ChildChangePayload.TYPE.DataSetChanged);
        notifyItemChanged(groupPosition, payload);
    }

    /**
     * 子列表item更新
     *
     * @param groupPosition
     * @param childPosition
     */
    public void notifyChildItemChanged(int groupPosition, int childPosition) {
        ChildChangePayload payload = new ChildChangePayload(ChildChangePayload.TYPE.ItemChanged);
        payload.position = childPosition;
        notifyItemChanged(groupPosition, payload);
    }

    /**
     * 子列表item更新
     *
     * @param groupPosition
     * @param childPosition
     * @param childPayload
     */
    public void notifyChildItemChanged(int groupPosition, int childPosition, Object childPayload) {
        ChildChangePayload payload = new ChildChangePayload(ChildChangePayload.TYPE.ItemChanged);
        payload.position = childPosition;
        payload.payload = childPayload;
        notifyItemChanged(groupPosition, payload);
    }

    /**
     * 子列表item更新
     *
     * @param groupPosition
     * @param childStart
     * @param childCount
     */
    public void notifyChildItemRangeChanged(int groupPosition, int childStart, int childCount) {
        ChildChangePayload payload = new ChildChangePayload(ChildChangePayload.TYPE.ItemRangeChanged);
        payload.positionStart = childStart;
        payload.itemCount = childCount;
        notifyItemChanged(groupPosition, payload);
    }

    /**
     * 子列表item更新
     *
     * @param groupPosition
     * @param childStartPosition
     * @param childCount
     * @param childPayload
     */
    public void notifyChildItemRangeChanged(int groupPosition, int childStartPosition, int childCount, Object childPayload) {
        ChildChangePayload payload = new ChildChangePayload(ChildChangePayload.TYPE.ItemRangeChanged);
        payload.positionStart = childStartPosition;
        payload.itemCount = childCount;
        payload.payload = childPayload;
        notifyItemChanged(groupPosition, payload);
    }

    /**
     * 子列表item增加
     *
     * @param groupPosition
     * @param childPosition
     */
    public void notifyChildItemInserted(int groupPosition, int childPosition) {
        ChildChangePayload payload = new ChildChangePayload(ChildChangePayload.TYPE.ItemInserted);
        payload.position = childPosition;
        notifyItemChanged(groupPosition, payload);
    }

    /**
     * 子列表item增加
     *
     * @param groupPosition
     * @param childStart
     * @param childCount
     */
    public void notifyChildItemRangeInserted(int groupPosition, int childStart, int childCount) {
        ChildChangePayload payload = new ChildChangePayload(ChildChangePayload.TYPE.ItemRangeInserted);
        payload.positionStart = childStart;
        payload.itemCount = childCount;
        notifyItemChanged(groupPosition, payload);
    }

    /**
     * 子列表item移除
     *
     * @param groupPosition
     * @param childPosition
     */
    public void notifyChildItemRemoved(int groupPosition, int childPosition) {
        ChildChangePayload payload = new ChildChangePayload(ChildChangePayload.TYPE.ItemRemoved);
        payload.position = childPosition;
        notifyItemChanged(groupPosition, payload);
    }

    /**
     * 子列表item移除
     *
     * @param groupPosition
     * @param childStart
     * @param childCount
     */
    public void notifyChildItemRangeRemoved(int groupPosition, int childStart, int childCount) {
        ChildChangePayload payload = new ChildChangePayload(ChildChangePayload.TYPE.ItemRangeRemoved);
        payload.positionStart = childStart;
        payload.itemCount = childCount;
        notifyItemChanged(groupPosition, payload);
    }

    /**
     * 子列表item交换位置
     *
     * @param groupPosition
     * @param fromChildPosition
     * @param toChildPosition
     */
    public void notifyChildItemMoved(int groupPosition, int fromChildPosition, int toChildPosition) {
        ChildChangePayload payload = new ChildChangePayload(ChildChangePayload.TYPE.ItemMoved);
        payload.fromPosition = fromChildPosition;
        payload.toPosition = toChildPosition;
        notifyItemChanged(groupPosition, payload);
    }

    public interface GroupItemEntity<T> {

        /**
         * 组是否展开
         *
         * @return
         */
        boolean isExpand();

        /**
         * 分组下的列表数据
         *
         * @return 分组下的child数据集合
         */
        List<T> getChildren();
    }

    public static class ExpandViewHolder extends BaseViewHolder {
        private View root;

        public ExpandViewHolder(@NonNull View view) {
            super(view);
            this.root = view;
        }

        public View getRoot() {
            return root;
        }
    }

    public interface OnChildItemClickListener {
        void onItemClick(BaseQuickAdapter adapterChildren, @NonNull View childView, int positionChild, int positionGroup);
    }

    public interface OnChildItemChildClickListener {
        void onItemChildClick(BaseQuickAdapter adapterChildren, @NonNull View childChildView, int positionChild, int positionGroup);
    }

    public interface OnChildItemLongClickListener {
        boolean onChildItemLongClick(BaseQuickAdapter adapterChildren, @NonNull View childView, int positionChild, int positionGroup);
    }

    public interface OnChildItemChildLongClickListener {
        boolean onChildItemLongClick(BaseQuickAdapter adapterChildren, @NonNull View childChildView, int positionChild, int positionGroup);
    }
}
