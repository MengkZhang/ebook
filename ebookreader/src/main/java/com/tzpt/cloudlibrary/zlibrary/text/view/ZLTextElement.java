package com.tzpt.cloudlibrary.zlibrary.text.view;

/**
 * Created by Administrator on 2017/4/8.
 */

public abstract class ZLTextElement {
    public final static ZLTextElement HSpace = new ZLTextElement() {};//空白字符，空白符包含：空格、tab键、换行符
    public final static ZLTextElement NBSpace = new ZLTextElement() {};//Unicode空白字符
//    public final static ZLTextElement AfterParagraph = new ZLTextElement() {};
//    public final static ZLTextElement Indent = new ZLTextElement() {};
    public final static ZLTextElement StyleClose = new ZLTextElement() {};
}
