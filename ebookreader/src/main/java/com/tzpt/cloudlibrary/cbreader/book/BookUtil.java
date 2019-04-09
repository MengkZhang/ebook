package com.tzpt.cloudlibrary.cbreader.book;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.cbreader.formats.BookReadingException;
import com.tzpt.cloudlibrary.cbreader.formats.FormatPlugin;
import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;

/**
 * Created by Administrator on 2017/4/10.
 */

public abstract class BookUtil {
//    public static UID createUid(AbstractBook book, String algorithm) {
//        return createUid(fileByBook(book), algorithm);
//    }
//
//    public static UID createUid(ZLFile file, String algorithm) {
//        InputStream stream = null;
//
//        try {
//            final MessageDigest hash = MessageDigest.getInstance(algorithm);
//            stream = file.getInputStream();
//
//            final byte[] buffer = new byte[2048];
//            while (true) {
//                final int nread = stream.read(buffer);
//                if (nread == -1) {
//                    break;
//                }
//                hash.update(buffer, 0, nread);
//            }
//
//            final Formatter f = new Formatter();
//            for (byte b : hash.digest()) {
//                f.format("%02X", b & 0xFF);
//            }
//            return new UID(algorithm, f.toString());
//        } catch (IOException e) {
//            return null;
//        } catch (NoSuchAlgorithmException e) {
//            return null;
//        } finally {
//            if (stream != null) {
//                try {
//                    stream.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//    }

//    public static FormatPlugin getPlugin(PluginCollection pluginCollection, AbstractBook book) throws BookReadingException {
//        final ZLFile file = fileByBook(book);
//        final FormatPlugin plugin = pluginCollection.getPlugin(file);
//        if (plugin == null) {
//            throw new BookReadingException("pluginNotFound", file);
//        }
//        return plugin;
//    }
//
//    public static String getEncoding(AbstractBook book, PluginCollection pluginCollection) {
//        if (book.getEncodingNoDetection() == null) {
//            try {
//                BookUtil.getPlugin(pluginCollection, book).detectLanguageAndEncoding(book);
//            } catch (BookReadingException e) {
//            }
//            if (book.getEncodingNoDetection() == null) {
//                book.setEncoding("utf-8");
//            }
//        }
//        return book.getEncodingNoDetection();
//    }

    static void readMetainfo(AbstractBook book, FormatPlugin plugin, String bookTitle, String bookAuthor) throws BookReadingException {
//        book.myEncoding = null;
//        book.myLanguage = null;
//        book.setTitle(null);
//        book.myAuthors = null;
//        book.myTags = null;
//        book.mySeriesInfo = null;
//        book.myUids = null;

//        plugin.readMetainfo(book);//读取信息，并对book进行赋值
//        if (book.myUids == null || book.myUids.isEmpty()) {
//            plugin.readUids(book);
//        }

        if (book.isTitleEmpty() && !TextUtils.isEmpty(bookTitle)) {
            book.setTitle(bookTitle);
        }

        if (!TextUtils.isEmpty(bookAuthor)) {
            book.addAuthor(bookAuthor);
        } else {
            book.addAuthor("未署名");
        }
    }

    public static ZLFile fileByBook(AbstractBook book) {
        return ZLFile.createFileByPath(book.getPath());
    }
}
