package com.tzpt.cloudlibrary.zlibrary.text.hyphenation;

/**
 * Created by Administrator on 2017/4/8.
 */
// 根据断字判断是哪种语言
public final class ZLTextHyphenationInfo {
    final boolean[] Mask;

    public ZLTextHyphenationInfo(int length) {
        Mask = new boolean[length - 1];
    }

    public boolean isHyphenationPossible(int position) {
        return (position < Mask.length && Mask[position]);
    }
}
