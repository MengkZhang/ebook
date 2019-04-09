package com.tzpt.cloudlibrary.zlibrary.text.view;

/**
 * Created by Administrator on 2017/4/8.
 */

public class ZLTextVideoRegionSoul extends ZLTextRegion.Soul {
    public final ZLTextVideoElement VideoElement;

    ZLTextVideoRegionSoul(ZLTextPosition position, ZLTextVideoElement videoElement) {
        super(position.getParagraphIndex(), position.getElementIndex(), position.getElementIndex());
        VideoElement = videoElement;
    }
}
