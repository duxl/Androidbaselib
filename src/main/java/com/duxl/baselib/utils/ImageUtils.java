package com.duxl.baselib.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.IntRange;

import jp.wasabeef.glide.transformations.internal.FastBlur;
import jp.wasabeef.glide.transformations.internal.RSBlur;

/**
 * 图片相关工具类
 */
public class ImageUtils {

    /**
     * 高斯模糊，部分模糊可参考 https://github.com/centerzx/ShapeBlurView
     *
     * @param context
     * @param bitmap
     * @param radius  0～25
     * @return
     */
    public static Bitmap blurBitmap(Context context, Bitmap bitmap, @IntRange(from = 0, to = 25) int radius) {
        try {
            return RSBlur.blur(context, bitmap, radius);
        } catch (Exception e) {
            return FastBlur.blur(bitmap, radius, true);
        }
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getMinimumWidth();
        int h = drawable.getMinimumHeight();
        Bitmap.Config config = null;
        if (drawable.getOpacity() != PixelFormat.OPAQUE) {
            config = Bitmap.Config.ARGB_8888;
        } else {
            config = Bitmap.Config.RGB_565;
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * bitmap转drawable
     *
     * @param resources
     * @param bitmap
     * @return
     */
    public static Drawable bitmapToDrawable(Resources resources, Bitmap bitmap) {
        return new BitmapDrawable(resources, bitmap);
    }
}
