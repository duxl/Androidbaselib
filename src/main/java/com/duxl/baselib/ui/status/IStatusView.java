package com.duxl.baselib.ui.status;

import android.view.View;

/**
 * IStatusView
 * create by duxl 2020/8/15
 */
public interface IStatusView {

    void setLoadingText(String loadingText);

    void setLoadingImgRes(int loadingImgRes);

    void setErrorText(String errorText);

    void setErrorImgRes(int errorImgRes);

    void setErrorBtnText(String errorText);

    void setEmptyText(String emptyText);

    void setEmptyImgRes(int emptyImgRes);

    void setEmptyBtnText(String emptyText);

    void showLoading();

    void showError();

    void showEmpty();

    void showContent();

    void loadComplete();

    IRefreshContainer getRefreshContainer();

    View getView();
}
