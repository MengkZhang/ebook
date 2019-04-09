package com.tzpt.cloudlibrary.zlibrary.text.view;

/**
 * Created by Administrator on 2017/4/8.
 */

public class ZLTextImageRegionSoul extends ZLTextRegion.Soul {
    public final ZLTextImageElement ImageElement;

    ZLTextImageRegionSoul(ZLTextPosition position, ZLTextImageElement imageElement) {
        super(position.getParagraphIndex(), position.getElementIndex(), position.getElementIndex());
        ImageElement = imageElement;
    }
}
