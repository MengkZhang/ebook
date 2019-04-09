package com.tzpt.cloudlibrary.ui.ebook;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2017/10/23.
 */

public class SlideMenuPagerAdapter extends PagerAdapter {
    private List<View> mViewLists;
    private String[] mTitles;

    public SlideMenuPagerAdapter(List<View> list, String[] titles){
        mViewLists = list;
        mTitles = titles;
    }

    @Override
    public int getCount() {
        return mViewLists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewLists.get(position));//添加页卡
        return mViewLists.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewLists.get(position));//删除页卡
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];//页卡标题
    }
}
