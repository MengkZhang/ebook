package com.tzpt.cloudlibrary.ui.ebook;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.LocalEBook;
import com.tzpt.cloudlibrary.modle.local.db.BookColumns;
import com.tzpt.cloudlibrary.modle.local.db.DBManager;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/11/20.
 */

public class EBookShelfPresenter extends RxPresenter<EBookShelfContract.View>
        implements EBookShelfContract.Presenter {

    @Override
    public void getLocalEBookList() {
        Subscription subscription = Observable.create(new Observable.OnSubscribe<List<BookColumns>>() {

            @Override
            public void call(Subscriber<? super List<BookColumns>> subscriber) {
                subscriber.onNext(DBManager.getInstance().getBookList());
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BookColumns>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<BookColumns> bookColumns) {
                        if (mView != null) {
                            if (bookColumns != null && bookColumns.size() > 0) {
                                List<LocalEBook> localEBookList = new ArrayList<>();
                                for (BookColumns item : bookColumns) {
                                    LocalEBook eBook = new LocalEBook();
                                    eBook.mId = item.getBook_id();
                                    eBook.mAuthor = item.getAuthor();
                                    eBook.mCoverImg = item.getCover_image();
                                    eBook.mFilePath = item.getLocal_path();
                                    eBook.mDownloadUrl = item.getDownload_file();
                                    eBook.mProgress = item.getRead_progress();
                                    eBook.mSize = item.getSize();
                                    eBook.mTitle = item.getTitle();
                                    eBook.mShareUrl = item.getShare_url();
                                    eBook.mShareContent = item.getShare_content();
                                    eBook.mBelongLibCode = item.getBelongLibCode();
                                    eBook.mDescContent = item.getDesc_content();
                                    try {
                                        eBook.mTimestamp = Long.valueOf(item.getTime_stamp());
                                    } catch (Exception e) {
                                        eBook.mTimestamp = System.currentTimeMillis();
                                    }
                                    localEBookList.add(eBook);
                                }
                                mView.showLocalEBook(localEBookList);
                                mView.checkEditFunction(true);
                            } else {
                                mView.showLocalEBookEmpty();
                                mView.checkEditFunction(false);
                            }

                        }
                    }
                });
        addSubscrebe(subscription);

    }

    @Override
    public void delLocalEBook(final List<String> bookIdList) {
        Subscription subscription = Observable.create(new Observable.OnSubscribe<List<BookColumns>>() {

            @Override
            public void call(Subscriber<? super List<BookColumns>> subscriber) {
                DBManager.getInstance().delBook(bookIdList);
                subscriber.onNext(DBManager.getInstance().getBookList());
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BookColumns>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<BookColumns> bookColumns) {
                        if (mView != null) {
                            if (bookColumns != null && bookColumns.size() > 0) {
                                List<LocalEBook> localEBookList = new ArrayList<>();
                                for (BookColumns item : bookColumns) {
                                    LocalEBook eBook = new LocalEBook();
                                    eBook.mId = item.getBook_id();
                                    eBook.mAuthor = item.getAuthor();
                                    eBook.mCoverImg = item.getCover_image();
                                    eBook.mFilePath = item.getLocal_path();
                                    eBook.mDownloadUrl = item.getDownload_file();
                                    eBook.mProgress = item.getRead_progress();
                                    eBook.mSize = item.getSize();
                                    eBook.mTitle = item.getTitle();
                                    eBook.mShareUrl = item.getShare_url();
                                    eBook.mShareContent = item.getShare_content();
                                    eBook.mDescContent = item.getDesc_content();
                                    eBook.mTimestamp = Long.valueOf(item.getTime_stamp());
                                    localEBookList.add(eBook);
                                }
                                mView.showLocalEBook(localEBookList);
                                mView.checkEditFunction(true);
                            } else {
                                mView.showLocalEBookEmpty();
                                mView.checkEditFunction(false);
                            }

                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
