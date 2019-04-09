package com.tzpt.cloudlibrary.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * 控制RecyclerView是否可滑动
 * Created by ZhiqiangJia on 2017-10-12.
 */
public class ScrollLinearLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;

    public ScrollLinearLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}
