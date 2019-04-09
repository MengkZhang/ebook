package com.tzpt.cloudlibrary.cbreader.formats;

import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;
import com.tzpt.cloudlibrary.zlibrary.core.filetypes.FileType;
import com.tzpt.cloudlibrary.zlibrary.core.filetypes.FileTypeCollection;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 获取本地的所有格式的FormatPlugin集合
 * Created by Administrator on 2017/4/9.
 */

public class PluginCollection implements IFormatPluginCollection {
    static {
        System.loadLibrary("NativeFormats-v4");
    }

    private static volatile PluginCollection ourInstance;

    private final List<BuiltinFormatPlugin> mBuiltinPlugins = new LinkedList<>();

    public static PluginCollection Instance() {
        if (ourInstance == null) {
            createInstance();
        }
        return ourInstance;
    }

    private static synchronized void createInstance() {
        if (ourInstance == null) {
            ourInstance = new PluginCollection();

            // This code cannot be moved to constructor
            // because nativePlugins() is a native method
            //获取本地的所有格式的Plugin集合nativePlugins(数组)
            Collections.addAll(ourInstance.mBuiltinPlugins, ourInstance.nativePlugins());
        }
    }

    public static void deleteInstance() {
        if (ourInstance != null) {
            ourInstance = null;
        }
    }

    private PluginCollection() {
    }

    public FormatPlugin getPlugin(ZLFile file) {
        final FileType fileType = FileTypeCollection.Instance.typeForFile(file);
        return getPlugin(fileType);
    }

    private FormatPlugin getPlugin(FileType fileType) {
        if (fileType == null) {
            return null;
        }

        for (FormatPlugin p : mBuiltinPlugins) {
            if (fileType.Id.equalsIgnoreCase(p.supportedFileType())) {
                return p;
            }
        }
        return null;
    }

    private native NativeFormatPlugin[] nativePlugins();

    private native void free();

    protected void finalize() throws Throwable {
        free();
        super.finalize();
    }
}
