package com.tzpt.cloudlibrary.zlibrary.core.filetypes;

import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;
import com.tzpt.cloudlibrary.zlibrary.core.util.MimeType;

import java.util.Collection;
import java.util.TreeMap;

/**
 * Created by Administrator on 2017/4/8.
 */

public class FileTypeCollection {
    public static final FileTypeCollection Instance = new FileTypeCollection();

    private final TreeMap<String, FileType> mTypes = new TreeMap<>();

    private FileTypeCollection() {
        //y 能看到哪些类型的文件
//		addType(new FileTypeFB2());
        addType(new FileTypeEpub());
//		addType(new FileTypeMobipocket());
//      addType(new FileTypeHtml());
//      addType(new SimpleFileType("txt", "txt", MimeType.TYPES_TXT));
//		addType(new SimpleFileType("RTF", "rtf", MimeType.TYPES_RTF));
//		addType(new SimpleFileType("PDF", "pdf", MimeType.TYPES_PDF));
//		addType(new FileTypeDjVu());
//		addType(new FileTypeCBZ());
//		addType(new SimpleFileType("ZIP archive", "zip", Collections.singletonList(MimeType.APP_ZIP)));
//      addType(new SimpleFileType("msdoc", "doc", MimeType.TYPES_DOC));
    }

    private void addType(FileType type) {
        mTypes.put(type.Id.toLowerCase(), type);
    }

    public Collection<FileType> types() {
        return mTypes.values();
    }

    public FileType typeById(String id) {
        return mTypes.get(id.toLowerCase());
    }

    /**
     * 返回文件格式
     *
     * @param file 需要打开的文件
     * @return 文件格式
     */
    public FileType typeForFile(ZLFile file) {
        for (FileType type : types()) {
            if (type.acceptsFile(file)) {
                return type;
            }
        }
        return null;
    }

    public FileType typeForMime(MimeType mime) {
        if (mime == null) {
            return null;
        }
        mime = mime.clean();
        for (FileType type : types()) {
            if (type.mimeTypes().contains(mime)) {
                return type;
            }
        }
        return null;
    }

    public MimeType mimeType(ZLFile file) {
        for (FileType type : types()) {
            final MimeType mime = type.mimeType(file);
            if (mime != MimeType.NULL) {
                return mime;
            }
        }
        return MimeType.UNKNOWN;
    }

    public MimeType rawMimeType(ZLFile file) {
        for (FileType type : types()) {
            final MimeType mime = type.rawMimeType(file);
            if (mime != MimeType.NULL) {
                return mime;
            }
        }
        return MimeType.UNKNOWN;
    }
}
