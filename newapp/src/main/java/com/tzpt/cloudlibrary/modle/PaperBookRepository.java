package com.tzpt.cloudlibrary.modle;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.Installation;
import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.business_bean.OperateReservationBookResultBean;
import com.tzpt.cloudlibrary.modle.remote.CloudLibraryApi;
import com.tzpt.cloudlibrary.modle.remote.exception.ExceptionEngine;
import com.tzpt.cloudlibrary.modle.remote.exception.ServerException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookDetailInfoNewVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookListItemVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.OperateReservationBookResultVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.RankingBookListVo;
import com.tzpt.cloudlibrary.utils.DateUtils;
import com.tzpt.cloudlibrary.utils.ImageUrlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Administrator on 2018/6/27.
 */

public class PaperBookRepository {
    private static PaperBookRepository mInstance;

    public static PaperBookRepository getInstance() {
        if (mInstance == null) {
            mInstance = new PaperBookRepository();
        }
        return mInstance;
    }

    private PaperBookRepository() {

    }

    private class HttpResultFunc<T> implements Func1<Throwable, Observable<T>> {
        @Override
        public Observable<T> call(Throwable throwable) {
            return Observable.error(ExceptionEngine.handleException(throwable));
        }
    }

    public Observable<List<BookBean>> getHotBookList(Map<String, String> parameters) {
        return CloudLibraryApi.getInstance().getHotBookList(parameters)
                .map(new Func1<BaseResultEntityVo<BookListVo>, List<BookBean>>() {
                    @Override
                    public List<BookBean> call(BaseResultEntityVo<BookListVo> bookListVoBaseResultEntityVo) {
                        if (bookListVoBaseResultEntityVo.status != 200) {
                            throw new ServerException(bookListVoBaseResultEntityVo.data.errorCode, bookListVoBaseResultEntityVo.data.message);
                        }
                        return getBookList(bookListVoBaseResultEntityVo.data.resultList);
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<BookBean>>());
    }

    public Observable<List<BookBean>> getNewBookList(Map<String, String> parameters) {
        return CloudLibraryApi.getInstance().getNewBookList(parameters)
                .map(new Func1<BaseResultEntityVo<BookListVo>, List<BookBean>>() {
                    @Override
                    public List<BookBean> call(BaseResultEntityVo<BookListVo> bookListVoBaseResultEntityVo) {
                        if (bookListVoBaseResultEntityVo.status != 200) {
                            throw new ServerException(bookListVoBaseResultEntityVo.data.errorCode, bookListVoBaseResultEntityVo.data.message);
                        }
                        return getBookList(bookListVoBaseResultEntityVo.data.resultList);
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<BookBean>>());
    }

    public Observable<List<BookBean>> getSearchBookList(Map<String, String> parameters) {
        return CloudLibraryApi.getInstance().getSearchBookList(parameters)
                .map(new Func1<BaseResultEntityVo<BookListVo>, List<BookBean>>() {
                    @Override
                    public List<BookBean> call(BaseResultEntityVo<BookListVo> bookListVoBaseResultEntityVo) {
                        if (bookListVoBaseResultEntityVo.status != 200) {
                            throw new ServerException(bookListVoBaseResultEntityVo.data.errorCode, bookListVoBaseResultEntityVo.data.message);
                        }
                        return getBookList(bookListVoBaseResultEntityVo.data.resultList);
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<BookBean>>());
    }

    public Observable<List<BookBean>> getLibrarySearchBookList(String libCode, Map<String, String> parameters) {
        return CloudLibraryApi.getInstance().getLibrarySearchBookList(libCode, parameters)
                .map(new Func1<BaseResultEntityVo<BookListVo>, List<BookBean>>() {
                    @Override
                    public List<BookBean> call(BaseResultEntityVo<BookListVo> bookListVoBaseResultEntityVo) {
                        if (bookListVoBaseResultEntityVo.status != 200) {
                            throw new ServerException(bookListVoBaseResultEntityVo.data.errorCode, bookListVoBaseResultEntityVo.data.message);
                        }
                        return getBookList(bookListVoBaseResultEntityVo.data.resultList);
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<BookBean>>());
    }

    public Observable<List<BookBean>> getRankingBorrowList(int pageNum, int pageCount, int sortType, int categoryId) {
        return CloudLibraryApi.getInstance().getRankingBorrowList(pageNum, pageCount, sortType, categoryId)
                .map(new Func1<BaseResultEntityVo<RankingBookListVo>, List<BookBean>>() {
                    @Override
                    public List<BookBean> call(BaseResultEntityVo<RankingBookListVo> bookListVoBaseResultEntityVo) {
                        if (bookListVoBaseResultEntityVo.status != 200) {
                            throw new ServerException(bookListVoBaseResultEntityVo.data.errorCode, bookListVoBaseResultEntityVo.data.message);
                        }
                        return getBookList(bookListVoBaseResultEntityVo.data.resultList);
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<BookBean>>());
    }

    public Observable<List<BookBean>> getRankingRecommendList(int pageNum, int pageCount, int sortType, int categoryId) {
        return CloudLibraryApi.getInstance().getRankingRecommendList(pageNum, pageCount, sortType, categoryId)
                .map(new Func1<BaseResultEntityVo<RankingBookListVo>, List<BookBean>>() {
                    @Override
                    public List<BookBean> call(BaseResultEntityVo<RankingBookListVo> bookListVoBaseResultEntityVo) {
                        if (bookListVoBaseResultEntityVo.status != 200) {
                            throw new ServerException(bookListVoBaseResultEntityVo.data.errorCode, bookListVoBaseResultEntityVo.data.message);
                        }
                        return getBookList(bookListVoBaseResultEntityVo.data.resultList);
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<BookBean>>());
    }

    public Observable<List<BookBean>> getRankingPraiseList(int pageNum, int pageCount, int sortType, int categoryId) {
        return CloudLibraryApi.getInstance().getRankingPraiseList(pageNum, pageCount, sortType, categoryId)
                .map(new Func1<BaseResultEntityVo<RankingBookListVo>, List<BookBean>>() {
                    @Override
                    public List<BookBean> call(BaseResultEntityVo<RankingBookListVo> bookListVoBaseResultEntityVo) {
                        if (bookListVoBaseResultEntityVo.status != 200) {
                            throw new ServerException(bookListVoBaseResultEntityVo.data.errorCode, bookListVoBaseResultEntityVo.data.message);
                        }
                        return getBookList(bookListVoBaseResultEntityVo.data.resultList);
                    }
                }).onErrorResumeNext(new HttpResultFunc<List<BookBean>>());
    }

    private List<BookBean> getBookList(List<BookListItemVo> resultList) {
        List<BookBean> bookBeanList = new ArrayList<>();
        for (BookListItemVo item : resultList) {
            BookBean book = new BookBean();
            book.mBook.mName = item.bookName;
            book.mAuthor.mName = item.author;
            book.mBook.mId = item.id;
            book.mBook.mBookId = item.bookId;
            book.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(item.image);
            book.mBook.mIsbn = item.isbn;
            book.mBook.mPublishDate = item.publishDate;
            book.mPress.mName = item.publisher;
            if (item.storageTime != null
                    && !item.storageTime.isEmpty()) {
                book.mHasNewBookFlag = DateUtils.isWithinTimeLimit30Days(item.storageTime);
            }
            bookBeanList.add(book);
        }
        return bookBeanList;
    }

    public Observable<BookBean> getBookDetail(String isbn, int fromSearch, String libCode) {
        String identity;
        if (UserRepository.getInstance().isLogin()) {
            identity = UserRepository.getInstance().getLoginUserIdCard();
        } else {
            identity = Installation.id(CloudLibraryApplication.getAppContext());
        }
        return CloudLibraryApi.getInstance().getBookDetail(isbn, identity, 1, fromSearch, libCode)
                .map(new Func1<BaseResultEntityVo<BookDetailInfoNewVo>, BookBean>() {
                    @Override
                    public BookBean call(BaseResultEntityVo<BookDetailInfoNewVo> bookDetailInfoNewVoBaseResultEntityVo) {
                        if (bookDetailInfoNewVoBaseResultEntityVo.status == 200) {
                            BookBean baseInfo = new BookBean();
                            baseInfo.mBook.mId = bookDetailInfoNewVoBaseResultEntityVo.data.bookId;
                            baseInfo.mBook.mName = bookDetailInfoNewVoBaseResultEntityVo.data.bookName;
                            baseInfo.mBook.mIsbn = bookDetailInfoNewVoBaseResultEntityVo.data.isbn;
                            baseInfo.mBook.mSummary = bookDetailInfoNewVoBaseResultEntityVo.data.summary;
                            baseInfo.mBook.mCatalog = bookDetailInfoNewVoBaseResultEntityVo.data.catalog;
                            baseInfo.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(bookDetailInfoNewVoBaseResultEntityVo.data.image);

                            baseInfo.mAuthor.mName = bookDetailInfoNewVoBaseResultEntityVo.data.author;
                            baseInfo.mAuthor.mAuthorInfo = bookDetailInfoNewVoBaseResultEntityVo.data.authorInfo;
                            baseInfo.mCategory.mName = bookDetailInfoNewVoBaseResultEntityVo.data.categoryName;
                            baseInfo.mPress.mName = bookDetailInfoNewVoBaseResultEntityVo.data.publisher;
                            baseInfo.mBorrowNum = bookDetailInfoNewVoBaseResultEntityVo.data.extraInfo.borrowNum;
                            baseInfo.mPraiseNum = bookDetailInfoNewVoBaseResultEntityVo.data.extraInfo.praiseNum;
                            baseInfo.mRecommendNum = bookDetailInfoNewVoBaseResultEntityVo.data.extraInfo.recommendNum;
                            baseInfo.mShareNum = bookDetailInfoNewVoBaseResultEntityVo.data.extraInfo.shareNum;
                            baseInfo.mHtmlUrl = bookDetailInfoNewVoBaseResultEntityVo.data.htmlUrl;

                            //出版年
                            if (bookDetailInfoNewVoBaseResultEntityVo.data.publishDate != null
                                    && !bookDetailInfoNewVoBaseResultEntityVo.data.publishDate.isEmpty()) {
                                int length = bookDetailInfoNewVoBaseResultEntityVo.data.publishDate.length();
                                if (length >= 4) {
                                    baseInfo.mBook.mPublishDate = bookDetailInfoNewVoBaseResultEntityVo.data.publishDate.substring(0, 4);
                                } else {
                                    baseInfo.mBook.mPublishDate = bookDetailInfoNewVoBaseResultEntityVo.data.publishDate;
                                }
                            } else {
                                baseInfo.mBook.mPublishDate = "暂无数据";
                            }
                            return baseInfo;
                        } else {
                            throw new ServerException(bookDetailInfoNewVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<BookBean>());
    }

    /**
     * 预约/取消预约图书
     *
     * @param appointType 操作类型：1预约，0取消预约
     * @param isbn        图书ISBN号
     * @param libCode     馆号
     * @param readerId    读者ID
     * @return 操作结果
     */
    public Observable<OperateReservationBookResultBean> operateReservationBook(int appointType, String isbn, String libCode, long readerId) {
        return CloudLibraryApi.getInstance().reservationBook(appointType, isbn, libCode, readerId)
                .map(new Func1<BaseResultEntityVo<OperateReservationBookResultVo>, OperateReservationBookResultBean>() {
                    @Override
                    public OperateReservationBookResultBean call(BaseResultEntityVo<OperateReservationBookResultVo> operateReservationBookResultVoBaseResultEntityVo) {
                        if (operateReservationBookResultVoBaseResultEntityVo.status == 200) {
                            OperateReservationBookResultBean result = new OperateReservationBookResultBean();
                            result.mIsOpeateSuccess = true;
                            if (operateReservationBookResultVoBaseResultEntityVo.data.appointStatus == 0) {
                                result.mIsNeedIDCard = false;
                            } else if (operateReservationBookResultVoBaseResultEntityVo.data.appointStatus == 1) {
                                result.mIsNeedIDCard = true;
                            }
                            UserRepository.getInstance().refreshUserInfo();
                            return result;
                        } else {
                            if (operateReservationBookResultVoBaseResultEntityVo.data.errorCode == 30100) {
                                UserRepository.getInstance().logout();
                            }
                            throw new ServerException(operateReservationBookResultVoBaseResultEntityVo.data.errorCode, "");
                        }
                    }
                }).onErrorResumeNext(new HttpResultFunc<OperateReservationBookResultBean>());
    }
}
