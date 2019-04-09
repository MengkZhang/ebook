package com.tzpt.cloudlibrary.cbreader.book;

import com.tzpt.cloudlibrary.zlibrary.core.util.ZLColor;

/**
 * Created by Administrator on 2017/4/8.
 */

public class HighlightingStyle {
    public final int Id;
    public final long LastUpdateTimestamp;

    private String myName;
    private ZLColor myBackgroundColor;
    private ZLColor myForegroundColor;

    HighlightingStyle(int id, long timestamp, String name, ZLColor bgColor, ZLColor fgColor) {
        Id = id;
        LastUpdateTimestamp = timestamp;

        myName = name;
        myBackgroundColor = bgColor;
        myForegroundColor = fgColor;
    }

    public String getNameOrNull() {
        return "".equals(myName) ? null : myName;
    }

    void setName(String name) {
        myName = name;
    }

    public ZLColor getBackgroundColor() {
        return myBackgroundColor;
    }

    public void setBackgroundColor(ZLColor bgColor) {
        myBackgroundColor = bgColor;
    }

    public ZLColor getForegroundColor() {
        return myForegroundColor;
    }

    public void setForegroundColor(ZLColor fgColor) {
        myForegroundColor = fgColor;
    }
}
