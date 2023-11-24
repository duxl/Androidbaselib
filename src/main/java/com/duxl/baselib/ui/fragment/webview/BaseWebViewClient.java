package com.duxl.baselib.ui.fragment.webview;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.duxl.baselib.utils.EmptyUtils;

/**
 * create by duxl 2021/5/18
 */
public class BaseWebViewClient<T extends BaseWebFragment> extends WebViewClient {

    private T mWebFragment;
    private ProgressBar mProgressBar;

    public BaseWebViewClient(T webFragment, ProgressBar progressBar) {
        this.mWebFragment = webFragment;
        this.mProgressBar = progressBar;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Uri uri = Uri.parse(url);
        if (mWebFragment != null) {
            if ("newBrowser".equals(uri.getQueryParameter("appTarget"))) { // app内打开新窗口
                mWebFragment.getActivity().runOnUiThread(() -> {
                    Intent intent = new Intent(mWebFragment.getContext(), mWebFragment.getActivity().getClass());
                    intent.putExtra("title", uri.getQueryParameter("appTitle"));
                    intent.putExtra("url", url);

                    String requestCode = uri.getQueryParameter("requestCode");

                    try {
                        if (EmptyUtils.isEmpty(requestCode)) {
                            mWebFragment.startActivity(intent);
                        } else {
                            mWebFragment.startActivityForResult(intent, Integer.parseInt(requestCode));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else if ("outerBrowser".equals(uri.getQueryParameter("appTarget"))) { // 外部浏览器打开新窗口
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    mWebFragment.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                view.loadUrl(url);
            }
        }
        return true;
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

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        doError(view, request);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        doError(view, request);
    }

    /**
     * 加载出错处理
     *
     * @param view
     * @param request
     */
    protected void doError(WebView view, WebResourceRequest request) {
        if (useBlackOnError()) {
            // 避免出现默认（网页无法打开）的错误界面
            view.loadUrl("about:blank");
        }
    }

    /**
     * 当加载页面出错是，是否启用about:black页面，避免显示网页无法打开的错误页
     * ps：开发阶段最好设置成false，不然url加载失败的情况不知为什么
     *
     * @return
     */
    protected boolean useBlackOnError() {
        return false;
    }
}
