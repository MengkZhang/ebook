package com.tzpt.cloudlibrary.app.ebook.constant;

import android.os.Environment;

/**
 * 常量配置
 * Created by ZhiqiangJia on 2017-01-16.
 */
public class EbookConstant {

    //电子书下载地址
    public static final String FILE_DOWNLOAD_URL = "http://m.ytsg.cn";
    //电子书储存地址
    public static final String FILE_SAVE_URL = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ytsg/epub/";

    public static final String EPUB_BOOK_DOWNLOAD_PATH = "book_download_path";
    public static final String EPUB_BOOK_IAMGE_PATH = "book_image_path";
    public static final String EPUB_BOOK_NAME = "book_name";
}
