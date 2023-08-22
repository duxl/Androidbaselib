package com.duxl.baselib.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 字体工具
 */
public class FontUtil {

    private static Map<Integer, SoftReference<Typeface>> cache = new HashMap<>();

    /**
     * 给文本设置字体
     *
     * @param view
     * @param fontResId 在res/font资源目录里面的字体resId
     */
    public static void setType(TextView view, int fontResId) {
        Typeface tf = getTypeFace(view.getContext(), fontResId);
        view.setTypeface(tf);
    }

    /**
     * 获取字体
     *
     * @param context
     * @param fontResId 在res/font资源目录里面的字体resId
     * @return
     */
    public static Typeface getTypeFace(Context context, int fontResId) {
        Typeface tf;
        if (cache.containsKey(fontResId) && cache.get(fontResId).get() != null) {
            tf = cache.get(fontResId).get();
        } else {
            tf = ResourcesCompat.getFont(context, fontResId);
            cache.put(fontResId, new SoftReference(tf));
        }
        return tf;
    }
}
