package com.duxl.baselib.utils;

import android.text.TextUtils;

import java.util.Collection;
import java.util.Map;

/**
 * <pre>
 * EmptyUtils，验证传入参数是否为null或者集合是否未空集合，数组是否为空数组。
 * 与{@link NullUtils} 的区别是，{@link NullUtils} 是用于转换，EmptyUtils用于验证判断。
 * 前者着重转换，后者着重判断
 * create by duxl 2020/8/18
 * </pre>
 */
public class EmptyUtils {

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return isNull(map) || map.isEmpty();
    }

    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }

    public static <T> boolean isEmpty(Collection<T> collection) {
        return isNull(collection) || collection.isEmpty();
    }

    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(CharSequence text) {
        return TextUtils.isEmpty(text);
    }

    public static boolean isNotEmpty(CharSequence text) {
        return !isEmpty(text);
    }

    public static <T> boolean isEmpty(T[] array) {
        return isNull(array) || array.length == 0;
    }

    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    public static boolean isEmpty(byte[] array) {
        return isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(byte[] array) {
        return !isEmpty(array);
    }

    public static boolean isEmpty(int[] array) {
        return isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(int[] array) {
        return !isEmpty(array);
    }

    public static boolean isEmpty(short[] array) {
        return isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(short[] array) {
        return !isEmpty(array);
    }

    public static boolean isEmpty(long[] array) {
        return isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(long[] array) {
        return !isEmpty(array);
    }

    public static boolean isEmpty(float[] array) {
        return isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(float[] array) {
        return !isEmpty(array);
    }

    public static boolean isEmpty(double[] array) {
        return isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(double[] array) {
        return !isEmpty(array);
    }

    public static boolean isEmpty(char[] array) {
        return isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(char[] array) {
        return !isEmpty(array);
    }

    public static boolean isEmpty(boolean[] array) {
        return isNull(array) || array.length == 0;
    }

    public static boolean isNotEmpty(boolean[] array) {
        return !isEmpty(array);
    }
}
