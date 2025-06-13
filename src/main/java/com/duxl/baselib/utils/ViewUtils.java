package com.duxl.baselib.utils;

import android.os.Build;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

public class ViewUtils {

    /**
     * <pre>
     * 清除tabLayout长按Toast提示
     * ps：
     * 1、如果是在代码中动态添加的tab，需要在添加完tab后调用clearTabToast
     * 2、如果要去掉tab按压的水波纹效果，请按如下设置属性
     * app:tabBackground="@android:color/transparent"
     * app:tabRippleColor="@null"
     * 3、如果自定义tab的布局文件，最好添如下设置属性
     * app:tabMinWidth="0dp"
     *
     * </pre>
     *
     * @param tabLayout
     */
    public static void clearTabToast(TabLayout tabLayout) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                View tabView = tab.view;
                tabView.setContentDescription(null);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tabView.setTooltipText(null);
                }
                tabView.setLongClickable(false);
                tabView.setOnLongClickListener(v -> true);
            }
        }
    }
}
