package com.duxl.baselib.widget.clip;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.duxl.baselib.R;
import com.duxl.baselib.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 图片裁剪容器，兼容Android10以上：
 * 使用的时候，只需要将此View放置到需要的布局中，
 * 然后调用 {@link #setImageSrc(Uri)} 设置原图，
 * 调用{@link #generateUri(Context)} 生成裁剪后的图片uri
 */
public class ClipViewLayout extends RelativeLayout {
    //裁剪原图
    private ImageView imageView;
    //裁剪框
    private ClipView clipView;
    //裁剪框水平方向间距，xml布局文件中指定
    private float mHorizontalPadding;
    //裁剪框垂直方向间距，计算得出
    private float mVerticalPadding;
    //图片缩放、移动操作矩阵
    private Matrix matrix = new Matrix();
    //图片原来已经缩放、移动过的操作矩阵
    private Matrix savedMatrix = new Matrix();
    //动作标志：无
    private static final int NONE = 0;
    //动作标志：拖动
    private static final int DRAG = 1;
    //动作标志：缩放
    private static final int ZOOM = 2;
    //初始化动作标志
    private int mode = NONE;
    //记录起始坐标
    private PointF start = new PointF();
    //记录缩放时两指中间点坐标
    private PointF mid = new PointF();
    private float oldDist = 1f;
    //用于存放矩阵的9个值
    private final float[] matrixValues = new float[9];
    //最小缩放比例
    private float minScale;
    //最大缩放比例
    private float maxScale = 4;


    public ClipViewLayout(Context context) {
        this(context, null);
    }

    public ClipViewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    //初始化控件自定义的属性
    public void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ClipViewLayout);

        //获取剪切框距离左右的边距, 默认为50dp
        mHorizontalPadding = array.getDimensionPixelSize(R.styleable.ClipViewLayout_mHorizontalPadding,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()));
        //获取裁剪框边框宽度，默认1dp
        int clipBorderWidth = array.getDimensionPixelSize(R.styleable.ClipViewLayout_clipBorderWidth,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        //裁剪框类型(圆或者矩形)
        int clipType = array.getInt(R.styleable.ClipViewLayout_clipType, 1);

        //回收
        array.recycle();
        clipView = new ClipView(context);
        //设置裁剪框类型
        clipView.setClipType(clipType == 1 ? ClipView.ClipType.CIRCLE : ClipView.ClipType.RECTANGLE);
        //设置剪切框边框
        clipView.setClipBorderWidth(clipBorderWidth);
        //设置剪切框水平间距
        clipView.setmHorizontalPadding(mHorizontalPadding);
        imageView = new ImageView(context);
        //相对布局布局参数
        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(imageView, lp);
        this.addView(clipView, lp);
    }


    /**
     * 初始化图片
     */
    public void setImageSrc(final Uri uri) {
        //需要等到imageView绘制完毕再初始化原图
        ViewTreeObserver observer = imageView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                initSrcPic(uri);
                imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    /**
     * 初始化图片
     * step 1: decode 出 720*1280 左右的照片  因为原图可能比较大 直接加载出来会OOM
     * step 2: 将图片缩放 移动到imageView 中间
     */
    public void initSrcPic(Uri uri) {
        if (uri == null) {
            return;
        }
        Log.d("evan", "**********clip_view uri*******  " + uri);
        String path = FileUtil.getRealFilePathFromUri(getContext(), uri);
        Log.d("evan", "**********clip_view path*******  " + path);
        if (TextUtils.isEmpty(path)) {
            return;
        }

        //这里decode出720*1280 左右的照片,防止OOM

        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (path.startsWith(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath())) { // 私域目录
                bitmap = decodeSampledBitmap(path, 720, 1280);
            } else {
                bitmap = decodeSampledBitmap(getImageContentUri(getContext(), path), 720, 1280);
            }
        } else {
            bitmap = decodeSampledBitmap(path, 720, 1280);
        }
        if (bitmap == null) {
            return;
        }

        //竖屏拍照的照片，直接使用的话，会旋转90度，下面代码把角度旋转过来
        int rotation = getExifOrientation(path); //查询旋转角度
        Matrix m = new Matrix();
        m.setRotate(rotation);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

        //图片的缩放比
        float scale;
        if (bitmap.getWidth() >= bitmap.getHeight()) {//宽图
            scale = (float) imageView.getWidth() / bitmap.getWidth();
            //如果高缩放后小于裁剪区域 则将裁剪区域与高的缩放比作为最终的缩放比
            Rect rect = clipView.getClipRect();
            //高的最小缩放比
            minScale = rect.height() / (float) bitmap.getHeight();
            if (scale < minScale) {
                scale = minScale;
            }
        } else {//高图
            //高的缩放比
            scale = (float) imageView.getHeight() / bitmap.getHeight();
            //如果宽缩放后小于裁剪区域 则将裁剪区域与宽的缩放比作为最终的缩放比
            Rect rect = clipView.getClipRect();
            //宽的最小缩放比
            minScale = rect.width() / (float) bitmap.getWidth();
            if (scale < minScale) {
                scale = minScale;
            }
        }
        // 缩放
        matrix.postScale(scale, scale);
        // 平移,将缩放后的图片平移到imageview的中心
        //imageView的中心x
        int midX = imageView.getWidth() / 2;
        //imageView的中心y
        int midY = imageView.getHeight() / 2;
        //bitmap的中心x
        int imageMidX = (int) (bitmap.getWidth() * scale / 2);
        //bitmap的中心y
        int imageMidY = (int) (bitmap.getHeight() * scale / 2);
        matrix.postTranslate(midX - imageMidX, midY - imageMidY);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        imageView.setImageMatrix(matrix);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * 查询图片旋转角度
     */
    public static int getExifOrientation(String filepath) {// YOUR MEDIA PATH AS STRING
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }
        return degree;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                //设置开始点位置
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //开始放下时候两手指间的距离
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) { //拖动
                    matrix.set(savedMatrix);
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    mVerticalPadding = clipView.getClipRect().top;
                    matrix.postTranslate(dx, dy);
                    //检查边界
                    checkBorder();
                } else if (mode == ZOOM) { //缩放
                    //缩放后两手指间的距离
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        //手势缩放比例
                        float scale = newDist / oldDist;
                        if (scale < 1) { //缩小
                            if (getScale() > minScale) {
                                matrix.set(savedMatrix);
                                mVerticalPadding = clipView.getClipRect().top;
                                matrix.postScale(scale, scale, mid.x, mid.y);
                                //缩放到最小范围下面去了，则返回到最小范围大小
                                while (getScale() < minScale) {
                                    //返回到最小范围的放大比例
                                    scale = 1 + 0.01F;
                                    matrix.postScale(scale, scale, mid.x, mid.y);
                                }
                            }
                            //边界检查
                            checkBorder();
                        } else { //放大
                            if (getScale() <= maxScale) {
                                matrix.set(savedMatrix);
                                mVerticalPadding = clipView.getClipRect().top;
                                matrix.postScale(scale, scale, mid.x, mid.y);
                            }
                        }
                    }
                }
                imageView.setImageMatrix(matrix);
                break;
        }
        return true;
    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     */
    private RectF getMatrixRectF(Matrix matrix) {
        RectF rect = new RectF();
        Drawable d = imageView.getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    /**
     * 边界检测
     */
    private void checkBorder() {
        RectF rect = getMatrixRectF(matrix);
        float deltaX = 0;
        float deltaY = 0;
        int width = imageView.getWidth();
        int height = imageView.getHeight();
        // 如果宽或高大于屏幕，则控制范围 ; 这里的0.001是因为精度丢失会产生问题，但是误差一般很小，所以我们直接加了一个0.01
        if (rect.width() >= width - 2 * mHorizontalPadding) {
            if (rect.left > mHorizontalPadding) {
                deltaX = -rect.left + mHorizontalPadding;
            }
            if (rect.right < width - mHorizontalPadding) {
                deltaX = width - mHorizontalPadding - rect.right;
            }
        }
        if (rect.height() >= height - 2 * mVerticalPadding) {
            if (rect.top > mVerticalPadding) {
                deltaY = -rect.top + mVerticalPadding;
            }
            if (rect.bottom < height - mVerticalPadding) {
                deltaY = height - mVerticalPadding - rect.bottom;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 获得当前的缩放比例
     */
    public final float getScale() {
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }


    /**
     * 多点触控时，计算最先放下的两指距离
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 多点触控时，计算最先放下的两指中心坐标
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


    /**
     * 获取剪切图
     */
    public Bitmap clip() {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Rect rect = clipView.getClipRect();
        Bitmap cropBitmap = null;
        Bitmap zoomedCropBitmap = null;
        try {
            cropBitmap = Bitmap.createBitmap(imageView.getDrawingCache(), rect.left, rect.top, rect.width(), rect.height());
            zoomedCropBitmap = zoomBitmap(cropBitmap, 200, 200);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cropBitmap != null) {
            cropBitmap.recycle();
        }
        // 释放资源
        imageView.destroyDrawingCache();
        return zoomedCropBitmap;
    }

    // Android10以后，不能直接访问文件，需要通过
    public static Uri getImageContentUri(Context context, String path) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{path}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            // 如果图片不在手机的共享图片数据库，就先把它插入。
            if (new File(path).exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, path);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 图片等比例压缩
     *
     * @param uri
     * @param reqWidth  期望的宽
     * @param reqHeight 期望的高
     * @return
     */
    public Bitmap decodeSampledBitmap(Uri uri, int reqWidth, int reqHeight) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            InputStream inputStream2 = getContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream2, null, options);
            inputStream2.close();
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 图片等比例压缩
     *
     * @param filePath
     * @param reqWidth  期望的宽
     * @param reqHeight 期望的高
     * @return
     */
    public static Bitmap decodeSampledBitmap(String filePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        //bitmap is null
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算InSampleSize
     * 宽的压缩比和高的压缩比的较小值  取接近的2的次幂的值
     * 比如宽的压缩比是3 高的压缩比是5 取较小值3  而InSampleSize必须是2的次幂，取接近的2的次幂4
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            int ratio = heightRatio < widthRatio ? heightRatio : widthRatio;
            // inSampleSize只能是2的次幂  将ratio就近取2的次幂的值
            if (ratio < 3)
                inSampleSize = ratio;
            else if (ratio < 6.5)
                inSampleSize = 4;
            else if (ratio < 8)
                inSampleSize = 8;
            else
                inSampleSize = ratio;
        }

        return inSampleSize;
    }

    /**
     * 图片缩放到指定宽高
     * <p/>
     * 非等比例压缩，图片会被拉伸
     *
     * @param bitmap 源位图对象
     * @param w      要缩放的宽度
     * @param h      要缩放的高度
     * @return 新Bitmap对象
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return newBmp;
    }

    /**
     * 将裁剪框中的图片生产uri
     *
     * @param context
     * @return 裁剪成功返回图片uri，否则返回null
     */
    public Uri generateUri(Context context) {
        //调用返回剪切图
        Bitmap zoomedCropBitmap = clip();
        if (zoomedCropBitmap == null) {
            Log.e("android", "zoomedCropBitmap == null");
            return null;
        }
        Uri mSaveUri = Uri.fromFile(new File(context.getCacheDir(), "cropped_" + System.currentTimeMillis() + ".jpg"));
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = context.getContentResolver().openOutputStream(mSaveUri);
                if (outputStream != null) {
                    zoomedCropBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                }
            } catch (IOException ex) {
                Log.e("android", "Cannot open file: " + mSaveUri, ex);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mSaveUri;
    }
}
