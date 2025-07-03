package com.duxl.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duxl.baselib.R;
import com.duxl.baselib.ui.status.IRefreshContainer;
import com.duxl.baselib.ui.status.IStatusView;
import com.duxl.baselib.ui.status.IStatusViewContainer;
import com.duxl.baselib.ui.status.SimpleStatusView;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * SmartRecycler 封装了上拉刷新，下载加载更多，显示各种状态的列表
 * create by duxl 2020/8/16
 */
public class SmartRecyclerView extends XSmartRefreshLayout implements IRefreshContainer, IStatusViewContainer {

    protected RecyclerView mRecyclerView;
    protected IStatusView mStatusView;
    private static final Class<?>[] LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE =
            new Class<?>[]{Context.class, AttributeSet.class, int.class, int.class};

    public SmartRecyclerView(Context context) {
        this(context, null);
    }

    public SmartRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmartRecyclerView);
        if (isInEditMode()) {
            int itemResId = typedArray.getResourceId(R.styleable.SmartRecyclerView_srv_preview_listitem, -1);
            int itemCount = typedArray.getInt(R.styleable.SmartRecyclerView_srv_preview_itemCount, 20);
            if (itemResId > 0 && itemCount > 0) {
                showPreview(itemResId, itemCount);
            }
        }

        String layoutManagerName = typedArray.getString(R.styleable.SmartRecyclerView_layoutManager);
        if (layoutManagerName == null) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            createLayoutManager(context, layoutManagerName, attrs, 0, 0);
        }

        typedArray.recycle();
    }

    private void initView(Context context) {
        setRefreshHeader(new ClassicsHeader(context));
        setRefreshFooter(new ClassicsFooter(context));

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setId(R.id.status_view_container);

        mRecyclerView = new RecyclerView(context);
        frameLayout.addView(mRecyclerView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        mStatusView = initStatusView();
        frameLayout.addView(mStatusView.getView(), FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        addView(frameLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    /**
     * 从RecyclerView拷贝过来的代码
     *
     * @param context
     * @param className
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    private void createLayoutManager(Context context, String className, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (className != null) {
            className = className.trim();
            if (!className.isEmpty()) {
                className = getFullClassName(context, className);
                try {
                    ClassLoader classLoader;
                    if (isInEditMode()) {
                        // Stupid layoutlib cannot handle simple class loaders.
                        classLoader = this.getClass().getClassLoader();
                    } else {
                        classLoader = context.getClassLoader();
                    }
                    Class<? extends RecyclerView.LayoutManager> layoutManagerClass =
                            Class.forName(className, false, classLoader)
                                    .asSubclass(RecyclerView.LayoutManager.class);
                    Constructor<? extends RecyclerView.LayoutManager> constructor;
                    Object[] constructorArgs = null;
                    try {
                        constructor = layoutManagerClass
                                .getConstructor(LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE);
                        constructorArgs = new Object[]{context, attrs, defStyleAttr, defStyleRes};
                    } catch (NoSuchMethodException e) {
                        try {
                            constructor = layoutManagerClass.getConstructor();
                        } catch (NoSuchMethodException e1) {
                            e1.initCause(e);
                            throw new IllegalStateException(attrs.getPositionDescription()
                                    + ": Error creating LayoutManager " + className, e1);
                        }
                    }
                    constructor.setAccessible(true);
                    mRecyclerView.setLayoutManager(constructor.newInstance(constructorArgs));
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Unable to find LayoutManager " + className, e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Could not instantiate the LayoutManager: " + className, e);
                } catch (InstantiationException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Could not instantiate the LayoutManager: " + className, e);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Cannot access non-public constructor " + className, e);
                } catch (ClassCastException e) {
                    throw new IllegalStateException(attrs.getPositionDescription()
                            + ": Class is not a LayoutManager " + className, e);
                }
            }
        }
    }

    private String getFullClassName(Context context, String className) {
        if (className.charAt(0) == '.') {
            return context.getPackageName() + className;
        }
        if (className.contains(".")) {
            return className;
        }
        return RecyclerView.class.getPackage().getName() + '.' + className;
    }

    protected IStatusView initStatusView() {
        SimpleStatusView simpleStatusView = new SimpleStatusView(mRecyclerView.getContext(), null);
        simpleStatusView.setRefreshContainer(this);
        return simpleStatusView;
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

    /**
     * 显示预览效果
     *
     * @param itemResId 预览的布局id
     * @param itemCount 预览item数量
     */
    protected void showPreview(int itemResId, int itemCount) {
        mRecyclerView.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(getContext()).inflate(itemResId, parent, false)) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return itemCount;
            }
        });
    }
}
