package com.tzpt.cloudlibrary.cbreader.cbreader;

import android.graphics.Typeface;
import android.util.Log;

import com.tzpt.cloudlibrary.cbreader.book.Book;
import com.tzpt.cloudlibrary.cbreader.bookmodel.BookModel;
import com.tzpt.cloudlibrary.cbreader.bookmodel.TOCTree;
import com.tzpt.cloudlibrary.cbreader.formats.BookReadingException;
import com.tzpt.cloudlibrary.cbreader.formats.FormatPlugin;
import com.tzpt.cloudlibrary.cbreader.formats.PluginCollection;
import com.tzpt.cloudlibrary.zlibrary.core.application.ZLApplication;
import com.tzpt.cloudlibrary.zlibrary.core.filesystem.ZLFile;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLIntegerRangeOption;
import com.tzpt.cloudlibrary.zlibrary.core.util.ZLColor;
import com.tzpt.cloudlibrary.zlibrary.core.view.ZLViewEnums;
import com.tzpt.cloudlibrary.zlibrary.text.view.ZLTextFixedPosition;
import com.tzpt.cloudlibrary.zlibrary.text.view.ZLTextPosition;
import com.tzpt.cloudlibrary.zlibrary.text.view.ZLTextWordCursor;

/**
 * Created by Administrator on 2017/4/8.
 */

public final class CBReaderApp extends ZLApplication {
    private final CBView mBookTextView;
    private volatile BookModel mModel;

    public CBReaderApp() {
        super();
        mBookTextView = new CBView();
        setView(mBookTextView);
    }

    public boolean openBook(String path, ZLTextPosition position, String bookTitle, String bookAuthor) {
        PluginCollection pluginCollection = PluginCollection.Instance();//所有解析插件集合

        try {
            ZLFile zlFile = ZLFile.createFileByPath(path);
            FormatPlugin plugin = pluginCollection.getPlugin(zlFile);
            Book book = new Book(zlFile, plugin, bookTitle, bookAuthor);
            openBook(book, position, plugin);
            return true;
        } catch (BookReadingException e) {
            e.printStackTrace();
            return false;
        }
    }

    //打开书籍
    private void openBook(Book book, ZLTextPosition position, FormatPlugin plugin) {
        if (mModel != null) {
            if (book == null) {
                return;
            }
        }

        //加载电子书
        openBookInternal(book, false, position, plugin);
    }

    public void clearTextCaches() {
        mBookTextView.clearCaches();
    }

    public void releaseModel() {
        PluginCollection.deleteInstance();
        mBookTextView.setModel(null, null);
        mModel = null;
    }

    private boolean sameBook(Book b0, Book b1) {
        return b0 == b1 || !(b0 == null || b1 == null) && b0.getPath().equals(b1.getPath());

    }

