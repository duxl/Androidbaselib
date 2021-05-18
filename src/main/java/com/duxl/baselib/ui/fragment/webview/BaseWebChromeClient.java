package com.duxl.baselib.ui.fragment.webview;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.duxl.baselib.ui.fragment.BaseFragment;

/**
 * create by duxl 2021/5/18
 */
public class BaseWebChromeClient extends WebChromeClient {

    private BaseFragment mFragment;
    private ProgressBar mProgressBar;

    public BaseWebChromeClient(BaseFragment fragment, ProgressBar progressBar) {
        this.mFragment = fragment;
        this.mProgressBar = progressBar;
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
     * @return
     */
    protected boolean useH5Title() {
        return true;
    }
}
