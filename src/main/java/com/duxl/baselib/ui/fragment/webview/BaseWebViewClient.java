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
        super.onReceivedSslError(view, handler, error);
        // webView加载的不是https的地址可能会报错，加载失败，如果要想正常加载http
        // 需要重写此方法，并屏蔽掉super.onReceivedSslError调用，直接调用handler.proceed()即可

        // google上架审核不允许简单粗暴的直接调用handler.proceed()，详见https://support.google.com/faqs/answer/7071387
        // google的意思可以handler.cancel();，如果要handler.proceed()需要根据不同情况做处理，可参考（https://www.nhooo.com/note/qad7b6.html）

    }

    /*
    根据不同情况做处理参考代码
    public void onReceivedSslError(WebView view,final SslErrorHandler handler,
                                   SslError error) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
        String message = "SSL Certificate error.";
        switch (error.getPrimaryError()) {
            case SslError.SSL_UNTRUSTED:
                message = "The certificate authority is not trusted.";
                break;
            case SslError.SSL_EXPIRED:
                message = "The certificate has expired.";
                break;
            case SslError.SSL_IDMISMATCH:
                message = "The certificate Hostname mismatch.";
                break;
            case SslError.SSL_NOTYETVALID:
                message = "The certificate is not yet valid.";
                break;
            case SslError.SSL_DATE_INVALID:
                message = "The date of the certificate is invalid";
                break;
            case SslError.SSL_INVALID:
            default:
                message = "A generic error occurred";
                break;
        }
        message += " Do you want to continue anyway?";

        builder.setTitle("SSL Certificate Error");
        builder.setMessage(message);

        builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.proceed();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }*/

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
