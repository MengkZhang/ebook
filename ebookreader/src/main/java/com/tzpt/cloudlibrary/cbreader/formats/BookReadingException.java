package com.tzpt.cloudlibrary.cbreader.formats;

import com.amse.ys.zip.ZipException;
import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;
import com.tzpt.cloudlibrary.zlibrary.core.resources.ZLResource;

import java.io.IOException;

/**
 * Created by Administrator on 2017/4/9.
 */

public final class BookReadingException extends Exception {
    private static String getResourceText(String resourceId, String... params) {
        String message = ZLResource.resource("errorMessage").getResource(resourceId).getValue();
        for (String p : params) {
            message = message.replaceFirst("%s", p);
        }
        return message;
    }

    public final ZLFile File;

    public BookReadingException(String resourceId, ZLFile file, String[] params) {
        super(getResourceText(resourceId, params));
        File = file;
    }

    public BookReadingException(String resourceId, ZLFile file) {
        this(resourceId, file, new String[] { file.getPath() });
    }

    public BookReadingException(IOException e, ZLFile file) {
        super(getResourceText(
                e instanceof ZipException ? "errorReadingZip" : "errorReadingFile",
                file.getPath()
        ), e);
        File = file;
    }
}
