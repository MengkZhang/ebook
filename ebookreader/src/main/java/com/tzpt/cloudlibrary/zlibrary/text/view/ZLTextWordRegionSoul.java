package com.tzpt.cloudlibrary.zlibrary.text.view;

/**
 * Created by Administrator on 2017/4/8.
 */

public class ZLTextWordRegionSoul extends ZLTextRegion.Soul {
    public final ZLTextWord Word;

    ZLTextWordRegionSoul(ZLTextPosition position, ZLTextWord word) {
        super(position.getParagraphIndex(), position.getElementIndex(), position.getElementIndex());
        Word = word;
    }
}
