package com.duxl.baselib;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.duxl.baselib.utils.AppManager;

/**
 * ActivityLifecycleCallbacks
 * create by duxl 2021/3/14
 */
public class BaseActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        AppManager.getAppManager().addActivity(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        AppManager.getAppManager().setCurrentActivity(activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        if (AppManager.getAppManager().getCurrentActivity() == activity) {
            AppManager.getAppManager().setCurrentActivity(null);
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        AppManager.getAppManager().removeActivity(activity);
    }
}
