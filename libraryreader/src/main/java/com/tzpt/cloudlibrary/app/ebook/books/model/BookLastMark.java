package com.tzpt.cloudlibrary.app.ebook.books.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.app.ebook.books.db.CloudLibraryReaderContentProvider;

/**
 * 离开书的标记
 */
public class BookLastMark {
    public String bookid;
    public String title;
    public String author;
    public String file;
    public long size;
    public String detail_image;
    public String list_image;
    public String big_image;
    public String medium_image;
    public String small_image;
    public String chapter_title;
    public long chapter;
    public long modified_date;
    public long total_page;
    public long current_page;
    public long total_offset;
    public long current_offset;

    private Context context;

    public BookLastMark(Context context) {
        this.context = context;
    }

    public void load() {
        if (TextUtils.isEmpty(bookid)) {
            return;
        }
        //throw new IllegalStateException("Please set the book id and try again");
        Cursor cursor = context.getContentResolver().query(CloudLibraryReaderContentProvider.BOOKMARK_CONTENT_URI, null, "bookid=?", new String[]{bookid}, null);
        if (cursor != null && cursor.moveToNext())
            constructFromCursor(cursor);

        if (cursor != null)
            cursor.close();
    }

    /**
     * 保存书签
     */
    public void save() {
        if (TextUtils.isEmpty(bookid)) {
            return;
        }
        //throw new IllegalStateException("Please set the book id and try again");
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
        values.put("modified_date", modified_date);
        values.put("chapter", chapter);
        values.put("chapter_title", chapter_title);
        values.put("total_page", total_page);
        values.put("current_page", current_page);
        values.put("total_offset", total_offset);
        values.put("current_offset", current_offset);
        if (0 == context.getContentResolver().update(CloudLibraryReaderContentProvider.BOOKMARK_CONTENT_URI, values, "bookid=?", new String[]{bookid})) {
            values.put("bookid", bookid);
            context.getContentResolver().insert(CloudLibraryReaderContentProvider.BOOKMARK_CONTENT_URI, values);
        }
    }

    public void delete(String bookid) {
        if (TextUtils.isEmpty(bookid)) {
            return;
        }
        //throw new IllegalStateException("Please set the book id and try again");
        context.getContentResolver().delete(CloudLibraryReaderContentProvider.BOOKMARK_CONTENT_URI, "bookid=?", new String[]{bookid});
    }

    public void constructFromCursor(Cursor cursor) {
        bookid = cursor.getString(cursor.getColumnIndex("bookid"));
        title = cursor.getString(cursor.getColumnIndex("title"));
        author = cursor.getString(cursor.getColumnIndex("author"));
        file = cursor.getString(cursor.getColumnIndex("file"));
        size = cursor.getLong(cursor.getColumnIndex("size"));
        detail_image = cursor.getString(cursor.getColumnIndex("detail_image"));
        list_image = cursor.getString(cursor.getColumnIndex("list_image"));
        big_image = cursor.getString(cursor.getColumnIndex("big_image"));
        medium_image = cursor.getString(cursor.getColumnIndex("medium_image"));
        small_image = cursor.getString(cursor.getColumnIndex("small_image"));
        modified_date = cursor.getLong(cursor.getColumnIndex("modified_date"));
        chapter = cursor.getLong(cursor.getColumnIndex("chapter"));
        chapter_title = cursor.getString(cursor.getColumnIndex("chapter_title"));
        total_page = cursor.getLong(cursor.getColumnIndex("total_page"));
        current_page = cursor.getLong(cursor.getColumnIndex("current_page"));
        total_offset = cursor.getLong(cursor.getColumnIndex("total_offset"));
        current_offset = cursor.getLong(cursor.getColumnIndex("current_offset"));
    }

    /**
     * 获取书签
     *
     * @param context
     * @param bookid
     * @return
     */
    public static BookLastMark getBookmark(Context context, String bookid) {
        BookLastMark bookmark = null;

        Cursor cursor = context.getContentResolver().query(CloudLibraryReaderContentProvider.BOOKMARK_CONTENT_URI, null, "bookid=?", new String[]{bookid}, null);
        if (cursor != null && cursor.moveToNext()) {
            bookmark = new BookLastMark(context);
            bookmark.constructFromCursor(cursor);
        }
        if (cursor != null)
            cursor.close();

        return bookmark;
    }

}
