package com.tzpt.cloudlibrary.zlibrary.text.model;

/**
 * Created by Administrator on 2017/4/8.
 */

public interface ZLTextModel {
    String getId();

    String getLanguage();

    int getParagraphsNumber();

    ZLTextParagraph getParagraph(int index);

//    void removeAllMarks();
//    ZLTextMark getFirstMark();
//    ZLTextMark getLastMark();
//    ZLTextMark getNextMark(ZLTextMark position);
//    ZLTextMark getPreviousMark(ZLTextMark position);
//
//    List<ZLTextMark> getMarks();

    // text length for paragraphs from 0 to index
    int getTextLength(int index);

    int findParagraphByTextLength(int length);

//    int search(final String text, int startIndex, int endIndex, boolean ignoreCase);
}
