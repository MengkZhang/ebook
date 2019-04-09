package com.tzpt.cloudlibrary.app.ebook.books.db;

import android.content.Context;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;

import com.tzpt.cloudlibrary.app.ebook.utils.DatabaseUtil;
import com.tzpt.cloudlibrary.app.ebook.utils.FileGuider;

/**
 * 数据库
 */
public class CloudLibraryReaderSQLiteOpenHelper {
    private static final String DATABASE_NAME = "cloudlibrary_reader_data.db";
    private static final int DATABASE_VERSION = 1;

    public static final String BOOK_TABLE_NAME = "book";                        //电子书书架
    public static final String BOOKMARK_TABLE_NAME = "bookmark";                //最后一次离开电子书标记
    public static final String BUILD_IN_BOOK_TABLE_NAME = "build_in";           //电子书
    public static final String ELECTRONIC_BOOK_MARKS = "electronic_book_marks"; //电子书书签

    //电子书书签
    private static final String SQL_CREATE_ELECTRONIC_BOOK_MARKS = "CREATE TABLE IF NOT EXISTS " + ELECTRONIC_BOOK_MARKS + " ("
            + "id TEXT,"
            + "title TEXT,"
            + "author TEXT,"
            + "file TEXT,"
            + "size INTEGER,"
            + "detail_image TEXT,"
            + "list_image TEXT,"
            + "big_image TEXT,"
            + "medium_image TEXT,"
            + "small_image TEXT,"
            + "addition_date INTEGER,"
            + "last_access_date INTEGER,"
            + "total_page INTEGER,"
            + "current_page INTEGER,"
            + "total_offset INTEGER,"
            + "current_offset INTEGER"
            + ");";

    //电子书书架
    private static final String SQL_CREATE_TABLE_BOOK = "CREATE TABLE IF NOT EXISTS " + BOOK_TABLE_NAME + " ("
            + "id TEXT,"
            + "title TEXT,"
            + "author TEXT,"
            + "file TEXT,"
            + "size INTEGER,"
            + "detail_image TEXT,"
            + "list_image TEXT,"
            + "big_image TEXT,"
            + "medium_image TEXT,"
            + "small_image TEXT,"
            + "addition_date INTEGER,"
            + "last_access_date INTEGER,"
            + "total_page INTEGER,"
            + "current_page INTEGER,"
            + "total_offset INTEGER,"
            + "current_offset INTEGER"
            + ");";
    //最后一次离开电子书标记
    private static final String SQL_CREATE_TABLE_BOOKMARK = "CREATE TABLE IF NOT EXISTS " + BOOKMARK_TABLE_NAME + " ("
            + "bookid TEXT,"
            + "title TEXT,"
            + "author TEXT,"
            + "file TEXT,"
            + "size INTEGER,"
            + "detail_image TEXT,"
            + "list_image TEXT,"
            + "big_image TEXT,"
            + "medium_image TEXT,"
            + "small_image TEXT,"
            + "chapter INTEGER,"
            + "chapter_title TEXT,"
            + "modified_date INTEGER,"
            + "total_page INTEGER,"
            + "current_page INTEGER,"
            + "total_offset INTEGER,"
            + "current_offset INTEGER"
            + ");";

    private static final String SQL_CREATE_TABLE_BUILD_IN_BOOK_TABLE_NAME = "CREATE TABLE IF NOT EXISTS " + BUILD_IN_BOOK_TABLE_NAME + " ("
            + "id TEXT,"
            + "saved INTEGER"
            + ");";

    private SQLiteDatabase mSQLiteDatabase;

    public CloudLibraryReaderSQLiteOpenHelper(Context context) {
        //super(context, DATABASE_NAME, null, DATABASE_VERSION);
        FileGuider savePath = new FileGuider(context, FileGuider.SPACE_PRIORITY_EXTERNAL);
        savePath.setSpace(FileGuider.SPACE_PRIORITY_EXTERNAL);
        savePath.setImmutable(true);
        savePath.setChildDirName("databases");
        savePath.setFileName(DATABASE_NAME);
        savePath.setMode(Context.MODE_WORLD_WRITEABLE);

        try {
            mSQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(savePath.getFilePath(), null);
        } catch (SQLiteCantOpenDatabaseException e) {
            savePath = new FileGuider(context, FileGuider.SPACE_ONLY_INTERNAL);
            savePath.setSpace(FileGuider.SPACE_ONLY_INTERNAL);
            savePath.setImmutable(true);
            savePath.setChildDirName("databases");
            savePath.setFileName(DATABASE_NAME);
            savePath.setMode(Context.MODE_WORLD_WRITEABLE);
            mSQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(savePath.getFilePath(), null);
        }

        int version = mSQLiteDatabase.getVersion();
        if (version != DATABASE_VERSION) {
            mSQLiteDatabase.beginTransaction();
            try {
                if (version == 0) {
                    onCreate(mSQLiteDatabase);
                } else {
                    if (version > DATABASE_VERSION) {
                        onDowngrade(mSQLiteDatabase, version, DATABASE_VERSION);
                    } else {
                        onUpgrade(mSQLiteDatabase, version, DATABASE_VERSION);
                    }
                }
                mSQLiteDatabase.setVersion(DATABASE_VERSION);
                mSQLiteDatabase.setTransactionSuccessful();
            } finally {
                mSQLiteDatabase.endTransaction();
            }
        }
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_BOOK);
        db.execSQL(SQL_CREATE_TABLE_BOOKMARK);
        db.execSQL(SQL_CREATE_TABLE_BUILD_IN_BOOK_TABLE_NAME);
        db.execSQL(SQL_CREATE_ELECTRONIC_BOOK_MARKS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DatabaseUtil.updateVerify(db, oldVersion, newVersion, SQL_CREATE_TABLE_BOOK);
        DatabaseUtil.updateVerify(db, oldVersion, newVersion, SQL_CREATE_TABLE_BOOKMARK);
        DatabaseUtil.updateVerify(db, oldVersion, newVersion, SQL_CREATE_TABLE_BUILD_IN_BOOK_TABLE_NAME);
        DatabaseUtil.updateVerify(db, oldVersion, newVersion, SQL_CREATE_ELECTRONIC_BOOK_MARKS);

    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public SQLiteDatabase getReadableDatabase() {
        return mSQLiteDatabase;
    }
}
