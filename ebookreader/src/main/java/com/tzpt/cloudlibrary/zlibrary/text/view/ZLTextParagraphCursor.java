package com.tzpt.cloudlibrary.zlibrary.text.view;

import com.tzpt.cloudlibrary.zlibrary.core.image.ZLImage;
import com.tzpt.cloudlibrary.zlibrary.core.image.ZLImageData;
import com.tzpt.cloudlibrary.zlibrary.text.model.ZLImageEntry;
import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextModel;
import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextParagraph;
import com.tzpt.cloudlibrary.zlibrary.ui.android.image.ZLAndroidImageManager;
import com.vimgadgets.linebreak.LineBreaker;

import java.util.ArrayList;

/**
 * 一个控制Model的游标类
 * 类似数据库操作，含有next()方法，可以找到下一段内容。
 * Created by Administrator on 2017/4/8.
 */

public final class ZLTextParagraphCursor {
    final int mIndex;//段落索引值
    final CursorManager mCursorManager;
    final ZLTextModel mModel;
    private final ArrayList<ZLTextElement> mElements = new ArrayList<>();

    ZLTextParagraphCursor(CursorManager cManager, ZLTextModel model, int index) {
        mCursorManager = cManager;
        mModel = model;
        mIndex = Math.min(index, model.getParagraphsNumber() - 1);
        fill();
    }

    void fill() {
        ZLTextParagraph paragraph = mModel.getParagraph(mIndex);
        //Log.e("ZLTextParagraphCursor", "*******index " + mIndex + " ***************kind " + paragraph.getKind());
        switch (paragraph.getKind()) {
            case ZLTextParagraph.Kind.TEXT_PARAGRAPH:
                new Processor(paragraph, new LineBreaker(mModel.getLanguage()), mElements).fill();
                break;
            default:
                break;
        }
    }

    void clear() {
        mElements.clear();
    }

    /**
     * 判断是否是第一段落
     *
     * @return true 是 false 反之
     */
    public boolean isFirst() {
        return mIndex == 0;
    }

    /**
     * 判断是否是最后一个段落
     *
     * @return true 是 false 不是
     */
    public boolean isLast() {
        return mIndex + 1 >= mModel.getParagraphsNumber();
    }

    /**
     * 判断是否是章节的结尾
     *
     * @return true章节结尾 false反之
     */
    boolean isEndOfSection() {
//        Log.e("ParagraphCursor", "++++++++++++++++++++isEndOfSection " + (mModel.getParagraph(mIndex).getKind() == ZLTextParagraph.Kind.END_OF_SECTION_PARAGRAPH));
        return mModel.getParagraph(mIndex).getKind() == ZLTextParagraph.Kind.END_OF_SECTION_PARAGRAPH;
    }

    /**
     * 获取每个段落的长度
     *
     * @return 段落的长度
     */
    int getParagraphLength() {
        return mElements.size();
    }

    /**
     * 获取上一段落游标，并且获取上一段的游标
     *
     * @return ZLTextParagraphCursor
     */
    public ZLTextParagraphCursor previous() {
        return isFirst() ? null : mCursorManager.get(mIndex - 1);
    }

    /**
     * 获取下一段落游标，并且获取下一段的游标
     *
     * @return ZLTextParagraphCursor
     */
    public ZLTextParagraphCursor next() {
        return isLast() ? null : mCursorManager.get(mIndex + 1);
    }

