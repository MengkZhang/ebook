package com.tzpt.cloudlibrary.zlibrary.text.view;

/**
 * Created by Administrator on 2017/4/8.
 */

public interface PaintStateEnum {
    int NOTHING_TO_PAINT = 0;
    int READY = 1;
    int START_IS_KNOWN = 2;
    int END_IS_KNOWN = 3;
    int TO_SCROLL_FORWARD = 4;
    int TO_SCROLL_BACKWARD = 5;
}
