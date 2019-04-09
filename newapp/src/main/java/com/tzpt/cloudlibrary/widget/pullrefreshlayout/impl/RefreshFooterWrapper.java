package com.tzpt.cloudlibrary.widget.pullrefreshlayout.impl;

import android.annotation.SuppressLint;
import android.view.View;

import com.tzpt.cloudlibrary.widget.pullrefreshlayout.api.RefreshFooter;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.internal.InternalAbstract;


/**
 * 刷新底部包装
 */
@SuppressLint("ViewConstructor")
public class RefreshFooterWrapper extends InternalAbstract implements RefreshFooter {

    public RefreshFooterWrapper(View wrapper) {
        super(wrapper);
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        return mWrapperView instanceof RefreshFooter && ((RefreshFooter) mWrapperView).setNoMoreData(noMoreData);
    }

}
