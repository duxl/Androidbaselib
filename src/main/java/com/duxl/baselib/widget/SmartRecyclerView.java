package com.duxl.baselib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duxl.baselib.R;
import com.duxl.baselib.ui.status.IRefreshContainer;
import com.duxl.baselib.ui.status.IStatusView;
import com.duxl.baselib.ui.status.IStatusViewContainer;
import com.duxl.baselib.ui.status.SimpleStatusView;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;

/**
 * SmartRecyclerView
 * create by duxl 2020/8/16
 */
public class SmartRecyclerView extends XSmartRefreshLayout implements IRefreshContainer, IStatusViewContainer {

    protected RecyclerView mRecyclerView;
    protected IStatusView mStatusView;

    public SmartRecyclerView(Context context) {
        this(context, null);
    }

    public SmartRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        setRefreshHeader(new ClassicsHeader(context));
        setRefreshFooter(new ClassicsFooter(context));

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setId(R.id.status_view_container);

        mRecyclerView = new RecyclerView(context);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        frameLayout.addView(mRecyclerView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        mStatusView = initStatusView();
        frameLayout.addView(mStatusView.getView(), FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        addView(frameLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    protected IStatusView initStatusView() {
        return new SimpleStatusView(this);
    }

    @Override
    public IStatusView getStatusView() {
        return mStatusView;
    }

    public void setStatusView(IStatusView statusView) {
        this.mStatusView = statusView;
        // 移除旧的状态View
        FrameLayout frameLayout = findViewById(R.id.status_view_container);
        for (int i = 0; i < frameLayout.getChildCount(); i++) {
            View childView = frameLayout.getChildAt(i);
            if (childView instanceof IStatusView) {
                frameLayout.removeView(childView);
                break;
            }
        }
        frameLayout.addView(mStatusView.getView(), FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

    }

    @Override
    public View getRootContentView() {
        return this;
    }

    @Override
    public RecyclerView getContentView() {
        return mRecyclerView;
    }

    @Override
    public XSmartRefreshLayout getRefreshLayout() {
        return this;
    }
}
