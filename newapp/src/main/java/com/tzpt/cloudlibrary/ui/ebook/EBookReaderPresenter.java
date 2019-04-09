package com.tzpt.cloudlibrary.ui.ebook;

import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.BookMarkBean;
import com.tzpt.cloudlibrary.bean.ReadingColorBean;
import com.tzpt.cloudlibrary.cbreader.bookmodel.TOCTree;
import com.tzpt.cloudlibrary.cbreader.cbreader.CBReaderApp;
import com.tzpt.cloudlibrary.cbreader.cbreader.options.ColorProfile;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.EBookRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.local.db.BookColumns;
import com.tzpt.cloudlibrary.modle.local.db.BookMarkColumns;
import com.tzpt.cloudlibrary.modle.local.db.DBManager;
import com.tzpt.cloudlibrary.modle.local.db.DownInfoColumns;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadListener;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadTask;
import com.tzpt.cloudlibrary.modle.remote.newdownload.PDownload;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.cause.EndCause;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseDataResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.utils.DateUtils;
import com.tzpt.cloudlibrary.zlibrary.core.library.ZLibrary;
import com.tzpt.cloudlibrary.zlibrary.core.opstions.ZLIntegerRangeOption;
import com.tzpt.cloudlibrary.zlibrary.core.util.ZLColor;
import com.tzpt.cloudlibrary.zlibrary.text.view.ZLTextFixedPosition;
import com.tzpt.cloudlibrary.zlibrary.text.view.ZLTextPosition;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/10/18.
 */

public class EBookReaderPresenter extends RxPresenter<EBookReaderContract.View> implements EBookReaderContract.Presenter {
    //电子书储存地址
    private static final String FILE_SAVE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ytsg/epub/";
    private String mAbsolutePath;
    private String mBookTitle;
    private String mBookAuthor;
    private String mBookId;
    private String mCoverImg;
    private String mDownLoadUrl;
    private String mShareUrl;
    private String mShareContent;
    private String mDescContent;//简介
    private String mBelongLibCode;
    private int mPageCount;

    private CBReaderApp mCBReaderApp;

    private List<BookMarkBean> mBookMarkList = new ArrayList<>();

    EBookReaderPresenter() {
        mCBReaderApp = (CBReaderApp) CBReaderApp.Instance();
        if (mCBReaderApp == null) {
            mCBReaderApp = new CBReaderApp();
        }
    }

    @Override
    public void openBook(String fileId, String coverImg, String fileDownLoadUrl, String bookTitle, String bookAuthor,
                         String shareUrl, String shareContent, String belongLibCode,String descContent) {
        mBookId = fileId;
        mCoverImg = coverImg;
        mBookTitle = bookTitle;
        mBookAuthor = bookAuthor;
        mDownLoadUrl = fileDownLoadUrl;
        mShareUrl = shareUrl;
        mShareContent = shareContent;
        mBelongLibCode = belongLibCode;
        mDescContent = descContent;
        File file = new File(FILE_SAVE_PATH, fileId + ".epub");
        mAbsolutePath = file.getPath();
        if (checkFileComplete(fileId, fileDownLoadUrl)) {
            // 已经存在则直接打开文件
            parserEpubBook();
        } else {
            if (mView != null) {
                mView.startDownload();
            }
            start(fileId, fileDownLoadUrl);
        }
    }

    private boolean checkFileComplete(String fileId, String fileDownLoadUrl) {
        File file = new File(FILE_SAVE_PATH, fileId + ".epub");
        if (!file.exists()) {
            return false;
        }
        DownInfoColumns downInfoColumns = DBManager.getInstance().getDownInfo(fileDownLoadUrl);
        return downInfoColumns != null
                && downInfoColumns.getCountLength() > 0
                && downInfoColumns.getCountLength() == downInfoColumns.getReadLength();
    }

    private void start(String fileId, String fileDownLoadUrl) {
        File parentFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ytsg/epub/");
        DownloadTask newTask = new DownloadTask.Builder(fileDownLoadUrl, parentFile, DownloadTask.EPUB)
                .setFilename(fileId + ".epub")
                .setFromStart()
                .setIsCheck(false)
                .setAutoCallbackToUIThread(true)
                .setMinIntervalMillisCallbackProcess(100)
                .build();
        newTask.execute(new DownloadListenerWrapper());
    }


