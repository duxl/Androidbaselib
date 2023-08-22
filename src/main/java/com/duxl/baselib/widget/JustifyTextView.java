package com.duxl.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;

import com.duxl.baselib.R;

/**
 * 两端分散对齐的TextView（使用本控件前提条件：单行普通文本）
 * 借鉴：https://github.com/androiddevelop/AlignTextView
 */
public class JustifyTextView extends AppCompatTextView {

    private final String TAG = "JustifyTextView";

    /**
     * 占位文本，用于计算TextView的宽度，例如显示3个字符需要占用4个字符的宽度就设置4个字符，不设置为TextView自己的宽度
     */
    protected String placeWidthText;

    /**
     * 最后一个字符是否不对齐，例“姓名：”末尾的冒号与“名”紧挨着，不需要分散
     */
    protected boolean disableLastChar;

    /**
     * 是否启用两端分散布局
     */
    protected boolean enableJustify;

    public void setPlaceWidthText(String text) {
        this.placeWidthText = text;
        invalidate();
    }

    public void setDisableLastChar(boolean disable) {
        this.disableLastChar = disable;
        invalidate();
    }

    public void setEnableJustify(boolean enable) {
        this.enableJustify = enable;
        invalidate();
    }

    public JustifyTextView(@NonNull Context context) {
        this(context, null);
    }

    public JustifyTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public JustifyTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JustifyTextView);
        if (typedArray != null) {
            placeWidthText = typedArray.getString(R.styleable.JustifyTextView_jtv_placeWidthText);
            disableLastChar = typedArray.getBoolean(R.styleable.JustifyTextView_jtv_disableLastChar, false);
            enableJustify = typedArray.getBoolean(R.styleable.JustifyTextView_jtv_enableJustify, false);
            typedArray.recycle();
        }
        setGravity(getGravity() | Gravity.CENTER_HORIZONTAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!TextUtils.isEmpty(placeWidthText) && enableJustify) {
            int width = (int) StaticLayout.getDesiredWidth(placeWidthText, getPaint());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);

        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onDraw(Canvas canvas) {
        if (!enableJustify || !justifyDraw(canvas, this, disableLastChar)) {
            super.onDraw(canvas);
        }
    }

    /**
     * 分散布局
     *
     * @param canvas
     * @param textView
     * @param disableLastChar
     * @return 返回true表示已分散对齐，false表示未处理
     */
    public static boolean justifyDraw(Canvas canvas, TextView textView, boolean disableLastChar) {
        // 启用分散对齐所需文字的最少个数
        int minCount = 2;
        if (disableLastChar) {
            minCount = 3;
        }
        String text = textView.getText().toString();
        if (TextUtils.isEmpty(text) && text.length() < minCount) {
            return false;
        }
        TextPaint paint = textView.getPaint();
        paint.setColor(textView.getCurrentTextColor());
        paint.drawableState = textView.getDrawableState();

        // 控件的宽度
        int viewWidth = textView.getMeasuredWidth();
        // 绘制文字所需总宽度
        float textWidth = StaticLayout.getDesiredWidth(text, paint);
        // 中间缝隙个数
        int spaceCount = text.length() - 1;
        if (disableLastChar) {
            spaceCount--;
        }
        // 总的缝隙宽度
        float totalGapWidth = viewWidth - textWidth - textView.getPaddingLeft() - textView.getPaddingRight();

        // 单个文字之间的缝隙宽度
        float gapWidth = totalGapWidth / spaceCount;

        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseLine = textView.getPaddingTop() - fontMetrics.top;

        int x = textView.getPaddingLeft();
        for (int i = 0; i < text.length(); i++) {
            String charText = text.substring(i, i + 1);
            canvas.drawText(charText, x, baseLine, paint);
            x += StaticLayout.getDesiredWidth(charText, paint);
            if (i != text.length() - 2 || !disableLastChar) {
                x += gapWidth;
            }
        }
        return true;
    }
}

