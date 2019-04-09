
package com.tzpt.cloudlibrary.app.ebook.books.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.app.ebook.books.db.CloudLibraryReaderContentProvider;
import com.tzpt.cloudlibrary.app.ebook.utils.Md5Encrypt;

import java.util.ArrayList;

/**
 * 书签
 */
public class EpubBookMarks {

    public enum OrderColumn {
        BY_TITLE, BY_AUTHOR, BY_ADDITION_DATE, BY_LAST_ACCESS_TIME
    }

    public enum OrderType {
        ASC, DESC
    }

    public String id = "";
    public String title = "";
    public String author = "";
    public String file = "";
    public long size = 0;
    public String detail_image = "";
    public String list_image = "";
    public String big_image = "";
    public String medium_image = "";
    public String small_image = "";
    public long addition_date = 0;
    public long last_access_date = 0;
    public long total_page = 0;
    public long current_page = 0;
    public long total_offset = 0;
    public long current_offset = 0;

    private Context context;

    public EpubBookMarks(Context context) {
        this.context = context;
    }

    public void load() {
        if (TextUtils.isEmpty(id))
            throw new IllegalStateException("Please set the book id and try again");

        Cursor cursor = context.getContentResolver().query(CloudLibraryReaderContentProvider.BOOK_CONTENT_MARKS, null, "id=?", new String[]{id}, null);
        if (cursor != null && cursor.moveToNext()) {
            constructFromCursor(cursor);
        }

        if (cursor != null)
            cursor.close();
    }

    /**
     * 删除这边书所有的书签
     *
     * @param id
     */
    public void delete(String id) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        context.getContentResolver().delete(CloudLibraryReaderContentProvider.BOOK_CONTENT_MARKS, "id=?", new String[]{id});
    }

    /**
     * 删除书签
     *
     * @param lastAccessTime
     */
    public void deleteByAddTime(String lastAccessTime) {
        if (TextUtils.isEmpty(lastAccessTime)) {
            return;
        }
        context.getContentResolver().delete(CloudLibraryReaderContentProvider.BOOK_CONTENT_MARKS, "last_access_date=?", new String[]{lastAccessTime});
    }

    /**
     * 保存书签
     */
    public void save() {
//        String where = "id=?";
//        String[] selectionArgs = new String[]{id};
        ContentValues values = new ContentValues();

        values.put("title", title);
        values.put("author", author);
        values.put("file", file);
        values.put("size", size);
        values.put("detail_image", detail_image);
        values.put("list_image", list_image);
        values.put("big_image", big_image);
        values.put("medium_image", medium_image);
        values.put("small_image", small_image);
        values.put("addition_date", addition_date);
        values.put("last_access_date", last_access_date);
        values.put("total_page", total_page);
        values.put("current_page", current_page);
        values.put("total_offset", total_offset);
        values.put("current_offset", current_offset);
        //if (0 == context.getContentResolver().update(CloudLibraryReaderContentProvider.BOOK_CONTENT_MARKS, values, where, selectionArgs)) {
        values.put("id", id);
        context.getContentResolver().insert(CloudLibraryReaderContentProvider.BOOK_CONTENT_MARKS, values);
        //}
    }

    public void constructFromCursor(Cursor cursor) {
        id = cursor.getString(cursor.getColumnIndex("id"));
        title = cursor.getString(cursor.getColumnIndex("title"));
        author = cursor.getString(cursor.getColumnIndex("author"));
        file = cursor.getString(cursor.getColumnIndex("file"));
        size = cursor.getLong(cursor.getColumnIndex("size"));
        detail_image = cursor.getString(cursor.getColumnIndex("detail_image"));
        list_image = cursor.getString(cursor.getColumnIndex("list_image"));
        big_image = cursor.getString(cursor.getColumnIndex("big_image"));
        medium_image = cursor.getString(cursor.getColumnIndex("medium_image"));
        small_image = cursor.getString(cursor.getColumnIndex("small_image"));
        addition_date = cursor.getLong(cursor.getColumnIndex("addition_date"));
        last_access_date = cursor.getLong(cursor.getColumnIndex("last_access_date"));
        total_page = cursor.getLong(cursor.getColumnIndex("total_page"));
        current_page = cursor.getLong(cursor.getColumnIndex("current_page"));
        total_offset = cursor.getLong(cursor.getColumnIndex("total_offset"));
        current_offset = cursor.getLong(cursor.getColumnIndex("current_offset"));
    }

    /**
     * 是否有个这个时间
     *
     * @param cursor
     * @param lastAccessDate
     * @return
     */
    public boolean constructFromCursorForLastTime(Cursor cursor, int lastAccessDate) {
        current_page = cursor.getLong(cursor.getColumnIndex("current_page"));
        if (lastAccessDate == current_page) {
            return true;
        }
        return false;
    }

    public static EpubBookMarks getLocalBook(Context context, String filePath) {
        String id = Md5Encrypt.md5(filePath);
        Cursor cursor = context.getContentResolver().query(CloudLibraryReaderContentProvider.BOOK_CONTENT_MARKS, null, "id=?", new String[]{id}, null);
        EpubBookMarks book = null;
        if (cursor != null && cursor.moveToNext()) {
            book = new EpubBookMarks(context);
            book.constructFromCursor(cursor);
        }

        if (cursor != null)
            cursor.close();

        return book;
    }

    /**
     * 获取书签列表
     *
     * @param context
     * @param orderColumn
     * @param orderType
     * @return
     */
    public static ArrayList<EpubBookMarks> getLocalBookList(Context context, String bookId, OrderColumn orderColumn, OrderType orderType) {
        if (null == bookId || TextUtils.isEmpty(bookId)) {
            return null;
        }
        ArrayList<EpubBookMarks> localBooks = new ArrayList<>();
        localBooks.clear();

        String sortOrder = "";
        switch (orderColumn) {
            case BY_TITLE:
                sortOrder += "title ";
                break;
            case BY_AUTHOR:
                sortOrder += "author ";
                break;
            case BY_ADDITION_DATE:
                sortOrder += "addition_date ";
                break;
            case BY_LAST_ACCESS_TIME:
                sortOrder += "last_access_date ";
                break;
        }

        switch (orderType) {
            case ASC:
                sortOrder += "ASC";
                break;
            case DESC:
                sortOrder += "DESC";
                break;
        }
        Cursor cursor = context.getContentResolver().query(CloudLibraryReaderContentProvider.BOOK_CONTENT_MARKS, null, "id=?", new String[]{bookId}, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                EpubBookMarks book = new EpubBookMarks(context);
                book.constructFromCursor(cursor);
                localBooks.add(book);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return localBooks;
    }

    /**
     * 已有该书签
     *
     * @param context
     * @return
     */
    public static boolean hasBookMark(Context context, int mBookCurPageIndex) throws Exception {
        ArrayList<EpubBookMarks> localBooks = new ArrayList<>();
        localBooks.clear();
        Cursor cursor = context.getContentResolver().query(CloudLibraryReaderContentProvider.BOOK_CONTENT_MARKS, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                EpubBookMarks book = new EpubBookMarks(context);
                if (book.constructFromCursorForLastTime(cursor, mBookCurPageIndex)) {
                    localBooks.add(book);
                    break;
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return localBooks.size() > 0;
    }

    //last_access_date
}
