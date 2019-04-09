package com.tzpt.cloudlibrary.zlibrary.text.view;

/**
 * Created by Administrator on 2017/4/8.
 */

public abstract class ZLTextPosition implements Comparable<ZLTextPosition> {
    public abstract int getParagraphIndex();//段落索引
    public abstract int getElementIndex();//元素索引
    public abstract int getCharIndex();//字符索引

    public boolean samePositionAs(ZLTextPosition position) {
        return
                getParagraphIndex() == position.getParagraphIndex() &&
                        getElementIndex() == position.getElementIndex() &&
                        getCharIndex() == position.getCharIndex();
    }

    public int compareTo(ZLTextPosition position) {
        final int p0 = getParagraphIndex();
        final int p1 = position.getParagraphIndex();
        if (p0 != p1) {
            return p0 < p1 ? -1 : 1;
        }

        final int e0 = getElementIndex();
        final int e1 = position.getElementIndex();
        if (e0 != e1) {
            return e0 < e1 ? -1 : 1;
        }

        return getCharIndex() - position.getCharIndex();
    }

    public int compareToIgnoreChar(ZLTextPosition position) {
        final int p0 = getParagraphIndex();
        final int p1 = position.getParagraphIndex();
        if (p0 != p1) {
            return p0 < p1 ? -1 : 1;
        }

        return getElementIndex() - position.getElementIndex();
    }

    @Override
    public int hashCode() {
        return (getParagraphIndex() << 16) + (getElementIndex() << 8) + getCharIndex();
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof ZLTextPosition)) {
            return false;
        }
        final ZLTextPosition position = (ZLTextPosition)object;
        return
                getParagraphIndex() == position.getParagraphIndex() &&
                        getElementIndex() == position.getElementIndex() &&
                        getCharIndex() == position.getCharIndex();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + getParagraphIndex() + "," + getElementIndex() + "," + getCharIndex() + "]";
    }
}
