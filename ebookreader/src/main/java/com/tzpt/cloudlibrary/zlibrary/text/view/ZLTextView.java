package com.tzpt.cloudlibrary.zlibrary.text.view;

import com.tzpt.cloudlibrary.zlibrary.core.library.ZLibrary;
import com.tzpt.cloudlibrary.zlibrary.core.view.ZLPaintContext;
import com.tzpt.cloudlibrary.zlibrary.core.view.ZLViewEnums;
import com.tzpt.cloudlibrary.zlibrary.text.hyphenation.ZLTextHyphenationInfo;
import com.tzpt.cloudlibrary.zlibrary.text.hyphenation.ZLTextHyphenator;
import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextAlignmentType;
import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextModel;
import com.tzpt.cloudlibrary.zlibrary.text.model.ZLTextParagraph;
import com.tzpt.cloudlibrary.zlibrary.ui.android.library.ZLAndroidLibrary;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/8.
 */

public abstract class ZLTextView extends ZLTextViewBase {
    private ZLTextModel mTextModel;

    private int mPower;

    private interface SizeUnit {
        int PIXEL_UNIT = 0;
        int LINE_UNIT = 1;
    }

    private ZLTextPage mPreviousPage = new ZLTextPage();
    private ZLTextPage mCurrentPage = new ZLTextPage();
    private ZLTextPage mNextPage = new ZLTextPage();

    private CursorManager mCursorManager;

    public synchronized void setModel(ZLTextModel model) {
        mCursorManager = model != null ? new CursorManager(model) : null; // 位置管理 最多有200个cursor在缓存中

        mTextModel = model; // 设置model

        mCurrentPage.reset(); // 重置页面
        mPreviousPage.reset();
        mNextPage.reset();

        //按\r\n的方式得到段落结构,Model添加数据的时候,是以段为单位的
        if (mTextModel != null) {
            final int paragraphsNumber = mTextModel.getParagraphsNumber();
            if (paragraphsNumber > 0) {
                //把Model的第一段的游标传给现在的Page,以后就可以自己找了
                mCurrentPage.moveStartCursor(mCursorManager.get(0));
            }
        }
    }

    public void setPower(int power) {
        mPower = power;
    }

    public ZLTextWordCursor getStartCursor(ZLViewEnums.PageIndex pageIndex) {
        switch (pageIndex) {
            case current:
                if (mCurrentPage.mStartCursor.isNull()) {
                    preparePaintInfo(mCurrentPage);
                }
                return mCurrentPage.mStartCursor;
            case previous:
                if (mPreviousPage.mStartCursor.isNull()) {
                    preparePaintInfo(mPreviousPage);
                }
                return mPreviousPage.mStartCursor;
            case next:
                if (mNextPage.mStartCursor.isNull()) {
                    preparePaintInfo(mNextPage);
                }
                return mNextPage.mStartCursor;
        }
        return null;
    }

    public String getCurrentPageLines() {
        StringBuilder pageInfo = new StringBuilder();
        for (ZLTextLineInfo lineInfo : mCurrentPage.mLineInfos) {
            pageInfo.append(lineInfo.toString());
        }
        return pageInfo.toString();
    }

    public ZLTextWordCursor getEndCursor() {
        if (mCurrentPage.mEndCursor.isNull()) {
            preparePaintInfo(mCurrentPage);
        }
        return mCurrentPage.mEndCursor;
    }

    @Override
    public synchronized void onScrollingFinished(ZLViewEnums.PageIndex pageIndex) {
        switch (pageIndex) {
            case current:
                break;
            case previous: {
                final ZLTextPage swap = mNextPage; // P C N
                mNextPage = mCurrentPage; //  P->N.reset C->P N->C
                mCurrentPage = mPreviousPage;
                mPreviousPage = swap;
                mPreviousPage.reset();

                if (mCurrentPage.mPaintState == PaintStateEnum.NOTHING_TO_PAINT) {
                    preparePaintInfo(mNextPage);
                    mCurrentPage.mEndCursor.setCursor(mNextPage.mStartCursor);
                    mCurrentPage.mPaintState = PaintStateEnum.END_IS_KNOWN;
                } else if (!mCurrentPage.mEndCursor.isNull() &&
                        !mNextPage.mStartCursor.isNull() &&
                        !mCurrentPage.mEndCursor.samePositionAs(mNextPage.mStartCursor)) { // 当前页末尾 != 下一页开始
                    mNextPage.reset();
                    mNextPage.mStartCursor.setCursor(mCurrentPage.mEndCursor); // 向前翻页后,设置下一页的文字位置
                    mNextPage.mPaintState = PaintStateEnum.START_IS_KNOWN;
                }
                break;
            }
            case next: { // 动画结束时翻页   P C N
                final ZLTextPage swap = mPreviousPage;
                mPreviousPage = mCurrentPage;  // P->C C-> N->P.reset
                mCurrentPage = mNextPage;
                mNextPage = swap; // 少了会异常
                mNextPage.reset();
                switch (mCurrentPage.mPaintState) {
                    case PaintStateEnum.NOTHING_TO_PAINT:
                        preparePaintInfo(mPreviousPage);
                        mCurrentPage.mStartCursor.setCursor(mPreviousPage.mEndCursor);
                        mCurrentPage.mPaintState = PaintStateEnum.START_IS_KNOWN;
                        break;
                    case PaintStateEnum.READY:
                        mNextPage.mStartCursor.setCursor(mCurrentPage.mEndCursor);
                        mNextPage.mPaintState = PaintStateEnum.START_IS_KNOWN;
                        break;
                }
                break;
            }
        }
    }

