package com.tzpt.cloudlibrary.widget.menupager;

import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tonyjia on 2018/11/22.
 */
public class CLMenuPagerAdapter extends PagerAdapter {

    private SparseArray<View> mViewSparseArray;

    public CLMenuPagerAdapter(SparseArray<View> viewList) {
        this.mViewSparseArray = viewList;
    }

    @Override
    public int getCount() {
        return mViewSparseArray == null ? 0 : mViewSparseArray.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewSparseArray.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViewSparseArray.get(position);
        container.addView(view);
        return view;
    }
}
