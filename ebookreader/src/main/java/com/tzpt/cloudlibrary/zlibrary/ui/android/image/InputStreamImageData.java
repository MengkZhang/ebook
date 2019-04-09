package com.tzpt.cloudlibrary.zlibrary.ui.android.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.tzpt.cloudlibrary.zlibrary.core.image.ZLStreamImage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/4/8.
 */

public final class InputStreamImageData extends ZLAndroidImageData {
    private final ZLStreamImage myImage;

    InputStreamImageData(ZLStreamImage image) {
        myImage = image;
    }

    protected Bitmap decodeWithOptions(BitmapFactory.Options options) {
        final InputStream stream = myImage.inputStream();
        if (stream == null) {
            return null;
        }

        final Bitmap bmp = BitmapFactory.decodeStream(stream, new Rect(), options);
        try {
            stream.close();
        } catch (IOException e) {
        }
        return bmp;
    }
}
