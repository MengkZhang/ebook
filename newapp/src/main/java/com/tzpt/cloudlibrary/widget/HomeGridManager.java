package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * 设置recyclerView是否可以滑动
 * Created by tonyjia on 2018/10/17.
 */
public class HomeGridManager extends GridLayoutManager {

    public HomeGridManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public HomeGridManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public HomeGridManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }


    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }
}
