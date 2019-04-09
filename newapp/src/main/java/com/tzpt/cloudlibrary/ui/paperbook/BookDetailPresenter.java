package com.tzpt.cloudlibrary.ui.paperbook;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.BaseListResultData;
import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.business_bean.OperateReservationBookResultBean;
import com.tzpt.cloudlibrary.business_bean.ReservationBookBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.LibraryRepository;
import com.tzpt.cloudlibrary.modle.PaperBookRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookInLibListVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BookInLibraryItemVo;
import com.tzpt.cloudlibrary.ui.map.LocationManager;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 图书详情
 * Created by ZhiqiangJia on 2017-08-14.
 */
public class BookDetailPresenter extends RxPresenter<BookDetailContract.View> implements
        BookDetailContract.Presenter {
    private long mBookId = -1;

    @Override
    public void getBookDetail(final String isbn, final String libCode, final boolean needLibList, final boolean isInLibShelvingInfo, int fromSearch) {
        if (TextUtils.isEmpty(isbn)) {
            return;
        }
        if (null != mView) {
            mView.showProgressDialog();
        }

        Subscription subscription = PaperBookRepository.getInstance().getBookDetail(isbn, fromSearch, libCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (e instanceof ApiException) {
                                if (((ApiException) e).getCode() == 30317) {
                                    mView.showEmptyError();
                                } else {
                                    mView.showNetError();
                                }
                            } else {
                                mView.showNetError();
                            }
                        }
                    }

                    @Override
                    public void onNext(BookBean bookBean) {
                        if (mView != null) {
                            if (bookBean != null) {
                                mBookId = bookBean.mBook.mId;
                                mView.setBookBaseInfo(bookBean);

                                mView.setBookSummerInfo(bookBean.mAuthor.mAuthorInfo, bookBean.mBook.mSummary, bookBean.mBook.mCatalog, bookBean.mHtmlUrl);

                                if (needLibList) {
                                    if (bookBean.mBook.mId == 0) {
                                        mView.dismissProgressDialog();
                                    } else {
                                        getBookBelongLib(bookBean.mBook.mIsbn, libCode);
                                    }
                                }

                                //设置分享信息
                                if (TextUtils.isEmpty(libCode)) {
                                    mView.showShareItem(bookBean.mBorrowNum, bookBean.mPraiseNum, bookBean.mShareNum);
                                } else {
                                    mView.showShareItemEmpty();
                                }
                                //排架号信息（区分馆内馆外）
                                if (isInLibShelvingInfo) {
                                    getUserReservationBookList(bookBean.mBook.mIsbn, libCode);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);

    }

    private void getBookBelongLib(String isbn, final String libCode) {
        String lngLat = LocationManager.getInstance().getLngLat();
        Subscription subscription = LibraryRepository.getInstance().getBookBelongLib(isbn, lngLat, libCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseListResultData<LibraryBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.dismissProgressDialog();
                        }
                    }

                    @Override
                    public void onNext(BaseListResultData<LibraryBean> libraryBeanBaseListResultData) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (libraryBeanBaseListResultData != null
                                    && libraryBeanBaseListResultData.mResultList != null
                                    && libraryBeanBaseListResultData.mResultList.size() > 0) {
                                mView.setLibraryList(libraryBeanBaseListResultData.mResultList);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void getBookDetailSameBookList(String isbn, String libCode) {
        Subscription subscription = DataRepository.getInstance().getBookDetailSameBookList(isbn, libCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<BookInLibListVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.dismissProgressDialog();
                            mView.showNetError();
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<BookInLibListVo> bookInLibListVoBaseResultEntityVo) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (bookInLibListVoBaseResultEntityVo.status == 200
                                    && bookInLibListVoBaseResultEntityVo.data != null) {
                                if (bookInLibListVoBaseResultEntityVo.data.resultList.size() > 0) {
                                    boolean canReservation = false;
                                    for (BookInLibraryItemVo bookLibrary : bookInLibListVoBaseResultEntityVo.data.resultList) {
                                        //bookLibrary.status 1 在馆 2 在借 5在馆已预约
                                        //bookLibraryInfo.mBookStatus 0.在借 1.在馆 2:当前登录账户预约 3:其他账户预约
                                        //设置排序 0:基藏-不可借 1:不可预约 2:在借 3.其他人已预约 4.自己已预约 5.可预约
                                        switch (bookLibrary.status) {
                                            case 1://在馆 基藏库1 限制库2 通用库
                                                if (bookLibrary.storeRoom == 1) {
                                                    //基藏在馆
                                                } else {
                                                    //其他在馆
                                                    if (bookLibrary.canAppoint == 1) {
                                                        //在馆可预约
                                                        canReservation = true;
                                                    } else {
                                                        //在馆不可预约
                                                    }
                                                }
                                                break;
                                            case 2://在借
                                                //在借
                                                break;
                                            case 5://在馆已预约
                                                //默认其他人已预约
                                                //其他人预约
                                                break;
                                        }
                                    }
                                    if (canReservation) {
                                        mView.setReservationBtnStatus(1);
                                    } else {
                                        mView.setReservationBtnStatus(2);
                                    }
                                } else {
                                    mView.setReservationBtnStatus(2);
                                }
                            } else {
                                mView.showNetError();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public boolean isLoginStatus() {
        return UserRepository.getInstance().isLogin();
    }

    @Override
    public void orderBook(String isbn, String libCode) {
        if (!UserRepository.getInstance().isLogin()) {
            mView.pleaseLogin();
            return;
        }
        long readerId = UserRepository.getInstance().getLoginReaderIdL();
        mView.showLoadingView();
        Subscription subscription = PaperBookRepository.getInstance().operateReservationBook(1, isbn, libCode, readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OperateReservationBookResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissLoadingView();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case 30100:
                                        mView.pleaseLoginTip();
                                        break;
                                    case 30301://图书不外借
                                        mView.orderBookFailed(2);
                                        break;
                                    case 30202://限制馆，非本馆读者
                                        mView.orderBookFailed(1);
                                        break;
                                    case 30303://预约超限制,每天2本，每月6本
                                        mView.orderBookFailed(5);
                                        break;
                                    case 30304:
                                        mView.orderBookFailed(4);
                                        break;
                                    case 30308://图书预约时图书状态不是在馆
                                        mView.orderBookFailed(3);
                                        break;
                                    case 30114://添加失败（预约失败）
                                    case 30110://查询预约信息失败
                                    default:
                                        mView.orderBookFailed(6);
                                        break;
                                }
                            } else {
                                mView.orderBookFailed(6);
                            }
                        }
                    }

                    @Override
                    public void onNext(OperateReservationBookResultBean operateReservationBookResultBean) {
                        if (mView != null) {
                            mView.dismissLoadingView();
                            if (operateReservationBookResultBean != null
                                    && operateReservationBookResultBean.mIsOpeateSuccess) {
                                mView.orderBookSuccess(operateReservationBookResultBean.mIsNeedIDCard);//身份未验证
                                //已预约
                                mView.setReservationBtnStatus(3);
                            } else {
                                mView.orderBookFailed(6);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void reportBookShare() {
        if (mBookId != -1) {
            Subscription subscription = DataRepository.getInstance().reportBookShare(String.valueOf(mBookId), 1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResultEntityVo>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(BaseResultEntityVo baseResultEntityVo) {
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    /**
     * 当前预约的书籍
     */
    private void getUserReservationBookList(final String isbn, final String libCode) {
        String idCard = UserRepository.getInstance().getLoginUserIdCard();
        if (TextUtils.isEmpty(idCard)) {
            getBookDetailSameBookList(isbn, libCode);
            return;
        }
        Subscription subscription = DataRepository.getInstance().getReservationBookList(1, 20, idCard)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseListResultData<ReservationBookBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            mView.showNetError();
                        }
                    }

                    @Override
                    public void onNext(BaseListResultData<ReservationBookBean> reservationBookBeanBaseListResultData) {
                        if (mView != null) {
                            if (reservationBookBeanBaseListResultData != null
                                    && reservationBookBeanBaseListResultData.mResultList != null
                                    && reservationBookBeanBaseListResultData.mResultList.size() > 0) {
                                for (ReservationBookBean item : reservationBookBeanBaseListResultData.mResultList) {
                                    if (item.mBook.mIsbn.equals(isbn)
                                            && item.mLibrary.mCode.equals(libCode)) {
                                        //已预约
                                        mView.dismissProgressDialog();
                                        mView.setReservationBtnStatus(3);
                                        return;
                                    }
                                }
                            }
                            getBookDetailSameBookList(isbn, libCode);
                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
