package com.duxl.baselib.widget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;

import com.duxl.baselib.R;
import com.duxl.baselib.ui.activity.BaseActivity;
import com.duxl.baselib.ui.fragment.webview.BaseWebFragment;
import com.duxl.baselib.utils.DisplayUtil;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.ToastUtils;
import com.duxl.baselib.widget.dialog.ProgressDialog;

/**
 * <pre>
 * App提供给H5调用的通用api接口，使用本接口需要将WebView放在Fragment中，
 * 隐藏WebActivity的状态栏和标题栏，默认显示WebFragment的状态栏和标题栏。
 *
 * 在WebFragment的标题栏右图标被点击时调用如下代码
 * mWebView.loadUrl("javascript:onAppWebRightImageClick()")
 *
 * 在WebFragment的标题栏右文字被点击时调用如下代码
 * mWebView.loadUrl("javascript:onAppWebRightTextClick()");
 *
 * 在WebFragment下来刷新时调用如下代码
 * mWebView.loadUrl("javascript:onAppWebRefresh()");
 *
 * 使用示例见：BaseWebFragment.java和DemoCallWebWebApi.html
 * <pre/>
 * create by duxl 2021/5/18
 */
public abstract class BaseJSInterface {

    private ProgressDialog mProgressDialog;
    private Handler mHandler;

    /**
     * WebView的容器Activity
     *
     * @return
     */
    protected abstract <T extends BaseActivity> T getWebActivity();

    /**
     * WebView的容器Fragment
     *
     * @return
     */
    protected abstract <T extends BaseWebFragment> T getWebFragment();

    /**
     * 落网图片加载
     *
     * @param view
     * @param url
     */
    protected abstract void loadImage(ImageView view, String url);

    /**
     * 打开新的浏览器窗口
     *
     * @param title 新窗口显示的标题，如果标题为空，将显示网页的标题
     * @param url   要打开链接地址
     */
    @JavascriptInterface
    public void openNewBrowser(String title, String url) {
        runOnUiThread(() -> {
            Intent intent = new Intent(getWebActivity(), getWebActivity().getClass());
            intent.putExtra("title", title);
            intent.putExtra("url", url);
            getWebFragment().startActivity(intent);
        });
    }

    /**
     * 打开新的浏览器窗口
     *
     * @param title       新窗口显示的标题，如果标题为空，将显示网页的标题
     * @param url         要打开链接地址
     * @param requestCode 请求code
     */
    @JavascriptInterface
    public void openNewBrowser(String title, String url, int requestCode) {
        runOnUiThread(() -> {
            Intent intent = new Intent(getWebActivity(), getWebActivity().getClass());
            intent.putExtra("title", title);
            intent.putExtra("url", url);
            getWebFragment().startActivityForResult(intent, requestCode);
        });
    }

    /**
     * 设置返回数据：上一级Web页面打开新窗口如果传了requestCode，当前Web页面可以调用此方法设置返回数据后closeBrowser当前页面
     *
     * @param data
     */
    @JavascriptInterface
    public void setResultData(String data) {
        Intent result = new Intent();
        result.putExtra("h5ResultData", data);
        getWebActivity().setResult(Activity.RESULT_OK, result);
    }

    /**
     * 关闭浏览器窗口（关闭AppNative页面）
     */
    @JavascriptInterface
    public void closeBrowser() {
        runOnUiThread(() -> getWebActivity().finish());
    }

    /**
     * 返回历史记录，如果没有历史记录，关闭当前浏览器窗口
     */
    @JavascriptInterface
    public void goBack() {
        runOnUiThread(() -> {
            if (EmptyUtils.isNotNull(getWebFragment().getWebView())) {
                if (getWebFragment().getWebView().canGoBack()) {
                    getWebFragment().getWebView().goBack();
                    return;
                }
            }
            getWebActivity().finish();
        });
    }

    /**
     * 显示吐丝消息
     *
     * @param msg
     */
    @JavascriptInterface
    public void showToast(String msg) {
        runOnUiThread(() -> ToastUtils.show(msg));
    }


    /**
     * 显示进度框
     *
     * @param msg 进度消息
     */
    @JavascriptInterface
    public void showProgressDialog(String msg) {
        showProgressDialog(msg, false, false);
    }

    /**
     * 显示进度框
     *
     * @param msg                进度消息
     * @param cancelTouchOutside 点击加载框外是否取消弹框
     * @param cancelable         点击手机的返回键是否可取消弹框
     */
    @JavascriptInterface
    public void showProgressDialog(String msg, boolean cancelTouchOutside, boolean cancelable) {
        postHandler(() -> {
            if (EmptyUtils.isNull(mProgressDialog)) {
                mProgressDialog = new ProgressDialog(getWebFragment().getContext());
            }
            mProgressDialog.setCancelTouchOutside(cancelTouchOutside);
            mProgressDialog.setCancelable(cancelable);
            mProgressDialog.show(msg);
        }, 0);
    }

