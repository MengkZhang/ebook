package com.tzpt.cloudlibrary.camera;

import android.graphics.Rect;

/**
 * Created by Administrator on 2018/10/23.
 */

public interface IFrameRect {
    /**
     * 获得扫码区域(识别区域)
     */
    Rect getFramingRect();
}
