package com.tzpt.cloudlibrary.ui.paperbook;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookListItemVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.RankingBookListVo;
import com.tzpt.cloudlibrary.ui.search.SearchManager;
import com.tzpt.cloudlibrary.utils.DateUtils;
import com.tzpt.cloudlibrary.utils.HtmlFormatUtil;
import com.tzpt.cloudlibrary.utils.ImageUrlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 图书操作类型
 * Created by Administrator on 2017/6/5.
 */
public class PaperBookPresenter extends RxPresenter<PaperBookContract.View> implements PaperBookContract.Presenter {

    private int mCurrentType = PaperBookFilterType.Hot_Book_List;
    private Map<String, String> mParameter;
    private Subscription mHotBookSubscription;
    private Subscription mNewBookSubscription;
    private String mLibraryCode;
    private int mClassificationId = -1;     //分类ID
//    private int mSortType = 1;              //排行榜->1:月榜 2:季榜 3:年榜

    public PaperBookPresenter(PaperBookContract.View view) {
        attachView(view);
        mView.setPresenter(this);
    }

    @Override
    public void setFilterType(int type) {
        this.mCurrentType = type;
    }

    @Override
    public void setParameter(Map<String, String> parameters) {
        mParameter = parameters;
        if (mParameter.containsKey("libCode")) {
            mLibraryCode = mParameter.get("libCode");
            mParameter.remove("libCode");
        }
    }

    //设置图书分类ID
    @Override
    public void setPagerBookClassificationId(int classificationId) {
        this.mClassificationId = classificationId;
    }

//    @Override
//    public void setSortType(int sortType) {
//        this.mSortType = sortType;
//    }

    /**
     * 获取图书列表
     */
    @Override
    public void getPaperBook(int pageNum) {
        switch (mCurrentType) {
            //首页
            case PaperBookFilterType.Hot_Book_List:
                mParameter.put("pageNo", String.valueOf(pageNum));
                mParameter.put("pageCount", String.valueOf(20));
                if (mClassificationId > 0) {
                    mParameter.put("categoryId", String.valueOf(mClassificationId));
                } else {
                    mParameter.remove("categoryId");
                }
                if (!TextUtils.isEmpty(mLibraryCode)) {
                    mParameter.put("libCode", mLibraryCode);
                }
                getHotBookList(mParameter, pageNum);        //一周热门
                break;
            case PaperBookFilterType.New_Book_List:
                mParameter.put("pageNo", String.valueOf(pageNum));
                mParameter.put("pageCount", String.valueOf(20));
                if (mClassificationId > 0) {
                    mParameter.put("categoryId", String.valueOf(mClassificationId));
                } else {
                    mParameter.remove("categoryId");
                }
                if (!TextUtils.isEmpty(mLibraryCode)) {
                    mParameter.put("libCode", mLibraryCode);
                }
                getNewBookList(mParameter, pageNum);        //最新上架
                break;
            case PaperBookFilterType.Library_Book_list:     //本馆图书（使用本馆最新上架数据）
                mParameter.put("pageNo", String.valueOf(pageNum));
                mParameter.put("pageCount", String.valueOf(20));
                if (mClassificationId > 0) {
                    mParameter.put("categoryId", String.valueOf(mClassificationId));
                } else {
                    mParameter.remove("categoryId");
                }
                if (!TextUtils.isEmpty(mLibraryCode)) {
                    mParameter.put("libCode", mLibraryCode);
                }
                getLibrarySearchBookList(mParameter, pageNum);
                break;
            //全局搜索
            case PaperBookFilterType.Normal_Search_Book_List:   //图书搜索列表，包含全局高级搜索
                mParameter.put("pageNo", String.valueOf(pageNum));
                mParameter.put("pageCount", String.valueOf(20));
                getSearchBookList(mParameter, pageNum);
                break;
            case PaperBookFilterType.Library_Search_Book_List:  //馆内搜索，包含馆内高级搜索
                mParameter.put("pageNo", String.valueOf(pageNum));
                mParameter.put("pageCount", String.valueOf(20));
                getLibrarySearchBookList(mParameter, pageNum);
                break;
            //排行榜
            case PaperBookFilterType.Ranking_Like_Month:              //点赞排行榜
                getRankingPraiseBook(pageNum, 1);
                break;
            case PaperBookFilterType.Ranking_Like_Quarter:              //点赞排行榜
                getRankingPraiseBook(pageNum, 2);
                break;
            case PaperBookFilterType.Ranking_Like_Year:              //点赞排行榜
                getRankingPraiseBook(pageNum, 3);
                break;
            case PaperBookFilterType.Ranking_Read_Month:              //阅读排行榜
                getRankingBorrowBook(pageNum, 1);
                break;
            case PaperBookFilterType.Ranking_Read_Quarter:              //阅读排行榜
                getRankingBorrowBook(pageNum, 2);
                break;
            case PaperBookFilterType.Ranking_Read_Year:              //阅读排行榜
                getRankingBorrowBook(pageNum, 3);
                break;
            case PaperBookFilterType.Ranking_Recommend_Month:         //推荐排行榜
                getRankingRecommendBook(pageNum, 1);
                break;
            case PaperBookFilterType.Ranking_Recommend_Quarter:         //推荐排行榜
                getRankingRecommendBook(pageNum, 2);
                break;
            case PaperBookFilterType.Ranking_Recommend_Year:         //推荐排行榜
                getRankingRecommendBook(pageNum, 3);
                break;
        }
    }

