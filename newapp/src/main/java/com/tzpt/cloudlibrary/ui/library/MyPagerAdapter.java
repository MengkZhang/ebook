package com.tzpt.cloudlibrary.ui.library;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.bean.TabMenuBean;

import java.util.ArrayList;
import java.util.List;

/**
 * fragment 适配器
 *
 * @author jiazhiqiang
 */
public class MyPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments;
    private List<TabMenuBean> mTabMenuList;

    public MyPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<TabMenuBean> tabMenuList) {
        super(fm);
        this.fragments = fragmentList;
        this.mTabMenuList = tabMenuList;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabMenuList.get(position).getTitle();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        Object obj = super.instantiateItem(container, position);
        return obj;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}

