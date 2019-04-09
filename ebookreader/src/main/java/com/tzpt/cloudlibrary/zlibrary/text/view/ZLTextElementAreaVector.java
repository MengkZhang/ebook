package com.tzpt.cloudlibrary.zlibrary.text.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/4/8.
 */

public final class ZLTextElementAreaVector {
    private final List<ZLTextElementArea> mAreas = Collections.synchronizedList(new ArrayList<ZLTextElementArea>());
    private ZLTextRegion mCurrentElementRegion;

    void clear() {
        synchronized (mAreas) {
            mCurrentElementRegion = null;
            mAreas.clear();
        }
    }

    public int size() {
        return mAreas.size();
    }

    List<ZLTextElementArea> areas() {
        synchronized (mAreas) {
            return new ArrayList<>(mAreas);
        }
    }

    public ZLTextElementArea getFirstArea() {
        synchronized (mAreas) {
            return mAreas.isEmpty() ? null : mAreas.get(0);
        }
    }

    public ZLTextElementArea getLastArea() {
        synchronized (mAreas) {
            return mAreas.isEmpty() ? null : mAreas.get(mAreas.size() - 1);
        }
    }

    public boolean add(ZLTextElementArea area) {
        synchronized (mAreas) {
            if (mCurrentElementRegion != null
                    && mCurrentElementRegion.getSoul().accepts(area)) {
                mCurrentElementRegion.extend();
            } else {
                ZLTextRegion.Soul soul = null;
//                final ZLTextHyperlink hyperlink = area.Style.Hyperlink;
//                if (hyperlink.Id != null) {
//                    soul = new ZLTextHyperlinkRegionSoul(area, hyperlink);
//                } else
                if (area.Element instanceof ZLTextImageElement) {
                    soul = new ZLTextImageRegionSoul(area, (ZLTextImageElement) area.Element);
                } else if (area.Element instanceof ZLTextVideoElement) {
                    soul = new ZLTextVideoRegionSoul(area, (ZLTextVideoElement) area.Element);
                } else if (area.Element instanceof ZLTextWord && !((ZLTextWord) area.Element).isASpace()) {
                    soul = new ZLTextWordRegionSoul(area, (ZLTextWord) area.Element);
                }
//                else if (area.Element instanceof ExtensionElement) {
//                    soul = new ExtensionRegionSoul(area, (ExtensionElement) area.Element);
//                }
                if (soul != null) {
                    mCurrentElementRegion = new ZLTextRegion(soul, mAreas, mAreas.size());
                } else {
                    mCurrentElementRegion = null;
                }
            }
            return mAreas.add(area);
        }
    }

}
