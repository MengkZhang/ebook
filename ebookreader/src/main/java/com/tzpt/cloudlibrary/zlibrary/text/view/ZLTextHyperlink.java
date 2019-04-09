package com.tzpt.cloudlibrary.zlibrary.text.view;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/8.
 */

public class ZLTextHyperlink {
    public final byte Type;
    public final String Id;

    private List<Integer> myElementIndexes;

    public static final ZLTextHyperlink NO_LINK = new ZLTextHyperlink((byte)0, null);

    ZLTextHyperlink(byte type, String id) {
        Type = type;
        Id = id;
    }

    void addElementIndex(int elementIndex) {
        if (myElementIndexes == null) {
            myElementIndexes = new LinkedList<Integer>();
        }
        myElementIndexes.add(elementIndex);
    }

    List<Integer> elementIndexes() {
        return myElementIndexes != null
                ? Collections.unmodifiableList(myElementIndexes)
                : Collections.<Integer>emptyList();
    }
}
