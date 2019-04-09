package com.tzpt.cloudlibrary.zlibrary.text.view;

import java.util.List;

/**
 * Created by Administrator on 2017/4/8.
 */

public final class ZLTextRegion {
    public static abstract class Soul implements Comparable<Soul> {
        final int ParagraphIndex;
        final int StartElementIndex;
        final int EndElementIndex;

        protected Soul(int paragraphIndex, int startElementIndex, int endElementIndex) {
            ParagraphIndex = paragraphIndex;
            StartElementIndex = startElementIndex;
            EndElementIndex = endElementIndex;
        }

        final boolean accepts(ZLTextElementArea area) {
            return compareTo(area) == 0;
        }

        @Override
        public final boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof Soul)) {
                return false;
            }
            final Soul soul = (Soul) other;
            return
                    ParagraphIndex == soul.ParagraphIndex &&
                            StartElementIndex == soul.StartElementIndex &&
                            EndElementIndex == soul.EndElementIndex;
        }

        public final int compareTo(Soul soul) {
            if (ParagraphIndex != soul.ParagraphIndex) {
                return ParagraphIndex < soul.ParagraphIndex ? -1 : 1;
            }
            if (EndElementIndex < soul.StartElementIndex) {
                return -1;
            }
            if (StartElementIndex > soul.EndElementIndex) {
                return 1;
            }
            return 0;
        }

        public final int compareTo(ZLTextElementArea area) {
            if (ParagraphIndex != area.getParagraphIndex()) {
                return ParagraphIndex < area.getParagraphIndex() ? -1 : 1;
            }
            if (EndElementIndex < area.getElementIndex()) {
                return -1;
            }
            if (StartElementIndex > area.getElementIndex()) {
                return 1;
            }
            return 0;
        }

        public final int compareTo(ZLTextPosition position) {
            final int ppi = position.getParagraphIndex();
            if (ParagraphIndex != ppi) {
                return ParagraphIndex < ppi ? -1 : 1;
            }
            final int pei = position.getElementIndex();
            if (EndElementIndex < pei) {
                return -1;
            }
            if (StartElementIndex > pei) {
                return 1;
            }
            return 0;
        }
    }

    private final Soul mySoul;
    // this field must be accessed in synchronized context only
    private final List<ZLTextElementArea> myAreaList;
    private ZLTextElementArea[] myAreas;
    private final int myFromIndex;
    private int myToIndex;

    ZLTextRegion(Soul soul, List<ZLTextElementArea> list, int fromIndex) {
        mySoul = soul;
        myAreaList = list;
        myFromIndex = fromIndex;
        myToIndex = fromIndex + 1;
    }

    void extend() {
        ++myToIndex;
    }

    public Soul getSoul() {
        return mySoul;
    }

    ZLTextElementArea[] textAreas() {
        if (myAreas == null || myAreas.length != myToIndex - myFromIndex) {
            synchronized (myAreaList) {
                myAreas = new ZLTextElementArea[myToIndex - myFromIndex];
                for (int i = 0; i < myAreas.length; ++i) {
                    myAreas[i] = myAreaList.get(i + myFromIndex);
                }
            }
        }
        return myAreas;
    }

    ZLTextElementArea getFirstArea() {
        return textAreas()[0];
    }

    ZLTextElementArea getLastArea() {
        final ZLTextElementArea[] areas = textAreas();
        return areas[areas.length - 1];
    }

    public float getLeft() {
        float left = Integer.MAX_VALUE;
        for (ZLTextElementArea area : textAreas()) {
            left = Math.min(area.XStart, left);
        }
        return left;
    }

    public float getRight() {
        float right = Integer.MIN_VALUE;
        for (ZLTextElementArea area : textAreas()) {
            right = Math.max(area.XEnd, right);
        }
        return right;
    }

    public float getTop() {
        return getFirstArea().YStart;
    }

    public float getBottom() {
        return getLastArea().YEnd;
    }

    boolean isAtRightOf(ZLTextRegion other) {
        return
                other == null ||
                        getFirstArea().XStart >= other.getLastArea().XEnd;
    }

    boolean isAtLeftOf(ZLTextRegion other) {
        return other == null || other.isAtRightOf(this);
    }

    boolean isUnder(ZLTextRegion other) {
        return
                other == null ||
                        getFirstArea().YStart >= other.getLastArea().YEnd;
    }

    boolean isOver(ZLTextRegion other) {
        return other == null || other.isUnder(this);
    }

    boolean isExactlyUnder(ZLTextRegion other) {
        if (other == null) {
            return true;
        }
        if (!isUnder(other)) {
            return false;
        }
        final ZLTextElementArea[] areas0 = textAreas();
        final ZLTextElementArea[] areas1 = other.textAreas();
        for (ZLTextElementArea i : areas0) {
            for (ZLTextElementArea j : areas1) {
                if (i.XStart <= j.XEnd && j.XStart <= i.XEnd) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean isExactlyOver(ZLTextRegion other) {
        return other == null || other.isExactlyUnder(this);
    }

    public boolean isVerticallyAligned() {
        for (ZLTextElementArea area : textAreas()) {
            if (!area.Style.isVerticallyAligned()) {
                return false;
            }
        }
        return true;
    }
}
