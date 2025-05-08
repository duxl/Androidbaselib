package com.duxl.baselib.widget;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * androidx.viewpager2.widget.ViewPager2最佳Adapter实现
 *
 * @param <T>
 */
public class SimpleFragmentStateAdapter<T extends Fragment> extends FragmentStateAdapter {

    protected List<T> fragments;

    public SimpleFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, List<T> fragments) {
        super(fragmentActivity);
        this.fragments = fragments;
    }

    public SimpleFragmentStateAdapter(@NonNull Fragment fragment, List<T> fragments) {
        super(fragment);
        this.fragments = fragments;
    }

    public SimpleFragmentStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<T> fragments) {
        super(fragmentManager, lifecycle);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public T createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return (fragments == null) ? 0 : fragments.size();
    }

    /**
     * 重写此方法，可避免同一个ViewPager重复设置adapter出现奇奇怪怪的问题，还有切换黑白模式系统自动创建ViewPager的fragment也需要重写此函数
     *
     * @param position Adapter position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return fragments.get(position).hashCode();
    }

    /**
     * 重写此方法，可避免同一个ViewPager重复设置adapter出现奇奇怪怪的问题，还有切换黑白模式系统自动创建ViewPager的fragment也需要重写此函数
     *
     * @param itemId
     * @return
     */
    @Override
    public boolean containsItem(long itemId) {
        if (fragments != null) {
            for (int i = 0; i < fragments.size(); i++) {
                if (fragments.get(i).hashCode() == itemId) {
                    return true;
                }
            }
        }
        return false;
    }
}
