package com.tzpt.cloudlibrary.cbreader.formats;

import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;

/**
 * Created by Administrator on 2017/4/9.
 */

public interface IFormatPluginCollection {
    public interface Holder {
        IFormatPluginCollection getCollection();
    }

    FormatPlugin getPlugin(ZLFile file);
}
