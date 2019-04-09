package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * 设置recyclerView是否可以滑动
 * Created by tonyjia on 2018/10/17.
 */
public class HomeListManager extends LinearLayoutManager {

    public HomeListManager(Context context) {
        super(context);
    }

    public HomeListManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public HomeListManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }
}
