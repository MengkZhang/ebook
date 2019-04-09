package com.tzpt.cloudlibrary.widget.pullrefreshlayout.listener;

import android.support.annotation.NonNull;

import com.tzpt.cloudlibrary.widget.pullrefreshlayout.api.RefreshLayout;


/**
 * 加载更多监听器
 */

public interface OnLoadMoreListener {
    void onLoadMore(@NonNull RefreshLayout refreshLayout);
}
