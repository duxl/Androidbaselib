package com.duxl.baselib.utils;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

public class ViewUtils {

    /**
     * <pre>
     * 清除tabLayout长按Toast提示
     * ps：
     * 1、如果是在代码中动态添加的tab，需要在添加完tab后调用clearTabToast
     *
     * 2、如果要去掉tab按压的水波纹效果，请按如下设置属性
     * app:tabBackground="@android:color/transparent"
     * app:tabRippleColor="@null"
     *
     * 3、如果自定义tab的布局文件，最好添如下设置属性
     * app:tabMinWidth="0dp"
     *
     * 4、设置自定义下划线
     * app:tabIndicator="@drawable/my_tab_indicator"
     *
     * 5、自定义tab布局my_tab_layout中TextView的id需要设置为android:id="@android:id/text1"
     * <com.google.android.material.tabs.TabItem
     *      android:layout_width="wrap_content"
     *      android:layout_height="wrap_content"
     *      android:layout="@layout/my_tab_layout"
     *      android:text="@string/home" />
     *
     * 6、未选中和选中时的样式设置
     * app:tabTextAppearance="@style/myTabLayoutTextAppearance"
     * app:tabTextColor="#A0A1A4"
     *
     * app:tabSelectedTextAppearance="@style/myTabLayoutTextAppearanceSelected"
     * app:tabSelectedTextColor="#FFFFFF"
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

    /**
     * 设置AppBarLayout 折叠/展开的位置(不带动画)<br/>
     * 如果要设置完全展开或折叠，直接使用appBarLayout.setExpanded(boolean expanded, boolean animate);<br/>
     * 如果要设置完全展开和折叠的中间值，使用setAppBarLayoutScrollOffset方法非常有用<br/>
     *
     * @param appBarLayout appBarLayout
     * @param offset       垂直偏移量,取值范围：[-totalScrollRange, 0]，<br/>
     *                     如果设置为-totalScrollRange，表示完全折叠起来，<br/>
     *                     如果设置为0表示完全展开为初始状态
     * @return 是否实际发生偏移（true 表示位置变化）
     */
    public static boolean setAppBarLayoutScrollOffset(AppBarLayout appBarLayout, int offset) {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) layoutParams.getBehavior();
        if (behavior != null) {
            return behavior.setTopAndBottomOffset(offset);
        }
        return false;
    }

    /**
     * 代码动态设置AppBarLayout的滚动flags
     *
     * @param v     控件
     * @param flags 滚动标识，设置为AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL标识不能滚动
     */
    public static void setAppBarLayoutScrollFlags(View v, @AppBarLayout.LayoutParams.ScrollFlags int flags) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        if (layoutParams instanceof AppBarLayout.LayoutParams) {
            AppBarLayout.LayoutParams appBarLayoutParams = (AppBarLayout.LayoutParams) layoutParams;
            appBarLayoutParams.setScrollFlags(flags);
        }
    }
}
