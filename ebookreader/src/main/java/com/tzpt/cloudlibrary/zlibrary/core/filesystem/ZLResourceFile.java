package com.tzpt.cloudlibrary.zlibrary.core.filesystem;

import com.tzpt.cloudlibrary.zlibrary.core.library.ZLibrary;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 资源文件
 * Created by Administrator on 2017/4/7.
 */

public abstract class ZLResourceFile extends ZLFile {
    private static Map<String, ZLResourceFile> mCache =
            Collections.synchronizedMap(new HashMap<String, ZLResourceFile>());
    private final String mPath;

    public static ZLResourceFile createResourceFile(String path) {
        ZLResourceFile file = mCache.get(path);
        if (file == null) {
            file = ZLibrary.Instance().createResourceFile(path);
            mCache.put(path, file);
        }
        return file;
    }

    protected ZLResourceFile(String path) {
        mPath = path;
        init();
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public String getLongName() {
        return mPath.substring(mPath.lastIndexOf('/') + 1);
    }

    @Override
    public ZLPhysicalFile getPhysicalFile() {
        return null;
    }
}
