package com.duxl.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.duxl.baselib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 密码输入框
 */
public class PwdEditText extends AppCompatEditText {

    private Paint borderPaint, backPaint, textPaint;
    private String mText;
    private List<RectF> rectFS;
    private int strokeWidth, space;
    private int defaultBorderColor, checkedBorderColor, inputBorderColor;
    private int defaultBackColor, checkedBackColor, inputBackColor;
    private int contentColor, waitInputColor;
    private int textLength;
    private int circle, round; // 圆点半径和输入圆角大小
    private boolean isPwd, isWaitInput, showFocus;
    // 设置了背景图后，颜色设置将无效
    private int defaultBackDrawable, checkedBackDrawable, inputBackDrawable;

    public PwdEditText(Context context) {
        super(context);
        setAttrs(null);
        init();
    }

    public PwdEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttrs(attrs);
        init();
    }

    public PwdEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttrs(attrs);
        init();
    }

    private void setAttrs(AttributeSet attrs) {
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.PwdEditText);
        if (t != null) {
            textLength = t.getInt(R.styleable.PwdEditText_textLength, 6);
            space = t.getDimensionPixelSize(R.styleable.PwdEditText_space, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()));
            strokeWidth = t.getDimensionPixelSize(R.styleable.PwdEditText_strokeWidth, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
            round = t.getDimensionPixelSize(R.styleable.PwdEditText_round, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()));
            circle = t.getDimensionPixelSize(R.styleable.PwdEditText_circle, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics()));
            // 背景色
            defaultBackColor = t.getColor(R.styleable.PwdEditText_defaultBackColor, 0xfff1f1f1);
            checkedBackColor = t.getColor(R.styleable.PwdEditText_checkedBackColor, defaultBackColor);
            inputBackColor = t.getColor(R.styleable.PwdEditText_inputBackColor, defaultBackColor);
            // 默认边框色（未输入边框色）
            defaultBorderColor = t.getColor(R.styleable.PwdEditText_defaultBorderColor, defaultBackColor);
            // 已输入边框色（默认未输入的边框色）
            checkedBorderColor = t.getColor(R.styleable.PwdEditText_checkedBorderColor, defaultBorderColor);
            // 正在输入边框色（默认未输入的边框色）
            inputBorderColor = t.getColor(R.styleable.PwdEditText_inputBorderColor, defaultBorderColor);
            // 内容颜色
            contentColor = t.getColor(R.styleable.PwdEditText_contentColor, 0xFF444444);
            waitInputColor = t.getColor(R.styleable.PwdEditText_waitInputColor, 0xFF444444);
            isPwd = t.getBoolean(R.styleable.PwdEditText_isPwd, true);
            // 是否显示等待输入的光标
            isWaitInput = t.getBoolean(R.styleable.PwdEditText_isWaitInput, false);

            defaultBackDrawable = t.getResourceId(R.styleable.PwdEditText_defaultBackDrawable, View.NO_ID);
            checkedBackDrawable = t.getResourceId(R.styleable.PwdEditText_defaultBackDrawable, defaultBackDrawable);
            inputBackDrawable = t.getResourceId(R.styleable.PwdEditText_defaultBackDrawable, defaultBackDrawable);
            t.recycle();
        }
    }

    private void init() {
        setPadding(0, 0, 0, 0);
        setTextColor(0X00ffffff); //把用户输入的内容设置为透明
        //setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        borderPaint = new Paint();
        backPaint = new Paint();
        textPaint = new Paint();

        rectFS = new ArrayList<>();
        mText = getText().toString();

        this.setBackgroundDrawable(null);
        setLongClickable(false);
        setTextIsSelectable(false);
        setCursorVisible(false);
        setSingleLine();

    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (mText == null) {
            return;
        }
        //如果字数不超过用户设置的总字数，就赋值给成员变量mText；
        // 如果字数大于用户设置的总字数，就只保留用户设置的几位数字，并把光标制动到最后，让用户可以删除；
        if (text.toString().length() <= textLength) {
            mText = text.toString();
        } else {
            setText(mText);
            setSelection(getText().toString().length());  //光标制动到最后
        }
        if (OnTextChangeListener != null) OnTextChangeListener.onTextChange(mText);
    }

    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                heightSize = MeasureSpec.getSize(heightMeasureSpec);
                break;
            case MeasureSpec.AT_MOST:
                heightSize = widthSize / textLength;
                break;
        }
        setMeasuredDimension(widthSize, heightSize);
    }*/

    /**
     * 获取输入框输入状态
     *
     * @param index 第几的个输入框
     * @return -1未输入，0正在输入，1已经输入
     */
    protected int inputState(int index) {
        if (index < mText.length()) {
            return 1;
        }
        if (index == mText.length() && hasFocus()) {
            return 0;
        }
        return -1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //边框画笔
        borderPaint.setAntiAlias(true);//消除锯齿
        borderPaint.setStrokeWidth(strokeWidth);//设置画笔的宽度
        borderPaint.setStyle(Paint.Style.STROKE);//设置绘制轮廓
        borderPaint.setColor(defaultBorderColor);
        //背景色画笔
        backPaint.setStyle(Paint.Style.FILL);
        backPaint.setColor(defaultBackColor);
        //文字的画笔
        textPaint.setTextSize(getTextSize());
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(contentColor);

        int itemWidth = (getMeasuredWidth() - space * textLength) / textLength;
        int itemHeight = getMeasuredHeight();

        rectFS.clear();
        for (int i = 0; i < textLength; i++) {

            //RectF的参数(left,  top,  right,  bottom); 画出每个矩形框并设置间距，间距其实是增加左边框距离，缩小上下右边框距离；
            int left = i * (itemWidth + space) + strokeWidth / 2;
            int top = strokeWidth / 2;
            int right = (i + 1) * (itemWidth + space) - space - strokeWidth / 2;
            int bottom = itemHeight - strokeWidth / 2;
            RectF rect = new RectF(left, top, right, bottom);
            if (defaultBackDrawable != View.NO_ID) {
                canvas.save();
                canvas.translate(left, 0);
                Drawable drawable;
                if (inputState(i) == 1) {
                    drawable = ContextCompat.getDrawable(getContext(), checkedBackDrawable);
                } else if (inputState(i) == 0) {
                    drawable = ContextCompat.getDrawable(getContext(), inputBackDrawable);
                } else {
                    drawable = ContextCompat.getDrawable(getContext(), defaultBackDrawable);
                }
                drawable.setBounds(0, 0, (int) rect.width(), (int) rect.height());
                drawable.draw(canvas);
                canvas.restore();
            } else {
                if (inputState(i) == 1) {
                    borderPaint.setColor(checkedBorderColor);
                    backPaint.setColor(checkedBackColor);
                } else if (inputState(i) == 0) {
                    borderPaint.setColor(inputBorderColor);
                    backPaint.setColor(inputBackColor);
                } else {
                    borderPaint.setColor(defaultBorderColor);
                    backPaint.setColor(defaultBackColor);
                }
                canvas.drawRoundRect(rect, round, round, backPaint); //绘制背景色
                canvas.drawRoundRect(rect, round, round, borderPaint); //绘制边框
            }
            rectFS.add(rect);

            //显示待输入的线
            if (isWaitInput && i == mText.length() && showFocus && hasFocus()) {
                Paint l = new Paint();
                l.setStrokeWidth(3);
                l.setStyle(Paint.Style.FILL);
                l.setColor(waitInputColor);
                int startX = i * (itemWidth + space) + itemWidth / 2;
                int startY = itemHeight / 2 - itemHeight / 4;
                int stopX = startX;
                int stopY = itemHeight / 2 + itemHeight / 5;
                canvas.drawLine(startX, startY, stopX, stopY, l);
            }

            // 循环切换光标闪烁
            if (isWaitInput && hasFocus()) {
                removeCallbacks(showFocusRunnable);
                postDelayed(showFocusRunnable, 800);
            }
        }
        //画密码圆点
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float textBaseLine = (itemHeight - (fontMetrics.bottom - fontMetrics.top)) / 2 - fontMetrics.top;
        for (int j = 0; j < mText.length(); j++) {
            if (isPwd) {
                canvas.drawCircle(rectFS.get(j).centerX(), rectFS.get(j).centerY(), circle, textPaint);
            } else {
                String c = mText.substring(j, j + 1);
                float cWidth = textPaint.measureText(c);
                canvas.drawText(c, rectFS.get(j).centerX() - (cWidth / 2), textBaseLine, textPaint);
            }
        }
    }

    private Runnable showFocusRunnable = new Runnable() {
        @Override
        public void run() {
            showFocus = !showFocus;
            postInvalidate();
        }
    };

    private int dp2px(float dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 输入监听
     */
    public interface OnTextChangeListener {
        void onTextChange(String pwd);
    }

    private OnTextChangeListener OnTextChangeListener;

    public void setOnTextChangeListener(OnTextChangeListener OnTextChangeListener) {
        this.OnTextChangeListener = OnTextChangeListener;
    }

    /**
     * 清空所有输入的内容
     */
    public void clearText() {
        setText("");
        //setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getSpace() {
        return space;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public int getDefaultBorderColor() {
        return defaultBorderColor;
    }

    public void setDefaultBorderColor(int defaultBorderColor) {
        this.defaultBorderColor = defaultBorderColor;
    }

    public int getCheckedBorderColor() {
        return checkedBorderColor;
    }

    public void setCheckedBorderColor(int checkedBorderColor) {
        this.checkedBorderColor = checkedBorderColor;
    }

    public int getInputBorderColor() {
        return inputBorderColor;
    }

    public void setInputBorderColor(int inputBorderColor) {
        this.inputBorderColor = inputBorderColor;
    }

    public int getDefaultBackColor() {
        return defaultBackColor;
    }

    public void setDefaultBackColor(int defaultBackColor) {
        this.defaultBackColor = defaultBackColor;
    }

    public int getCheckedBackColor() {
        return checkedBackColor;
    }

    public void setCheckedBackColor(int checkedBackColor) {
        this.checkedBackColor = checkedBackColor;
    }

    public int getInputBackColor() {
        return inputBackColor;
    }

    public void setInputBackColor(int inputBackColor) {
        this.inputBackColor = inputBackColor;
    }

    public int getContentColor() {
        return contentColor;
    }

    public void setContentColor(int contentColor) {
        this.contentColor = contentColor;
    }

    public int getWaitInputColor() {
        return waitInputColor;
    }

    public void setWaitInputColor(int waitInputColor) {
        this.waitInputColor = waitInputColor;
    }

    public int getTextLength() {
        return textLength;
    }

    public void setTextLength(int textLength) {
        this.textLength = textLength;
    }

    public int getCircle() {
        return circle;
    }

    public void setCircle(int circle) {
        this.circle = circle;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public boolean isPwd() {
        return isPwd;
    }

    public void setPwd(boolean pwd) {
        isPwd = pwd;
    }

    public boolean isWaitInput() {
        return isWaitInput;
    }

    public void setWaitInput(boolean waitInput) {
        isWaitInput = waitInput;
    }

    public int getDefaultBackDrawable() {
        return defaultBackDrawable;
    }

    public void setDefaultBackDrawable(int defaultBackDrawable) {
        this.defaultBackDrawable = defaultBackDrawable;
    }

    public int getCheckedBackDrawable() {
        return checkedBackDrawable;
    }

    public void setCheckedBackDrawable(int checkedBackDrawable) {
        this.checkedBackDrawable = checkedBackDrawable;
    }

    public int getInputBackDrawable() {
        return inputBackDrawable;
    }

    public void setInputBackDrawable(int inputBackDrawable) {
        this.inputBackDrawable = inputBackDrawable;
    }

}