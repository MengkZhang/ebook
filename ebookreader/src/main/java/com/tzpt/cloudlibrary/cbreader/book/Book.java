package com.tzpt.cloudlibrary.cbreader.book;

import com.tzpt.cloudlibrary.cbreader.formats.BookReadingException;
import com.tzpt.cloudlibrary.cbreader.formats.FormatPlugin;
import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;

/**
 * 打开文件生成的对象
 * Created by Administrator on 2017/4/8.
 */

public final class Book extends AbstractBook {
    //文件路径
    private final String mPath;
    //封面路径（可以在外面设置封面图片）
    private String myCoverPath;

    public Book(String path, String title) {
        super(title);
        if (path == null) {
            throw new IllegalArgumentException("Creating book with no file");
        }
        mPath = path;
    }

    public Book(ZLFile file, FormatPlugin plugin, String bookTitle, String bookAuthor) throws BookReadingException {
        this(file.getPath(), bookTitle);
        BookUtil.readMetainfo(this, plugin, bookTitle, bookAuthor);
    }

    public void setMyCoverPath(String myCoverPath) {
        this.myCoverPath = myCoverPath;
    }

    public String getMyCoverPath() {
        return myCoverPath;
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public int hashCode() {
        return mPath.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        return mPath.equals(((Book) o).mPath);
    }
}
