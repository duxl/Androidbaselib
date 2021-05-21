package com.duxl.baselib.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.duxl.baselib.utils.Utils;

import java.util.List;

/**
 * ViewPagerFragmentAdapter
 * Created by duxl on 2021/5/21.
 */

public class ViewPagerFragmentAdapter<T extends Fragment> extends FragmentPagerAdapter {

    protected List<T> mFragments;
    protected List<CharSequence> mTitles;

    public ViewPagerFragmentAdapter(FragmentManager fm, T[] fragments) {
        this(fm, Utils.asList(fragments));
    }

    public ViewPagerFragmentAdapter(FragmentManager fm, List<T> fragments) {
        this(fm, fragments, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public ViewPagerFragmentAdapter(FragmentManager fm, List<T> fragments, int behavior) {
        super(fm, behavior);
        this.mFragments = fragments;
    }

    public void setTitles(CharSequence[] titles) {
        setTitles(Utils.asList(titles));
    }

    public void setTitles(List<CharSequence> titles) {
        this.mTitles = titles;
    }

    /**
     * 返回需要展示的fragment
     *
     * @param position
     * @return
     */
    @Override
    public T getItem(int position) {
        return mFragments.get(position);
    }


    /**
     * 返回需要展示的fangment数量
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