package com.tzpt.cloudlibrary.zlibrary.text.view;

import com.tzpt.cloudlibrary.zlibrary.core.image.ZLImageData;

/**
 * Created by Administrator on 2017/4/8.
 */

public class ZLTextImageElement extends ZLTextElement {
    public final String Id;
    public final ZLImageData ImageData;
    public final String URL;
    public final boolean IsCover; // 这里判断有问题

    ZLTextImageElement(String id, ZLImageData imageData, String url, boolean isCover) {
        Id = id;
        ImageData = imageData;
        URL = url;
        IsCover = isCover;
    }
}
