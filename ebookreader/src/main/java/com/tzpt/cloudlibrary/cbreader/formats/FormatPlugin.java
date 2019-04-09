package com.tzpt.cloudlibrary.cbreader.formats;

import com.tzpt.cloudlibrary.cbreader.book.AbstractBook;
import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;
import com.tzpt.cloudlibrary.zlibrary.core.resources.ZLResource;

/**
 * Created by Administrator on 2017/4/9.
 */

public abstract class FormatPlugin {
    private final String myFileType;

    protected FormatPlugin(String fileType) {
        myFileType = fileType;
    }

    public final String supportedFileType() {
        return myFileType;
    }

    public final String name() {
        return ZLResource.resource("format").getResource(myFileType).getValue();
    }

    public ZLFile realBookFile(ZLFile file) throws BookReadingException {
        return file;
    }

//    public List<FileEncryptionInfo> readEncryptionInfos(AbstractBook book) {
//
//        return Collections.emptyList();
//    }

    public abstract void readMetainfo(AbstractBook book) throws BookReadingException;

//    public abstract void readUids(AbstractBook book) throws BookReadingException;

//    public abstract void detectLanguageAndEncoding(AbstractBook book) throws BookReadingException;

//    public abstract ZLImage readCover(ZLFile file);

//    public abstract String readAnnotation(ZLFile file);

    /* lesser is higher: 0 for ePub/fb2, 5 for other native, 10 for external */
    public abstract int priority();

//    public abstract EncodingCollection supportedEncodings();
}
