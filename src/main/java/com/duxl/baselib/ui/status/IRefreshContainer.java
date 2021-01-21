package com.duxl.baselib.ui.status;

import android.content.Context;
import android.view.View;

import com.duxl.baselib.widget.XSmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

/**
 * IRefreshContainer
 * create by duxl 2020/8/16
 */
public interface IRefreshContainer {

    /**
     * 完成刷新
     */
    RefreshLayout finishRefresh();

    /**
     * 完成加载
     */
    RefreshLayout finishLoadMore();

    /**
     * 完成加载并标记没有更多数据
     */
    RefreshLayout finishLoadMoreWithNoMoreData();

    /**
     * 恢复没有更多数据的原始状态
     */
    RefreshLayout resetNoMoreData();

    Context getContext();

    /**
     * 真正的更布局
     * @return
     */
    View getRootContentView();

    /**
     * 业务相关的布局
     * @return
     */
    View getContentView();

    /**
     * 刷新控件
     * @return
     */
    XSmartRefreshLayout getRefreshLayout();
}
