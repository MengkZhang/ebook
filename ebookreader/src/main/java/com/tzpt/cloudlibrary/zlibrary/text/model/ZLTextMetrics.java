package com.tzpt.cloudlibrary.zlibrary.text.model;

/**
 * Created by Administrator on 2017/4/8.
 */

public final class ZLTextMetrics {
    final int DPI;
    final int FullWidth;
    final int FullHeight;
    final int FontSize;

    public ZLTextMetrics(int dpi, int fullWidth, int fullHeight, int fontSize) {
        DPI = dpi;
        FullWidth = fullWidth;
        FullHeight = fullHeight;
        FontSize = fontSize;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ZLTextMetrics)) {
            return false;
        }
        final ZLTextMetrics oo = (ZLTextMetrics)o;
        return
                DPI == oo.DPI &&
                        FullWidth == oo.FullWidth &&
                        FullHeight == oo.FullHeight;
    }

    @Override
    public int hashCode() {
        return DPI + 13 * (FullHeight + 13 * FullWidth);
    }
}
