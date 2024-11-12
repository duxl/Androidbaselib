package com.duxl.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.duxl.baselib.R;

/**
 * 可控制最大高宽的FrameLayout
 * create by duxl 2024/11/12
 */
public class MaxSizeFrameLayout extends FrameLayout {

    private int mMaxWidth;
    private int mMaxHeight;

    public void setMaxHeight(int maxHeight) {
        this.mMaxHeight = maxHeight;
    }

    public MaxSizeFrameLayout(Context context) {
        super(context);
        init(context, null);
    }

    public MaxSizeFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaxSizeFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected void init(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaxSizeFrameLayout);
            mMaxWidth = typedArray.getDimensionPixelSize(R.styleable.MaxSizeFrameLayout_msflMaxWidth, -1);
            mMaxHeight = typedArray.getDimensionPixelSize(R.styleable.MaxSizeFrameLayout_msflMaxHeight, -1);
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (mMaxWidth > 0 && width > mMaxWidth) {
            width = mMaxWidth;
        }

        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mMaxHeight > 0 && height > mMaxHeight) {
            height = mMaxHeight;
        }

        super.onMeasure(MeasureSpec.makeMeasureSpec(width, modeWidth), MeasureSpec.makeMeasureSpec(height, modeHeight));
    }
}
