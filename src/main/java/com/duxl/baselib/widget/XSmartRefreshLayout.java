package com.duxl.baselib.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;

/**
 * XSmartRefreshLayout
 * create by duxl 2020/8/16
 */
public class XSmartRefreshLayout extends SmartRefreshLayout {

    protected OnLoadListener mOnLoadListener;

    public XSmartRefreshLayout(Context context) {
        super(context);
    }

    public XSmartRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置加载监听
     *
     * @param listener
     */
    public void setOnLoadListener(OnLoadListener listener) {
        this.mOnLoadListener = listener;
        setOnRefreshListener(refreshLayout -> {
            if (mOnLoadListener != null) {
                mOnLoadListener.onRefresh();
            }
        });
        setOnLoadMoreListener(refreshLayout -> {
            if (mOnLoadListener != null) {
                mOnLoadListener.onLoadMore();
            }
        });
    }

    public OnLoadListener getOnLoadListener() {
        return mOnLoadListener;
    }
}
