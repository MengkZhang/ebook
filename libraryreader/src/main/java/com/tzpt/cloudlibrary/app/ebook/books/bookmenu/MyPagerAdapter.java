package com.tzpt.cloudlibrary.app.ebook.books.bookmenu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * fragment 适配器
 *
 * @author jiazhiqiang121
 */
public class MyPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments;
    private FragmentManager fm;
    private String[] TITLES;

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    public MyPagerAdapter(FragmentManager fm,
                          List<Fragment> fragmentList, String[] TITLES) {
        super(fm);
        this.fm = fm;
        this.fragments = fragmentList;
        this.TITLES = TITLES;
    }

    /**
     * 设置title
     * @param TITLES
     */
    public void setItemTitle(String[] TITLES) {
        this.TITLES = TITLES;
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
        if (null != TITLES) {
            return TITLES[position];
        } else {
            return "";
        }

    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setFragments(ArrayList<Fragment> fragments) {
        if (this.fragments != null) {
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : this.fragments) {
                ft.remove(f);
            }
            ft.commit();
            ft = null;
            fm.executePendingTransactions();
        }
        this.fragments = fragments;
        notifyDataSetChanged();
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
//	public static String makeFragmentName(int viewId, int index) {
//	    return "android:switcher:" + viewId + ":" + index;
//	  }
}

