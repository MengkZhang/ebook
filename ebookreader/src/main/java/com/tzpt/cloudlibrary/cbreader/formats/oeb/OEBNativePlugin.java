package com.tzpt.cloudlibrary.cbreader.formats.oeb;

import com.tzpt.cloudlibrary.cbreader.book.BookUtil;
import com.tzpt.cloudlibrary.cbreader.bookmodel.BookModel;
import com.tzpt.cloudlibrary.cbreader.formats.BookReadingException;
import com.tzpt.cloudlibrary.cbreader.formats.NativeFormatPlugin;
import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;

/**
 * Created by Administrator on 2017/4/10.
 */

public class OEBNativePlugin extends NativeFormatPlugin {
    public OEBNativePlugin() {
        super("ePub");
    }

    @Override
    public void readModel(BookModel model) throws BookReadingException {
        final ZLFile file = BookUtil.fileByBook(model.Book);
        file.setCached(true);
        try {
            super.readModel(model); // 调用父类中的本地方法
//            model.setLabelResolver(new BookModel.LabelResolver() {
//                public List<String> getCandidates(String id) {
//                    final int index = id.indexOf("#");
//                    return index > 0 ? Collections.<String>singletonList(id.substring(0, index))
//                            : Collections.<String>emptyList();
//                }
//            });
        } finally {
            file.setCached(false);
        }
    }
//
//    @Override
//    public EncodingCollection supportedEncodings() {
//        return new AutoEncodingCollection();
//    }

//    @Override
//    public void detectLanguageAndEncoding(AbstractBook book) {
//        book.setEncoding("auto");
//    }


    @Override
    public int priority() {
        return 0;
    }
}
