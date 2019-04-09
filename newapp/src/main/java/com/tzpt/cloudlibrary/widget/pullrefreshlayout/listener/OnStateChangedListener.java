package com.tzpt.cloudlibrary.widget.pullrefreshlayout.listener;


import android.support.annotation.NonNull;

import com.tzpt.cloudlibrary.widget.pullrefreshlayout.api.RefreshLayout;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.constant.RefreshState;

/**
 * 刷新状态改变监听器
 */

public interface OnStateChangedListener {
    /**
     * 状态改变事件 {@link RefreshState}
     * @param refreshLayout RefreshLayout
     * @param oldState 改变之前的状态
     * @param newState 改变之后的状态
     */
    void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState);
}
