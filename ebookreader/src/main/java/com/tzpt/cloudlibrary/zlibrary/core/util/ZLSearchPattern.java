package com.tzpt.cloudlibrary.zlibrary.core.util;

/**
 * Created by Administrator on 2017/4/9.
 */

public class ZLSearchPattern {
    final boolean IgnoreCase;
    final char[] LowerCasePattern;
    final char[] UpperCasePattern;

    public ZLSearchPattern(String pattern, boolean ignoreCase) {
        pattern = pattern.replace("\u200b", "");
        IgnoreCase = ignoreCase;
        if (IgnoreCase) {
            LowerCasePattern = pattern.toLowerCase().toCharArray();
            UpperCasePattern = pattern.toUpperCase().toCharArray();
        } else {
            LowerCasePattern = pattern.toCharArray();
            UpperCasePattern = null;
        }
    }

    public int getLength() {
        return LowerCasePattern.length;
    }
}