    /**
     * 绘制当前页面，根据myModel数据来画图
     */
    @Override
    public synchronized void paint(ZLPaintContext context, ZLViewEnums.PageIndex pageIndex) {
        setContext(context);
        //final ZLFile wallpaper = getWallpaperFile(); // 获取背景图片
//        if (wallpaper != null && bgColor.equals(getBackgroundColor())) {
//            context.clear(wallpaper);
//        } else {
//            context.clear(getBackgroundColor());
//        }

        context.setBackground(getBackgroundColor());

        if (mTextModel == null || mTextModel.getParagraphsNumber() == 0) {
            return;
        }

        ZLTextPage page;
        switch (pageIndex) {
            default:
            case current:
                page = mCurrentPage;
                break;
            case previous:
                page = mPreviousPage;
                if (mPreviousPage.mPaintState == PaintStateEnum.NOTHING_TO_PAINT) {
                    preparePaintInfo(mCurrentPage);
                    mPreviousPage.mEndCursor.setCursor(mCurrentPage.mStartCursor);
                    mPreviousPage.mPaintState = PaintStateEnum.END_IS_KNOWN;
                }
                break;
            case next:
                page = mNextPage;
                // 若下一页没有东西画,则画当前页
                if (mNextPage.mPaintState == PaintStateEnum.NOTHING_TO_PAINT) {
                    preparePaintInfo(mCurrentPage);
                    mNextPage.mStartCursor.setCursor(mCurrentPage.mEndCursor);
                    mNextPage.mPaintState = PaintStateEnum.START_IS_KNOWN;
                }
                break;
        }

        page.mTextElementMap.clear();
        // 准备要画的页面
        // ZLTextPage具有开始和结束cursor,每一行文字的信息
        preparePaintInfo(page);

        if (page.mStartCursor.isNull() || page.mEndCursor.isNull()) {
            return;
        }

        final ArrayList<ZLTextLineInfo> lineInfos = page.mLineInfos;
        final int[] labels = new int[lineInfos.size() + 1];
        float x = (float) getLeftMargin(); // MarginLeft
        float y = (float) getTopMargin();
        int index = 0;
        int columnIndex = 0;
        ZLTextLineInfo previousInfo = null;
        for (ZLTextLineInfo info : lineInfos) {
            info.adjust(previousInfo);
            prepareTextLine(page, info, x, y, columnIndex);
            y += info.Height + info.Descent + info.VSpaceAfter;
            labels[++index] = page.mTextElementMap.size();
//            if (index == 0) {
//                y = getTopMargin();
//                x += page.getTextWidth() + getSpaceBetweenColumns();
//                columnIndex = 1;
//            }
            previousInfo = info;
        }

        x = getLeftMargin();
        y = getTopMargin();
        index = 0;
        for (ZLTextLineInfo info : lineInfos) {
            drawTextLine(page, info, labels[index], labels[index + 1]);
            y += info.Height + info.Descent + info.VSpaceAfter;
            ++index;
//            if (index == 0) {
//                y = getTopMargin();
//                x += page.getTextWidth() + getSpaceBetweenColumns();
//            }
        }

        context.drawFooter(buildTimeString(), pagePositionPec(page), mPower, getBookTocTitle(page.mStartCursor.getParagraphIndex()));
    }

    private String buildTimeString() {
        return ZLibrary.Instance().getCurrentTimeString();
    }

    // Can be called only when (mTextModel.getParagraphsNumber() != 0)
    private synchronized float computeCharsPerPage() {
        setTextStyle(getTextStyleCollection().getBaseStyle());

        final int textWidth = getTextColumnWidth();
        final int textHeight = getTextAreaHeight();

        final int num = mTextModel.getParagraphsNumber();
        final int totalTextSize = mTextModel.getTextLength(num - 1);
        final float charsPerParagraph = ((float) totalTextSize) / num;

        final float charWidth = computeCharWidth();

//        final int indentWidth = getElementWidth(ZLTextElement.Indent, 0);
        final float effectiveWidth = textWidth - (0.5f * textWidth) / charsPerParagraph;
        float charsPerLine = Math.min(effectiveWidth / charWidth,
                charsPerParagraph * 1.2f);

        final int strHeight = getWordHeight() + getContext().getDescent();
        final int effectiveHeight = (int)
                (textHeight -
                        (getTextStyle().getSpaceBefore(metrics())
                                + getTextStyle().getSpaceAfter(metrics()) / 2) / charsPerParagraph);
        final int linesPerPage = effectiveHeight / strHeight;

        return charsPerLine * linesPerPage;
    }

    private static final char[] ourDefaultLetters = "System developers have used modeling languages for decades to specify, visualize, construct, and document systems. The Unified Modeling Language (UML) is one of those languages. UML makes it possible for team members to collaborate by providing a common language that applies to a multitude of different systems. Essentially, it enables you to communicate solutions in a consistent, tool-supported language.".toCharArray();

    private final char[] myLettersBuffer = new char[512];
    private int myLettersBufferLength = 0;
    private ZLTextModel myLettersModel = null;
    private float myCharWidth = -1f;

    private float computeCharWidth() {
        if (myLettersModel != mTextModel) {
            myLettersModel = mTextModel;
            myLettersBufferLength = 0;
            myCharWidth = -1f;

            int paragraph = 0;
            final int textSize = mTextModel.getTextLength(mTextModel.getParagraphsNumber() - 1);
            if (textSize > myLettersBuffer.length) {
                paragraph = mTextModel.findParagraphByTextLength((textSize - myLettersBuffer.length) / 2);
            }
            while (paragraph < mTextModel.getParagraphsNumber()
                    && myLettersBufferLength < myLettersBuffer.length) {
                final ZLTextParagraph.EntryIterator it = mTextModel.getParagraph(paragraph++).iterator();
                while (myLettersBufferLength < myLettersBuffer.length && it.next()) {
                    if (it.getType() == ZLTextParagraph.Entry.TEXT) {
                        final int len = Math.min(it.getTextLength(),
                                myLettersBuffer.length - myLettersBufferLength);
                        System.arraycopy(it.getTextData(), it.getTextOffset(),
                                myLettersBuffer, myLettersBufferLength, len);
                        myLettersBufferLength += len;
                    }
                }
            }

            if (myLettersBufferLength == 0) {
                myLettersBufferLength = Math.min(myLettersBuffer.length, ourDefaultLetters.length);
                System.arraycopy(ourDefaultLetters, 0, myLettersBuffer, 0, myLettersBufferLength);
            }
        }

        if (myCharWidth < 0f) {
            myCharWidth = computeCharWidth(myLettersBuffer, myLettersBufferLength);
        }
        return myCharWidth;
    }