    /**
     * 一周热门
     *
     * @param parameters 参数
     * @param pageNum    页码
     */
    private void getHotBookList(Map<String, String> parameters, final int pageNum) {
        if (null != mHotBookSubscription && !mHotBookSubscription.isUnsubscribed()) {
            mHotBookSubscription.unsubscribe();
        }
        mHotBookSubscription = DataRepository.getInstance().getHotBookList(parameters)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<BookListVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.showNetError(pageNum == 1);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<BookListVo> bookListVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (bookListVoBaseResultEntityVo.status == 200) {
                                if (bookListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                    List<BookBean> bookDetailInfos = new ArrayList<>();
                                    for (BookListItemVo item : bookListVoBaseResultEntityVo.data.resultList) {
                                        BookBean book = new BookBean();
                                        book.mBook.mName = item.bookName;
                                        book.mBook.mId = item.id;
                                        book.mBook.mBookId = item.bookId;
                                        book.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(item.image);
                                        book.mBook.mIsbn = item.isbn;
                                        book.mBook.mPublishDate = item.publishDate;
                                        book.mBook.mSummary = HtmlFormatUtil.delHTMLTag(item.contentDescript);

                                        book.mAuthor.mName = item.author;
                                        book.mPress.mName = item.publisher;
                                        book.mBorrowNum = item.borrowNum;
                                        bookDetailInfos.add(book);
                                    }
                                    mView.showBookList(bookDetailInfos, bookListVoBaseResultEntityVo.data.totalCount, 0, bookListVoBaseResultEntityVo.data.limitTotalCount, pageNum == 1);
                                } else {
                                    mView.showBookListIsEmpty(pageNum == 1);
                                }
                            } else {
                                mView.showNetError(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(mHotBookSubscription);
    }

    /**
     * 最新上架
     *
     * @param parameters 参数
     * @param pageNum    页码
     */
    private void getNewBookList(Map<String, String> parameters, final int pageNum) {
        if (null != mNewBookSubscription && !mNewBookSubscription.isUnsubscribed()) {
            mNewBookSubscription.unsubscribe();
        }
        mNewBookSubscription = DataRepository.getInstance().getNewBookList(parameters)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<BookListVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.showNetError(pageNum == 1);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<BookListVo> bookListVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (bookListVoBaseResultEntityVo.status == 200) {
                                if (bookListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                    List<BookBean> bookDetailInfos = new ArrayList<>();
                                    for (BookListItemVo item : bookListVoBaseResultEntityVo.data.resultList) {
                                        BookBean book = new BookBean();
                                        book.mBook.mId = item.id;
                                        book.mBook.mBookId = item.bookId;
                                        book.mBook.mName = item.bookName;
                                        book.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(item.image);
                                        book.mBook.mIsbn = item.isbn;
                                        book.mBook.mPublishDate = item.publishDate;
                                        book.mBook.mSummary = HtmlFormatUtil.delHTMLTag(item.contentDescript);

                                        book.mAuthor.mName = item.author;
                                        book.mPress.mName = item.publisher;
                                        book.mBorrowNum = item.borrowNum;
                                        //馆内显示新上架图书新标签
                                        if (!TextUtils.isEmpty(mLibraryCode) && !TextUtils.isEmpty(item.storageTime)) {
                                            book.mHasNewBookFlag = DateUtils.isWithinTimeLimit30Days(item.storageTime);
                                        }
                                        bookDetailInfos.add(book);
                                    }
                                    mView.showBookList(bookDetailInfos, bookListVoBaseResultEntityVo.data.totalCount, bookListVoBaseResultEntityVo.data.totalBooks, bookListVoBaseResultEntityVo.data.limitTotalCount, pageNum == 1);
                                } else {
                                    mView.showBookListIsEmpty(pageNum == 1);
                                }
                            } else {
                                mView.showNetError(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(mNewBookSubscription);
    }


    /**
     * 获取图书列表-包含全局搜索,全局高级搜索
     *
     * @param parameters 请求参数
     * @param pageNum    页数
     */
    private void getSearchBookList(Map<String, String> parameters, final int pageNum) {
        Subscription subscription = DataRepository.getInstance().getSearchBookList(parameters)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<BookListVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.showNetError(pageNum == 1);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<BookListVo> bookListVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (bookListVoBaseResultEntityVo.status == 200) {
                                if (bookListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                    List<BookBean> bookDetailInfos = new ArrayList<>();
                                    for (BookListItemVo item : bookListVoBaseResultEntityVo.data.resultList) {
                                        BookBean book = new BookBean();
                                        book.mBook.mId = item.id;
                                        book.mBook.mBookId = item.bookId;
                                        book.mBook.mName = item.bookName;
                                        book.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(item.image);
                                        book.mBook.mIsbn = item.isbn;
                                        book.mBook.mPublishDate = item.publishDate;
                                        book.mBook.mSummary = HtmlFormatUtil.delHTMLTag(item.contentDescript);

                                        book.mAuthor.mName = item.author;
                                        book.mPress.mName = item.publisher;
                                        book.mBorrowNum = item.borrowNum;
                                        bookDetailInfos.add(book);
                                    }
                                    mView.showBookList(bookDetailInfos, bookListVoBaseResultEntityVo.data.totalCount, bookListVoBaseResultEntityVo.data.totalBooks, bookListVoBaseResultEntityVo.data.limitTotalCount, pageNum == 1);
                                } else {
                                    mView.showBookListIsEmpty(pageNum == 1);
                                }
                            } else {
                                mView.showNetError(pageNum == 1);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 获取图书馆图书列表-包含馆内搜索，馆内图书列表，馆内高级搜索
     *
     * @param parameters 请求参数
     * @param pageNum    页数
     */
    private void getLibrarySearchBookList(Map<String, String> parameters, final int pageNum) {
        if (null != mLibraryCode) {
            Subscription subscription = DataRepository.getInstance().getLibrarySearchBookList(mLibraryCode, parameters)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResultEntityVo<BookListVo>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.showNetError(pageNum == 1);
                            }
                        }

                        @Override
                        public void onNext(BaseResultEntityVo<BookListVo> bookListVoBaseResultEntityVo) {
                            if (mView != null) {
                                if (bookListVoBaseResultEntityVo.status == 200) {
                                    if (bookListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                        List<BookBean> bookDetailInfos = new ArrayList<>();
                                        for (BookListItemVo item : bookListVoBaseResultEntityVo.data.resultList) {
                                            BookBean book = new BookBean();
                                            book.mBook.mId = item.id;
                                            book.mBook.mBookId = item.bookId;
                                            book.mBook.mName = item.bookName;
                                            book.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(item.image);
                                            book.mBook.mIsbn = item.isbn;
                                            book.mBook.mPublishDate = item.publishDate;
                                            book.mBook.mSummary = HtmlFormatUtil.delHTMLTag(item.contentDescript);

                                            book.mPress.mName = item.publisher;
                                            book.mBorrowNum = item.borrowNum;
                                            book.mIsShowBorrowNum = false;
                                            book.mAuthor.mName = item.author;
                                            if (!TextUtils.isEmpty(item.storageTime)) {
                                                book.mHasNewBookFlag = DateUtils.isWithinTimeLimit30Days(item.storageTime);
                                            }
                                            bookDetailInfos.add(book);
                                        }
                                        mView.showBookList(bookDetailInfos, bookListVoBaseResultEntityVo.data.totalCount, bookListVoBaseResultEntityVo.data.totalBooks, 0, pageNum == 1);
                                    } else {
                                        mView.showBookListIsEmpty(pageNum == 1);
                                    }
                                } else {
                                    mView.showNetError(pageNum == 1);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    //排行榜推荐书籍
    private void getRankingRecommendBook(final int pageNum, int sortType) {
        final Subscription subscription = DataRepository.getInstance().getRankingRecommendList(pageNum, 20, sortType, mClassificationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<RankingBookListVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.showNetError(pageNum == 1);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<RankingBookListVo> rankingBookListVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (rankingBookListVoBaseResultEntityVo.status == 200) {
                                if (rankingBookListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                    List<BookBean> bookDetailInfos = new ArrayList<>();
                                    for (BookListItemVo item : rankingBookListVoBaseResultEntityVo.data.resultList) {
                                        BookBean book = new BookBean();
                                        book.mBook.mId = item.id;
                                        book.mBook.mBookId = item.bookId;
                                        book.mBook.mName = item.bookName;
                                        book.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(item.image);
                                        book.mBook.mIsbn = item.isbn;
                                        book.mBook.mPublishDate = item.publishDate;
                                        book.mBook.mSummary = HtmlFormatUtil.delHTMLTag(item.contentDescript);

                                        book.mPress.mName = item.publisher;
                                        book.mBorrowNum = item.borrowNum;
                                        book.mRecommendNum = item.recommendNum;
                                        book.mPraiseNum = item.praiseNum;
                                        book.mAuthor.mName = item.author;
                                        bookDetailInfos.add(book);
                                    }
                                    mView.showBookList(bookDetailInfos, rankingBookListVoBaseResultEntityVo.data.totalCount, 0, rankingBookListVoBaseResultEntityVo.data.limitTotalCount, pageNum == 1);
                                } else {
                                    mView.showBookListIsEmpty(pageNum == 1);
                                }
                            } else {
                                mView.showNetError(pageNum == 1);
                            }
                        }
                    }
                });

        addSubscrebe(subscription);
    }

    //点赞排行榜
    private void getRankingPraiseBook(final int pageNum, int sortType) {
        final Subscription subscription = DataRepository.getInstance().getRankingPraiseList(pageNum, 20, sortType, mClassificationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<RankingBookListVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.showNetError(pageNum == 1);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<RankingBookListVo> rankingBookListVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (rankingBookListVoBaseResultEntityVo.status == 200) {
                                if (rankingBookListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                    List<BookBean> bookDetailInfos = new ArrayList<>();
                                    for (BookListItemVo item : rankingBookListVoBaseResultEntityVo.data.resultList) {
                                        BookBean book = new BookBean();
                                        book.mBook.mId = item.id;
                                        book.mBook.mBookId = item.bookId;
                                        book.mBook.mName = item.bookName;
                                        book.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(item.image);
                                        book.mBook.mIsbn = item.isbn;
                                        book.mBook.mPublishDate = item.publishDate;
                                        book.mBook.mSummary = HtmlFormatUtil.delHTMLTag(item.contentDescript);

                                        book.mPress.mName = item.publisher;
                                        book.mBorrowNum = item.borrowNum;
                                        book.mRecommendNum = item.recommendNum;
                                        book.mPraiseNum = item.praiseNum;
                                        book.mAuthor.mName = item.author;
                                        bookDetailInfos.add(book);
                                    }
                                    mView.showBookList(bookDetailInfos, rankingBookListVoBaseResultEntityVo.data.totalCount, 0, rankingBookListVoBaseResultEntityVo.data.limitTotalCount, pageNum == 1);
                                } else {
                                    mView.showBookListIsEmpty(pageNum == 1);
                                }
                            } else {
                                mView.showNetError(pageNum == 1);
                            }
                        }
                    }
                });

        addSubscrebe(subscription);
    }

    //借阅排行榜
    private void getRankingBorrowBook(final int pageNum, int sortType) {
        final Subscription subscription = DataRepository.getInstance().getRankingBorrowList(pageNum, 20, sortType, mClassificationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<RankingBookListVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.showNetError(pageNum == 1);
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<RankingBookListVo> rankingBookListVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (rankingBookListVoBaseResultEntityVo.status == 200) {
                                if (rankingBookListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                    List<BookBean> bookDetailInfos = new ArrayList<>();
                                    for (BookListItemVo item : rankingBookListVoBaseResultEntityVo.data.resultList) {
                                        BookBean book = new BookBean();
                                        book.mBook.mId = item.id;
                                        book.mBook.mBookId = item.bookId;
                                        book.mBook.mName = item.bookName;
                                        book.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(item.image);
                                        book.mBook.mIsbn = item.isbn;
                                        book.mBook.mPublishDate = item.publishDate;
                                        book.mBook.mSummary = HtmlFormatUtil.delHTMLTag(item.contentDescript);

                                        book.mPress.mName = item.publisher;
                                        book.mBorrowNum = item.borrowNum;
                                        book.mRecommendNum = item.recommendNum;
                                        book.mPraiseNum = item.praiseNum;
                                        book.mAuthor.mName = item.author;
                                        bookDetailInfos.add(book);
                                    }
                                    mView.showBookList(bookDetailInfos, rankingBookListVoBaseResultEntityVo.data.totalCount, 0, rankingBookListVoBaseResultEntityVo.data.limitTotalCount, pageNum == 1);
                                } else {
                                    mView.showBookListIsEmpty(pageNum == 1);
                                }
                            } else {
                                mView.showNetError(pageNum == 1);
                            }
                        }
                    }
                });

        addSubscrebe(subscription);
    }

