package com.tzpt.cloudlibrary.bean;

import com.tzpt.cloudlibrary.zlibrary.core.util.ZLColor;

/**
 * Created by Administrator on 2017/10/24.
 */

public class ReadingColorBean {
    public int defaultResourceId;
    public int textColorResource;
    public ZLColor mBgColor;
    public ZLColor mTextColor;
    public boolean colorChose = false;

    public ReadingColorBean(int defaultResourseId, ZLColor bgColor,
                        int textColorResourse, ZLColor textColor, boolean colorChoosed) {
        this.defaultResourceId = defaultResourseId;
        mBgColor = bgColor;
        this.textColorResource = textColorResourse;
        mTextColor = textColor;
        this.colorChose = colorChoosed;
    }
}