    private float computeCharWidth(char[] pattern, int length) {
        return getContext().getStringWidth(pattern, 0, length) / ((float) length);
    }

    /**
     * 获取当前页阅读的百分比
     *
     * @return 百分比
     */
    public final synchronized String pagePositionPec() {
        return pagePositionPec(mCurrentPage);
    }

    /**
     * 获取指定页阅读的百分比
     *
     * @param page 指定页 N、P、C
     * @return 百分比
     */
    private synchronized String pagePositionPec(ZLTextPage page) {
        int current = getCurrentNumber(page); // 传入要绘制的page
        int total = getCountOfFullText();
        if (current == 0) {
            return "0.00%";
        }
//        if (computeTextPageNumber(total) <= 3) {
//            current = page.mEndCursor.getParagraphIndex();
//            total = mTextModel.getParagraphsNumber() - 1;
//        }
        final StringBuilder info = new StringBuilder();
        float size = (float) current * 100 / total;
        DecimalFormat df = new DecimalFormat("0.00");
        info.append(df.format(size));
        info.append("%");
        return info.toString();
    }

    /**
     * 获取全部文字数量
     *
     * @return 文字数量
     */
    private synchronized int getCountOfFullText() {
        if (mTextModel == null || mTextModel.getParagraphsNumber() == 0) {
            return 1;
        }
        return mTextModel.getTextLength(mTextModel.getParagraphsNumber() - 1);
    }

    /**
     * 获取当前已经阅读的文字数量
     *
     * @param page 指定页 N、P、C
     * @return 文字数量
     */
    private synchronized int getCurrentNumber(ZLTextPage page) {
        if (mTextModel == null || mTextModel.getParagraphsNumber() == 0) {
            return 0;
        }
        int end = sizeOfTextBeforeCursor(page.mEndCursor);
        if (end == -1) {
            end = mTextModel.getTextLength(mTextModel.getParagraphsNumber() - 1) - 1;
        }
        return Math.max(1, end);
    }

    private int sizeOfTextBeforeCursor(ZLTextWordCursor wordCursor) {
        final ZLTextParagraphCursor paragraphCursor = wordCursor.getParagraphCursor();
        if (paragraphCursor == null) {
            return -1;
        }
        final int paragraphIndex = paragraphCursor.mIndex;
        int sizeOfText = mTextModel.getTextLength(paragraphIndex - 1);
        final int paragraphLength = paragraphCursor.getParagraphLength();
        if (paragraphLength > 0) {
            sizeOfText +=
                    (mTextModel.getTextLength(paragraphIndex) - sizeOfText)
                            * wordCursor.getElementIndex()
                            / paragraphLength;
        }
        return sizeOfText;
    }

//    private synchronized int computeTextPageNumber(int textSize) {
//        if (mTextModel == null || mTextModel.getParagraphsNumber() == 0) {
//            return 1;
//        }
//
//        final float factor = 1.0f / computeCharsPerPage();
//        final float pages = textSize * factor;
//        return Math.max((int) (pages + 1.0f - 0.5f * factor), 1);
//    }

    /**
     * 获取已读段落数量
     *
     * @return 段落数量
     */
    public synchronized int pagePosition1() {
        return mCurrentPage == null ? 0 : mCurrentPage.mEndCursor.getParagraphIndex();
    }

    /**
     * 获取全部段落数量
     *
     * @return 段落数量
     */
    public synchronized int pagePosition2() {
        return mTextModel == null ? 0 : mTextModel.getParagraphsNumber() - 1;
    }

    /**
     * 跳转到指定位置
     *
     * @param paragraphIndex 段落索引
     */
    public final synchronized void gotoPageByPec(int paragraphIndex) {
        if (mTextModel == null || mTextModel.getParagraphsNumber() == 0) {
            return;
        }

        if (mTextModel != null && mTextModel.getParagraphsNumber() > 0) {
            mCurrentPage.moveEndCursor(paragraphIndex);
            mPreviousPage.reset();
            mNextPage.reset();
            preparePaintInfo(mCurrentPage);
        }
    }

    /**
     * 跳转到指定的位置
     *
     * @param paragraphIndex 段落索引
     * @param wordIndex      单词索引
     * @param charIndex      字符索引
     */
    public final synchronized void gotoPosition(int paragraphIndex, int wordIndex, int charIndex) {
        if (mTextModel != null && mTextModel.getParagraphsNumber() > 0) {
            mCurrentPage.moveStartCursor(paragraphIndex, wordIndex, charIndex);
            mPreviousPage.reset();
            mNextPage.reset();
            preparePaintInfo(mCurrentPage);
        }
    }

//    private synchronized void turnPage(boolean forward) {
//        preparePaintInfo(mCurrentPage);
//        mPreviousPage.reset();
//        mNextPage.reset();
//        if (mCurrentPage.mPaintState == PaintStateEnum.READY) {
//            mCurrentPage.mPaintState = forward ? PaintStateEnum.TO_SCROLL_FORWARD : PaintStateEnum.TO_SCROLL_BACKWARD;
//        }
//    }

    public void clearCaches() {
        resetMetrics();
        rebuildPaintInfo();
        myCharWidth = -1;
    }

