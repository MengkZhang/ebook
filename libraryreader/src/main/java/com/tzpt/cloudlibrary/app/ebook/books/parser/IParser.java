package com.tzpt.cloudlibrary.app.ebook.books.parser;

import android.util.Pair;
import android.util.SparseArray;

import com.tzpt.cloudlibrary.app.ebook.books.model.Book;
import com.tzpt.cloudlibrary.app.ebook.books.model.Chapter;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * 解析类型
 *
 * @author Administrator
 */
public interface IParser {

    enum ParserType {
        Epub, UMD, Html, CHM, EBK2, EBK3
    }

    void constructChapterList(SparseArray<Chapter> chapterList, Book book);

    ParserType getParserType();

    String getChapterContent(String id);

//    long getChapterContentSize(String id);
    //获取文件流
    Pair<? extends InputStream, Long> getFileStream(String id);

    ArrayList<EpubParser.NavPoint> getNavMap();

    //获取本地文件路径
    String getFileLocalPath(String entryName);

    void close();
}
