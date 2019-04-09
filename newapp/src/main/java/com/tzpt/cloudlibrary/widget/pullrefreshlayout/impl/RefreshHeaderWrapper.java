package com.tzpt.cloudlibrary.widget.pullrefreshlayout.impl;

import android.annotation.SuppressLint;
import android.view.View;

import com.tzpt.cloudlibrary.widget.pullrefreshlayout.api.RefreshHeader;
import com.tzpt.cloudlibrary.widget.pullrefreshlayout.internal.InternalAbstract;


/**
 * 刷新头部包装
 */
@SuppressLint("ViewConstructor")
public class RefreshHeaderWrapper extends InternalAbstract implements RefreshHeader {

    public RefreshHeaderWrapper(View wrapper) {
        super(wrapper);
    }

}