    private synchronized void rebuildPaintInfo() {
        mPreviousPage.reset();
        mNextPage.reset();
        if (mCursorManager != null) {
            mCursorManager.evictAll();
        }

        if (mCurrentPage.mPaintState != PaintStateEnum.NOTHING_TO_PAINT) {
            mCurrentPage.mLineInfos.clear();
            if (!mCurrentPage.mStartCursor.isNull()) {
                mCurrentPage.mStartCursor.rebuild();
                mCurrentPage.mEndCursor.reset();
                mCurrentPage.mPaintState = PaintStateEnum.START_IS_KNOWN;
            } else if (!mCurrentPage.mEndCursor.isNull()) {
                mCurrentPage.mEndCursor.rebuild();
                mCurrentPage.mStartCursor.reset();
                mCurrentPage.mPaintState = PaintStateEnum.END_IS_KNOWN;
            }
        }
//        myLineInfoCache.clear();
    }

    protected abstract String getBookTocTitle(int index);

    private static final char[] SPACE = new char[]{' '};

    private void drawTextLine(ZLTextPage page, ZLTextLineInfo info, int from, int to) {
        final ZLPaintContext context = getContext();
        final ZLTextParagraphCursor paragraph = info.ParagraphCursor;
        int index = from;
        final int endElementIndex = info.EndElementIndex;
        int charIndex = info.RealStartCharIndex;
        final List<ZLTextElementArea> pageAreas = page.mTextElementMap.areas();
        if (to > pageAreas.size()) {
            return;
        }
        for (int wordIndex = info.RealStartElementIndex; wordIndex != endElementIndex && index < to; ++wordIndex, charIndex = 0) {
            final ZLTextElement element = paragraph.getElement(wordIndex);
            final ZLTextElementArea area = pageAreas.get(index);
            if (element == area.Element) {
                ++index;
                if (area.ChangeStyle) {
                    setTextStyle(area.Style);
                }
                final float areaX = area.XStart;
                final float areaY = area.YEnd - getElementDescent(element) - getTextStyle().getVerticalAlign(metrics());
                if (element instanceof ZLTextWord) {
                    drawWord(areaX, areaY, (ZLTextWord) element, charIndex, -1, false, getTextColor());
                } else if (element instanceof ZLTextImageElement) {
                    final ZLTextImageElement imageElement = (ZLTextImageElement) element;
                    context.drawImage(
                            areaX, areaY,
                            imageElement.ImageData,
                            getTextAreaSize(),
                            getScalingType(imageElement)
                    );
                } else if (element instanceof ZLTextVideoElement) {
                    // TODO: draw
//                    context.setLineColor(getTextColor(ZLTextHyperlink.NO_LINK));
//                    context.setFillColor(new ZLColor(127, 127, 127));
//                    final float xStart = area.XStart + 10;
//                    final float xEnd = area.XEnd - 10;
//                    final float yStart = area.YStart + 10;
//                    final float yEnd = area.YEnd - 10;
//                    context.fillRectangle(xStart, yStart, xEnd, yEnd);
//                    context.drawLine(xStart, yStart, xStart, yEnd);
//                    context.drawLine(xStart, yEnd, xEnd, yEnd);
//                    context.drawLine(xEnd, yEnd, xEnd, yStart);
//                    context.drawLine(xEnd, yStart, xStart, yStart);
//                    final float l = xStart + (xEnd - xStart) * 7 / 16;
//                    final float r = xStart + (xEnd - xStart) * 10 / 16;
//                    final float t = yStart + (yEnd - yStart) * 2 / 6;
//                    final float b = yStart + (yEnd - yStart) * 4 / 6;
//                    final float c = yStart + (yEnd - yStart) / 2;
//                    context.setFillColor(new ZLColor(196, 196, 196));
//                    context.fillPolygon(new float[]{l, l, r}, new float[]{t, b, c});
                }
//                else if (element instanceof ExtensionElement) {
//                    ((ExtensionElement) element).draw(context, area);
//                }
                else if (element == ZLTextElement.HSpace || element == ZLTextElement.NBSpace) {
                    final int cw = context.getSpaceWidth();
                    for (int len = 0; len < area.XEnd - area.XStart; len += cw) {
                        context.drawString(areaX + len, areaY, SPACE, 0, 1);
                    }
                }
            }
        }

        if (index != to) {
            ZLTextElementArea area = pageAreas.get(index);
            if (area.ChangeStyle) {
                setTextStyle(area.Style);
            }
            final int start = info.StartElementIndex == info.EndElementIndex
                    ? info.StartCharIndex : 0;
            final int len = info.EndCharIndex - start;
            final ZLTextWord word = (ZLTextWord) paragraph.getElement(info.EndElementIndex);
            drawWord(area.XStart, area.YEnd - context.getDescent() - getTextStyle().getVerticalAlign(metrics()),
                    word, start, len, area.AddHyphenationSign, getTextColor());
        }
    }


    private synchronized void preparePaintInfo(ZLTextPage page) {
        //设置页面大小
        page.setSize(getTextColumnWidth(), getTextAreaHeight());
        // 若没有下一页要绘制或者下一页已经绘制完成,则返回
        if (page.mPaintState == PaintStateEnum.NOTHING_TO_PAINT || page.mPaintState == PaintStateEnum.READY) {
            return;
        }
        final int oldState = page.mPaintState;

        switch (page.mPaintState) {
//            case PaintStateEnum.TO_SCROLL_FORWARD:
//                if (!page.mEndCursor.isEndOfText()) {
//                    final ZLTextWordCursor startCursor = new ZLTextWordCursor();
//
//                    if (!startCursor.isNull() && startCursor.samePositionAs(page.mStartCursor)) {
//                        page.findLineFromStart(startCursor, 1);
//                    }
//
//                    if (!startCursor.isNull()) {
//                        final ZLTextWordCursor endCursor = new ZLTextWordCursor();
//                        buildInfos(page, startCursor, endCursor);
//                        if (!page.isEmptyPage() && !endCursor.samePositionAs(page.mEndCursor)) {
//                            page.mStartCursor.setCursor(startCursor);
//                            page.mEndCursor.setCursor(endCursor);
//                            break;
//                        }
//                    }
//
//                    page.mStartCursor.setCursor(page.mEndCursor);
//                    buildInfos(page, page.mStartCursor, page.mEndCursor);
//                }
//                break;
//            case PaintStateEnum.TO_SCROLL_BACKWARD:
//                if (!page.mStartCursor.isStartOfText()) {
//                    page.mStartCursor.setCursor(findStartOfPreviousPage(page, page.mStartCursor));
//                    buildInfos(page, page.mStartCursor, page.mEndCursor);
//                    if (page.isEmptyPage()) {
//                        page.mStartCursor.setCursor(findStart(page, page.mStartCursor, SizeUnit.LINE_UNIT, 1));
//                        buildInfos(page, page.mStartCursor, page.mEndCursor);
//                    }
//                }
//                break;
            case PaintStateEnum.START_IS_KNOWN:
                if (!page.mStartCursor.isNull()) {
                    buildInfos(page, page.mStartCursor, page.mEndCursor);
                }
                break;
            case PaintStateEnum.END_IS_KNOWN:
                if (!page.mEndCursor.isNull()) {
                    page.mStartCursor.setCursor(findStartOfPreviousPage(page, page.mEndCursor));
                    buildInfos(page, page.mStartCursor, page.mEndCursor);
                }
                break;
            default:
                break;
        }
        page.mPaintState = PaintStateEnum.READY;

        if (page == mCurrentPage) {
            if (oldState != PaintStateEnum.START_IS_KNOWN) {
                mPreviousPage.reset();
            }
            if (oldState != PaintStateEnum.END_IS_KNOWN) {
                mNextPage.reset();
            }
        }
    }

