package com.duxl.baselib.utils;

import androidx.annotation.FloatRange;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 算数相关工具类
 * create by duxl 2021/3/8
 */
public class ArithmeticUtils {

    /**
     * <pre>
     *     格式化数字成为有效的字符串
     *     将整数位最高位前面的0去掉，小数位末尾的0去掉
     *     例：
     *     输入 012.340 输出 12.34
     *     输入 .340  输出 0.34
     *     输入 0.00 输出 0
     * </pre>
     *
     * @param price
     * @return
     */
    public static String formatValid(BigDecimal price) {
        /*
        有关 new BigDecimal("1.2300").stripTrailingZeros() 返回1.23(正确)
        但是 new BigDecimal("0.0000").stripTrailingZeros() 返回0.0000(奇怪)
        似乎是 bug .但是它已在 Java 8 中修复
        return price
                .stripTrailingZeros()
                .toPlainString();
         */
        return new DecimalFormat("#.#######################################").format(price);
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s
     * @return
     */
    public static String formatValid(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    /**
     * 数字转换：
     * 例如将人民币153分转换成1.5元，153分到元四舍五入的方式保留一位小数的调用方式如下：
     * convertNum(153, 2, 1, RoundingMode.HALF_UP)
     *
     * @param num          原始数字
     * @param scale        原始小数位位数，scale <= 0 表示没有小数位
     * @param newScale     需要保留的小数位位数，newScale < 0 表示保留原始小数位
     * @param groupSize    整数部分逗号分割位数，当 groupSize >=0 时起效
     * @param roundingMode 保留小数位的模式：
     *                     舍 {@link java.math.RoundingMode#DOWN}、
     *                     入 {@link java.math.RoundingMode#UP}、
     *                     四舍五入 {@link java.math.RoundingMode#HALF_UP}、
     * @return
     */
    public static String convertNum(long num, int scale, int newScale, int groupSize, RoundingMode roundingMode) {
        return convertNum(num, scale, newScale, groupSize, true, roundingMode);
    }

    /**
     * 数字转换：
     * 例如将人民币153分转换成1.5元，153分到元四舍五入的方式保留一位小数的调用方式如下：
     * convertNum(153, 2, 1, RoundingMode.HALF_UP)
     *
     * @param num          原始数字
     * @param scale        原始小数位位数，scale <= 0 表示没有小数位
     * @param newScale     需要保留的小数位位数，newScale < 0 表示保留原始小数位
     * @param groupSize    整数部分逗号分割位数，当 groupSize >=0 时起效
     * @param minDigits    小数位末尾的0是否保留，true小数位末尾是0也会保留，false会舍去末尾的0
     *                     当newScale < 0 时，minDigits始终为true
     *                     例：
     *                     如果为true，当newScale=2时，1.000返回1.00
     *                     如果为false，当newScale=2时，1.000返回1
     * @param roundingMode 保留小数位的模式：
     *                     舍 {@link java.math.RoundingMode#DOWN}、
     *                     入 {@link java.math.RoundingMode#UP}、
     *                     四舍五入 {@link java.math.RoundingMode#HALF_UP}、
     * @return
     */
    public static String convertNum(long num, int scale, int newScale, int groupSize, boolean minDigits, RoundingMode roundingMode) {
        scale = scale < 0 ? 0 : scale;
        BigDecimal bigDecimal = BigDecimal.valueOf(num, scale);
        return convertNum(bigDecimal, newScale, groupSize, minDigits, roundingMode);
    }

    /**
     * 数字转换：
     *
     * @param bigDecimal   原始数字
     * @param newScale     需要保留的小数位位数，newScale < 0 表示保留原始小数位
     * @param groupSize    整数部分逗号分割位数，当 groupSize >=0 时起效
     * @param minDigits    小数位末尾的0是否保留，true小数位末尾是0也会保留，false会舍去末尾的0
     *                     当newScale < 0 时，minDigits始终为true
     *                     例：
     *                     如果为true，当newScale=2时，1.000返回1.00
     *                     如果为false，当newScale=2时，1.000返回1
     * @param roundingMode 保留小数位的模式：
     *                     舍 {@link java.math.RoundingMode#DOWN}、
     *                     入 {@link java.math.RoundingMode#UP}、
     *                     四舍五入 {@link java.math.RoundingMode#HALF_UP}、
     * @return
     */
    public static String convertNum(BigDecimal bigDecimal, int newScale, int groupSize, boolean minDigits, RoundingMode roundingMode) {
        if (newScale < 0) {
            newScale = bigDecimal.scale();
            minDigits = true;
        }
        bigDecimal = bigDecimal.setScale(newScale, roundingMode);
        DecimalFormat fmtEight = new DecimalFormat(",0.########################################");
        if (minDigits) {
            fmtEight.setMinimumFractionDigits(newScale);
        } else {
            fmtEight.setMaximumFractionDigits(newScale);
        }
        if (groupSize >= 0) {
            fmtEight.setGroupingSize(groupSize);
        }
        return fmtEight.format(bigDecimal);
    }

    /**
     * <pre>
     * 获取数字字符串中整数位分组长度
     * 0 → 0
     * 0.123 → 0
     * 123.123 → 0
     * 12345.1 → 0
     * 12,345.1 → 3
     * 1,2345.1 → 4
     * <pre/>
     * @param numStr
     * @return
     */
    public static int getNumGroupSize(String numStr) {
        if (numStr == null || numStr.indexOf(",") == -1) {
            return 0;
        }
        numStr = new StringBuilder(numStr).reverse().toString();
        int dotIndex = numStr.indexOf(".");
        int groupSize = numStr.indexOf(",", dotIndex) - dotIndex - 1;
        return groupSize;
    }

    /**
     * 根据传入的数字字符串实例化BigDecimal
     *
     * @param num
     * @return
     */
    public static BigDecimal newBigDecimal(String num) {
        try {
            if (EmptyUtils.isNotEmpty(num)) {
                num = num.replaceAll(",", ""); // 将数字中的分号全部去掉
                return new BigDecimal(num);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BigDecimal(0);
    }

    /**
     * 计算范围数值，比如数字10变化到20，变化比例是0.5，最后得到的值就是15
     *
     * @param from  数值开始值
     * @param to    数值结束值
     * @param scale 开始到结束的比例
     * @return
     */
    public static int getRangeValue(int from, int to, @FloatRange(from = 0.0, to = 1.0) float scale) {
        return (int) getRangeValue(Float.valueOf(from), Float.valueOf(to), scale);
    }

    /**
     * 计算范围数值，比如数字10变化到20，变化比例是0.5，最后得到的值就是15
     *
     * @param from  数值开始值
     * @param to    数值结束值
     * @param scale 开始到结束的比例
     * @return
     */
    public static float getRangeValue(float from, float to, @FloatRange(from = 0.0, to = 1.0) float scale) {
        return from + (to - from) * scale;
    }

    /**
     * 根据输入值等比例计算后输出，
     * 例如normalize(10, 0, 100, 0, 200)=20
     * 例如normalize(10, 0, 100, 20, 70)=25
     *
     * @param inputValue 当前输入值
     * @param inputMin   可输入的最小值
     * @param inputMax   可输入的最大值
     * @param outputMin  输出最小值
     * @param outputMax  输出最大值
     * @return
     */
    public static float normalize(
            float inputValue,
            float inputMin,
            float inputMax,
            float outputMin,
            float outputMax
    ) {
        if (inputValue < inputMin) {
            return outputMin;
        } else if (inputValue > inputMax) {
            return outputMax;
        }

        return outputMin * (1 - (inputValue - inputMin) / (inputMax - inputMin))
                + outputMax * ((inputValue - inputMin) / (inputMax - inputMin));
    }

    /**
     * 根据角度算正弦sin值（角度转幅度可用Math.toRadians()，幅度转角度可用Math.toDegrees()）
     *
     * @param angle 输入角度，比如30
     * @return
     */
    public static double sinAngle(float angle) {
        return Math.sin(Math.PI / 180 * angle);
    }

    /**
     * 通过正弦值算角度
     *
     * @param value 正弦值
     * @return
     */
    public static double sinRevert(double value) {
        return Math.toDegrees(Math.asin(value));
    }

    /**
     * 根据角度算余弦cos值
     *
     * @param angle 输入角度，比如30
     * @return
     */
    public static double cosAngle(float angle) {
        return Math.cos(Math.PI / 180 * angle);
    }

    /**
     * 通过余弦值算角度
     *
     * @param value 余弦值
     * @return
     */
    public static double cosRevert(double value) {
        return Math.toDegrees(Math.acos(value));
    }

    /**
     * 根据角度算正切tan值
     *
     * @param angle 输入角度，比如30
     * @return
     */
    public static double tanAngle(float angle) {
        return Math.tan(Math.PI / 180 * angle);
    }

    /**
     * 通过正切值算角度
     *
     * @param value 正切值
     * @return
     */
    public static double tanRevert(double value) {
        return Math.toDegrees(Math.atan(value));
    }
}
