package com.tzpt.cloudlibrary.reader.util;

/**
 * Created by Administrator on 2017/4/7.
 */

public class ComparisonUtil {
    public static boolean equal(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    public static int hashCode(Object o) {
        return o != null ? o.hashCode() : 0;
    }
}
