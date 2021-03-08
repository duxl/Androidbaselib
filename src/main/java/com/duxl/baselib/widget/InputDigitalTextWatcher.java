package com.duxl.baselib.widget;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.duxl.baselib.utils.EmptyUtils;

/**
 * 输入数字监听，控制输入数字的最小值，最大值和小数位位数
 * create by duxl 2021/3/8
 */
public abstract class InputDigitalTextWatcher implements TextWatcher {

    private EditText mEditText;
    private int mDecimalCount; // 可输入的小数位位数
    private float mMaxValue; // 可输入的最大值
    private float mMinValue; // 可输入的最大值
    private CharSequence mBeforeText; // 上一次输入的值
    private int mBeforeStart; // 上一次输入的位置
    private boolean mUserInput = true; // 是否是用户输入

    /**
     * 输入资产数量监听
     *
     * @param view
     * @param max  可输入的最大值，当超过最大值时，会触发{@link #onOverMax(float)}
     */
    public InputDigitalTextWatcher(EditText view, float max) {
        this(view, 2, max);
    }

    /**
     * 输入资产数量监听
     *
     * @param view
     * @param decimalCount 可输入的小数位位数
     * @param max          可输入的最大值，当超过最大值时，会触发{@link #onOverMax(float)}，如果最大值等于了{@link Float#MAX_VALUE},不验证最大值
     */
    public InputDigitalTextWatcher(EditText view, int decimalCount, float max) {
        this(view, decimalCount, Float.MIN_VALUE, max);
    }

    /**
     * 输入资产数量监听
     *
     * @param view
     * @param decimalCount 可输入的小数位位数
     * @param min          可输入的最小值，当低于最小值时，会触发{@link #onBelowMin(float)}，如果最小值等于了{@link Float#MIN_VALUE},不验证最小值
     * @param max          可输入的最大值，当超过最大值时，会触发{@link #onOverMax(float)}，如果最大值等于了{@link Float#MAX_VALUE},不验证最大值
     */
    public InputDigitalTextWatcher(EditText view, int decimalCount, float min, float max) {
        this.mEditText = view;
        this.mDecimalCount = decimalCount;
        this.mMinValue = min;
        this.mMaxValue = max;
    }

    public void setMaxValue(float maxValue) {
        this.mMaxValue = maxValue;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (mUserInput) {
            mBeforeStart = start;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mUserInput) {
            String value = s.toString();
            if (EmptyUtils.isNotEmpty(value)) {
                // 超过了小数位位数
                if (value.contains(".") && value.substring(value.indexOf(".") + 1).length() > mDecimalCount) {
                    mUserInput = false;
                    mEditText.setText(mBeforeText);
                    mEditText.setSelection(mBeforeStart <= mEditText.getText().length() ? mBeforeStart : mEditText.getText().length());
                }

                try {
                    float fValue = Float.valueOf(value);
                    if (fValue > mMaxValue && mMaxValue != Float.MAX_VALUE) { // 超过了最大值
                        if (!onOverMax(fValue)) {
                            mUserInput = false;
                            mEditText.setText(String.valueOf(mMaxValue));
                            mEditText.setSelection(mEditText.length());
                        }
                    } else if (fValue < mMinValue && mMinValue != Float.MIN_VALUE) { // 低于了最小值
                        if (!onBelowMin(fValue)) {
                            mUserInput = false;
                            mEditText.setText(String.valueOf(mMinValue));
                            mEditText.setSelection(mEditText.length());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            CharSequence afterText = mEditText.getText().toString();
            if (!TextUtils.equals(mBeforeText, afterText)) { // 输入框内容发送了改变
                try {
                    if (EmptyUtils.isEmpty(afterText)) {
                        onChanged(0);
                    } else {
                        onChanged(Float.valueOf(afterText.toString()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            mBeforeText = afterText;
            mUserInput = true;
        }
    }

    /**
     * 超过了可输入的最大值的回调
     *
     * @param value 当前输入的值
     * @return true表示已经处理了该输入值，false没有处理将当前值改为最大值{@link #mMaxValue}
     */
    public abstract boolean onOverMax(float value);

    /**
     * 低于了可输入的最小值的回调
     *
     * @param value 当前输入值
     * @return true表示已经处理了该输入值，false没有处理将当前值改为最小值{@link #mMinValue}
     */
    public abstract boolean onBelowMin(float value);

    /**
     * 输入框内容发生了改变回调
     *
     * @param value 当前值
     * @return
     */
    public abstract void onChanged(float value);
}
