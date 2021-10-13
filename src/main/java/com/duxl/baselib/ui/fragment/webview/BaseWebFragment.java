package com.duxl.baselib.ui.fragment.webview;

import android.os.Build;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.duxl.baselib.R;
import com.duxl.baselib.ui.activity.BaseActivity;
import com.duxl.baselib.ui.fragment.LazyFragment;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.Utils;
import com.duxl.baselib.widget.BaseJSInterface;
import com.duxl.baselib.widget.SimpleOnLoadListener;

import java.text.MessageFormat;

/**
 * 提供通用的WebView
 * create by duxl 2021/5/18
 */
public class BaseWebFragment extends LazyFragment {

    protected WebView mWebView;
    protected ProgressBar mProgressBar;

    protected String mTitle;
    protected String mUrl;
    protected String mContent;

    @Override
    protected void initView(View v) {
        super.initView(v);
        mWebView = v.findViewById(R.id.web_view);
        mProgressBar = v.findViewById(R.id.progress_bar);

        showStateBar();
        showActionBar();
        setOnLoadListener(new SimpleOnLoadListener() {
            @Override
            public void onRefresh() {
                //mWebView.loadUrl("javascript:onAppWebRefresh()");
                mWebView.evaluateJavascript("javascript:onAppWebRefresh()", null);
            }
        });

        initArgs();
        initWebView(mWebView);
        setJavascriptInterface(mWebView);

        if (!EmptyUtils.isEmpty(mTitle)) {
            setTitle(mTitle);
        }

        mWebView.setWebChromeClient(getWebChromeClient());
        mWebView.setWebViewClient(getWebViewClient());

        if (!EmptyUtils.isEmpty(mUrl)) {
            mWebView.loadUrl(mUrl);
        } else {
            mWebView.loadData(mContent, "text/html", "utf-8");
        }

    }

    public WebView getWebView() {
        return mWebView;
    }

    @Override
    public void onDestroyView() {
        if (mWebView != null) {
            mWebView.removeAllViews();
            mWebView.destroy();
        }
        super.onDestroyView();
    }

    @Override
    protected boolean getDisableContentWhenRefresh() {
        return false;
    }

    @Override
    protected void onLazyHiddenChanged(boolean isVisible, boolean isFirstVisible) {
        String js = "javascript:onAppWebHiddenChanged(%isVisible)"
                .replace("%isVisible", String.valueOf(isVisible));
        mWebView.evaluateJavascript(js, null);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_web;
    }

    @Override
    protected void onClickActionBack(View v) {
        mWebView.evaluateJavascript("javascript:onAppWebBackClick()", result -> {
            if (!"true".equals(result)) {
                super.onClickActionBack(v);
            }
        });
    }

    @Override
    protected void onClickActionIvRight(View v) {
        //mWebView.loadUrl("javascript:onAppWebRightImageClick()");
        mWebView.evaluateJavascript("javascript:onAppWebRightImageClick()", null);
    }

    @Override
    protected void onClickActionTvRight(View v) {
        //mWebView.loadUrl("javascript:onAppWebRightTextClick()");
        mWebView.evaluateJavascript("javascript:onAppWebRightTextClick()", null);
    }

    protected void initArgs() {
        mTitle = getArguments().getString("title");
        mUrl = getArguments().getString("url");
        mContent = getArguments().getString("content");
    }

    /**
     * 初始化webView
     *
     * @param webView
     */
    protected WebSettings initWebView(WebView webView) {
        WebSettings webSettings = BaseWebFragment.initCommonSettings(webView);
        webSettings.setUserAgentString(getUserAgentString(webSettings));
        return webSettings;
    }

    /**
     * webView通用基本设置
     *
     * @param webView
     */
    public static WebSettings initCommonSettings(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);

        // 支持缩放（false关闭）
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        // 设置WebView是否使用预览模式加载界面。(神州专车登录无法跳转，会出现白板页面)
//        webSettings.setLoadWithOverviewMode(true);

        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //支持通过JS打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //支持自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        //设置编码格式
        webSettings.setDefaultTextEncodingName("utf-8");

        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);

        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
//        String cacheDirPath = getContext().getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
//        webSettings.setDatabasePath(cacheDirPath);
//        webSettings.setAppCachePath(cacheDirPath);
//        webSettings.setAppCacheEnabled(false);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setBlockNetworkImage(false);
        return webSettings;
    }

    /**
     * 重写此方法，自定义UserAgent
     *
     * @param webSettings
     * @return
     */
    protected String getUserAgentString(WebSettings webSettings) {
        String userAgent = webSettings.getUserAgentString();
        userAgent += MessageFormat.format(" AppAndroid/{0}/{1}", Utils.getVersionCode(), Utils.getVersionName());
        return userAgent;
    }

    /**
     * 重写此方法，自定义JavascriptInterface
     *
     * @param webView
     */
    protected void setJavascriptInterface(WebView webView) {
        webView.addJavascriptInterface(new BaseJSInterface() {
            @Override
            protected BaseActivity getWebActivity() {
                return (BaseActivity) getActivity();
            }

            @Override
            protected BaseWebFragment getWebFragment() {
                return BaseWebFragment.this;
            }

            @Override
            protected void loadImage(ImageView view, String url) {

            }
        }, "AppAndroid");
    }

    /**
     * 重写此方法，自定义WebChromeClient
     *
     * @return
     */
    protected WebChromeClient getWebChromeClient() {
        return new BaseWebChromeClient(this, mProgressBar) {
            @Override
            protected boolean useH5Title() {
                return EmptyUtils.isEmpty(mTitle);
            }
        };
    }

    /**
     * 重写此方法，自定义WebViewClient
     *
     * @return
     */
    protected WebViewClient getWebViewClient() {
        return new BaseWebViewClient(this, mProgressBar);
    }
}
