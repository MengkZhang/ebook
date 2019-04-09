package com.tzpt.cloudlibrary.cbreader.bookmodel;

import com.tzpt.cloudlibrary.zlibrary.core.tree.ZLTree;

/**
 * 章节/目录
 * Created by Administrator on 2017/4/8.
 */

public class TOCTree extends ZLTree<TOCTree> {
    //目录名字
    private String mText;
    //目录索引
    private int mParagraphIndex = -1;

    TOCTree() {
        super();
    }

    TOCTree(TOCTree parent) {
        super(parent);
    }

    public final String getText() {
        return mText;
    }

    // faster replacement for
    // return text.trim().replaceAll("[\t ]+", " ");
    private static String trim(String text) {
        final char[] data = text.toCharArray();
        int count = 0;
        int shift = 0;
        boolean changed = false;
        char space = ' ';
        for (int i = 0; i < data.length; ++i) {
            final char ch = data[i];
            if (ch == ' ' || ch == '\t') {
                ++count;
                space = ch;
            } else {
                if (count > 0) {
                    if (count == i) {
                        shift += count;
                        changed = true;
                    } else {
                        shift += count - 1;
                        if (shift > 0 || space == '\t') {
                            data[i - shift - 1] = ' ';
                            changed = true;
                        }
                    }
                    count = 0;
                }
                if (shift > 0) {
                    data[i - shift] = data[i];
                }
            }
        }
        if (count > 0) {
            changed = true;
            shift += count;
        }
        return changed ? new String(data, 0, data.length - shift) : text;
    }

    public final void setText(String text) {
        mText = text != null ? trim(text) : null;
    }

    public int getParagraphIndex() {
        return mParagraphIndex;
    }

    void setParagraphIndex(int reference) {
        mParagraphIndex = reference;
    }

}