    private boolean mIsPageFirst;

    private void buildInfos(ZLTextPage page, ZLTextWordCursor start, ZLTextWordCursor end) {
//        Log.e("ZLTextView", "************************************ZLTextPage");
        end.setCursor(start);
        int textAreaHeight = page.getTextHeight();
        page.mLineInfos.clear();
        boolean nextParagraph;
        ZLTextLineInfo info = null;
        mIsPageFirst = true;

        //生成page.mLineInfos
        do {
            final ZLTextLineInfo previousInfo = info;
            resetTextStyle();//TODO why reset text style here?
            //applyStyleChanges(paragraphCursor, 0, wordIndex);
            info = new ZLTextLineInfo(end.getParagraphCursor(), end.getElementIndex(), end.getCharIndex(), getTextStyle());
            while (!info.isEndOfParagraph()) {
                info = processTextLine(page, info.ParagraphCursor, info.EndElementIndex, info.EndCharIndex, info.ParagraphCursorLength, previousInfo);
                textAreaHeight -= info.Height + info.Descent;
                if (textAreaHeight < 0 && page.mLineInfos.size() > 0) {
                    break;
                }
                textAreaHeight -= info.VSpaceAfter;
                end.moveTo(info.EndElementIndex, info.EndCharIndex);
                page.mLineInfos.add(info);
                if (textAreaHeight < 0) {
                    break;
                }
            }
            nextParagraph = end.isEndOfParagraph() && end.nextParagraph();

            //Log.e("ZLTextView", "nextParagraph*** " + nextParagraph + " ***isEndOfSection*** " + end.getParagraphCursor().isEndOfSection());
        } while (nextParagraph
                && (!end.getParagraphCursor().isEndOfSection()
                || page.mLineInfos.size() == 0));
        resetTextStyle();
    }

    private ZLTextLineInfo processTextLine(
            ZLTextPage page,
            ZLTextParagraphCursor paragraphCursor,
            final int startIndex,
            final int startCharIndex,
            final int endIndex,
            ZLTextLineInfo previousInfo) {
        final ZLTextLineInfo info = processTextLineInternal(page, paragraphCursor, startIndex,
                startCharIndex, endIndex, previousInfo);
        if (info.EndElementIndex == startIndex && info.EndCharIndex == startCharIndex) {
            info.EndElementIndex = paragraphCursor.getParagraphLength();
            info.EndCharIndex = 0;
            // TODO: add error element
        }
        return info;
    }

