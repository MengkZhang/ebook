package com.tzpt.cloudlibrary.zlibrary.text.model;

import com.tzpt.cloudlibrary.zlibrary.core.image.ZLImage;

import java.util.Map;

/**
 * 所有图片数据
 * Created by Administrator on 2017/4/8.
 */

public final class ZLImageEntry {
    private final Map<String, ZLImage> myImageMap;
    public final String Id;
//    public final short VOffset;
    public final boolean IsCover;

    ZLImageEntry(Map<String, ZLImage> imageMap, String id, boolean isCover) {
        myImageMap = imageMap;
        Id = id;
//        VOffset = vOffset;
        IsCover = isCover;
    }

    public ZLImage getImage() {
        return myImageMap.get(Id);
    }
}
