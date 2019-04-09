package com.tzpt.cloudlibrary.widget.pullrefreshlayout.impl;

import android.graphics.PointF;
import android.view.View;

import com.tzpt.cloudlibrary.widget.pullrefreshlayout.api.ScrollBoundaryDecider;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.util.ScrollBoundaryUtil;

/**
 * 滚动边界
 */

@SuppressWarnings("WeakerAccess")
public class ScrollBoundaryDeciderAdapter implements ScrollBoundaryDecider {

    //<editor-fold desc="Internal">
    public PointF mActionEvent;
    public ScrollBoundaryDecider boundary;
    public boolean mEnableLoadMoreWhenContentNotFull = true;
    //</editor-fold>

    //<editor-fold desc="ScrollBoundaryDecider">
    @Override
    public boolean canRefresh(View content) {
        if (boundary != null) {
            return boundary.canRefresh(content);
        }
        //mActionEvent == null 时 canRefresh 不会动态递归搜索
        return ScrollBoundaryUtil.canRefresh(content, mActionEvent);
    }

    @Override
    public boolean canLoadMore(View content) {
        if (boundary != null) {
            return boundary.canLoadMore(content);
        }
        //mActionEvent == null 时 canLoadMore 不会动态递归搜索
        return ScrollBoundaryUtil.canLoadMore(content, mActionEvent, mEnableLoadMoreWhenContentNotFull);
    }
    //</editor-fold>
}