    /**
     * 获取指定索引值的元素
     *
     * @param index 索引值
     * @return 元素
     */
    ZLTextElement getElement(int index) {
        try {
            return mElements.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    ZLTextParagraph getParagraph() {
        return mModel.getParagraph(mIndex);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mElements.size(); i++) {
            ZLTextElement element = getElement(i);
            if (element instanceof ZLTextWord) {
                sb.append(element);
            }
        }
        return sb.toString();
    }

    public int length() {
        int count = 0;
        for (int i = 0; i < mElements.size(); i++) {
            ZLTextElement element = getElement(i);
            if (element instanceof ZLTextWord) {
                count++;
            }
        }
        return count;
    }

    private static final class Processor {
        private final ZLTextParagraph mParagraph;
        private final LineBreaker mLineBreaker;
        private final ArrayList<ZLTextElement> mElements;
        private int mOffset;

        private Processor(ZLTextParagraph paragraph, LineBreaker lineBreaker, ArrayList<ZLTextElement> elements) {
            mParagraph = paragraph;
            mLineBreaker = lineBreaker;
            mElements = elements;
            mOffset = 0;
        }

        void fill() {
            final ArrayList<ZLTextElement> elements = mElements;
            for (ZLTextParagraph.EntryIterator it = mParagraph.iterator(); it.next(); ) {
                switch (it.getType()) {
                    case ZLTextParagraph.Entry.TEXT:
                        processTextEntry(it.getTextData(), it.getTextOffset(), it.getTextLength());
                        break;
                    case ZLTextParagraph.Entry.CONTROL:
                        //Log.e("ParagraphCursor", "++++++++++++++++++++CONTROL " + it.getControlKind() + " ++IsStart " + it.getControlIsStart() + " ++Size " + elements.size());
                        elements.add(ZLTextControlElement.get(it.getControlKind(), it.getControlIsStart()));
                        break;
                    case ZLTextParagraph.Entry.HYPERLINK_CONTROL:
                        //Log.e("ParagraphCursor", "++++++++++++++++++++HYPERLINK_CONTROL");
                        break;
                    case ZLTextParagraph.Entry.IMAGE:
                        //Log.e("ParagraphCursor", "++++++++++++++++++++IMAGE");
                        final ZLImageEntry imageEntry = it.getImageEntry();
                        final ZLImage image = imageEntry.getImage();
                        if (image != null) {
                            ZLImageData data = ZLAndroidImageManager.getInstance().getImageData(image);
                            if (data != null) {
                                elements.add(new ZLTextImageElement(imageEntry.Id, data, image.getURI(), imageEntry.IsCover));
                            }
                        }
                        break;
                    case ZLTextParagraph.Entry.AUDIO:
                        //Log.e("ParagraphCursor", "++++++++++++++++++++AUDIO");
                        break;
                    case ZLTextParagraph.Entry.VIDEO:
                        //Log.e("ParagraphCursor", "++++++++++++++++++++VIDEO");
                        break;
                    case ZLTextParagraph.Entry.EXTENSION:
                        //Log.e("ParagraphCursor", "++++++++++++++++++++EXTENSION");
                        break;
                    case ZLTextParagraph.Entry.STYLE_CSS:
                    case ZLTextParagraph.Entry.STYLE_OTHER:
                        //Log.e("ParagraphCursor", "++++++++++++++++++++STYLE_CSS");
                        break;
                    case ZLTextParagraph.Entry.STYLE_CLOSE:
                        //Log.e("ParagraphCursor", "++++++++++++++++++++STYLE_CLOSE");
                        elements.add(ZLTextElement.StyleClose);
                        break;
                    case ZLTextParagraph.Entry.FIXED_HSPACE:
                        //Log.e("ParagraphCursor", "++++++++++++++++++++FIXED_HSPACE");
                        break;
                }
            }
        }

        private static byte[] ourBreaks = new byte[1024];
        private static final int NO_SPACE = 0;
        private static final int SPACE = 1;
        private static final int NON_BREAKABLE_SPACE = 2;

        private void processTextEntry(final char[] data, final int offset, final int length) {
            if (length != 0) {
                if (ourBreaks.length < length) {
                    ourBreaks = new byte[length];
                }
                final byte[] breaks = ourBreaks;
                mLineBreaker.setLineBreaks(data, offset, length, breaks);

                final ZLTextElement hSpace = ZLTextElement.HSpace;
                final ZLTextElement nbSpace = ZLTextElement.NBSpace;
                final ArrayList<ZLTextElement> elements = mElements;
                char ch = 0;
                char previousChar;
                int spaceState = NO_SPACE;
                int wordStart = 0;
                for (int index = 0; index < length; ++index) {
                    previousChar = ch;
                    ch = data[offset + index];
                    if (Character.isWhitespace(ch)) {//判断指定字符是否为空白字符，空白符包含：空格、tab键、换行符
                        if (index > 0 && spaceState == NO_SPACE) {
                            addWord(data, offset + wordStart, index - wordStart, mOffset + wordStart);
                        }
                        spaceState = SPACE;
                    } else if (Character.isSpaceChar(ch)) {//判断字符是否为Unicode空白字符
                        if (index > 0 && spaceState == NO_SPACE) {
                            addWord(data, offset + wordStart, index - wordStart, mOffset + wordStart);
                        }
                        elements.add(nbSpace);
                        if (spaceState != SPACE) {
                            spaceState = NON_BREAKABLE_SPACE;
                        }
                    } else {
                        switch (spaceState) {
                            case SPACE:
                                //if (breaks[index - 1] == LineBreak.NOBREAK || previousChar == '-') {
                                //}
                                elements.add(hSpace);
                                wordStart = index;
                                break;
                            case NON_BREAKABLE_SPACE:
                                wordStart = index;
                                break;
                            case NO_SPACE:
                                if (index > 0 &&
                                        breaks[index - 1] != LineBreaker.NOBREAK &&
                                        previousChar != '-' &&
                                        index != wordStart) {
                                    addWord(data, offset + wordStart, index - wordStart, mOffset + wordStart);
                                    wordStart = index;
                                }
                                break;
                        }
                        spaceState = NO_SPACE;
                    }
                }
                switch (spaceState) {
                    case SPACE:
                        elements.add(hSpace);
                        break;
                    case NON_BREAKABLE_SPACE:
                        elements.add(nbSpace);
                        break;
                    case NO_SPACE:
                        addWord(data, offset + wordStart, length - wordStart, mOffset + wordStart);
                        break;
                }
                mOffset += length;
            }
        }

        private void addWord(char[] data, int offset, int len, int paragraphOffset) {
            ZLTextWord word = new ZLTextWord(data, offset, len, paragraphOffset);
            //Log.e("ParagraphCursor", "++++++++++++++++++++TEXT " + word.toString());
            mElements.add(word);
        }
    }
}
