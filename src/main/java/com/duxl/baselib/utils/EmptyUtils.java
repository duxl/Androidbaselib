package com.duxl.baselib.utils;

import android.text.TextUtils;

import java.util.Collection;
import java.util.Map;

/**
 * EmptyUtils，验证传入参数是否为null或者集合是否未空集合
 * create by duxl 2020/8/18
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
}
