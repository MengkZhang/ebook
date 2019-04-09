package com.tzpt.cloudlibrary.zlibrary.text.model;

/**
 * Created by Administrator on 2017/4/9.
 */

public class ZLTextParagraphImpl implements ZLTextParagraph {
    private final ZLTextPlainModel mModel;
    private final int mIndex;

    ZLTextParagraphImpl(ZLTextPlainModel model, int index) {
        mModel = model;
        mIndex = index;
    }

    public EntryIterator iterator() {
        return mModel.new EntryIteratorImpl(mIndex);
    }

    public byte getKind() {
        return Kind.TEXT_PARAGRAPH;
    }
}
