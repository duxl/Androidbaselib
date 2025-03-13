package com.duxl.baselib.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.activity.result.ActivityResult;

import com.duxl.baselib.widget.DataRunnable;

import java.util.List;
import java.util.Map;

public interface ISupportComponentResult {

    /**
     * 启动Activity
     *
     * @param intent   要启动的intent
     * @param onResult 返回值回调
     */
    void launchStartActivityForResult(Intent intent, DataRunnable<ActivityResult> onResult);

    /**
     * 支持单个文件，返回单个文件
     *
     * @param mateType 文件类型
     */
    void launchOpenFile(String mateType, DataRunnable<Uri> onResult);

    /**
     * 支持多种类型的文件，返回单个
     *
     * @param mateTypes 文件类型, 例如new String[]{"image/*"}
     * @param onResult  返回回调
     */
    void launchOpenDocument(String[] mateTypes, DataRunnable<Uri> onResult);

    /**
     * 支持多种类型的文件，返回多个
     *
     * @param mateTypes 文件类型, 例如new String[]{"image/*"}
     * @param onResult  返回回调
     */
    void launchOpenDocuments(String[] mateTypes, DataRunnable<List<Uri>> onResult);

    /**
     * 打开相机拍照
     *
     * @param saveUri  拍照图片保存路径
     * @param onResult 回调是否拍照成功
     */
    void launchOpenCamera(Uri saveUri, DataRunnable<Boolean> onResult);

    /**
     * 拍照返回bitmap
     *
     * @param onResult bitmap回调
     */
    void launchOpenCameraBitmap(DataRunnable<Bitmap> onResult);

    /**
     * 录像
     *
     * @param saveVideoUri 录像保存地址
     * @param onResult     录像回调，回调中的Bitmap始终为空，需要自己判断是否录像成功
     */
    void launchRecordVideo(Uri saveVideoUri, DataRunnable<Bitmap> onResult);

    /**
     * 单个权限获取
     *
     * @param permission 要申请的权限
     * @param onResult   是否获取到权限的回调
     */
    void launchRequestPermission(String permission, DataRunnable<Boolean> onResult);

    /**
     * 多权限获取
     *
     * @param permissions 要申请的权限
     * @param onResult    是否获取到权限的回调
     */
    void launchRequestPermissions(String[] permissions, DataRunnable<Map<String, Boolean>> onResult);
}
