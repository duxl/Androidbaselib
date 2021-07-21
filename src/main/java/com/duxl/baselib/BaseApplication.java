package com.duxl.baselib;

import android.app.Application;

import com.duxl.baselib.download.DownLoadManager;
import com.duxl.baselib.http.GlobalHttpConfig;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.Utils;

/**
 * BaseApplication
 * create by duxl 2020/8/15
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.setApp(this);
        DownLoadManager.getInstance();

        ActivityLifecycleCallbacks lifecycleCallbacks = getActivityLifecycleCallbacks();
        if (EmptyUtils.isNotNull(lifecycleCallbacks)) {
            registerActivityLifecycleCallbacks(lifecycleCallbacks);
        }
    }

    /**
     * 获取全局的http配置参数
     *
     * @return
     */
    public GlobalHttpConfig getGlobalHttpConfig() {
        return null;
    }

    /**
     * Activity生命周期回调
     *
     * @return
     */
    public BaseActivityLifecycleCallbacks getActivityLifecycleCallbacks() {
        return new BaseActivityLifecycleCallbacks();
    }
}
