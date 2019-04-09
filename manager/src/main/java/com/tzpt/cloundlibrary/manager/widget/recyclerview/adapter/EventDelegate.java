package com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter;

import android.view.View;

/**
 * Created by Administrator on 2017/6/8.
 */

public interface EventDelegate {
    void addData(int length);
    void clear();

    void stopLoadMore();
    void pauseLoadMore();
//    void resumeLoadMore();

    void setCustomMoreView(View moreView, View noMoreView, View errorMoreView, OnLoadMoreListener listener);
    void setNullMoreView(OnLoadMoreListener listener);
}
