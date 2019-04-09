package com.tzpt.cloudlibrary.zlibrary.core.filetypes;

import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;
import com.tzpt.cloudlibrary.zlibrary.core.util.MimeType;

import java.util.List;

/**
 * epub格式文件
 * Created by Administrator on 2017/4/8.
 */

public class FileTypeEpub extends FileType {
    FileTypeEpub() {
        super("ePub");
    }

    @Override
    public boolean acceptsFile(ZLFile file) {
        final String extension = file.getExtension();
        return
                "epub".equalsIgnoreCase(extension) ||
                        "oebzip".equalsIgnoreCase(extension) ||
                        ("opf".equalsIgnoreCase(extension) && file != file.getPhysicalFile());
    }

    @Override
    public List<MimeType> mimeTypes() {
        return MimeType.TYPES_EPUB;
    }

    @Override
    public MimeType mimeType(ZLFile file) {
        final String extension = file.getExtension();
        if ("epub".equalsIgnoreCase(extension)) {
            return MimeType.APP_EPUB_ZIP;
        }
        // TODO: process other extensions (?)
        return MimeType.NULL;
    }

    @Override
    public MimeType rawMimeType(ZLFile file) {
        final String extension = file.getExtension();
        if ("epub".equalsIgnoreCase(extension)) {
            return MimeType.APP_ZIP;
        }
        // TODO: process other extensions (?)
        return MimeType.NULL;
    }

    @Override
    public String defaultExtension(MimeType mime) {
        return "epub";
    }
}
