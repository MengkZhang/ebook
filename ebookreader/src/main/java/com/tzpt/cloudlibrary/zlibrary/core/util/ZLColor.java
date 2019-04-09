package com.tzpt.cloudlibrary.zlibrary.core.util;

/**
 * Created by Administrator on 2017/4/7.
 */

public class ZLColor {
    public final short Red;
    public final short Green;
    public final short Blue;

    public ZLColor(int r, int g, int b) {
        Red = (short)(r & 0xFF);
        Green = (short)(g & 0xFF);
        Blue = (short)(b & 0xFF);
    }

    public ZLColor(int intValue) {
        Red = (short)((intValue >> 16) & 0xFF);
        Green = (short)((intValue >> 8) & 0xFF);
        Blue = (short)(intValue & 0xFF);
    }

    public int intValue() {
        return (Red << 16) + (Green << 8) + Blue;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ZLColor)) {
            return false;
        }

        ZLColor color = (ZLColor)o;
        return color.Red == Red && color.Green == Green && color.Blue == Blue;
    }

    @Override
    public int hashCode() {
        return intValue();
    }

    @Override
    public String toString() {
        return new StringBuilder("ZLColor(")
                .append(String.valueOf(Red)).append(", ")
                .append(String.valueOf(Green)).append(", ")
                .append(String.valueOf(Blue)).append(")")
                .toString();
    }
}
