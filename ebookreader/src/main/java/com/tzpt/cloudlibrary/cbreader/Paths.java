package com.tzpt.cloudlibrary.cbreader;

import com.tzpt.cloudlibrary.zlibrary.core.util.SystemInfo;
import com.tzpt.cloudlibrary.zlibrary.ui.android.library.ZLAndroidLibrary;

/**
 * 路径
 * Created by Administrator on 2017/4/7.
 */

public abstract class Paths {

    public static SystemInfo systemInfo() {

        return new SystemInfo() {
            public String tempDirectory() {
                return ZLAndroidLibrary.Instance().getExternalCacheDir();
            }
        };
    }
}