    @Override
    public void cancelDownload() {
        PDownload.with().downloadDispatcher().cancel(mDownLoadUrl);
    }

    @Override
    public void getBookToc() {
        if (mView != null) {
            mView.showBookToc(mCBReaderApp.getBookToc(), mCBReaderApp.getTypeface());
        }
    }

    @Override
    public void getBookMarkList() {
        Subscription subscription = Observable.create(new Observable.OnSubscribe<List<BookMarkColumns>>() {

            @Override
            public void call(Subscriber<? super List<BookMarkColumns>> subscriber) {
                subscriber.onNext(DBManager.getInstance().getBookMarkList(mBookId));
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BookMarkColumns>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<BookMarkColumns> markList) {
                        if (mView != null) {
                            if (markList == null || markList.size() == 0) {
                                mView.showBookMarkEmpty();
                            } else {
                                mBookMarkList.clear();
                                for (BookMarkColumns item : markList) {
                                    BookMarkBean bean = new BookMarkBean();
                                    bean.setAddDate(item.getAdd_date());
                                    bean.setProgress(item.getProgress());
                                    bean.setTocTitle(item.getToc_title());
                                    bean.setContent(item.getContent());
                                    bean.setParagraphIndex(Integer.valueOf(item.getParagraph_index()));
                                    bean.setElementIndex(Integer.valueOf(item.getElement_index()));
                                    bean.setCharIndex(Integer.valueOf(item.getChar_index()));
                                    bean.setCharIndex(Integer.valueOf(item.getChar_index()));
                                    mBookMarkList.add(bean);
                                }
                                mView.showBookMark(mBookMarkList, mCBReaderApp.getTypeface());
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void addBookMark() {
        Subscription subscription = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                ZLTextPosition position = mCBReaderApp.getReadPosition();
                String paragraphIndex = String.valueOf(position.getParagraphIndex());
                String elementIndex = String.valueOf(position.getElementIndex());
                String charIndex = String.valueOf(position.getCharIndex());
                String timestampStr = DateUtils.formatDate(System.currentTimeMillis());
                String progress = mCBReaderApp.pagePositionPec();
                String tocStr = mCBReaderApp.getCurrentTocTitle();
                String content = mCBReaderApp.getCurrentPageInfo();
                if (TextUtils.isEmpty(tocStr)) {
                    tocStr = "未知";
                }
                long id = DBManager.getInstance().saveBookMark(mBookId, paragraphIndex,
                        elementIndex, charIndex, timestampStr, progress, tocStr, content);
                if (id > 0) {
                    BookMarkBean bean = new BookMarkBean();
                    bean.setTocTitle(tocStr);
                    bean.setContent(content);
                    bean.setProgress(progress);
                    bean.setAddDate(timestampStr);
                    bean.setParagraphIndex(position.getParagraphIndex());
                    bean.setElementIndex(position.getElementIndex());
                    bean.setCharIndex(position.getCharIndex());
                    mBookMarkList.add(bean);
                    subscriber.onNext(position.getParagraphIndex());
                } else {
                    subscriber.onNext(null);
                }
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer markIndex) {
                        if (mView != null) {
                            if (markIndex >= 0) {
                                mView.setMarkBtnStatus(true, markIndex);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void delBookMark(final int markIndex) {
        Subscription subscription = Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                DBManager.getInstance().delBookMark(mBookId, String.valueOf(markIndex));
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Void aVoid) {
                        for (BookMarkBean item : mBookMarkList) {
                            if (item.getParagraphIndex() == markIndex) {
                                mBookMarkList.remove(item);
                                break;
                            }
                        }
                        mView.showBookMark(mBookMarkList, mCBReaderApp.getTypeface());
                        mView.setMarkBtnStatus(false, -1);
                    }
                });
        addSubscrebe(subscription);

    }

    @Override
    public void setBattery(int power) {
        mCBReaderApp.setPower(power);
    }

    @Override
    public void getReadingColor() {
        List<ReadingColorBean> list = new ArrayList<>();
        list.add(new ReadingColorBean(R.drawable.ic_drawable_font_color1_def, new ZLColor(Color.parseColor("#cdc2b7")),
                R.color.color_2a1e12, new ZLColor(Color.parseColor("#2a1e12")), true));
        list.add(new ReadingColorBean(R.drawable.ic_drawable_font_color3_def, new ZLColor(Color.parseColor("#9bae88")),
                R.color.color_041d19, new ZLColor(Color.parseColor("#2e3516")), false));
        list.add(new ReadingColorBean(R.drawable.ic_drawable_font_color5_def, new ZLColor(Color.parseColor("#ffffff")),
                R.color.color_2a1e12, new ZLColor(Color.parseColor("#2a1e12")), false));
        list.add(new ReadingColorBean(R.drawable.ic_drawable_font_color8_def, new ZLColor(Color.parseColor("#3b3922")),
                R.color.color_c2ede7, new ZLColor(Color.parseColor("#88886a")), false));
        list.add(new ReadingColorBean(R.drawable.ic_drawable_font_color10_def, new ZLColor(Color.parseColor("#332b24")),
                R.color.color_7b6d62, new ZLColor(Color.parseColor("#7b6d62")), false));
        mView.setReadingColorSet(list);
    }

    @Override
    public void setFontMinus(float minSize) {
        ZLIntegerRangeOption fontSizeOption = mCBReaderApp.getFontSizeOption();
        if (fontSizeOption.getValue() > minSize) {
            fontSizeOption.setValue(fontSizeOption.getValue() - 2);
            mCBReaderApp.clearTextCaches();
            mView.repaint();
        }
    }

    @Override
    public void setFontIncrease(float maxSize) {
        ZLIntegerRangeOption fontSizeOption = mCBReaderApp.getFontSizeOption();
        if (fontSizeOption.getValue() < maxSize) {
            fontSizeOption.setValue(fontSizeOption.getValue() + 2);
            mCBReaderApp.clearTextCaches();
            mView.repaint();
        }
    }

    @Override
    public void setBgTheme(ReadingColorBean data) {
        mCBReaderApp.setColorProfileName(ColorProfile.DAY);
        mCBReaderApp.setRegularTextColor(data.mTextColor);
        mCBReaderApp.setBackgroundColor(data.mBgColor);
        mView.repaint();
        mView.setSelectedBgTheme(data.mBgColor);
    }

    @Override
    public void setDayOrNight() {

        if (mCBReaderApp.isColorProfileDay()) {
            mCBReaderApp.setColorProfileName(ColorProfile.NIGHT);
            mView.showDayOrNight(false);
            mView.setScreenBrightness(30);
        } else {
            mCBReaderApp.setColorProfileName(ColorProfile.DAY);
            mView.showDayOrNight(true);
            int readerScreenBright = ZLibrary.Instance().ScreenBrightnessLevelOption.getValue();
            mView.setScreenBrightness(readerScreenBright);
        }
        mView.repaint();
    }

    @Override
    public void getDayOrNightState() {
        if (mCBReaderApp.isColorProfileDay()) {
            mView.showDayOrNight(true);
        } else {
            mView.showDayOrNight(false);
        }
    }

    @Override
    public void getBgTheme() {
        if (mCBReaderApp.isColorProfileDay()) {
            mView.setSelectedBgTheme(mCBReaderApp.getBackgroundColor());
        } else {
            mView.setNoSelectedBgTheme();
        }
    }

    @Override
    public void saveScreenBrightness(int level) {
        ZLibrary.Instance().ScreenBrightnessLevelOption.setValue(level);
    }

    @Override
    public void getReadProgressInfo() {
        int maxProgress = mCBReaderApp.getMaxReadProgress();
        int currentProgress = mCBReaderApp.getReadProgress();
        StringBuilder builder = new StringBuilder();
        builder.append(mCBReaderApp.pagePositionPec());
        String currentTocTitle = mCBReaderApp.getCurrentTocTitle();
        if (currentTocTitle != null) {
            builder.append(currentTocTitle);
        }

        mView.setReadProgressInfo(maxProgress, currentProgress, builder.toString());
    }

    @Override
    public void saveReadPosition() {
        ZLTextPosition position = mCBReaderApp.getReadPosition();
        String paragraphIndex = String.valueOf(position.getParagraphIndex());
        String elementIndex = String.valueOf(position.getElementIndex());
        String charIndex = String.valueOf(position.getCharIndex());
        String timestampStr = String.valueOf(System.currentTimeMillis());
        String progress = mCBReaderApp.pagePositionPec();
        DBManager.getInstance().saveBookReadPosition(mBookId, paragraphIndex, elementIndex, charIndex, timestampStr, progress, mPageCount);
    }

    @Override
    public void turnNextToc() {
        mCBReaderApp.gotoNextToc();

        mView.repaint();

        getReadProgressInfo();
        getMarkBtnStatus();
    }

    @Override
    public void turnPreToc() {
        mCBReaderApp.gotoPreToc();
        mView.repaint();

        getReadProgressInfo();
        getMarkBtnStatus();
    }

    @Override
    public void releaseBookData() {
        mCBReaderApp.releaseModel();
        mCBReaderApp.clearTextCaches();
    }

    @Override
    public void gotoPageByPec(int progress) {
        mCBReaderApp.gotoPageByPec(progress);
        getReadProgressInfo();
    }

    @Override
    public void gotoPosition(int paragraphIndex) {
        mCBReaderApp.gotoPosition(paragraphIndex, 0, 0);
    }

    @Override
    public void gotoMarkPosition(int position) {
        BookMarkBean bean = mBookMarkList.get(mBookMarkList.size() - 1 - position);
        mCBReaderApp.gotoPosition(Integer.valueOf(bean.getParagraphIndex()), 0, 0);
    }

    @Override
    public void getMarkBtnStatus() {
        boolean includeMark = false;
        int markIndex = -1;
        for (BookMarkBean item : mBookMarkList) {
            int paragraph = Integer.valueOf(item.getParagraphIndex());
            if (paragraph >= mCBReaderApp.getParagraphStartIndex()
                    && paragraph < mCBReaderApp.getParagraphEndIndex()) {
                includeMark = true;
                markIndex = item.getParagraphIndex();
                break;
            }
        }
        mView.setMarkBtnStatus(includeMark, markIndex);
    }

    @Override
    public void getCurrentToc() {
        TOCTree currentTocTree = mCBReaderApp.getCurrentTOCElement();
        mView.setCurrentToc(currentTocTree);
    }

    @Override
    public void getScreenBrightness() {
        int readerScreenBright = ZLibrary.Instance().ScreenBrightnessLevelOption.getValue();
        mView.setScreenBrightness(readerScreenBright);
    }

    private void reportEBookRead() {
        Subscription subscription = DataRepository.getInstance().reportEBookRead(mBookId, mBelongLibCode)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<BaseResultEntityVo<BaseDataResultVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResultEntityVo<BaseDataResultVo> baseDataResultVoBaseResultEntityVo) {

                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void saveReadPageCount(int pageCount) {
        mPageCount++;
        if (mPageCount == 20) {
            reportEBookRead();
        }

        if (mPageCount >= 20 && !UserRepository.getInstance().isLogin()) {
            if (mView != null) {
                mView.needLoginTip(false);
                saveReadPosition();
            }
        }
    }


    private void parserEpubBook() {
        mView.startParseBooks();
        Subscription subscription = Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                BookColumns book = DBManager.getInstance().getBookStoredPosition(mBookId);
                ZLTextFixedPosition position;
                if (book != null && book.getParagraph_index() != null) {
                    mPageCount = book.getReadPageCount();
                    position = new ZLTextFixedPosition(
                            Integer.valueOf(book.getParagraph_index()),
                            Integer.valueOf(book.getElement_index()),
                            Integer.valueOf(book.getChar_index()));
                } else {
                    mPageCount = 0;
                    position = new ZLTextFixedPosition(0, 0, 0);
                }
                boolean openBookSuccess = mCBReaderApp.openBook(mAbsolutePath, position, mBookTitle, mBookAuthor);
                if (openBookSuccess) {
                    DBManager.getInstance().insertBook(mBookId, mBookAuthor, mBookTitle, mCoverImg, mDownLoadUrl,
                            mAbsolutePath, "", mShareUrl, mShareContent, mBelongLibCode,mDescContent);
                } else {
                    File file = new File(mAbsolutePath);
                    file.deleteOnExit();
                }
                subscriber.onNext(openBookSuccess);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.openEpubBooksFailure();
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            if (aBoolean) {
                                mView.openEpubBooksSuccess();

                                if (mPageCount >= 20 && !UserRepository.getInstance().isLogin()) {
                                    mView.needLoginTip(true);
                                }
                                //获取电子书收藏状态
                                getEBookCollectionStatus();
                            } else {
                                mView.openEpubBooksFailure();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private class DownloadListenerWrapper implements DownloadListener {

        @Override
        public void taskWait(DownloadTask task) {

        }

        @Override
        public void taskStart(@NonNull DownloadTask task) {

        }

        @Override
        public void connectTrialStart(@NonNull DownloadTask task) {

        }

        @Override
        public void connectTrialEnd(@NonNull DownloadTask task) {

        }

        @Override
        public void connectStart(@NonNull DownloadTask task) {

        }

        @Override
        public void connectEnd(@NonNull DownloadTask task) {

        }

        @Override
        public void fetchStart(@NonNull DownloadTask task) {

        }

        @Override
        public void fetchProgress(@NonNull DownloadTask task) {
            if (mView != null
                    && task.getInfo() != null) {
                mView.downLoadProgress((int) (100 * (task.getInfo().getTotalOffset() * 1.0f / task.getInfo().getTotalLength())));
            }
        }

        @Override
        public void fetchEnd(@NonNull DownloadTask task) {

        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
            if (cause == EndCause.COMPLETED) {
                if (mView != null) {
                    mView.downLoadSuccess();
                    parserEpubBook();
                }
            } else {
                if (mView != null) {
                    mView.downLoadFailure();
                }
            }
        }
    }


    private boolean mIsCollectedEBook;

    @Override
    public void getEBookCollectionStatus() {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(mBookId) && !TextUtils.isEmpty(readerId)) {
            Subscription subscription = EBookRepository.getInstance().getEBookCollectStatus(mBookId, readerId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                if (e instanceof ApiException) {
                                    ApiException exception = (ApiException) e;
                                    switch (exception.getCode()) {
                                        case 30100:
                                            mView.pleaseLogin();
                                            break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (null != mView) {
                                mIsCollectedEBook = aBoolean;
                                mView.setEBookCollectionStatus(mIsCollectedEBook);
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public void collectionOrCancelEBook() {
        if (mIsCollectedEBook) {
            //已收藏电子书，点击则取消
            cancelCollectionEBook();
        } else {
            //未收藏，点击收藏
            collectionEBook();
        }
    }

    @Override
    public void collectionEBook() {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)) {
            //please login
            mView.pleaseLogin();
            return;
        }
        if (!TextUtils.isEmpty(mBookId)) {
            ArrayMap<String, Object> map = new ArrayMap<>();
            map.put("ebookId", mBookId);
            map.put("readerId", readerId);
            Subscription subscription = EBookRepository.getInstance().collectionEBook(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                if (e instanceof ApiException) {
                                    ApiException exception = (ApiException) e;
                                    switch (exception.getCode()) {
                                        case 30100:
                                            mView.pleaseLogin();
                                            break;
                                        default:
                                            //mView.showErrorMsg(R.string.network_fault);
                                            break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (null != mView) {
                                if (aBoolean) {
                                    mIsCollectedEBook = true;
                                    mView.collectEBookSuccess(true);
                                } else {
                                    //mView.showErrorMsg(R.string.collect_video_fail);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public boolean isLogin() {
        return UserRepository.getInstance().isLogin();
    }

    //取消收藏电子书
    private void cancelCollectionEBook() {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)) {
            //please login
            mView.pleaseLogin();
            return;
        }
        if (!TextUtils.isEmpty(mBookId)) {
            JSONArray ebookIds = new JSONArray();
            ebookIds.put(mBookId);

            Subscription subscription = EBookRepository.getInstance().cancelCollectionEBook(ebookIds, readerId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                if (e instanceof ApiException) {
                                    ApiException exception = (ApiException) e;
                                    switch (exception.getCode()) {
                                        case 30100:
                                            mView.pleaseLogin();
                                            break;
                                        default:
                                            //mView.showErrorMsg(R.string.network_fault);
                                            break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (null != mView) {
                                if (aBoolean) {
                                    mIsCollectedEBook = false;
                                    mView.collectEBookSuccess(false);
                                } else {
                                    //mView.showErrorMsg(R.string.network_fault);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }
}
