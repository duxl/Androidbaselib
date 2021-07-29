package com.duxl.baselib.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.duxl.baselib.utils.Utils;

import java.util.List;

/**
 * create by duxl 2021/6/25
 */
public class ViewPagerStateFragmentAdapter<T extends Fragment> extends FragmentStatePagerAdapter {

    protected List<T> mFragments;
    protected List<CharSequence> mTitles;

    public ViewPagerStateFragmentAdapter(FragmentManager fm, T[] fragments) {
        this(fm, Utils.asList(fragments));
    }

    public ViewPagerStateFragmentAdapter(FragmentManager fm, List<T> fragments) {
        this(fm, fragments, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public ViewPagerStateFragmentAdapter(FragmentManager fm, List<T> fragments, int behavior) {
        super(fm, behavior);
        this.mFragments = fragments;
    }

    public void setTitles(CharSequence[] titles) {
        setTitles(Utils.asList(titles));
    }

    public void setTitles(List<CharSequence> titles) {
        this.mTitles = titles;
    }

    @NonNull
    @Override
    public T getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    /**
     * 返回需要展示的fragment数量
     *
     * @return
     */
    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null && position < mTitles.size())
            return mTitles.get(position);
        return super.getPageTitle(position);
    }
}
