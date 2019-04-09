package com.tzpt.cloudlibrary.zlibrary.text.hyphenation;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/4/8.
 */

public final class ZLTextTeXHyphenator extends ZLTextHyphenator {
    private final HashMap<ZLTextTeXHyphenationPattern, ZLTextTeXHyphenationPattern> myPatternTable = new HashMap<>();
    private int myMaxPatternLength;

    public void unload() {
        myPatternTable.clear();
        myMaxPatternLength = 0;
    }

    public void hyphenate(char[] stringToHyphenate, boolean[] mask, int length) {
        if (myPatternTable.isEmpty()) {
            for (int i = 0; i < length - 1; i++) {
                mask[i] = false;
            }
            return;
        }

        byte[] values = new byte[length + 1];

        final HashMap<ZLTextTeXHyphenationPattern, ZLTextTeXHyphenationPattern> table = myPatternTable;
        ZLTextTeXHyphenationPattern pattern =
                new ZLTextTeXHyphenationPattern(stringToHyphenate, 0, length, false);
        for (int offset = 0; offset < length - 1; offset++) {
            int len = Math.min(length - offset, myMaxPatternLength) + 1;
            pattern.update(stringToHyphenate, offset, len - 1);
            while (--len > 0) {
                pattern.reset(len);
                ZLTextTeXHyphenationPattern toApply = table.get(pattern);
                if (toApply != null) {
                    toApply.apply(values, offset);
                }
            }
        }

        for (int i = 0; i < length - 1; i++) {
            mask[i] = (values[i + 1] % 2) == 1;
        }
    }
}
