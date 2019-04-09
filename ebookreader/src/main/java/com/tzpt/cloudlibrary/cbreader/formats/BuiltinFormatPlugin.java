package com.tzpt.cloudlibrary.cbreader.formats;

import com.tzpt.cloudlibrary.cbreader.bookmodel.BookModel;

/**
 * Created by Administrator on 2017/4/9.
 */

public abstract class BuiltinFormatPlugin extends FormatPlugin {
    protected BuiltinFormatPlugin(String fileType) {
        super(fileType);
    }

    public abstract void readModel(BookModel model) throws BookReadingException;
}
