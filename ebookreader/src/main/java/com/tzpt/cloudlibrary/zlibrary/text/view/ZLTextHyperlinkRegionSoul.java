package com.tzpt.cloudlibrary.zlibrary.text.view;

import java.util.List;

/**
 * Created by Administrator on 2017/4/8.
 */

public class ZLTextHyperlinkRegionSoul extends ZLTextRegion.Soul {
    public final ZLTextHyperlink Hyperlink;

    private static int startElementIndex(ZLTextHyperlink hyperlink, int fallback) {
        final List<Integer> indexes = hyperlink.elementIndexes();
        return indexes.isEmpty() ? fallback : indexes.get(0);
    }

    private static int endElementIndex(ZLTextHyperlink hyperlink, int fallback) {
        final List<Integer> indexes = hyperlink.elementIndexes();
        return indexes.isEmpty() ? fallback : indexes.get(indexes.size() - 1);
    }

    ZLTextHyperlinkRegionSoul(ZLTextPosition position, ZLTextHyperlink hyperlink) {
        super(
                position.getParagraphIndex(),
                startElementIndex(hyperlink, position.getElementIndex()),
                endElementIndex(hyperlink, position.getElementIndex())
        );
        Hyperlink = hyperlink;
    }
}
