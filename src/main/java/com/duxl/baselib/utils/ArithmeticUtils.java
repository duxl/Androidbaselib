package com.duxl.baselib.utils;

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
     * 数字转换：
     * 例如将人民币153分转换成1.5元，153分到元四舍五入的方式保留一位小数的调用方式如下：
     * convertNum(153, 2, 1, RoundingMode.DOWN)
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
     * convertNum(153, 2, 1, RoundingMode.DOWN)
     *
     * @param num          原始数字
     * @param scale        原始小数位位数，scale <= 0 表示没有小数位
     * @param newScale     需要保留的小数位位数，newScale < 0 表示保留原始小数位
     * @param groupSize    整数部分逗号分割位数，当 groupSize >=0 时起效
     * @param minDigits    是否最低小数位，true小数位末尾是0也会保留，false会舍去末尾的0
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
        newScale = newScale < 0 ? scale : newScale;

        BigDecimal bigDecimal = BigDecimal.valueOf(num, scale).setScale(newScale, roundingMode);
        DecimalFormat fmtEight = new DecimalFormat(",0.#");
        if(minDigits) {
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
}
