package com.duxl.baselib.http;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.duxl.baselib.ui.status.IRefreshContainer;
import com.duxl.baselib.ui.status.IStatusView;
import com.duxl.baselib.ui.status.IStatusViewContainer;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.widget.SmartRecyclerView;
import com.scwang.smart.refresh.layout.constant.RefreshState;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * RVHttpObserver
 * <p>
 * create by duxl 2021/1/15
 */
public abstract class RVHttpObserver<T extends BaseRoot, M> extends BaseHttpObserver<T> {

    private BaseQuickAdapter mAdapter;
    private IRefreshContainer mIRefreshContainer;
    private IStatusViewContainer mIStatusViewContainer;

    public RVHttpObserver(BaseQuickAdapter adapter) {
        this(adapter, null, null);
    }

    /**
     * @param adapter
     * @param refreshContainer    刷新容器
     * @param statusViewContainer 状态容器
     */
    public RVHttpObserver(BaseQuickAdapter adapter, IRefreshContainer refreshContainer, IStatusViewContainer statusViewContainer) {
        mAdapter = adapter;
        if (mAdapter == null) {
            throw new IllegalArgumentException("The adapter parameter cannot be null");
        }
        mIRefreshContainer = refreshContainer;
        mIStatusViewContainer = statusViewContainer;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        super.onSubscribe(d);
        if (isFirstPage() && mIStatusViewContainer != null) {
            if (EmptyUtils.isEmpty(mAdapter.getData())) {
                mIStatusViewContainer.getStatusView().showLoading();
            }
        }
    }

    @Override
    public void onNext(@NonNull T root) {
        loadComplete();
        if (root.isSuccess()) {
            try {
                onSuccess(root);
                List<M> list = getListData(root);
                if (list != null) {
                    if (isFirstPage()) {
                        mAdapter.setNewInstance(list);
                    } else {
                        mAdapter.addData(list);
                    }
                }

                if (EmptyUtils.isEmpty(mAdapter.getData())) {
                    if (mIStatusViewContainer != null) {
                        mIStatusViewContainer.getStatusView().showEmpty();
                    }
                } else {
                    if (mIStatusViewContainer != null) {
                        mIStatusViewContainer.getStatusView().showContent();
                    }
                    if (hasMoreData(root)) {
                        if (mIRefreshContainer != null) {
                            mIRefreshContainer.resetNoMoreData();
                        }
                    } else {
                        if (mIRefreshContainer != null) {
                            mIRefreshContainer.finishLoadMoreWithNoMoreData();
                        }
                    }
                }
            } catch (Exception e) {
                onError(e);
            }

        } else {
            onError(root.getCode(), root.getMsg(), root);
        }
    }

    @Override
    public void onError(int code, String msg, T root) {
        super.onError(code, msg, root);
        loadComplete();
        if (mIStatusViewContainer != null) {
            if (EmptyUtils.isEmpty(mAdapter.getData())) {
                mIStatusViewContainer.getStatusView().showEmpty();
            }
        }
    }

    protected void loadComplete() {
        if (mIRefreshContainer != null) {
            if (mIRefreshContainer.getRefreshLayout().getState() == RefreshState.Refreshing) {
                mIRefreshContainer.getRefreshLayout().finishRefresh();
            } else if (mIRefreshContainer.getRefreshLayout().getState() == RefreshState.Loading) {
                mIRefreshContainer.getRefreshLayout().finishLoadMore(0);
            }
        }
    }

    @Override
    public void onSuccess(T root) {
    }

    public abstract List<M> getListData(T root);

    public abstract boolean isFirstPage();

    public abstract boolean hasMoreData(T root);
}
