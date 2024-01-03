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
    private int checkedBorderColor, defaultBorderColor, inputBorderColor, backColor, textColor, waitInputColor;
    private int textLength;
    private int Circle, Round;
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
            Round = t.getDimensionPixelSize(R.styleable.PwdEditText_round, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()));
            Circle = t.getDimensionPixelSize(R.styleable.PwdEditText_circle, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics()));
            // 背景色
            backColor = t.getColor(R.styleable.PwdEditText_backColor, 0xfff1f1f1);
            // 默认边框色（未输入边框色）
            defaultBorderColor = t.getColor(R.styleable.PwdEditText_defaultBorderColor, backColor);
            // 已输入边框色（默认未输入的边框色）
            checkedBorderColor = t.getColor(R.styleable.PwdEditText_checkedBorderColor, defaultBorderColor);
            // 正在输入边框色（默认未输入的边框色）
            inputBorderColor = t.getColor(R.styleable.PwdEditText_inputBorderColor, defaultBorderColor);
            // 文本颜色
            textColor = t.getColor(R.styleable.PwdEditText_textColor, 0xFF444444);
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
        backPaint.setColor(backColor);
        //文字的画笔
        textPaint.setTextSize(getTextSize());
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(textColor);

        int itemWidth = (getMeasuredWidth() - space * textLength) / textLength;
        int itemHeight = getMeasuredHeight();

        rectFS.clear();
        for (int i = 0; i < textLength; i++) {
            //区分已输入和未输入的边框颜色
            if (inputState(i) == 1) {
                borderPaint.setColor(checkedBorderColor);
            } else if (inputState(i) == 0) {
                borderPaint.setColor(inputBorderColor);
            } else {
                borderPaint.setColor(defaultBorderColor);
            }

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
                canvas.drawRoundRect(rect, Round, Round, backPaint); //绘制背景色
                canvas.drawRoundRect(rect, Round, Round, borderPaint); //绘制边框
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
                canvas.drawCircle(rectFS.get(j).centerX(), rectFS.get(j).centerY(), Circle, textPaint);
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

    /**
     * 设置输入框间距
     */
    public void setSpace(int space) {
        this.space = space;
    }

    /**
     * 设置密码框个数
     */
    public void setTextLength(int textLength) {
        this.textLength = textLength;
    }

    /**
     * 获得密码框个数
     */
    public int getTextLength() {
        return this.textLength;
    }

    /**
     * 设置已输入密码框颜色
     */
    public void setCheckedColorColor(int checkedColor) {
        this.checkedBorderColor = checkedColor;
    }

    /**
     * 设置未输入密码框颜色
     */
    public void setDefaultColorColor(int defaultColor) {
        this.defaultBorderColor = defaultColor;
    }

    /**
     * 设置密码框背景色
     */
    public void setBackColor(int backColor) {
        this.backColor = backColor;
    }

    /**
     * 设置密码圆点的颜色
     */
    public void setPwdTextColor(int textColor) {
        this.textColor = textColor;
    }

    /**
     * 设置密码框 边框的宽度
     */
    public void setStrokeWidth(int width) {
        strokeWidth = width;
    }

    /**
     * 密码的圆点大小
     */
    public void setCircle(int Circle) {
        this.Circle = Circle;
    }

    /**
     * 密码边框的圆角大小
     */
    public void setRound(int Round) {
        this.Round = Round;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public int getSpace() {
        return space;
    }

    public int getCheckedBorderColor() {
        return checkedBorderColor;
    }

    public int getDefaultBorderColor() {
        return defaultBorderColor;
    }

    public int getBackColor() {
        return backColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getCircle() {
        return Circle;
    }

    public int getRound() {
        return Round;
    }

    public boolean isPwd() {
        return isPwd;
    }

    /**
     * 是否密文输入
     *
     * @param pwd
     */
    public void setPwd(boolean pwd) {
        isPwd = pwd;
    }

    public int getWaitInputColor() {
        return waitInputColor;
    }

    /**
     * 待输入线的颜色
     *
     * @param waitInputColor
     */
    public void setWaitInputColor(int waitInputColor) {
        this.waitInputColor = waitInputColor;
    }

    public boolean isWaitInput() {
        return isWaitInput;
    }

    /**
     * 是否显示待输入的线
     *
     * @param waitInput
     */
    public void setWaitInput(boolean waitInput) {
        isWaitInput = waitInput;
    }

}