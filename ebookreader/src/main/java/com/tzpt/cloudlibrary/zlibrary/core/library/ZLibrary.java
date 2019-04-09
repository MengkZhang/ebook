package com.tzpt.cloudlibrary.zlibrary.core.library;

import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLResourceFile;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLIntegerRangeOption;

/**
 * Created by Administrator on 2017/4/7.
 */

public abstract class ZLibrary {
    public static ZLibrary Instance() {
        return ourImplementation;
    }

    private static ZLibrary ourImplementation;

    public final ZLIntegerRangeOption BatteryLevelToTurnScreenOffOption = new ZLIntegerRangeOption("LookNFeel", "BatteryLevelToTurnScreenOff", 0, 100, 50); //y 电池电量
    //    public final ZLBooleanOption DontTurnScreenOffDuringChargingOption = new ZLBooleanOption("LookNFeel", "DontTurnScreenOffDuringCharging", true); // 屏幕方向
    public final ZLIntegerRangeOption ScreenBrightnessLevelOption = new ZLIntegerRangeOption("LookNFeel", "ScreenBrightnessLevel", 0, 100, 0); // 屏幕亮度

    protected ZLibrary() {
        ourImplementation = this;
    }

    abstract public ZLResourceFile createResourceFile(String path);

    abstract public String getCurrentTimeString();

    abstract public int getDisplayDPI();

    abstract public float getDPI();

    abstract public float getSP();

    abstract public int getWidthInPixels();

    abstract public int getScreenWidth();

    abstract public int getScreenHeight();

    abstract public int getHeightInPixels();

    abstract public String getExternalCacheDir();

}
