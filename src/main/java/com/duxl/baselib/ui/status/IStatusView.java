package com.duxl.baselib.ui.status;

import android.view.View;

/**
 * IStatusView
 * create by duxl 2020/8/15
 */
public interface IStatusView {

    enum Status {
        /**
         * 显示加载中
         */
        Loading,

        /**
         * 显示空
         */
        Empty,

        /**
         * 显示错误
         */
        Error,

        /**
         * 普通
         */
        None
    }

    /**
     * 设置加载中提示文字
     *
     * @param loadingText
     */
    void setLoadingText(String loadingText);

    /**
     * 设置加载中提示图标
     *
     * @param loadingImgRes
     */
    void setLoadingImgRes(int loadingImgRes);

    /**
     * 设置加载中按钮文字
     *
     * @param loadingText
     */
    void setLoadingBtnText(String loadingText);

    /**
     * 设置加载中View的可见性
     *
     * @param textVisibility 提示文字可见性
     * @param imgVisibility  提示图标可见性
     * @param btnVisibility  提示按钮可见性
     */
    void setLoadingVisibility(int textVisibility, int imgVisibility, int btnVisibility);

    /**
     * 设置错误时提示文字
     *
     * @param errorText
     */
    void setErrorText(String errorText);

    /**
     * 设置错误时提示图标
     *
     * @param errorText
     */
    void setErrorImgRes(int errorImgRes);

    /**
     * 设置错误时按钮文字
     *
     * @param errorText
     */
    void setErrorBtnText(String errorText);

    /**
     * 设置错误时View的可见性
     *
     * @param textVisibility 提示文字可见性
     * @param imgVisibility  提示图标可见性
     * @param btnVisibility  提示按钮可见性
     */
    void setErrorVisibility(int textVisibility, int imgVisibility, int btnVisibility);

    /**
     * 设置空的时候提示文字
     *
     * @param emptyText
     */
    void setEmptyText(String emptyText);

    /**
     * 设置空的时候提示图标
     *
     * @param emptyText
     */
    void setEmptyImgRes(int emptyImgRes);

    /**
     * 设置空的时候按钮文字
     *
     * @param emptyText
     */
    void setEmptyBtnText(String emptyText);

    /**
     * 设置Empty的时候View的可见性
     *
     * @param textVisibility 提示文字可见性
     * @param imgVisibility  提示图标可见性
     * @param btnVisibility  提示按钮可见性
     */
    void setEmptyVisibility(int textVisibility, int imgVisibility, int btnVisibility);

    /**
     * 显示加载中
     */
    void showLoading();

    /**
     * 显示错误
     *
     * @param errCode 错误code
     */
    void showError(int errCode);

    /**
     * 显示空
     */
    void showEmpty();

    /**
     * 显示业务视图内容
     */
    void showContent();

    /**
     * 完成加载（停止下来刷新和加载更多动效）
     */
    void loadComplete();

    /**
     * 获取刷新（加载更多）控制器
     *
     * @return
     */
    IRefreshContainer getRefreshContainer();

    /**
     * 获取StatusView本身视图
     *
     * @return
     */
    View getView();

    /**
     * 获取当前显示状态
     *
     * @return
     */
    Status getStatus();
}
