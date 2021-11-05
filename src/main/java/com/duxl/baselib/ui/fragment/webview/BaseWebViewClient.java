package com.duxl.baselib.ui.fragment.webview;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.core.net.UriCompat;

import com.duxl.baselib.ui.activity.BaseActivity;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.Utils;
import com.duxl.baselib.widget.BaseJSInterface;

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
        if (mWebFragment != null && "newBrowser".equals(uri.getQueryParameter("appTarget"))) {
            mWebFragment.getActivity().runOnUiThread(() -> {
                Intent intent = new Intent(mWebFragment.getContext(), mWebFragment.getActivity().getClass());
                intent.putExtra("title", uri.getQueryParameter("appTitle"));
                intent.putExtra("url", url);

                String requestCode = uri.getQueryParameter("requestCode");
                if (EmptyUtils.isEmpty(requestCode)) {
                    mWebFragment.startActivity(intent);
                } else {
                    try {
                        mWebFragment.startActivityForResult(intent, Integer.parseInt(requestCode));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            view.loadUrl(url);
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
}
