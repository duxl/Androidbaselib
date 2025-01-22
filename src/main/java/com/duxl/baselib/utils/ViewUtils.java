package com.duxl.baselib.utils;

import android.os.Build;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

public class ViewUtils {

    /**
     * 清除tabLayout长按Toast提示
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
