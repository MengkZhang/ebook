package com.tzpt.cloudlibrary.widget.recyclerview.adapter;

import android.view.View;

/**
 * Created by Administrator on 2017/6/8.
 */

public interface EventDelegate {
    void addData(int length);

    void clear();

    void stopLoadMore();

    void pauseLoadMore();

    void resumeLoadMore();

    void setMore(View view, OnLoadMoreListener listener);

    void setNoMore(View view);

    void setErrorMore(View view);

    DefaultEventDelegate.EventFooter getEventFooter();
}
