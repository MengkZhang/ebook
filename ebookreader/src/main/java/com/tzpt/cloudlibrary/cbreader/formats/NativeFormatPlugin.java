package com.tzpt.cloudlibrary.cbreader.formats;

import com.tzpt.cloudlibrary.cbreader.book.AbstractBook;
import com.tzpt.cloudlibrary.cbreader.book.BookUtil;
import com.tzpt.cloudlibrary.cbreader.bookmodel.BookModel;
import com.tzpt.cloudlibrary.cbreader.formats.oeb.OEBNativePlugin;
import com.tzpt.cloudlibrary.zlibrary.text.model.CachedCharStorageException;
import com.tzpt.cloudlibrary.zlibrary.ui.android.library.ZLAndroidLibrary;

/**
 * Created by Administrator on 2017/4/10.
 */

public class NativeFormatPlugin extends BuiltinFormatPlugin {
    private static final Object ourNativeLock = new Object();

    public static NativeFormatPlugin create(String fileType) {
//		if ("fb2".equals(fileType)) {
//			return new FB2NativePlugin(systemInfo);
//		} else
        if ("ePub".equals(fileType)) {
            return new OEBNativePlugin();
        } else {
            return new NativeFormatPlugin(fileType);
        }
    }

    protected NativeFormatPlugin(String fileType) {
        super(fileType);
    }

    @Override
    synchronized public void readMetainfo(AbstractBook book) throws BookReadingException {
        final int code;
        synchronized (ourNativeLock) {
            code = readMetainfoNative(book);
        }
        if (code != 0) {
            throw new BookReadingException(
                    "nativeCodeFailure",
                    BookUtil.fileByBook(book),
                    new String[]{String.valueOf(code), book.getPath()}
            );
        }
    }

    private native int readMetainfoNative(AbstractBook book);

//    @Override
//    public List<FileEncryptionInfo> readEncryptionInfos(AbstractBook book) {
//        final FileEncryptionInfo[] infos;
//        synchronized (ourNativeLock) {
//            infos = readEncryptionInfosNative(book);
//        }
//        return infos != null
//                ? Arrays.<FileEncryptionInfo>asList(infos)
//                : Collections.<FileEncryptionInfo>emptyList();
//    }

//    private native FileEncryptionInfo[] readEncryptionInfosNative(AbstractBook book);

//    @Override
//    synchronized public void readUids(AbstractBook book) throws BookReadingException {
//        synchronized (ourNativeLock) {
//            readUidsNative(book);
//        }
//        if (book.uids().isEmpty()) {
//            book.addUid(BookUtil.createUid(book, "SHA-256"));
//        }
//    }

//    private native boolean readUidsNative(AbstractBook book);

//    @Override
//    public void detectLanguageAndEncoding(AbstractBook book) {
//        synchronized (ourNativeLock) {
//            detectLanguageAndEncodingNative(book);
//        }
//    }

//    private native void detectLanguageAndEncodingNative(AbstractBook book);

    // 修改传入的model
    @Override
    synchronized public void readModel(BookModel model) throws BookReadingException {
        final int code;
        final String tempDirectory = ZLAndroidLibrary.Instance().getExternalCacheDir();
        synchronized (ourNativeLock) {
            // 设置缓存路径
            code = readModelNative(model, tempDirectory + "/" + model.Book.getTitle()); // 核心
        }
        switch (code) {
            case 0:
                return;
            case 3:
                throw new CachedCharStorageException(
                        "Cannot write file from native code to " + tempDirectory
                );
            default:
                throw new BookReadingException(
                        "nativeCodeFailure",
                        BookUtil.fileByBook(model.Book),
                        new String[]{String.valueOf(code), model.Book.getPath()}
                );
        }
    }

    private native int readModelNative(BookModel model, String cacheDir);

//    @Override
//    public final ZLFileImageProxy readCover(ZLFile file) {
//        return new ZLFileImageProxy(file) {
//            @Override
//            protected ZLFileImage retrieveRealImage() {
//                final ZLFileImage[] box = new ZLFileImage[1];
//                synchronized (ourNativeLock) {
//                    readCoverNative(File, box);
//                }
//                return box[0];
//            }
//        };
//    }

//    private native void readCoverNative(ZLFile file, ZLFileImage[] box);

//    @Override
//    public String readAnnotation(ZLFile file) {
//        synchronized (ourNativeLock) {
//            return readAnnotationNative(file);
//        }
//    }

//    private native String readAnnotationNative(ZLFile file);

    @Override
    public int priority() {
        return 5;
    }

//    @Override
//    public EncodingCollection supportedEncodings() {
//        return JavaEncodingCollection.Instance();
//    }

    @Override
    public String toString() {
        return "NativeFormatPlugin [" + supportedFileType() + "]";
    }
}
