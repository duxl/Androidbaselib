package com.duxl.baselib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;

import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.shape.ShapeAppearanceModel;

public class MaterialUtils {

    /**
     * 获取带阴影的Drawable,阴影处于布局边界之外，所以需要自己设置父容器的clipChildren、clipToPadding属性
     *
     * @param context
     * @param fillColor   填充颜色
     * @param shadowColor 阴影颜色
     * @param elevation   阴影高度（即大小）
     * @param cornerSizes 圆角大小
     * @return
     */
    @SuppressLint("RestrictedApi")
    public static Drawable getShadowDrawable(Context context, @ColorInt int fillColor, @ColorInt int shadowColor, int elevation, @FloatRange(from = 0) float cornerSizes) {
        // Corner是角、Edge是边
        ShapeAppearanceModel.Builder builder = ShapeAppearanceModel.builder();
        builder.setAllCorners(new RoundedCornerTreatment()); // 设置角的样式为圆形
        builder.setAllCornerSizes(cornerSizes); // 设置圆角大小
        MaterialShapeDrawable drawable = new MaterialShapeDrawable(builder.build());
        drawable.setTint(fillColor); // 设置图片颜色
        drawable.setPaintStyle(Paint.Style.FILL);
        // 下面是阴影控制（阴影处于布局边界之外，所以需要使用clipChildren、clipToPadding属性）
        drawable.setShadowCompatibilityMode(MaterialShapeDrawable.SHADOW_COMPAT_MODE_ALWAYS);
        drawable.initializeElevationOverlay(context);
        drawable.setShadowColor(shadowColor); // 阴影颜色
        drawable.setElevation(elevation); // 阴影的海拔，相当于阴影大小
        drawable.setShadowVerticalOffset(0); // 设置为0表示四周扩散的阴影
        return drawable;
    }
}
