package com.tzpt.cloudlibrary.zlibrary.core.application;

import com.tzpt.cloudlibrary.zlibrary.core.view.ZLView;

/**
 * Created by Administrator on 2017/4/7.
 */

public abstract class ZLApplication {
    public static ZLApplication Instance() {
        return mInstance;
    }

    private static ZLApplication mInstance;
    static final String NoAction = "none";
    private volatile ZLView mView;

    protected ZLApplication() {
        mInstance = this;
    }

    protected final void setView(ZLView view) {
        mView = view;
    }

    public final ZLView getCurrentView() {
        return mView;
    }
}
