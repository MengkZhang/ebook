package com.tzpt.cloudlibrary.ui.ranklist;

import com.sina.weibo.sdk.utils.ImageUtils;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.RankingHomeVo;
import com.tzpt.cloudlibrary.utils.ImageUrlUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 排行榜列表
 * Created by ZhiqiangJia on 2017-08-11.
 */
public class RankListPresenter extends RxPresenter<RankListContract.View> implements
        RankListContract.Presenter {

    private boolean mIsEmpty = true;

    @Override
    public void getRankList() {
        mView.showRankProgress(mIsEmpty);
        Subscription subscription = DataRepository.getInstance().getRankingList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<RankingHomeVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.showNetError();
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<RankingHomeVo> rankListVo) {
                        if (mView != null) {
                            if (rankListVo.status == 200) {
                                if (null != rankListVo.data) {
                                    //点赞排行榜
                                    if (null != rankListVo.data.praises && rankListVo.data.praises.size() > 0) {
                                        List<BookBean> thumbsUpRankBookList = new ArrayList<>();
                                        int bookSize = rankListVo.data.praises.size();
                                        for (int i = 0; i < bookSize; i++) {
                                            BookBean listBookBean = new BookBean();
                                            RankingHomeVo.RankBookVo bookItem = rankListVo.data.praises.get(i);
                                            listBookBean.mBook.mId = bookItem.id;
                                            listBookBean.mBook.mBookId = bookItem.bookId;
                                            listBookBean.mBook.mName = bookItem.bookName;
                                            listBookBean.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(bookItem.image);
                                            listBookBean.mBook.mIsbn = bookItem.isbn;
                                            listBookBean.mAuthor.mName = bookItem.author;
                                            thumbsUpRankBookList.add(listBookBean);
                                        }
                                        mView.setRankThumbsUpBookList(thumbsUpRankBookList);
                                    } else {
                                        mView.hideRankThumbsUpBookList();
                                    }
                                    //推荐排行榜
                                    if (null != rankListVo.data.recommends && rankListVo.data.recommends.size() > 0) {

                                        List<BookBean> recommendRankBookList = new ArrayList<>();
                                        int bookSize = rankListVo.data.recommends.size();
                                        for (int i = 0; i < bookSize; i++) {
                                            RankingHomeVo.RankBookVo bookItem = rankListVo.data.recommends.get(i);
                                            BookBean listBookBean = new BookBean();
                                            listBookBean.mBook.mId = bookItem.id;
                                            listBookBean.mBook.mBookId = bookItem.bookId;
                                            listBookBean.mBook.mName = bookItem.bookName;
                                            listBookBean.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(bookItem.image);
                                            listBookBean.mBook.mIsbn = bookItem.isbn;
                                            listBookBean.mAuthor.mName = bookItem.author;

                                            recommendRankBookList.add(listBookBean);
                                        }
                                        mView.setRankRecommendBookList(recommendRankBookList);
                                    } else {
                                        mView.hideRankRecommendBookList();
                                    }
                                    //借阅排行榜
                                    if (null != rankListVo.data.borrows && rankListVo.data.borrows.size() > 0) {
                                        List<BookBean> borrowRankBookList = new ArrayList<>();
                                        int bookSize = rankListVo.data.borrows.size();
                                        for (int i = 0; i < bookSize; i++) {
                                            RankingHomeVo.RankBookVo bookItem = rankListVo.data.borrows.get(i);
                                            BookBean listBookBean = new BookBean();

                                            listBookBean.mBook.mId = bookItem.id;
                                            listBookBean.mBook.mBookId = bookItem.bookId;
                                            listBookBean.mBook.mName = bookItem.bookName;
                                            listBookBean.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(bookItem.image);
                                            listBookBean.mBook.mIsbn = bookItem.isbn;
                                            listBookBean.mAuthor.mName = bookItem.author;

                                            borrowRankBookList.add(listBookBean);
                                        }
                                        mView.setRankBorrowBookList(borrowRankBookList);
                                    } else {
                                        mView.hideRankBorrowBookList();
                                    }

                                    //阅读排行榜
                                    if (null != rankListVo.data.ebooks && rankListVo.data.ebooks.size() > 0) {
                                        List<EBookBean> readingRankBookList = new ArrayList<>();
                                        int bookSize = rankListVo.data.ebooks.size();
                                        for (int i = 0; i < bookSize; i++) {
                                            RankingHomeVo.RankEBookVo bookItem = rankListVo.data.ebooks.get(i);
                                            EBookBean listBookBean = new EBookBean();

                                            listBookBean.mEBook.mId = bookItem.id;
                                            listBookBean.mEBook.mName = bookItem.bookName;
                                            listBookBean.mEBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(bookItem.image);
                                            listBookBean.mEBook.mIsbn = bookItem.isbn;
                                            listBookBean.mAuthor.mName = bookItem.author;
                                            readingRankBookList.add(listBookBean);
                                        }
                                        mView.setRankReadingBookList(readingRankBookList);
                                    } else {
                                        mView.hideRankReadingBookList();
                                    }
                                    mView.setRankBookContentView();
                                    mIsEmpty = false;
                                } else {
                                    mView.setRankBookListEmpty();
                                    mIsEmpty = true;
                                }
                            } else {
                                mView.showNetError();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);

    }
}
