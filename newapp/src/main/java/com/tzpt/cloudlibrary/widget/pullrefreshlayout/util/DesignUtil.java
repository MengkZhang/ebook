package com.tzpt.cloudlibrary.widget.pullrefreshlayout.util;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.widget.pullrefreshlayout.api.RefreshKernel;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.api.RefreshLayout;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.listener.CoordinatorLayoutListener;


/**
 * Design 兼容包缺省尝试
 */

public class DesignUtil {

    public static void checkCoordinatorLayout(View content, RefreshKernel kernel, CoordinatorLayoutListener listener) {
        try {//try 不能删除，不然会出现兼容性问题
            if (content instanceof CoordinatorLayout) {
                kernel.getRefreshLayout().setEnableNestedScroll(false);
                wrapperCoordinatorLayout(((ViewGroup) content), kernel.getRefreshLayout(),listener);
            }
        } catch (Throwable ignored) {
        }
    }

    private static void wrapperCoordinatorLayout(ViewGroup layout, final RefreshLayout refreshLayout, final CoordinatorLayoutListener listener) {
        for (int i = layout.getChildCount() - 1; i >= 0; i--) {
            View view = layout.getChildAt(i);
            if (view instanceof AppBarLayout) {
                ((AppBarLayout) view).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        listener.onCoordinatorUpdate(
                                verticalOffset >= 0,
                                refreshLayout.isEnableLoadMore() && (appBarLayout.getTotalScrollRange() + verticalOffset) <= 0);
                    }
                });
            }
        }
    }

}
