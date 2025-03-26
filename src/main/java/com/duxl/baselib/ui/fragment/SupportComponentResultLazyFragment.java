package com.duxl.baselib.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.activity.result.ActivityResult;

import com.duxl.baselib.ui.activity.ISupportComponentResult;
import com.duxl.baselib.ui.activity.SupportComponentResultActivity;
import com.duxl.baselib.widget.DataRunnable;

import java.util.List;
import java.util.Map;

/**
 * 封装了androidx.activity.ComponentActivity#registerForActivityResult一些方法，方便调用
 */
public abstract class SupportComponentResultLazyFragment extends LazyFragment implements ISupportComponentResult {

    /**
     * 启动Activity
     *
     * @param intent   要启动的intent
     * @param onResult 返回值回调
     */
    public void launchStartActivityForResult(Intent intent, DataRunnable<ActivityResult> onResult) {
        requestSupportComponentResultActivity().launchStartActivityForResult(intent, onResult);
    }

    /**
     * 支持单个文件，返回单个文件
     *
     * @param mateType 文件类型
     */
    public void launchOpenFile(String mateType, DataRunnable<Uri> onResult) {
        requestSupportComponentResultActivity().launchOpenFile(mateType, onResult);
    }

    /**
     * 支持多种类型的文件，返回单个
     *
     * @param mateTypes 文件类型, 例如new String[]{"image/*"}
     * @param onResult  返回回调
     */
    public void launchOpenDocument(String[] mateTypes, DataRunnable<Uri> onResult) {
        requestSupportComponentResultActivity().launchOpenDocument(mateTypes, onResult);
    }

    /**
     * 支持多种类型的文件，返回多个
     *
     * @param mateTypes 文件类型, 例如new String[]{"image/*"}
     * @param onResult  返回回调
     */
    public void launchOpenDocuments(String[] mateTypes, DataRunnable<List<Uri>> onResult) {
        requestSupportComponentResultActivity().launchOpenDocuments(mateTypes, onResult);
    }

    /**
     * 打开相机拍照
     *
     * @param saveUri  拍照图片保存路径
     * @param onResult 回调是否拍照成功
     */
    public void launchOpenCamera(Uri saveUri, DataRunnable<Boolean> onResult) {
        requestSupportComponentResultActivity().launchOpenCamera(saveUri, onResult);
    }

    /**
     * 拍照返回bitmap
     *
     * @param onResult bitmap回调
     */
    public void launchOpenCameraBitmap(DataRunnable<Bitmap> onResult) {
        requestSupportComponentResultActivity().launchOpenCameraBitmap(onResult);
    }

    /**
     * 录像
     *
     * @param saveVideoUri 录像保存地址
     * @param onResult     录像回调，回调中的Bitmap始终为空，需要自己判断是否录像成功
     */
    public void launchRecordVideo(Uri saveVideoUri, DataRunnable<Bitmap> onResult) {
        requestSupportComponentResultActivity().launchRecordVideo(saveVideoUri, onResult);
    }

    /**
     * 单个权限获取
     *
     * @param permission 要申请的权限
     * @param onResult   是否获取到权限的回调
     */
    public void launchRequestPermission(String permission, DataRunnable<Boolean> onResult) {
        requestSupportComponentResultActivity().launchRequestPermission(permission, onResult);
    }

    /**
     * 多权限获取
     *
     * @param permissions 要申请的权限
     * @param onResult    是否获取到权限的回调
     */
    public void launchRequestPermissions(String[] permissions, DataRunnable<Map<String, Boolean>> onResult) {
        requestSupportComponentResultActivity().launchRequestPermissions(permissions, onResult);
    }

    protected SupportComponentResultActivity requestSupportComponentResultActivity() {
        if (requireActivity() instanceof SupportComponentResultActivity) {
            return (SupportComponentResultActivity) requireActivity();
        }
        // 使用launch方法的fragment对应的Activity必须是SupportComponentResultActivity的实例
        throw new RuntimeException("this fragment currently associated Activity not instanceof SupportComponentResultActivity");
    }
}
