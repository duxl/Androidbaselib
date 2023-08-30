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
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.duxl.baselib.R;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 可折叠分组的适配器
 *
 * @param <G> 分组数据类型
 * @param <C> 分组下的Child数据类型
 */
public abstract class BaseExpandableAdapter<G extends BaseExpandableAdapter.GroupItemEntity<C>, C> extends BaseQuickAdapter<G, BaseExpandableAdapter.ExpandViewHolder> {

    private int mGroupLayoutResId;
    private int mChildLayoutResId;

    // Child的item点击事件(ps:分组的item点击事件已有)
    private OnChildItemClickListener mOnChildItemClickListener;
    // Child的子view点击事件(ps:分组的子view点击事件已有)
    private OnChildItemChildClickListener mOnChildItemChildClickListener;
    // 用于保存需要设置点击事件的Child的子view
    private LinkedHashSet<Integer> mChildItemChildClickViewIds = new LinkedHashSet<>();

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

    @NonNull
    @Override
    protected ExpandViewHolder createBaseViewHolder(@NonNull View view) {
        FrameLayout flGroupContainer = view.findViewById(R.id.fl_group_container);
        View vGroup = onCreateGroupView(flGroupContainer, mGroupLayoutResId);
        flGroupContainer.removeAllViews();
        flGroupContainer.addView(vGroup);
        return new ExpandViewHolder(view);
    }

    @Override
    protected void convert(@NonNull ExpandViewHolder holder, G dataGroup) {
        int groupPosition = getItemPosition(dataGroup);
        // 处理Group组
        FrameLayout flGroup = holder.getView(R.id.fl_group_container);
        bindGroup(flGroup.getChildAt(0), dataGroup, getItemPosition(dataGroup));

        // 处理Children
        RecyclerView recyclerChildren = holder.findView(R.id.recyclerview_children);
        onBindChildren(recyclerChildren, dataGroup, groupPosition);
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
        View vGroup = inflater.inflate(groupLayoutResId, parent, false);
        return vGroup;
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
        View vChild = inflater.inflate(childLayoutResId, parent, false);
        return vChild;
    }

    protected RecyclerView.LayoutManager getChildrenLayoutManger(RecyclerView childrenView, G dataGroup, int positionGroup) {
        return new LinearLayoutManager(childrenView.getContext());
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
        recyclerChildren.setHasFixedSize(true);
        recyclerChildren.setNestedScrollingEnabled(false);
        recyclerChildren.setLayoutManager(getChildrenLayoutManger(recyclerChildren, dataGroup, positionGroup));
        BaseQuickAdapter childrenAdapter;
        recyclerChildren.setAdapter(childrenAdapter = new BaseQuickAdapter<C, ExpandViewHolder>(mChildLayoutResId, dataGroup.getChildren()) {
            @Override
            protected void convert(@NonNull ExpandViewHolder viewHolder, C dataChild) {
                bindChild(recyclerChildren, viewHolder.getRoot(), dataGroup, positionGroup, dataChild, getItemPosition(dataChild));
            }

            @NonNull
            @Override
            protected ExpandViewHolder onCreateDefViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ExpandViewHolder(onCreateChildView(parent, mChildLayoutResId));
            }
        });

        // 添加哪些子view可点击
        for (Integer childChildId : mChildItemChildClickViewIds) {
            childrenAdapter.addChildClickViewIds(childChildId);
        }

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
        ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
        if (dataGroup.isExpand()) {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            layoutParams.height = 0;
        }
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
}