    /**
     * 取消显示（隐藏）进度框
     */
    @JavascriptInterface
    public void hideProgressDialog() {
        postHandler(() -> {
            // 这里避免mProgressDialog还没有new出来，所以得延迟一点时间再执行
            if (EmptyUtils.isNotNull(mProgressDialog)) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }, 50);
    }

    /**
     * 获取手机状态栏高度
     *
     * @return
     */
    @JavascriptInterface
    public int getStateBarHeight() {
        return DisplayUtil.getBarHeight(getWebActivity());
    }

    /**
     * 设置状态栏图标字体颜色
     *
     * @param dark 是否是深色
     */
    @JavascriptInterface
    public void setStateBarMode(boolean dark) {
        runOnUiThread(() -> {
            if (dark) {
                getWebActivity().setStateBarDarkMode();
            } else {
                getWebActivity().setStateBarLightMode();
            }
        });
    }

    /**
     * 设置状态栏显示与隐藏
     *
     * @param visible 是否显示状态栏：true显示、false隐藏
     */
    @JavascriptInterface
    public void setStateBarVisible(boolean visible) {
        runOnUiThread(() -> {
            if (visible) {
                getWebFragment().showStateBar();
            } else {
                getWebFragment().hideStateBar();
            }
        });
    }

    /**
     * 设置状态栏背景颜色
     *
     * @param color
     */
    @JavascriptInterface
    public void setStateBarColor(String color) {
        runOnUiThread(() -> {
            try {
                getWebFragment().setStateBarColor(Color.parseColor(color));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 设置标题栏显示与隐藏
     *
     * @param visible 是否显示标题栏：true显示、false隐藏
     */
    @JavascriptInterface
    public void setTitleBarVisible(boolean visible) {
        runOnUiThread(() -> {
            if (visible) {
                getWebFragment().showActionBar();
            } else {
                getWebFragment().hideActionBar();
            }
        });
    }

    /**
     * 设置标题栏颜色
     *
     * @param color
     */
    @JavascriptInterface
    public void setTitleBarColor(String color) {
        runOnUiThread(() -> {
            try {
                getWebFragment().setActionBarColor(Color.parseColor(color));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 设置标题栏是否悬浮
     *
     * @param isFloat
     */
    @JavascriptInterface
    public void setTitleBarModel(boolean isFloat) {
        runOnUiThread(() -> getWebFragment().setActionBarFloat(isFloat));
    }

    /**
     * 设置标题文字
     *
     * @param title
     */
    @JavascriptInterface
    public void setTitleText(String title) {
        runOnUiThread(() -> getWebFragment().setTitle(title));
    }

    /**
     * 设置返回图标url地址
     *
     * @param url
     */
    @JavascriptInterface
    public void setBackImageUrl(String url) {
        runOnUiThread(() -> {
            ImageView ivBack = getWebFragment().getActionBarView().findViewById(R.id.iv_action_back);
            if (EmptyUtils.isEmpty(url)) {
                ivBack.setVisibility(View.INVISIBLE);
            } else {
                loadImage(ivBack, url);
            }
        });
    }

    /**
     * 设置右边文字按钮
     *
     * @param text
     */
    @JavascriptInterface
    public void setRightText(String text) {
        runOnUiThread(() -> {
            getWebFragment().getActionBarView().setRightText(text);
        });
    }

    /**
     * 设置右边图标按钮
     *
     * @param url
     */
    @JavascriptInterface
    public void setRightImage(String url) {
        runOnUiThread(() -> {
            ImageView ivMenu = getWebFragment().getActionBarView().findViewById(R.id.iv_action_right);
            if (EmptyUtils.isEmpty(url)) {
                ivMenu.setVisibility(View.GONE);
            } else {
                ivMenu.setVisibility(View.VISIBLE);
                ivMenu.setImageResource(0);
                loadImage(ivMenu, url);
            }
        });
    }

    /**
     * 是否启用页面刷新
     *
     * @return
     */
    @JavascriptInterface
    public void setEnableRefresh(boolean isEnableRefresh) {
        runOnUiThread(() -> getWebFragment().setEnableRefresh(isEnableRefresh));

    }

    /**
     * 停止(完成)刷新
     */
    @JavascriptInterface
    public void stopRefresh() {
        runOnUiThread(() -> getWebFragment().finishRefresh());
    }

    // 在UI线程中执行任务
    protected void runOnUiThread(Runnable runnable) {
        if (getWebActivity() != null) {
            getWebActivity().runOnUiThread(() -> {
                if (getWebActivity() != null) {
                    runnable.run();
                }
            });
        }
    }

    protected void postHandler(Runnable runnable, long delayMillis) {
        if (EmptyUtils.isNull(mHandler)) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.postDelayed(runnable, delayMillis);
    }
}
