package com.duxl.baselib.utils;

/**
 * Null工具类，format方法将传入的参数转换为非null对象
 * create by duxl 2021/3/4
 */
public class NullUtils {


    /**
     * 格式化字符串：
     * 如果字符串为空返回""字符串，否则返回字符串本身
     *
     * @param text
     * @return
     */
    public static CharSequence format(CharSequence text) {
        return format(text, "");
    }

    /**
     * 格式化字符串：
     * 如果字符串为空返回指定默认值，否则返回字符串本身
     *
     * @param text
     * @param defaultValue
     * @return
     */
    public static CharSequence format(CharSequence text, CharSequence defaultValue) {
        if (EmptyUtils.isEmpty(text)) {
            return defaultValue;
        }
        return text;
    }

    /**
     * 格式化数字：
     * 如果数字为null返回0，否则返回数字本身
     *
     * @param num
     * @return
     */
    public static Integer format(Integer num) {
        return format(num, 0);
    }

    /**
     * 格式化数字：
     * 如果数字为null返回指定默认值，否则返回数字本身
     *
     * @param num
     * @param defaultValue 如果num为null时返回的值
     * @return
     */
    public static Integer format(Integer num, int defaultValue) {
        if (EmptyUtils.isNull(num)) {
            return Integer.valueOf(defaultValue);
        }
        return num;
    }

    /**
     * 格式化数字：
     * 如果数字为null返回0，否则返回数字本身
     *
     * @param num
     * @return
     */
    public static Long format(Long num) {
        return format(num, 0);
    }

    /**
     * 格式化数字：
     * 如果数字为null返回指定默认值，否则返回数字本身
     *
     * @param num
     * @param defaultValue 如果num为null时返回的值
     * @return
     */
    public static Long format(Long num, long defaultValue) {
        if (EmptyUtils.isNull(num)) {
            return Long.valueOf(defaultValue);
        }
        return num;
    }

    /**
     * 格式化数字：
     * 如果数字为null返回0，否则返回数字本身
     *
     * @param num
     * @return
     */
    public static Float format(Float num) {
        return format(num, 0);
    }

    /**
     * 格式化数字：
     * 如果数字为null返回指定默认值，否则返回数字本身
     *
     * @param num
     * @param defaultValue 如果num为null时返回的值
     * @return
     */
    public static Float format(Float num, float defaultValue) {
        if (EmptyUtils.isNull(num)) {
            return Float.valueOf(defaultValue);
        }
        return num;
    }

    /**
     * 格式化数字：
     * 如果数字为null返回0，否则返回数字本身
     *
     * @param num
     * @return
     */
    public static Double format(Double num) {
        return format(num, 0);
    }

    /**
     * 格式化数字：
     * 如果数字为null返回指定默认值，否则返回数字本身
     *
     * @param num
     * @param defaultValue 如果num为null时返回的值
     * @return
     */
    public static Double format(Double num, double defaultValue) {
        if (EmptyUtils.isNull(num)) {
            return Double.valueOf(defaultValue);
        }
        return num;
    }
}
