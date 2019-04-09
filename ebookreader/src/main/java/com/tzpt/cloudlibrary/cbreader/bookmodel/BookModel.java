package com.tzpt.cloudlibrary.cbreader.bookmodel;

import com.tzpt.cloudlibrary.cbreader.book.Book;
import com.tzpt.cloudlibrary.cbreader.formats.BookReadingException;
import com.tzpt.cloudlibrary.cbreader.formats.BuiltinFormatPlugin;
import com.tzpt.cloudlibrary.cbreader.formats.FormatPlugin;
import com.tzpt.cloudlibrary.zlibrary.core.image.ZLImage;
import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextModel;
import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextPlainModel;

import java.util.HashMap;

/**
 * 书的数据结构
 * Created by Administrator on 2017/4/9.
 */

public final class BookModel {
    //FormatPlugin插件方式，为了让不同的格式走不同的代码去生成Model
    public static BookModel createModel(Book book, FormatPlugin plugin) throws BookReadingException {
        if (plugin instanceof BuiltinFormatPlugin) {
            final BookModel model = new BookModel(book);
            ((BuiltinFormatPlugin) plugin).readModel(model); // 进入本地解析 传入图书类,图书
            return model;
        }

        throw new BookReadingException(
                "unknownPluginType", null, new String[]{String.valueOf(plugin)}
        );
    }

    public final Book Book;
    public final TOCTree TOCTree = new TOCTree();

//    private CachedCharStorage myInternalHyperlinks;
    private final HashMap<String, ZLImage> mImageMap = new HashMap<>();
    private ZLTextModel mBookTextModel;
//    private final HashMap<String, ZLTextModel> myFootnotes = new HashMap<String, ZLTextModel>();

    /**
     * 标注
     */
//    public static final class Label {
//        final String ModelId;
//        final int ParagraphIndex;
//
//        Label(String modelId, int paragraphIndex) {
//            ModelId = modelId;
//            ParagraphIndex = paragraphIndex;
//        }
//    }

    protected BookModel(Book book) {
        Book = book;
    }

//    public interface LabelResolver {
//        List<String> getCandidates(String id);
//    }
//
//    private LabelResolver myResolver;
//
//    public void setLabelResolver(LabelResolver resolver) {
//        myResolver = resolver;
//    }

//    public Label getLabel(String id) {
//        Label label = getLabelInternal(id);
//        if (label == null && myResolver != null) {
//            for (String candidate : myResolver.getCandidates(id)) {
//                label = getLabelInternal(candidate);
//                if (label != null) {
//                    break;
//                }
//            }
//        }
//        return label;
//    }

//    public void registerFontFamilyList(String[] families) {
//        mFontManager.index(Arrays.asList(families));
//    }
//
//    public void registerFontEntry(String family, FontEntry entry) {
//        mFontManager.Entries.put(family, entry);
//    }

//    public void registerFontEntry(String family, FileInfo normal, FileInfo bold, FileInfo italic, FileInfo boldItalic) {
//        registerFontEntry(family, new FontEntry(family, normal, bold, italic, boldItalic));
//    }

    public ZLTextModel createTextModel(
            String id, String language, int paragraphsNumber,
            int[] entryIndices, int[] entryOffsets,
            int[] paragraphLenghts, int[] textSizes, byte[] paragraphKinds,
            String directoryName, String fileExtension, int blocksNumber
    ) {
        return new ZLTextPlainModel(
                id, language, paragraphsNumber,
                entryIndices, entryOffsets,
                paragraphLenghts, textSizes, paragraphKinds,
                directoryName, fileExtension, blocksNumber, mImageMap
        );
    }

    public void setBookTextModel(ZLTextModel model) {
        mBookTextModel = model;

    }

//    public void setFootnoteModel(ZLTextModel model) {
//        myFootnotes.put(model.getId(), model);
//    }

    public ZLTextModel getTextModel() {
        return mBookTextModel;
    }

//    public ZLTextModel getFootnoteModel(String id) {
//        return myFootnotes.get(id);
//    }

    public void addImage(String id, ZLImage image) {
        mImageMap.put(id, image);
    }

//    public void initInternalHyperlinks(String directoryName, String fileExtension, int blocksNumber) {
//        myInternalHyperlinks = new CachedCharStorage(directoryName, fileExtension, blocksNumber);
//    }

    private TOCTree mCurrentTree = TOCTree;

    public void addTOCItem(String text, int reference) {
        mCurrentTree = new TOCTree(mCurrentTree);
        mCurrentTree.setText(text);
        mCurrentTree.setParagraphIndex(reference);
    }

    public void leaveTOCItem() {
        mCurrentTree = mCurrentTree.Parent;
        if (mCurrentTree == null) {
            mCurrentTree = TOCTree;
        }
    }
}
