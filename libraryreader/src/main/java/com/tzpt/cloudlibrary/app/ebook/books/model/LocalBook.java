
package com.tzpt.cloudlibrary.app.ebook.books.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.app.ebook.books.db.CloudLibraryReaderContentProvider;
import com.tzpt.cloudlibrary.app.ebook.utils.Md5Encrypt;

import java.util.ArrayList;

public class LocalBook {

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
    public boolean selected;
    public boolean isDeleteMode;

    private Context context;

    public LocalBook(Context context) {
        this.context = context;
    }

    public void load() {
        if (TextUtils.isEmpty(id))
            throw new IllegalStateException("Please set the book id and try again");

        Cursor cursor = context.getContentResolver().query(CloudLibraryReaderContentProvider.BOOK_CONTENT_URI, null, "id=?", new String[]{id}, null);
        if (cursor != null && cursor.moveToNext()) {
            constructFromCursor(cursor);
        }

        if (cursor != null)
            cursor.close();
    }

    /**
     * 删除书签信息
     *
     * @param id
     */
    public void delete(String id) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        context.getContentResolver().delete(CloudLibraryReaderContentProvider.BOOK_CONTENT_URI, "id=?", new String[]{id});


    }

    public void save() {
        String where = "id=?";
        String[] selectionArgs = new String[]{id};
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
        if (0 == context.getContentResolver().update(CloudLibraryReaderContentProvider.BOOK_CONTENT_URI, values, where, selectionArgs)) {
            values.put("id", id);
            context.getContentResolver().insert(CloudLibraryReaderContentProvider.BOOK_CONTENT_URI, values);
        }
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

    public static LocalBook getLocalBook(Context context, String filePath) {
        String id = Md5Encrypt.md5(filePath);
        Cursor cursor = context.getContentResolver().query(CloudLibraryReaderContentProvider.BOOK_CONTENT_URI, null, "id=?", new String[]{id}, null);
        LocalBook book = null;
        if (cursor != null && cursor.moveToNext()) {
            book = new LocalBook(context);
            book.constructFromCursor(cursor);
        }

        if (cursor != null)
            cursor.close();

        return book;
    }

    /**
     * 获取书架列表
     *
     * @param context
     * @param orderColumn
     * @param orderType
     * @return
     */
    public static ArrayList<LocalBook> getLocalBookList(Context context, OrderColumn orderColumn, OrderType orderType) {
        ArrayList<LocalBook> localBooks = new ArrayList<>();
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
        Cursor cursor = context.getContentResolver().query(CloudLibraryReaderContentProvider.BOOK_CONTENT_URI, null, null, null, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                LocalBook book = new LocalBook(context);
                book.constructFromCursor(cursor);
                localBooks.add(book);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return localBooks;
    }

    public static boolean isBuildInBookHasSaved(Context context, String bookName) {
        boolean bookExisted = false;
        Cursor cursor = context.getContentResolver().query(CloudLibraryReaderContentProvider.BUILD_IN_BOOK_CONTENT_URI, null, "id=?", new String[]{Md5Encrypt.md5(bookName)}, null);
        if (cursor != null && cursor.moveToNext()) {
            bookExisted = true;
        }

        return bookExisted;
    }

    public static void setBuildInBookSaved(Context context, String bookName) {
        String where = "id=?";
        String[] selectionArgs = new String[]{Md5Encrypt.md5(bookName)};
        ContentValues values = new ContentValues();

        values.put("saved", 1);
        if (0 == context.getContentResolver().update(CloudLibraryReaderContentProvider.BUILD_IN_BOOK_CONTENT_URI, values, where, selectionArgs)) {
            values.put("id", Md5Encrypt.md5(bookName));
            context.getContentResolver().insert(CloudLibraryReaderContentProvider.BUILD_IN_BOOK_CONTENT_URI, values);
        }
    }
}
