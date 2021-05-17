package com.duxl.baselib.utils;

import android.app.Activity;

import androidx.annotation.Nullable;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * =========================================
 * 统一管理activity生命周期 以及其他操作等~
 * =========================================
 */
public class AppManager {

    private static volatile AppManager sAppManager;
    /**
     * 管理所有存活的 Activity, 容器中的顺序仅仅是 Activity 的创建顺序, 并不能保证和 Activity 任务栈顺序一致
     */
    private List<SoftReference<Activity>> mActivityList;

    /**
     * 当前在前台的 Activity
     */
    private SoftReference<Activity> mCurrentActivity;

    private AppManager() {
    }

    public static AppManager getAppManager() {
        if (sAppManager == null) {
            synchronized (AppManager.class) {
                if (sAppManager == null) {
                    sAppManager = new AppManager();
                }
            }
        }
        return sAppManager;
    }

    public AppManager init() {
        return sAppManager;
    }

    /**
     * 设置当前可见的activity
     */
    public void setCurrentActivity(Activity currentActivity) {
        this.mCurrentActivity = new SoftReference<>(currentActivity);
    }

    /**
     * 获取当前可见的activity
     */
    @Nullable
    public Activity getCurrentActivity() {
        return mCurrentActivity == null ? null : mCurrentActivity.get();
    }


    /**
     * 获取栈顶的activity
     */
    public Activity getTopActivity() {
        if (mActivityList == null) {
            return null;
        }
        return mActivityList.size() > 0 ? mActivityList.get(mActivityList.size() - 1).get() : null;
    }

    /**
     * 返回一个存储所有未销毁的 {@link Activity} 的集合
     */
    public List<Activity> getActivityList() {
        List<Activity> result = new ArrayList<>();
        if (mActivityList != null) {
            for (SoftReference<Activity> activityReference : mActivityList) {
                if (activityReference.get() != null) {
                    result.add(activityReference.get());
                }
            }
        }
        return result;
    }

    /**
     * 添加 {@link Activity} 到集合
     */
    public void addActivity(Activity activity) {
        synchronized (AppManager.class) {
            if (mActivityList == null) {
                mActivityList = new ArrayList<>();
            } else {
                for (SoftReference<Activity> reference : mActivityList) {
                    if (reference.get() == activity) {
                        return;
                    }
                }
            }
            mActivityList.add(new SoftReference<>(activity));
        }
    }

    /**
     * 删除集合里的指定的 {@link Activity} 实例
     *
     * @param {@link Activity}
     */
    public void removeActivity(Activity activity) {
        if (mActivityList == null) {
            return;
        }
        synchronized (AppManager.class) {
            synchronized (AppManager.class) {
                Iterator<SoftReference<Activity>> iterator = mActivityList.iterator();
                while (iterator.hasNext()) {
                    Activity next = iterator.next().get();
                    if (next == activity) {
                        iterator.remove();
                        return;
                    }
                }
            }
        }
    }

    /**
     * 删除集合里的指定位置的 {@link Activity}
     *
     * @param position
     */
    public Activity removeActivity(int position) {
        if (mActivityList == null) {
            return null;
        }
        synchronized (AppManager.class) {
            if (position > 0 && position < mActivityList.size()) {
                return mActivityList.remove(position).get();
            }
        }
        return null;
    }

    /**
     * 关闭指定的 {@link Activity} Class 的所有的实例
     *
     * @param activityClass
     */
    public void finishActivity(Class<?> activityClass) {
        if (mActivityList == null) {
            return;
        }
        synchronized (AppManager.class) {
            Iterator<SoftReference<Activity>> iterator = mActivityList.iterator();
            while (iterator.hasNext()) {
                Activity next = iterator.next().get();
                if (next != null && next.getClass().equals(activityClass)) {
                    iterator.remove();
                    next.finish();
                }
            }
        }
    }

    /**
     * 关闭指定的 {@link Activity} 的实例
     *
     * @param activity
     */
    public void finishActivity(Activity activity) {
        if (mActivityList == null) {
            return;
        }
        synchronized (AppManager.class) {
            Iterator<SoftReference<Activity>> iterator = mActivityList.iterator();
            while (iterator.hasNext()) {
                Activity next = iterator.next().get();
                if (next != null && next == activity) {
                    iterator.remove();
                    next.finish();
                    return;
                }
            }
        }
    }

    /**
     * 指定的 {@link Activity} 实例是否存活
     *
     * @param {@link Activity}
     * @return
     */
    public boolean activityInstanceIsLive(Activity activity) {
        if (mActivityList != null) {
            for (SoftReference<Activity> reference : mActivityList) {
                if (reference.get() == activity) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 指定的 {@link Activity} Class 是否存活(同一个 {@link Activity} Class 可能有多个实例)
     *
     * @param activityClass
     * @return
     */
    public boolean activityClassIsLive(Class<?> activityClass) {
        if (mActivityList == null) {
            return false;
        }

        for (SoftReference<Activity> reference : mActivityList) {
            if (reference.get() != null && reference.get().getClass().equals(activityClass)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取指定 {@link Activity} Class 的实例,没有则返回 null(同一个 {@link Activity} Class 可能有多个实例)
     *
     * @param activityClass
     * @return
     */
    public List<Activity> findActivity(Class<?> activityClass) {
        List<Activity> result = null;
        if (mActivityList != null) {
            for (SoftReference<Activity> reference : mActivityList) {
                if (reference.get() != null && reference.get().getClass().equals(activityClass)) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(reference.get());
                }
            }
        }
        return result;
    }

    /**
     * 关闭所有 {@link Activity}
     */
    public void killAll() {
        if (mActivityList == null) {
            return;
        }

        synchronized (AppManager.class) {
            Iterator<SoftReference<Activity>> iterator = mActivityList.iterator();
            while (iterator.hasNext()) {
                Activity next = iterator.next().get();
                if (next != null) {
                    next.finish();
                }
                iterator.remove();
            }
        }
    }

    /**
     * 关闭所有 {@link Activity},排除指定的 {@link Activity}
     *
     * @param excludeActivityClasses Activity Class
     */
    public void killAll(Class<?>... excludeActivityClasses) {
        if (mActivityList == null) {
            return;
        }

        List<Class<?>> excludeList = Arrays.asList(excludeActivityClasses);
        synchronized (AppManager.class) {
            Iterator<SoftReference<Activity>> iterator = mActivityList.iterator();
            while (iterator.hasNext()) {
                Activity next = iterator.next().get();
                if (next != null) {
                    if (excludeList.contains(next.getClass())) {
                        continue;
                    }
                    next.finish();
                }
                iterator.remove();
            }
        }
    }

    /**
     * 关闭所有 {@link Activity},排除指定的 {@link Activity}
     *
     * @param excludeActivityName {@link Activity} 的完整全路径
     */
    public void killAll(String... excludeActivityName) {
        if (mActivityList == null) {
            return;
        }

        List<String> excludeList = Arrays.asList(excludeActivityName);
        synchronized (AppManager.class) {
            Iterator<SoftReference<Activity>> iterator = mActivityList.iterator();
            while (iterator.hasNext()) {
                Activity next = iterator.next().get();

                if (next != null) {
                    if (excludeList.contains(next.getClass().getName())) {
                        continue;
                    }
                    next.finish();
                }
                iterator.remove();
            }
        }
    }

    /**
     * 退出应用程序
     */
    public void appExit() {
        try {
            killAll();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
