package com.tzpt.cloudlibrary.zlibrary.text.model;

/**
 * Created by Administrator on 2017/4/9.
 */

public class ZLTextSpecialParagraphImpl extends ZLTextParagraphImpl {
    private final byte myKind;

    ZLTextSpecialParagraphImpl(byte kind, ZLTextPlainModel model, int offset) {
        super(model, offset);
        myKind = kind;
    }

    public byte getKind() {
        return myKind;
    }
}
