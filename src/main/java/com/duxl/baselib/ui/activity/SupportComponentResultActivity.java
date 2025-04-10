package com.duxl.baselib.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.widget.DataRunnable;

import java.util.List;
import java.util.Map;

/**
 * 封装了androidx.activity.ComponentActivity#registerForActivityResult一些方法，方便调用
 */
public abstract class SupportComponentResultActivity extends BaseActivity implements ISupportComponentResult {

    //---------------------------- 启动Activity ----------------------------
    private DataRunnable<ActivityResult> _onActivityResult;
    private final ActivityResultLauncher<Intent> registerForActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResult -> {
        if (_onActivityResult != null) {
            _onActivityResult.run(activityResult);
        }
    });

    /**
     * 启动Activity
     *
     * @param intent   要启动的intent
     * @param onResult 返回值回调
     */
    public void launchStartActivityForResult(Intent intent, DataRunnable<ActivityResult> onResult) {
        _onActivityResult = onResult;
        registerForActivityResult.launch(intent);
    }

    //---------------------------- 支持单个文件，返回单个文件 ----------------------------
    private DataRunnable<Uri> _openFileResult;
    private final ActivityResultLauncher<String> mGetContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        if (_openFileResult != null && result != null) {
            _openFileResult.run(result);
        }
    });

    /**
     * 支持单个文件，返回单个文件
     *
     * @param mateType 文件类型
     * @param onResult
     */
    public void launchOpenFile(String mateType, DataRunnable<Uri> onResult) {
        _openFileResult = onResult;
        mGetContentLauncher.launch(mateType);
    }

    //---------------------------- 支持多种类型的文件，返回单个 ----------------------------
    private DataRunnable<Uri> _onOpenDocumentResult;
    private final ActivityResultLauncher<String[]> registerOpenDocument = registerForActivityResult(new ActivityResultContracts.OpenDocument(), result -> {
        if (_onOpenDocumentResult != null && result != null) {
            _onOpenDocumentResult.run(result);
        }
    });

    /**
     * 支持多种类型的文件，返回单个
     *
     * @param mateTypes 文件类型, 例如new String[]{"image/*"}
     * @param onResult  返回回调
     */
    public void launchOpenDocument(String[] mateTypes, DataRunnable<Uri> onResult) {
        _onOpenDocumentResult = onResult;
        registerOpenDocument.launch(mateTypes);
    }

    //---------------------------- 支持多种类型的文件，返回单个 ----------------------------
    private DataRunnable<List<Uri>> _onLaunchOpenDocuments;
    private final ActivityResultLauncher<String[]> registerOpenMultipleDocuments = registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), result -> {
        if (_onLaunchOpenDocuments != null && EmptyUtils.isNotEmpty(result)) {
            _onLaunchOpenDocuments.run(result);
        }
    });

    /**
     * 支持多种类型的文件，返回多个
     *
     * @param mateTypes 文件类型, 例如new String[]{"image/*"}
     * @param onResult  返回回调
     */
    public void launchOpenDocuments(String[] mateTypes, DataRunnable<List<Uri>> onResult) {
        _onLaunchOpenDocuments = onResult;
        registerOpenMultipleDocuments.launch(mateTypes);
    }

    //---------------------------- 打开相机拍照 ----------------------------
    private DataRunnable<Boolean> _onOpenCamera;
    private final ActivityResultLauncher<Uri> registerOpenCamera = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
        if (_onOpenCamera != null) {
            _onOpenCamera.run(result);
        }
    });

    /**
     * 打开相机拍照
     *
     * @param saveUri  拍照图片保存路径
     * @param onResult 回调是否拍照成功
     */
    public void launchOpenCamera(Uri saveUri, DataRunnable<Boolean> onResult) {
        _onOpenCamera = onResult;
        registerOpenCamera.launch(saveUri);
    }

    //---------------------------- 拍照返回bitmap ----------------------------
    private DataRunnable<Bitmap> _launchOpenCameraBitmap;
    private final ActivityResultLauncher<Void> mTakePicturePreviewLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), result -> {
        if (_launchOpenCameraBitmap != null && result != null) {
            _launchOpenCameraBitmap.run(result);
        }
    });

    /**
     * 拍照返回bitmap
     *
     * @param onResult bitmap回调
     */
    public void launchOpenCameraBitmap(DataRunnable<Bitmap> onResult) {
        _launchOpenCameraBitmap = onResult;
        mTakePicturePreviewLauncher.launch(null);
    }


    //---------------------------- 录像 ----------------------------
    private DataRunnable<Bitmap> _recordVideoResult;
    private final ActivityResultLauncher<Uri> mRecordVideoLauncher = registerForActivityResult(new ActivityResultContracts.TakeVideo(), result -> {
        if (_recordVideoResult != null) {
            _recordVideoResult.run(result);
        }
    });

    /**
     * 录像
     *
     * @param saveVideoUri 录像保存地址
     * @param onResult     录像回调，回调中的Bitmap始终为空，需要自己判断是否录像成功
     */
    public void launchRecordVideo(Uri saveVideoUri, DataRunnable<Bitmap> onResult) {
        _recordVideoResult = onResult;
        mRecordVideoLauncher.launch(saveVideoUri);
    }

    //---------------------------- 单个权限获取 ----------------------------
    private DataRunnable<Boolean> _onRequestPermission;
    private final ActivityResultLauncher<String> mPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (_onRequestPermission != null) {
            _onRequestPermission.run(result);
        }
    });

    /**
     * 单个权限获取
     *
     * @param permission 要申请的权限
     * @param onResult   是否获取到权限的回调
     */
    public void launchRequestPermission(String permission, DataRunnable<Boolean> onResult) {
        _onRequestPermission = onResult;
        mPermissionLauncher.launch(permission);
    }


    //---------------------------- 多权限获取 ----------------------------
    private DataRunnable<Map<String, Boolean>> _onRequestPermissions;
    private final ActivityResultLauncher<String[]> mPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), map -> {
        if (_onRequestPermissions != null) {
            _onRequestPermissions.run(map);
        }
    });

    /**
     * 多权限获取
     *
     * @param permissions 要申请的权限
     * @param onResult    是否获取到权限的回调
     */
    public void launchRequestPermissions(String[] permissions, DataRunnable<Map<String, Boolean>> onResult) {
        _onRequestPermissions = onResult;
        mPermissionsLauncher.launch(permissions);
    }
}