    private ZLTextLineInfo processTextLineInternal(
            ZLTextPage page,
            ZLTextParagraphCursor paragraphCursor,
            final int elementIndex,
            final int charIndex,
            final int endElementIndex,
            ZLTextLineInfo previousInfo
    ) {
        final ZLPaintContext context = getContext();
        final ZLTextLineInfo info = new ZLTextLineInfo(paragraphCursor, elementIndex, charIndex, getTextStyle());

        int currentElementIndex = elementIndex;
        int currentCharIndex = charIndex;
        final boolean isFirstLine = elementIndex == 0 && charIndex == 0;

        if (isFirstLine) {
            //每段第一行文字
            ZLTextElement element = paragraphCursor.getElement(currentElementIndex);
            while (isStyleChangeElement(element)) {
                applyStyleChangeElement(element);
                ++currentElementIndex;
                currentCharIndex = 0;
                if (currentElementIndex == endElementIndex) {
                    break;
                }
                element = paragraphCursor.getElement(currentElementIndex);
            }
            info.StartStyle = getTextStyle();
            info.RealStartElementIndex = currentElementIndex;
            info.RealStartCharIndex = currentCharIndex;
        }

        ZLTextStyle storedStyle = getTextStyle();//TODO 假设TextStyle都是ZLTextNGStyle

        final int maxWidth = page.getTextWidth();
        //这里计算缩进距离，决定了每段第一行文字读取的个数。
        info.LeftIndent = 0;
        if (isFirstLine && storedStyle.getAlignment() != ZLTextAlignmentType.ALIGN_CENTER) {
            //这里还无法得到文字（尤其是汉字）的宽度，临时定义文字，计算宽度。
            final ZLTextWord wordTemp = new ZLTextWord("缩进", 0);
            info.LeftIndent += getWordWidth(wordTemp, 0);
        }

        info.Width = info.LeftIndent;

        if (info.RealStartElementIndex == endElementIndex) {
            info.EndElementIndex = info.RealStartElementIndex;
            info.EndCharIndex = info.RealStartCharIndex;
            return info;
        }

        int newWidth = info.Width;
        int newHeight = info.Height;
        int newDescent = info.Descent;
        boolean wordOccurred = false;
        boolean isVisible = false;
        int lastSpaceWidth = 0;
        int internalSpaceCounter = 0;
        boolean removeLastSpace = false;

        do {
            ZLTextElement element = paragraphCursor.getElement(currentElementIndex);
            newWidth += getElementWidth(element, currentCharIndex);
            newHeight = Math.max(newHeight, getElementHeight(element));
            newDescent = Math.max(newDescent, getElementDescent(element));
            if (element == ZLTextElement.HSpace) {
                if (wordOccurred) {
                    wordOccurred = false;
                    internalSpaceCounter++;
                    lastSpaceWidth = context.getSpaceWidth();
                    newWidth += lastSpaceWidth;
                }
            } else if (element == ZLTextElement.NBSpace) {
                wordOccurred = true;
            } else if (element instanceof ZLTextWord) {
                wordOccurred = true;
                isVisible = true;
            } else if (element instanceof ZLTextImageElement) {
                wordOccurred = true;
                isVisible = true;
            } else if (element instanceof ZLTextVideoElement) {
                wordOccurred = true;
                isVisible = true;
            } else if (isStyleChangeElement(element)) {
                applyStyleChangeElement(element);
            }
            if (newWidth > maxWidth) {
                if (info.EndElementIndex != elementIndex || element instanceof ZLTextWord) {
                    break;
                }
            }
            ZLTextElement previousElement = element;
            ++currentElementIndex;
            currentCharIndex = 0;
            boolean allowBreak = currentElementIndex == endElementIndex;
            if (!allowBreak) {
                element = paragraphCursor.getElement(currentElementIndex);
                allowBreak =
                        previousElement != ZLTextElement.NBSpace &&
                                element != ZLTextElement.NBSpace &&
                                (!(element instanceof ZLTextWord) || previousElement instanceof ZLTextWord) &&
                                !(element instanceof ZLTextImageElement) &&
                                !(element instanceof ZLTextControlElement);
            }
            if (allowBreak) {
                info.IsVisible = isVisible;
                info.Width = newWidth;
                if (info.Height < newHeight) {
                    info.Height = newHeight;
                }
                if (info.Descent < newDescent) {
                    info.Descent = newDescent;
                }
                info.EndElementIndex = currentElementIndex;
                info.EndCharIndex = currentCharIndex;
                info.SpaceCounter = internalSpaceCounter;
                storedStyle = getTextStyle();
                removeLastSpace = !wordOccurred && (internalSpaceCounter > 0);
            }
        } while (currentElementIndex != endElementIndex);

        if (currentElementIndex != endElementIndex &&
                (isHyphenationPossible() || info.EndElementIndex == elementIndex)) {
            ZLTextElement element = paragraphCursor.getElement(currentElementIndex);
            if (element instanceof ZLTextWord) {
                final ZLTextWord word = (ZLTextWord) element;
                newWidth -= getWordWidth(word, currentCharIndex);
                int spaceLeft = maxWidth - newWidth;
                if ((word.Length > 3 && spaceLeft > 2 * context.getSpaceWidth())
                        || info.EndElementIndex == elementIndex) {
                    ZLTextHyphenationInfo hyphenationInfo = getHyphenationInfo(word);
                    int hyphenationPosition = currentCharIndex;
                    int subwordWidth = 0;
                    for (int right = word.Length - 1, left = currentCharIndex; right > left; ) {
                        final int mid = (right + left + 1) / 2;
                        int m1 = mid;
                        while (m1 > left && !hyphenationInfo.isHyphenationPossible(m1)) {
                            --m1;
                        }
                        if (m1 > left) {
                            final int w = getWordWidth(
                                    word,
                                    currentCharIndex,
                                    m1 - currentCharIndex,
                                    word.Data[word.Offset + m1 - 1] != '-'
                            );
                            if (w < spaceLeft) {
                                left = mid;
                                hyphenationPosition = m1;
                                subwordWidth = w;
                            } else {
                                right = mid - 1;
                            }
                        } else {
                            left = mid;
                        }
                    }
                    if (hyphenationPosition == currentCharIndex && info.EndElementIndex == elementIndex) {
                        subwordWidth = getWordWidth(word, currentCharIndex, 1, false);
                        int right = word.Length == currentCharIndex + 1 ? word.Length : word.Length - 1;
                        int left = currentCharIndex + 1;
                        while (right > left) {
                            final int mid = (right + left + 1) / 2;
                            final int w = getWordWidth(
                                    word,
                                    currentCharIndex,
                                    mid - currentCharIndex,
                                    word.Data[word.Offset + mid - 1] != '-'
                            );
                            if (w <= spaceLeft) {
                                left = mid;
                                subwordWidth = w;
                            } else {
                                right = mid - 1;
                            }
                        }
                        hyphenationPosition = right;
                    }
                    if (hyphenationPosition > currentCharIndex) {
                        info.IsVisible = true;
                        info.Width = newWidth + subwordWidth;
                        if (info.Height < newHeight) {
                            info.Height = newHeight;
                        }
                        if (info.Descent < newDescent) {
                            info.Descent = newDescent;
                        }
                        info.EndElementIndex = currentElementIndex;
                        info.EndCharIndex = hyphenationPosition;
                        info.SpaceCounter = internalSpaceCounter;
                        storedStyle = getTextStyle();
                        removeLastSpace = false;
                    }
                }
            }
        }

        if (removeLastSpace) {
            info.Width -= lastSpaceWidth;
            info.SpaceCounter--;
        }

        setTextStyle(storedStyle);

        if (isFirstLine) {
            if (!mIsPageFirst) {
                info.VSpaceBefore = info.StartStyle.getSpaceBefore(metrics());
                info.PreviousInfoUsed = true;
                info.Height += Math.max(0, info.VSpaceBefore);
            } else {
                info.VSpaceBefore = 0;
                info.PreviousInfoUsed = false;
                info.Height += info.VSpaceBefore;
                mIsPageFirst = false;
            }
        } else {
            mIsPageFirst = false;
        }
        if (info.isEndOfParagraph()) {
            info.VSpaceAfter = getTextStyle().getSpaceAfter(metrics());
        }

//        if (info.EndElementIndex != endIndex || endIndex == info.ParagraphCursorLength) {
//            myLineInfoCache.put(info, info);
//        }
        //Log.e("ZLTextView", "**" + info + "**");

        return info;
    }

