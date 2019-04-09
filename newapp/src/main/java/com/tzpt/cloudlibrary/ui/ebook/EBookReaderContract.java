package com.tzpt.cloudlibrary.ui.ebook;

import android.graphics.Typeface;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.BookMarkBean;
import com.tzpt.cloudlibrary.bean.ReadingColorBean;
import com.tzpt.cloudlibrary.cbreader.bookmodel.TOCTree;
import com.tzpt.cloudlibrary.zlibrary.core.util.ZLColor;

import java.util.List;

/**
 * Created by Administrator on 2017/10/18.
 */

public interface EBookReaderContract {
    interface View extends BaseContract.BaseView {
        /**
         * 开始解析电子书
         */
        void startParseBooks();

        /**
         * 打开epub电子图书
         */
        void openEpubBooksSuccess();

        /**
         * 打开文件失败
         */
        void openEpubBooksFailure();

        /**
         * 开始下载
         */
        void startDownload();

        /**
         * 下载进度
         */
        void downLoadProgress(int progress);

        /**
         * 文件下载成功
         */
        void downLoadSuccess();

        /**
         * 文件下载失败
         */
        void downLoadFailure();

        void showBookToc(TOCTree tocList, Typeface typeface);

        void setReadingColorSet(List<ReadingColorBean> data);

        void repaint();

        void setSelectedBgTheme(ZLColor color);

        void setNoSelectedBgTheme();

        void setReadProgressInfo(int max, int progress, String title);

        void showBookMark(List<BookMarkBean> list, Typeface typeface);

        void showBookMarkEmpty();

        /**
         * 设置书签标识状态
         *
         * @param includeMark 是否添加书签
         */
        void setMarkBtnStatus(boolean includeMark, int markIndex);

        /**
         * 设置当前目录
         *
         * @param tree 当前目录
         */
        void setCurrentToc(TOCTree tree);

        /**
         * 显示日间模式或者夜间模式
         *
         * @param isDay true表示日间模式，false表示夜间模式
         */
        void showDayOrNight(boolean isDay);

        /**
         * 设置屏幕亮度
         *
         * @param level 亮度
         */
        void setScreenBrightness(int level);

        void needLoginTip(boolean showActivity);

        void pleaseLogin();

        void collectEBookSuccess(boolean isCollection);

        /**
         * 设置电子书收藏状态
         *
         * @param isCollectedEBook
         */
        void setEBookCollectionStatus(boolean isCollectedEBook);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void openBook(String fileId, String coverImg, String fileDownLoadUrl, String bookTitle, String bookAuthor,
                      String shareUrl, String shareContent, String belongLibCode,String descContent);

        void cancelDownload();

        /**
         * 获取目录列表
         */
        void getBookToc();

        /**
         * 获取书签
         */
        void getBookMarkList();

        /**
         * 添加书签
         */
        void addBookMark();

        /**
         * 删除书签
         */
        void delBookMark(int markIndex);

        void setBattery(int power);

        void getReadingColor();

        /**
         * 减小字体
         */
        void setFontMinus(float minSize);

        /**
         * 增加字体
         */
        void setFontIncrease(float maxSize);

        /**
         * 设置主题
         *
         * @param data 主题
         */
        void setBgTheme(ReadingColorBean data);

        /**
         * 日间模式和夜间模式切换
         */
        void setDayOrNight();

        /**
         * 获取模式
         */
        void getDayOrNightState();

        /**
         * 获取主题
         */
        void getBgTheme();

        /**
         * 保存亮度
         *
         * @param level 亮度
         */
        void saveScreenBrightness(int level);

        /**
         * 获取阅读进度
         */
        void getReadProgressInfo();

        /**
         * 保存阅读进度
         */
        void saveReadPosition();

        /**
         * 跳转到下一个章节
         */
        void turnNextToc();

        /**
         * 跳转到上一个章节
         */
        void turnPreToc();

        /**
         * 释放书籍数据
         */
        void releaseBookData();

        /**
         * 跳转到指定位置
         *
         * @param progress 进度
         */
        void gotoPageByPec(int progress);

        /**
         * 跳转到指定位置
         *
         * @param paragraphIndex 段落索引
         */
        void gotoPosition(int paragraphIndex);

        /**
         * 跳转到书签位置
         *
         * @param position 书签索引
         */
        void gotoMarkPosition(int position);

        /**
         * 获取书签标识状态
         */
        void getMarkBtnStatus();

        /**
         * 获取当前目录
         */
        void getCurrentToc();

        /**
         * 获取保存的屏幕亮度
         */
        void getScreenBrightness();

        /**
         * 保存阅读页数
         *
         * @param pageCount 页数
         */
        void saveReadPageCount(int pageCount);

        /**
         * 获取电子书收藏状态
         */
        void getEBookCollectionStatus();

        /**
         * 收藏或者取消电子书
         */
        void collectionOrCancelEBook();

        /**
         * 收藏电子书
         */
        void collectionEBook();

        boolean isLogin();

    }
}
