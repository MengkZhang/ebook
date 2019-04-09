package com.tzpt.cloudlibrary.cbreader.bookmodel;

/**
 * Created by Administrator on 2017/4/9.
 */

public interface FBTextKind {
    byte REGULAR = 0;
    byte TITLE = 1;
    byte SECTION_TITLE = 2;
    byte POEM_TITLE = 3;
    byte SUBTITLE = 4;
    byte ANNOTATION = 5;
    byte EPIGRAPH = 6;
    byte STANZA = 7;
    byte VERSE = 8;
    byte PREFORMATTED = 9;
    byte IMAGE = 10;
    //byte END_OF_SECTION = 11;
    byte CITE = 12;
    byte AUTHOR = 13;
    byte DATE = 14;
    byte INTERNAL_HYPERLINK = 15;
    byte FOOTNOTE = 16;
    byte EMPHASIS = 17;
    byte STRONG = 18;
    byte SUB = 19;
    byte SUP = 20;
    byte CODE = 21;
    byte STRIKETHROUGH = 22;
    //byte CONTENTS_TABLE_ENTRY = 23;
    //byte LIBRARY_AUTHOR_ENTRY = 24;
    //byte LIBRARY_BOOK_ENTRY = 25;
    //byte LIBRARY_ENTRY = 25;
    //byte RECENT_BOOK_LIST = 26;
    byte ITALIC = 27;
    byte BOLD = 28;
    byte DEFINITION = 29;
    byte DEFINITION_DESCRIPTION = 30;
    byte H1 = 31;
    byte H2 = 32;
    byte H3 = 33;
    byte H4 = 34;
    byte H5 = 35;
    byte H6 = 36;
    byte EXTERNAL_HYPERLINK = 37;
    //byte BOOK_HYPERLINK = 38;

    byte XHTML_TAG_P = 51;
}
