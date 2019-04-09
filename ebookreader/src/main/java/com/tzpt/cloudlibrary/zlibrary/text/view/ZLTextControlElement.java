package com.tzpt.cloudlibrary.zlibrary.text.view;

/**
 * Created by Administrator on 2017/4/8.
 */

public class ZLTextControlElement extends ZLTextElement {
    private final static ZLTextControlElement[] myStartElements = new ZLTextControlElement[256];
//    private final static ZLTextControlElement[] myEndElements = new ZLTextControlElement[256];

    static ZLTextControlElement get(byte kind, boolean isStart) {
        ZLTextControlElement[] elements = myStartElements ;
        ZLTextControlElement element = elements[kind & 0xFF];
        if (element == null) {
            element = new ZLTextControlElement(kind, isStart);
            elements[kind & 0xFF] = element;
        }
        return element;
    }

    public final byte Kind;
    final boolean IsStart;

    private ZLTextControlElement(byte kind, boolean isStart) {
        Kind = kind;
        IsStart = isStart;
    }
}
