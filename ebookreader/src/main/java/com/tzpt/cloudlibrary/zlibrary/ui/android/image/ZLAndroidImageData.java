package com.tzpt.cloudlibrary.zlibrary.ui.android.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tzpt.cloudlibrary.zlibrary.core.image.ZLImageData;
import com.tzpt.cloudlibrary.zlibrary.core.view.ZLPaintContext;

/**
 * 图片数据
 * Created by Administrator on 2017/4/8.
 */

public abstract class ZLAndroidImageData implements ZLImageData {
    private Bitmap myBitmap;
    private int myRealWidth;
    private int myRealHeight;
    private ZLPaintContext.Size myLastRequestedSize = null;
    private ZLPaintContext.ScalingType myLastRequestedScaling = ZLPaintContext.ScalingType.OriginalSize;

    protected ZLAndroidImageData() {
    }

    protected abstract Bitmap decodeWithOptions(BitmapFactory.Options options);

    public Bitmap getFullSizeBitmap() {
        return getBitmap(null, ZLPaintContext.ScalingType.FitMaximum);
    }

    public Bitmap getBitmap(int maxWidth, int maxHeight) {
        return getBitmap(new ZLPaintContext.Size(maxWidth, maxHeight), ZLPaintContext.ScalingType.FitMaximum);
    }

    public synchronized Bitmap getBitmap(ZLPaintContext.Size maxSize, ZLPaintContext.ScalingType scaling) {
        if (scaling != ZLPaintContext.ScalingType.OriginalSize) {
            if (maxSize == null || maxSize.Width <= 0 || maxSize.Height <= 0) {
                return null;
            }
        }
        if (maxSize == null) {
            maxSize = new ZLPaintContext.Size(-1, -1);
        }
        if (!maxSize.equals(myLastRequestedSize) || scaling != myLastRequestedScaling) {
            myLastRequestedSize = maxSize;
            myLastRequestedScaling = scaling;

            if (myBitmap != null) {
                myBitmap.recycle();
                myBitmap = null;
            }
            try {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                if (myRealWidth <= 0) {
                    options.inJustDecodeBounds = true;
                    decodeWithOptions(options);
                    myRealWidth = options.outWidth;
                    myRealHeight = options.outHeight;
                }
                options.inJustDecodeBounds = false;
                int coefficient = 1;
                if (scaling == ZLPaintContext.ScalingType.IntegerCoefficient) {
                    if (myRealHeight > maxSize.Height || myRealWidth > maxSize.Width) {
                        coefficient = 1 + Math.max(
                                (myRealHeight - 1) / maxSize.Height,
                                (myRealWidth - 1) / maxSize.Width
                        );
                    }
                }
                options.inSampleSize = coefficient;
                myBitmap = decodeWithOptions(options);
                if (myBitmap != null) {
                    switch (scaling) {
                        case OriginalSize:
                            break;
                        case FitMaximum:
                        {
                            final int bWidth = myBitmap.getWidth();
                            final int bHeight = myBitmap.getHeight();
                            if (bWidth > 0 && bHeight > 0 &&
                                    bWidth != maxSize.Width && bHeight != maxSize.Height) {
                                final int w, h;
                                if (bWidth * maxSize.Height > bHeight * maxSize.Width) {
                                    w = maxSize.Width;
                                    h = Math.max(1, bHeight * w / bWidth);
                                } else {
                                    h = maxSize.Height;
                                    w = Math.max(1, bWidth * h / bHeight);
                                }
                                final Bitmap scaled =
                                        Bitmap.createScaledBitmap(myBitmap, w, h, false);
                                if (scaled != null) {
                                    myBitmap = scaled;
                                }
                            }
                            break;
                        }
                        case IntegerCoefficient:
                        {
                            final int bWidth = myBitmap.getWidth();
                            final int bHeight = myBitmap.getHeight();
                            if (bWidth > 0 && bHeight > 0 &&
                                    (bWidth > maxSize.Width || bHeight > maxSize.Height)) {
                                final int w, h;
                                if (bWidth * maxSize.Height > bHeight * maxSize.Width) {
                                    w = maxSize.Width;
                                    h = Math.max(1, bHeight * w / bWidth);
                                } else {
                                    h = maxSize.Height;
                                    w = Math.max(1, bWidth * h / bHeight);
                                }
                                final Bitmap scaled =
                                        Bitmap.createScaledBitmap(myBitmap, w, h, false);
                                if (scaled != null) {
                                    myBitmap = scaled;
                                }
                            }
                            break;
                        }
                    }
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return myBitmap;
    }
}