    private boolean isHyphenationPossible() {
        return getTextStyleCollection().getBaseStyle().AutoHyphenationOption.getValue()
                && getTextStyle().allowHyphenations();
    }

    private volatile ZLTextWord myCachedWord;
    private volatile ZLTextHyphenationInfo myCachedInfo;

    private synchronized ZLTextHyphenationInfo getHyphenationInfo(ZLTextWord word) {
        if (myCachedWord != word) {
            myCachedWord = word;
            myCachedInfo = ZLTextHyphenator.Instance().getInfo(word);
        }
        return myCachedInfo;
    }

    //准备每一行中每一个文字的画布位置，保存在ZLTextElementArea中
    private void prepareTextLine(ZLTextPage page, ZLTextLineInfo info, float x, float y, int columnIndex) {
        y = Math.min(y + info.Height, getTopMargin() + page.getTextHeight() - 1);

        final ZLPaintContext context = getContext();

        setTextStyle(info.StartStyle);
        int spaceCounter = info.SpaceCounter;
        int fullCorrection = 0;
        final boolean endOfParagraph = info.isEndOfParagraph();
        boolean wordOccurred = false;
        boolean changeStyle = true;
        x += info.LeftIndent;

        final int maxWidth = page.getTextWidth();
        switch (getTextStyle().getAlignment()) {
            case ZLTextAlignmentType.ALIGN_RIGHT:
                x += maxWidth - info.Width;
                break;
            case ZLTextAlignmentType.ALIGN_CENTER:
                x += (maxWidth - info.Width) / 2;
                break;
            case ZLTextAlignmentType.ALIGN_JUSTIFY:
//                if (!endOfParagraph && (paragraphCursor.getElement(info.EndElementIndex) != ZLTextElement.AfterParagraph)) {
//                    fullCorrection = maxWidth - getTextStyle().getRightIndent(metrics()) - info.Width;
//                }
                break;
            case ZLTextAlignmentType.ALIGN_LEFT:
            case ZLTextAlignmentType.ALIGN_UNDEFINED:
                break;
        }

        final ZLTextParagraphCursor paragraph = info.ParagraphCursor;
        final int paragraphIndex = paragraph.mIndex;
        final int endElementIndex = info.EndElementIndex;
        int charIndex = info.RealStartCharIndex;
        ZLTextElementArea spaceElement = null;

        int lineWidth = 0;
        for (int wordIndex = info.RealStartElementIndex; wordIndex != endElementIndex; ++wordIndex, charIndex = 0) {
            final ZLTextElement element = paragraph.getElement(wordIndex);
            final int width = getElementWidth(element, charIndex);
            lineWidth += width;
        }
        float lineXSpace = 0;
        boolean firstOne = true;
        if (!info.isEndOfParagraph()) {
            lineXSpace = (ZLAndroidLibrary.Instance().getScreenWidth() - x - getLeftMargin() - lineWidth) / (info.length() - 1);
        }

        for (int wordIndex = info.RealStartElementIndex; wordIndex != endElementIndex; ++wordIndex, charIndex = 0) {
            final ZLTextElement element = paragraph.getElement(wordIndex);
            final int width = getElementWidth(element, charIndex);
            if (element == ZLTextElement.HSpace) {
                //英文单词中间的间隔
                if (wordOccurred && spaceCounter > 0) {
                    final int correction = fullCorrection / spaceCounter;
                    final float spaceLength = context.getSpaceWidth() + correction;
                    if (getTextStyle().isUnderline()) {
                        //补充下划线
                        if (lineXSpace == 0) {//如果不是段落的末尾行使用均分间隔
                            spaceElement = new ZLTextElementArea(
                                    paragraphIndex, wordIndex, 0,
                                    0, // length
                                    true, // is last in element
                                    false, // add hyphenation sign
                                    false, // changed style
                                    getTextStyle(), element, x, x + spaceLength, y, y, columnIndex
                            );
                        } else {
                            spaceElement = new ZLTextElementArea(
                                    paragraphIndex, wordIndex, 0,
                                    0, // length
                                    true, // is last in element
                                    false, // add hyphenation sign
                                    false, // changed style
                                    getTextStyle(), element, x, x + lineXSpace, y, y, columnIndex
                            );
                        }
                    } else {
                        spaceElement = null;
                    }
                    if (lineXSpace == 0) {//如果使用了均分间隔，则不在加上英文单词的间隔
                        x += spaceLength;
                    }
                    fullCorrection -= correction;
                    wordOccurred = false;
                    --spaceCounter;
                }
            } else if (element instanceof ZLTextWord
                    || element instanceof ZLTextImageElement
                    || element instanceof ZLTextVideoElement) {
                final int height = getElementHeight(element);
                final int descent = getElementDescent(element);
                final int length = element instanceof ZLTextWord ? ((ZLTextWord) element).Length : 0;
                if (spaceElement != null) {
                    page.mTextElementMap.add(spaceElement);
                    spaceElement = null;
                }
                if (firstOne) {//段落的第一个字不需要添加间隔
                    firstOne = false;
                } else {
                    x += lineXSpace;
                }
                page.mTextElementMap.add(new ZLTextElementArea(
                        paragraphIndex, wordIndex, charIndex,
                        length - charIndex,
                        true, // is last in element
                        false, // add hyphenation sign
                        changeStyle, getTextStyle(), element,
                        x, x + width - 1, y - height + 1, y + descent, columnIndex
                ));
                changeStyle = false;
                wordOccurred = true;
            } else if (isStyleChangeElement(element)) {
                applyStyleChangeElement(element);
                changeStyle = true;
            }
            x += width;
        }
        if (!endOfParagraph) {
            final int len = info.EndCharIndex;
            if (len > 0) {
                final int wordIndex = info.EndElementIndex;
                final ZLTextWord word = (ZLTextWord) paragraph.getElement(wordIndex);
                final boolean addHyphenationSign = word.Data[word.Offset + len - 1] != '-';
                final int width = getWordWidth(word, 0, len, addHyphenationSign);
                final int height = getElementHeight(word);
                final int descent = context.getDescent();
                page.mTextElementMap.add(
                        new ZLTextElementArea(
                                paragraphIndex, wordIndex, 0,
                                len,
                                false, // is last in element
                                addHyphenationSign,
                                changeStyle, getTextStyle(), word,
                                x, x + width - 1, y - height + 1, y + descent, columnIndex
                        )
                );
            }
        }
    }

