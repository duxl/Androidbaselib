package com.duxl.baselib.ui.fragment.webview;

import android.net.Uri;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.duxl.baselib.ui.fragment.BaseFragment;
import com.duxl.baselib.utils.EmptyUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * create by duxl 2021/5/18
 */
public class BaseWebChromeClient extends WebChromeClient {

    private BaseFragment mFragment;
    private @Nullable ProgressBar mProgressBar;
    protected ValueCallback<Uri[]> mFilePathCallback = null;
    protected Uri mTakeCaptureUri = null;

    // 拍照
    protected ActivityResultLauncher<Uri> mTakePictureLauncher;
    // 多选
    protected ActivityResultLauncher<String[]> mOpenMultipleDocumentsLauncher;
    // 单选
    protected ActivityResultLauncher<String> mGetContentLauncher;

    public void setProgressBar(@Nullable ProgressBar progressBar) {
        this.mProgressBar = progressBar;
    }

    /**
     * @param fragment
     * @param progressBar 实例化时还没有ProgressBar，可以之后调用setProgressBar设置
     */
    public BaseWebChromeClient(BaseFragment fragment, @Nullable ProgressBar progressBar) {
        this.mFragment = fragment;
        this.mProgressBar = progressBar;

        // 拍照
        this.mTakePictureLauncher = mFragment.registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result) {
                List<Uri> uris = new ArrayList<>();
                uris.add(mTakeCaptureUri);
                callbackReceiveFile(uris);
            } else {
                callbackReceiveFile(null);
            }
        });

        // 多选
        mOpenMultipleDocumentsLauncher = mFragment.registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), new ActivityResultCallback<List<Uri>>() {
            @Override
            public void onActivityResult(List<Uri> result) {
                callbackReceiveFile(result);
            }
        });

        // 单选
        mGetContentLauncher = mFragment.registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    List<Uri> uris = new ArrayList<>();
                    uris.add(result);
                    callbackReceiveFile(uris);
                } else {
                    callbackReceiveFile(null);
                }
            }
        });
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (newProgress == 100) {
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.GONE);
            }
        } else {
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(newProgress);
            }
        }

    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (useH5Title()) {
            if (!mFragment.isDetached() && !mFragment.isRemoving()) {
                mFragment.setTitle(title);
            }
        }
    }

    /**
     * 使用H5的标题
     *
     * @return
     */
    protected boolean useH5Title() {
        return true;
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        mFilePathCallback = filePathCallback;
        if (fileChooserParams != null) {
            if (fileChooserParams.isCaptureEnabled()) {
                // 拍照
                takeCapture();
            } else {
                // 文件选择
                chooseFiles(fileChooserParams);
            }
        }
        return true;
    }

    /**
     * 拍照（需要在Manifests添加android.permission.CAMERA权限）
     */
    protected void takeCapture() {
        File file = new File(mFragment.requireContext().getExternalCacheDir(), "capture-" + System.currentTimeMillis() + ".jpg");
        mTakeCaptureUri = FileProvider.getUriForFile(mFragment.requireContext(), mFragment.requireContext().getPackageName() + ".fileprovider", file);
        mTakePictureLauncher.launch(mTakeCaptureUri);
    }

    /**
     * 文件选择
     *
     * @param fileChooserParams
     */
    protected void chooseFiles(FileChooserParams fileChooserParams) {
        String[] acceptTypes = fileChooserParams.getAcceptTypes();
        if (EmptyUtils.isNotEmpty(acceptTypes)) {
            if (fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE) {
                // 多选
                mOpenMultipleDocumentsLauncher.launch(acceptTypes);

            } else {
                // 单选
                mGetContentLauncher.launch(acceptTypes[0]);
            }
        } else {
            callbackReceiveFile(null);
        }
    }

    /**
     * 将拍照或文件、图片选择接口返回给H5
     *
     * @param uris
     */
    protected void callbackReceiveFile(List<Uri> uris) {
        if (mFilePathCallback != null) {
            if (uris == null) {
                mFilePathCallback.onReceiveValue(null);
            } else {
                Uri[] arr = new Uri[uris.size()];
                for (int i = 0; i < uris.size(); i++) {
                    arr[i] = uris.get(i);
                }
                mFilePathCallback.onReceiveValue(arr);
            }
        }
        mFilePathCallback = null;
    }
}
