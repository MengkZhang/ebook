package com.tzpt.cloudlibrary.widget.pullrefreshlayout.listener;

import android.support.annotation.NonNull;

import com.tzpt.cloudlibrary.widget.pullrefreshlayout.api.RefreshLayout;


/**
 * 刷新监听器
 */

public interface OnRefreshListener {
    void onRefresh(@NonNull RefreshLayout refreshLayout);
}
