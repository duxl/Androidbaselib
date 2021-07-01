package com.duxl.baselib.ui.fragment.webview;

import android.net.Uri;
import android.net.http.SslError;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * create by duxl 2021/5/18
 */
public class BaseWebViewClient extends WebViewClient {

    private ProgressBar mProgressBar;

    public BaseWebViewClient(ProgressBar progressBar) {
        this.mProgressBar = progressBar;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Uri uri = request.getUrl();
        view.loadUrl(uri.toString());
        return false;
    }


    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        //super.onReceivedSslError(view, handler, error);
        handler.proceed();
    }
}
