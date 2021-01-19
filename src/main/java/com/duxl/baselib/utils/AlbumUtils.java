package com.duxl.baselib.utils;

import android.Manifest;
import android.content.pm.ActivityInfo;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.duxl.baselib.BaseApplication;
import com.duxl.baselib.rx.SimpleObserver;
import com.tbruyelle.rxpermissions3.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.SelectionCreator;
import com.zhihu.matisse.engine.ImageEngine;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.Set;

import io.reactivex.rxjava3.annotations.NonNull;

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
    public void openVideoForResult(FragmentActivity activity, int maxSelectable, int requestCode) {
        requestPermissions(activity, () -> choose(Matisse.from(activity), MimeType.ofVideo(), maxSelectable, requestCode));
    }

    /**
     * 选择视频
     *
     * @param fragment
     * @param maxSelectable 最大选择数量
     * @param requestCode   请求code
     */
    public void openVideoForResult(Fragment fragment, int maxSelectable, int requestCode) {
        requestPermissions(fragment, () -> choose(Matisse.from(fragment), MimeType.ofVideo(), maxSelectable, requestCode));
    }

    /**
     * 选择图片
     *
     * @param activity
     * @param maxSelectable 最大选择数量
     * @param requestCode   请求code
     */
    public void openImageForResult(FragmentActivity activity, int maxSelectable, int requestCode) {
        requestPermissions(activity, () -> choose(Matisse.from(activity), MimeType.ofImage(), maxSelectable, requestCode));
    }

    /**
     * 选择图片
     *
     * @param fragment
     * @param maxSelectable 最大选择数量
     * @param requestCode   请求code
     */
    public void openImageForResult(Fragment fragment, int maxSelectable, int requestCode) {
        requestPermissions(fragment, () -> choose(Matisse.from(fragment), MimeType.ofImage(), maxSelectable, requestCode));
    }

    /**
     * 选择媒体
     *
     * @param activity
     * @param mimeTypes     媒体类型集合，图片和视频
     * @param maxSelectable 最多选择数量
     * @param requestCode   请求code
     */
    public void openAlbumForResult(FragmentActivity activity, Set<MimeType> mimeTypes, int maxSelectable, int requestCode) {
        requestPermissions(activity, () -> choose(Matisse.from(activity), mimeTypes, maxSelectable, requestCode));
    }

    /**
     * 选择媒体
     *
     * @param fragment
     * @param mimeTypes     媒体类型集合，图片和视频
     * @param maxSelectable 最多选择数量
     * @param requestCode   请求code
     */
    public void openAlbumForResult(Fragment fragment, Set<MimeType> mimeTypes, int maxSelectable, int requestCode) {
        requestPermissions(fragment, () -> choose(Matisse.from(fragment), mimeTypes, maxSelectable, requestCode));
    }

    private void requestPermissions(FragmentActivity activity, Runnable runnable) {
        requestPermissions(new RxPermissions(activity), runnable);
    }

    private void requestPermissions(Fragment fragment, Runnable runnable) {
        requestPermissions(new RxPermissions(fragment), runnable);
    }

    private void requestPermissions(RxPermissions rxPermissions, Runnable runnable) {
        rxPermissions.request(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ).subscribe(new SimpleObserver<Boolean>() {
            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    runnable.run();
                }
            }
        });
    }

    private void choose(Matisse matisse, Set<MimeType> mimeTypes, int maxSelectable, int requestCode) {
        SelectionCreator creator = matisse.choose(mimeTypes);
        if (getFilter() != null) {
            creator.addFilter(getFilter());
        }
        creator.capture(isCapture())
                .captureStrategy(new CaptureStrategy(true, BaseApplication.getInstance().getPackageName() + ".fileprovider"))
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
