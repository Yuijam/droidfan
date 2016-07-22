package com.arenas.droidfan.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Arenas on 2016/6/23.
 */
public class TabFragmentAdapter extends FragmentPagerAdapter {

    private List<String> tabTitles;
    private List<Fragment> fragments;

    public TabFragmentAdapter(FragmentManager fm , List<String> tabTitles , List<Fragment> fragmentList) {
        super(fm);
        this.tabTitles = tabTitles;
        fragments = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }

}
