package com.duxl.baselib.widget;

/**
 * OnLoadListener
 * create by duxl 2020/8/16
 */
public interface OnLoadListener {

    void onRefresh();

    void onLoadMore();

    void onErrorClick(int errCode);

    void onEmptyClick();
}