    //是否排行榜列表
    @Override
    public boolean isRankingList() {
        return mCurrentType == PaperBookFilterType.Ranking_Recommend_Month
                || mCurrentType == PaperBookFilterType.Ranking_Recommend_Quarter
                || mCurrentType == PaperBookFilterType.Ranking_Recommend_Year
                || mCurrentType == PaperBookFilterType.Ranking_Like_Month
                || mCurrentType == PaperBookFilterType.Ranking_Like_Quarter
                || mCurrentType == PaperBookFilterType.Ranking_Like_Year
                || mCurrentType == PaperBookFilterType.Ranking_Read_Month
                || mCurrentType == PaperBookFilterType.Ranking_Read_Quarter
                || mCurrentType == PaperBookFilterType.Ranking_Read_Year;
    }

    @Override
    public boolean isBorrowRanking() {
        return mCurrentType == PaperBookFilterType.Ranking_Read_Month
                || mCurrentType == PaperBookFilterType.Ranking_Read_Quarter
                || mCurrentType == PaperBookFilterType.Ranking_Read_Year;
    }

    @Override
    public boolean isRecommendRanking() {
        return mCurrentType == PaperBookFilterType.Ranking_Recommend_Month
                || mCurrentType == PaperBookFilterType.Ranking_Recommend_Quarter
                || mCurrentType == PaperBookFilterType.Ranking_Recommend_Year;
    }

    @Override
    public boolean isPraiseRanking() {
        return mCurrentType == PaperBookFilterType.Ranking_Like_Month
                || mCurrentType == PaperBookFilterType.Ranking_Like_Quarter
                || mCurrentType == PaperBookFilterType.Ranking_Like_Year;
    }

    @Override
    public int getCurrentType() {
        return mCurrentType;
    }

    @Override
    public boolean isLibraryBookList() {
        return null != mLibraryCode;
    }

    @Override
    public boolean isSearchBookList() {
        return mCurrentType == PaperBookFilterType.Normal_Search_Book_List
                || mCurrentType == PaperBookFilterType.Library_Search_Book_List;
    }

    @Override
    public String getLibraryCode() {
        return mLibraryCode;
    }

    @Override
    public void saveHistoryTag(String content) {
        SearchManager.saveHistoryTag(0, content);
    }

    @Override
    public void mustShowProgressLoading() {
        mView.showRefreshLoading();
    }

}
