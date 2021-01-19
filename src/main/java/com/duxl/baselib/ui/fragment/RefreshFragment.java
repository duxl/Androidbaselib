package com.duxl.baselib.ui.fragment;

import com.duxl.baselib.ui.status.IRefreshContainer;
import com.duxl.baselib.widget.OnLoadListener;
import com.duxl.baselib.widget.XSmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.trello.rxlifecycle4.components.support.RxFragment;

/**
 * RefreshFragment
 * create by duxl 2020/8/16
 */
public abstract class RefreshFragment extends RxFragment implements IRefreshContainer {

    protected XSmartRefreshLayout mXSmartRefreshLayout;

    protected void initSmartRefreshLayout(XSmartRefreshLayout xSmartRefreshLayout) {
        mXSmartRefreshLayout = xSmartRefreshLayout;
        setEnableRefresh(false);
        setEnableLoadMore(false);
        mXSmartRefreshLayout.setDisableContentWhenRefresh(getDisableContentWhenRefresh());//是否在刷新的时候禁止列表的操作
        mXSmartRefreshLayout.setDisableContentWhenLoading(getDisableContentWhenLoading());//是否在加载的时候禁止列表的操作
    }

    /**
     * 设置是否启用上拉加载更多（默认不启用）
     *
     * @param enabled 是否启用
     * @return RefreshLayout
     */
    public void setEnableLoadMore(boolean enabled) {
        mXSmartRefreshLayout.setEnableLoadMore(enabled);
    }

    /**
     * 是否启用下拉刷新（默认不启用）
     *
     * @param enabled 是否启用
     * @return SmartRefreshLayout
     */
    public void setEnableRefresh(boolean enabled) {
        mXSmartRefreshLayout.setEnableRefresh(enabled);
    }

    /**
     * 完成刷新
     */
    @Override
    public RefreshLayout finishRefresh() {
        mXSmartRefreshLayout.finishRefresh();
        return mXSmartRefreshLayout;
    }

    /**
     * 完成加载
     */
    @Override
    public RefreshLayout finishLoadMore() {
        mXSmartRefreshLayout.finishLoadMore();
        return mXSmartRefreshLayout;
    }

    /**
     * 完成加载并标记没有更多数据
     */
    @Override
    public RefreshLayout finishLoadMoreWithNoMoreData() {
        mXSmartRefreshLayout.finishLoadMoreWithNoMoreData();
        return mXSmartRefreshLayout;
    }

    /**
     * 恢复没有更多数据的原始状态
     */
    @Override
    public RefreshLayout resetNoMoreData() {
        mXSmartRefreshLayout.resetNoMoreData();
        return mXSmartRefreshLayout;
    }


    /**
     * 是否在刷新的时候禁止列表的操作
     *
     * @return
     */
    protected boolean getDisableContentWhenRefresh() {
        return true;
    }

    /**
     * 是否在加载的时候禁止列表的操作
     *
     * @return
     */
    protected boolean getDisableContentWhenLoading() {
        return true;
    }

    @Override
    public XSmartRefreshLayout getRefreshLayout() {
        return mXSmartRefreshLayout;
    }

    /**
     * 设置加载监听
     *
     * @param listener
     */
    public void setOnLoadListener(OnLoadListener listener) {
        mXSmartRefreshLayout.setOnLoadListener(listener);
    }
}
