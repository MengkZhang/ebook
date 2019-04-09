package com.tzpt.cloudlibrary.zlibrary.core.filetypes;

import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;
import com.tzpt.cloudlibrary.zlibrary.core.util.MimeType;

import java.util.List;

/**
 * 文件格式
 * Created by Administrator on 2017/4/8.
 */

public abstract class FileType {
    public final String Id;

    FileType(String id) {
        Id = id;
    }

    /**
     * 是否支持该文件格式
     *
     * @param file 需要打开的文件
     * @return true表示支持 false表示不支持
     */
    public abstract boolean acceptsFile(ZLFile file);

    public abstract List<MimeType> mimeTypes();

    public abstract MimeType mimeType(ZLFile file);

    public MimeType rawMimeType(ZLFile file) {
        return mimeType(file);
    }

    public abstract String defaultExtension(MimeType mime);
}
