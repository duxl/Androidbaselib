package com.duxl.baselib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.duxl.baselib.R;

/**
 * 遮罩ImageView
 */
public class MaskImageView extends AppCompatImageView {

    private int mMaskResId;

    public MaskImageView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public MaskImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MaskImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaskImageView);
        if (a != null) {
            mMaskResId = a.getResourceId(R.styleable.MaskImageView_maskSrc, 0);
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int layer = 0;
        if (mMaskResId > 0) {
            layer = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        }
        super.onDraw(canvas);

        if (mMaskResId > 0) {
            //获取遮罩的bitmap
            Bitmap mask = BitmapFactory.decodeResource(getResources(), mMaskResId);
            if (mask == null) {
                Drawable drawable = getResources().getDrawable(mMaskResId);
                if (drawable != null) {
                    mask = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas1 = new Canvas(mask);
                    drawable.setBounds(0, 0, getWidth(), getHeight());
                    drawable.draw(canvas1);
                }
            }
            if (mask == null) {
                canvas.restoreToCount(layer);
                return;
            }
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            //设置图层混合模式
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            //计算mask的绘制比例
            Matrix mMatrix = new Matrix();
            //这里有个小坑，别忘了getWidth和getHeight的值转为float，不然算出来的也是整数。
            mMatrix.setScale((float) getWidth() / (float) mask.getWidth(), (float) getHeight() / (float) mask.getHeight());
            canvas.drawBitmap(mask, mMatrix, paint);
            paint.setXfermode(null);
            mask.recycle();

            canvas.restoreToCount(layer);
        }
    }
}
