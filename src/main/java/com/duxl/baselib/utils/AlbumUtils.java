package com.duxl.baselib.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;

import com.duxl.baselib.ui.activity.ISupportComponentResult;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.SelectionCreator;
import com.zhihu.matisse.engine.ImageEngine;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.Set;

/**
 * 支持图片选择和视频选择~
 * create by duxl 2020/8/19
 */
public class AlbumUtils {

    /**
     * 选择视频
     *
     * @param activity
     * @param maxSelectable 最大选择数量
     * @param requestCode   请求code
     */
    public void openVideoForResult(ISupportComponentResult componentResult, int maxSelectable, int requestCode) {
        if (componentResult.getContext() instanceof Activity) {
            requestPermissions(componentResult, () -> choose(Matisse.from((Activity) componentResult.getContext()), MimeType.ofVideo(), maxSelectable, requestCode));
        }
    }

    /**
     * 选择图片
     *
     * @param componentResult
     * @param maxSelectable   最大选择数量
     * @param requestCode     请求code
     */
    public void openImageForResult(ISupportComponentResult componentResult, int maxSelectable, int requestCode) {
        if (componentResult.getContext() instanceof Activity) {
            requestPermissions(componentResult, () -> choose(Matisse.from((Activity) componentResult.getContext()), MimeType.ofImage(), maxSelectable, requestCode));
        }
    }

    /**
     * 选择媒体
     *
     * @param componentResult
     * @param mimeTypes       媒体类型集合，图片和视频
     * @param maxSelectable   最多选择数量
     * @param requestCode     请求code
     */
    public void openAlbumForResult(ISupportComponentResult componentResult, Set<MimeType> mimeTypes, int maxSelectable, int requestCode) {
        if (componentResult.getContext() instanceof Activity) {
            requestPermissions(componentResult, () -> choose(Matisse.from((Activity) componentResult.getContext()), mimeTypes, maxSelectable, requestCode));
        }
    }

    private void requestPermissions(ISupportComponentResult componentResult, Runnable runnable) {
        componentResult.launchRequestPermissions(
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                resultMap -> {
                    boolean all = true;
                    for (Boolean value : resultMap.values()) {
                        if (!value) {
                            all = false;
                            break;
                        }
                    }
                    if (all) {
                        runnable.run();
                    }
                }
        );
    }

    private void choose(Matisse matisse, Set<MimeType> mimeTypes, int maxSelectable, int requestCode) {
        SelectionCreator creator = matisse.choose(mimeTypes);
        if (getFilter() != null) {
            creator.addFilter(getFilter());
        }
        creator.capture(isCapture())
                .captureStrategy(new CaptureStrategy(false, Utils.getApp().getPackageName() + ".fileprovider"))
                //是否只显示选择的类型的缩略图，就不会把所有图片视频都放在一起，而是需要什么展示什么
                .showSingleMediaType(true)
                //有序选择图片 123456...
                .countable(true)
                .maxSelectable(maxSelectable)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                // 图片加载方式
                .imageEngine(getImageEngine())
                .forResult(requestCode);
    }

    /**
     * 文件顾虑器
     *
     * @return
     */
    protected Filter getFilter() {
        return null;
    }

    /**
     * 是否支持拍摄
     *
     * @return
     */
    protected boolean isCapture() {
        return true;
    }

    /**
     * 图片加载引擎
     *
     * @return
     */
    protected ImageEngine getImageEngine() {
        return new GlideEngine();
    }
}
