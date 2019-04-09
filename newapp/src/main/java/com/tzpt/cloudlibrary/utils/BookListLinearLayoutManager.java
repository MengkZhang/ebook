package com.tzpt.cloudlibrary.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by ZhiqiangJia on 2017-08-11.
 */

public class BookListLinearLayoutManager extends LinearLayoutManager {

    private boolean IsScrollEnabled = true;

    public BookListLinearLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.IsScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return IsScrollEnabled && super.canScrollVertically();
    }
}
