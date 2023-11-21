package com.duxl.baselib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.OffsetEdgeTreatment;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.shape.TriangleEdgeTreatment;

import java.util.ArrayList;

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

    public enum Orientation {
        LEFT, TOP, RIGHT, BOTTOM
    }


    /**
     * 带三角尖的图
     *
     * @param fillColor    填充颜色
     * @param triangleSize 三角尖大小
     * @param offset       三角尖所在边的偏移量（相对于边的中间偏移）
     * @param cornerSizes  图圆角大小
     * @param inside       三角尖是否朝内（注意：三角朝外时，需要设置父布局的android:clipChildren=false和android:clipToPadding=false，使其有空间显示三角尖）
     * @param orientation  三角尖所在的边
     * @return
     */
    public static Drawable getTriangleDrawable(@ColorInt int fillColor, int triangleSize, int offset, float cornerSizes, boolean inside, Orientation orientation) {
        ShapeAppearanceModel.Builder builder = ShapeAppearanceModel.builder();
        OffsetEdgeTreatment triangleEdge = new OffsetEdgeTreatment(new TriangleEdgeTreatment(triangleSize, inside), offset);
        switch (orientation) {
            case LEFT:
                builder.setLeftEdge(triangleEdge);
                break;
            case TOP:
                builder.setTopEdge(triangleEdge);
                break;
            case RIGHT:
                builder.setRightEdge(triangleEdge);
                break;
            case BOTTOM:
                builder.setBottomEdge(triangleEdge);
                break;
        }
        builder.setAllCorners(new RoundedCornerTreatment()); // 设置角的样式为圆形
        builder.setAllCornerSizes(cornerSizes); // 设置圆角大小
        MaterialShapeDrawable drawable = new MaterialShapeDrawable(builder.build());
        drawable.setTint(fillColor); // 设置图片颜色
        drawable.setPaintStyle(Paint.Style.FILL);
        return drawable;
    }
}