    private int infoSize(ZLTextLineInfo info, int unit) {
        return (unit == SizeUnit.PIXEL_UNIT) ? (info.Height + info.Descent + info.VSpaceAfter) : (info.IsVisible ? 1 : 0);
    }

    private static class ParagraphSize {
        public int Height;
        public int TopMargin;
        public int BottomMargin;
    }

    @Override
    int getAreaLength(ZLTextParagraphCursor paragraph, ZLTextElementArea area, int toCharIndex) {
        return super.getAreaLength(paragraph, area, toCharIndex);
    }

    private ParagraphSize paragraphSize(ZLTextPage page, ZLTextWordCursor cursor, boolean beforeCurrentPosition, int unit) {
        final ParagraphSize size = new ParagraphSize();

        final ZLTextParagraphCursor paragraphCursor = cursor.getParagraphCursor();
        if (paragraphCursor == null) {
            return size;
        }
        final int endElementIndex =
                beforeCurrentPosition ? cursor.getElementIndex() : paragraphCursor.getParagraphLength();

        resetTextStyle();

        int wordIndex = 0;
        int charIndex = 0;
        ZLTextLineInfo info = null;
        while (wordIndex != endElementIndex) {
            final ZLTextLineInfo prev = info;
            info = processTextLine(page, paragraphCursor, wordIndex, charIndex, endElementIndex, prev);
            wordIndex = info.EndElementIndex;
            charIndex = info.EndCharIndex;
            size.Height += infoSize(info, unit);
            if (prev == null) {
                size.TopMargin = info.VSpaceBefore;
            }
            size.BottomMargin = info.VSpaceAfter;
        }

        return size;
    }

    private void skip(ZLTextPage page, ZLTextWordCursor cursor, int unit, int size) {
        final ZLTextParagraphCursor paragraphCursor = cursor.getParagraphCursor();
        if (paragraphCursor == null) {
            return;
        }
        final int endElementIndex = paragraphCursor.getParagraphLength();

        resetTextStyle();
        applyStyleChanges(paragraphCursor, 0, cursor.getElementIndex());

        ZLTextLineInfo info = null;
        while (!cursor.isEndOfParagraph() && size > 0) {
            info = processTextLine(page, paragraphCursor, cursor.getElementIndex(), cursor.getCharIndex(), endElementIndex, info);
            cursor.moveTo(info.EndElementIndex, info.EndCharIndex);
            size -= infoSize(info, unit);
        }
    }

    private ZLTextWordCursor findStartOfPreviousPage(ZLTextPage page, ZLTextWordCursor end) {
//        if (twoColumnView()) {
//            end = findStart(page, end, SizeUnit.PIXEL_UNIT, page.getTextHeight());
//        }
        end = findStart(page, end, SizeUnit.PIXEL_UNIT, page.getTextHeight());
        return end;
    }

    private ZLTextWordCursor findStart(ZLTextPage page, ZLTextWordCursor end, int unit, int height) {
        final ZLTextWordCursor start = new ZLTextWordCursor(end);
        ParagraphSize size = paragraphSize(page, start, true, unit);
        height -= size.Height;
        boolean positionChanged = !start.isStartOfParagraph();
        start.moveToParagraphStart();
        while (height > 0) {
            final ParagraphSize previousSize = size;
            if (positionChanged && start.getParagraphCursor().isEndOfSection()) {
                break;
            }
            if (!start.previousParagraph()) {
                break;
            }
            if (!start.getParagraphCursor().isEndOfSection()) {
                positionChanged = true;
            }
            size = paragraphSize(page, start, false, unit);
            height -= size.Height;
            height += Math.min(size.BottomMargin, previousSize.TopMargin);
        }
        skip(page, start, unit, -height);

        if (unit == SizeUnit.PIXEL_UNIT) {
            boolean sameStart = start.samePositionAs(end);
            if (!sameStart && start.isEndOfParagraph() && end.isStartOfParagraph()) {
                ZLTextWordCursor startCopy = new ZLTextWordCursor(start);
                startCopy.nextParagraph();
                sameStart = startCopy.samePositionAs(end);
            }
            if (sameStart) {
                start.setCursor(findStart(page, end, SizeUnit.LINE_UNIT, 1));
            }
        }

        return start;
    }

    @Override
    public boolean canScroll(ZLViewEnums.PageIndex index) {
        switch (index) {
            default:
                return true;
            case next: {
                final ZLTextWordCursor cursor = getEndCursor();
                return cursor != null && !cursor.isNull() && !cursor.isEndOfText();
            }
            case previous: {
                final ZLTextWordCursor cursor = getStartCursor(ZLViewEnums.PageIndex.current);
                return cursor != null && !cursor.isNull() && !cursor.isStartOfText();
            }
        }
    }

//    ZLTextParagraphCursor cursor(int index) {
//        //当get方法获取不到缓存的时候调用create(Integer index)
//        return mCursorManager.get(index);
//    }

//    protected abstract ExtensionElementManager getExtensionManager();
}
