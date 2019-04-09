package com.tzpt.cloudlibrary.zlibrary.ui.android.image;

import com.tzpt.cloudlibrary.zlibrary.core.image.ZLImage;
import com.tzpt.cloudlibrary.zlibrary.core.image.ZLImageProxy;
import com.tzpt.cloudlibrary.zlibrary.core.image.ZLStreamImage;

/**
 * Created by Administrator on 2017/4/8.
 */

public final class ZLAndroidImageManager {
    private static ZLAndroidImageManager mInstance;

    public static ZLAndroidImageManager getInstance() {
        if (mInstance == null) {
            mInstance = new ZLAndroidImageManager();
        }
        return mInstance;
    }

    private ZLAndroidImageManager() {

    }

    public ZLAndroidImageData getImageData(ZLImage image) {
        if (image instanceof ZLImageProxy) {
            return getImageData(((ZLImageProxy) image).getRealImage());
        } else if (image instanceof ZLStreamImage) {
            return new InputStreamImageData((ZLStreamImage) image);
        } else if (image instanceof ZLBitmapImage) {
            return BitmapImageData.get((ZLBitmapImage) image);
        } else {
            // unknown image type or null
            return null;
        }
    }
}
