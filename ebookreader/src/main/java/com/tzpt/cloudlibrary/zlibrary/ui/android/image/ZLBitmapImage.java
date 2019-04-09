package com.tzpt.cloudlibrary.zlibrary.ui.android.image;

import android.graphics.Bitmap;

import com.tzpt.cloudlibrary.zlibrary.core.image.ZLImage;

/**
 * Created by Administrator on 2017/4/8.
 */

public class ZLBitmapImage implements ZLImage {
    private final Bitmap myBitmap;

    public ZLBitmapImage(Bitmap bitmap) {
        myBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return myBitmap;
    }

    @Override
    public String getURI() {
        return "bitmap image";
    }
}
