package com.tzpt.cloudlibrary.zlibrary.core.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 本地文件
 * Created by Administrator on 2017/4/7.
 */

public class ZLPhysicalFile extends ZLFile {
    private final File mFile;

    ZLPhysicalFile(String path) {
        this(new File(path)); // 把书的路径传进来 new 一个File
        // 本地书籍查找时各个文件也是用的这个
    }

    /**
     * 构造函数会利用这个完整路径创建一个ZLPhysicalFile类
     * 正因这里设置了File类，所以才能在getInputStream方法中返回FileInputStream类。
     */
    private ZLPhysicalFile(File file) {
        mFile = file;
        init(); // 初始化
    }

    @Override
    public boolean exists() {
        return mFile.exists();
    }

    @Override
    public long size() {
        return mFile.length();
    }

    @Override
    public boolean isDirectory() {
        return mFile.isDirectory();
    }

    public boolean delete() {
        return mFile.delete();
    }

    @Override
    public String getPath() {
        try {
            return mFile.getCanonicalPath();
        } catch (Exception e) {
            // should be never thrown
            return mFile.getPath();
        }
    }

    @Override
    public String getLongName() {
        return isDirectory() ? getPath() : mFile.getName();
    }

    @Override
    public ZLFile getParent() {
        return isDirectory() ? null : new ZLPhysicalFile(mFile.getParent());
    }

    @Override
    public ZLPhysicalFile getPhysicalFile() {
        return this;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(mFile);
    }
}
