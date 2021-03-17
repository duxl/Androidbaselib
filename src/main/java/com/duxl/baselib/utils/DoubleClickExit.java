package com.duxl.baselib.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

import com.duxl.baselib.R;

/**
 * 双击退出应用确认提示
 *
 * @author duxl（20150916）
 */
public class DoubleClickExit implements Runnable {

    private Activity mActivity;
    private Handler handler = new Handler();
    private boolean exit;

    public DoubleClickExit(Activity activity) {
        mActivity = activity;
    }

    /**
     * 退出app
     *
     * @param toDesktop      true回到桌面，false为关闭当前Activity
     * @param promptRunnable 提示回调函数，可以在这个回调里面自定义提示效果；默认使用Toast提示，提示文案可在string中修改
     */
    public void exit(boolean toDesktop, Runnable promptRunnable) {
        if (exit) {
            if (mActivity != null) {
                if (toDesktop) {
                    Intent setIntent = new Intent(Intent.ACTION_MAIN);
                    setIntent.addCategory(Intent.CATEGORY_HOME);
                    setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivity.startActivity(setIntent);
                } else {
                    mActivity.finish();
                }
            }

        } else {
            exit = true;
            if (EmptyUtils.isNull(promptRunnable)) {
                ToastUtils.show(R.string.exit_app_confirm_msg);
            } else {
                promptRunnable.run();
            }
            handler.postDelayed(this, 2000);
        }
    }

    @Override
    public void run() {
        exit = false;
    }
}