    //生成数据Model对象
    private synchronized void openBookInternal(final Book book, boolean force, ZLTextPosition position, FormatPlugin plugin) {
        if (!force && mModel != null && sameBook(book, mModel.Book)) {//相同书籍判断
            return;
        }

        clearTextCaches();
        mModel = null;
        System.gc();
        System.gc();

//        mTypeface = Typeface.createFromAsset(((ZLAndroidLibrary) ZLAndroidLibrary.Instance()).getAssets(), "fonts/fangzhenglanting.ttf");
//        mBookTextView.setTypeface(mTypeface);
        // 使用插件的方式,让不同的格式走不同的代码去生成Model(根据后缀名判断属于哪种插件)

        try {
            //获取BookModel，调用C层，对Java层的BookModel进行赋值。
            mModel = BookModel.createModel(book, plugin); // NativeFormatPlugin [ePub] 慢慢加载
            mBookTextView.setModel(mModel.getTextModel(), mModel.TOCTree); // 给CBView传入TextModel 操作-UI在这里分界
            gotoStoredPosition(position);

            setView(mBookTextView);

        } catch (BookReadingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        for (FileEncryptionInfo info : plugin.readEncryptionInfos(book)) {
//            if (info != null && !EncryptionMethod.isSupported(info.Method)) {
//                // 不支持加密方法
//                break;
//            }
//        }
    }

    public void setPower(int power) {
        mBookTextView.setPower(power);
    }

    //跳转到指定阅读位置
    private void gotoStoredPosition(ZLTextPosition position) {
        if (mModel == null || mModel.Book == null) {
            return;
        }
        gotoPosition(position.getParagraphIndex(), position.getElementIndex(), position.getCharIndex());
    }


    public ZLTextPosition getReadPosition() {
        return new ZLTextFixedPosition(mBookTextView.getStartCursor(ZLViewEnums.PageIndex.current));
    }

    public TOCTree getBookToc() {
        return mModel.TOCTree;
    }

    public Typeface getTypeface() {
        return null;
    }

    public ZLIntegerRangeOption getFontSizeOption() {
        return mBookTextView.getFontSizeOption();
    }

    public String getCurrentTocTitle() {
        return mBookTextView.getBookTocTitle(mBookTextView.getStartCursor(ZLViewEnums.PageIndex.current).getParagraphIndex());
    }

    public String getCurrentPageInfo() {
        return mBookTextView.getCurrentPageLines();
    }

    public void setColorProfileName(String value) {
        mBookTextView.setColorProfileName(value);
    }

    public void setRegularTextColor(ZLColor value) {
        mBookTextView.setRegularTextColor(value);
    }

    public void setBackgroundColor(ZLColor value) {
        mBookTextView.setBackgroundColor(value);
    }

    public ZLColor getBackgroundColor() {
        return mBookTextView.getBackgroundColor();
    }

    public boolean isColorProfileDay() {
        return mBookTextView.isColorProfileDay();
    }

    public int getMaxReadProgress() {
        return mBookTextView.pagePosition2();
    }

    public int getReadProgress() {
        return mBookTextView.pagePosition1();
    }

    public String pagePositionPec() {
        return mBookTextView.pagePositionPec();
    }

    public TOCTree getCurrentTOCElement() {
        final ZLTextWordCursor cursor = mBookTextView.getStartCursor(ZLViewEnums.PageIndex.current);
        if (mModel == null || cursor == null) {
            return null;
        }

        int index = cursor.getParagraphIndex();
        if (cursor.isEndOfParagraph()) {
            ++index;
        }
        TOCTree treeToSelect = null;
        for (TOCTree tree : mModel.TOCTree) {//章节起始点\
            int paragraphIndex = tree.getParagraphIndex();
            if (paragraphIndex == -1) {
                continue;
            }
            if (paragraphIndex > index) {
                break;
            }
            treeToSelect = tree;
        }
        return treeToSelect;
    }

    public void gotoNextToc() {
        final ZLTextWordCursor cursor = mBookTextView.getStartCursor(ZLViewEnums.PageIndex.current);
        if (mModel == null || cursor == null) {
            return;
        }

        int index = cursor.getParagraphIndex();
        if (cursor.isEndOfParagraph()) {
            ++index;
        }
        for (TOCTree tree : mModel.TOCTree) {//章节起始点\
            int paragraphIndex = tree.getParagraphIndex();
            if (paragraphIndex == -1) {
                continue;
            }
            if (paragraphIndex > index + 1) {
                mBookTextView.gotoPosition(paragraphIndex, 0, 0);
                break;
            }
        }
    }

    public void gotoPreToc() {
        final ZLTextWordCursor cursor = mBookTextView.getStartCursor(ZLViewEnums.PageIndex.current);
        if (mModel == null || cursor == null) {
            return;
        }

        int index = cursor.getParagraphIndex();
        if (cursor.isEndOfParagraph()) {
            ++index;
        }
        int paragraphIndex = -1;
        for (TOCTree tree : mModel.TOCTree) {//章节起始点\
            int paragraphIndexTemp = tree.getParagraphIndex();
            if (paragraphIndexTemp == -1) {
                continue;
            }
            if (paragraphIndexTemp > index - 1) {
                break;
            }
            paragraphIndex = paragraphIndexTemp;
        }
        if (paragraphIndex > -1) {
            mBookTextView.gotoPosition(paragraphIndex, 0, 0);
        }

    }

    public void gotoPosition(int paragraphIndex, int wordIndex, int charIndex) {
        mBookTextView.gotoPosition(paragraphIndex, wordIndex, charIndex);
    }

    public void gotoPageByPec(int progress) {
        mBookTextView.gotoPageByPec(progress);
    }

    public int getParagraphStartIndex() {
        return mBookTextView.getStartCursor(ZLViewEnums.PageIndex.current).getParagraphIndex();
    }

    public int getParagraphEndIndex() {
        return mBookTextView.getEndCursor().getParagraphIndex();
    }
}
