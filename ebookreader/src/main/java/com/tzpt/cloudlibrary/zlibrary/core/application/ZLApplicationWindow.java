package com.tzpt.cloudlibrary.zlibrary.core.application;

import com.tzpt.cloudlibrary.zlibrary.core.view.ZLViewWidget;

/**
 * Created by Administrator on 2017/4/7.
 */

public interface ZLApplicationWindow {
    void showErrorMessage(String resourceKey);
    void showErrorMessage(String resourceKey, String parameter);
    void processException(Exception e);

    void refresh();

    ZLViewWidget getViewWidget();

    void hideViewWidget(boolean flag);

    void close();

    int getBatteryLevel();
}
