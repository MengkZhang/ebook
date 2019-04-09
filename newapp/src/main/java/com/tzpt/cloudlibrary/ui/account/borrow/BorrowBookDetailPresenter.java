package com.tzpt.cloudlibrary.ui.account.borrow;

import android.text.TextUtils;
import android.util.Log;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.base.data.Note;
import com.tzpt.cloudlibrary.business_bean.BorrowBookBean;
import com.tzpt.cloudlibrary.business_bean.BoughtBookBean;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.business_bean.ReadNoteBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BorrowBookDetailInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.NoteListItemVo;
import com.tzpt.cloudlibrary.ui.map.LocationManager;
import com.tzpt.cloudlibrary.utils.DateUtils;
import com.tzpt.cloudlibrary.utils.ImageUrlUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 借阅图书详情
 * Created by ZhiqiangJia on 2017-08-25.
 */
public class BorrowBookDetailPresenter extends RxPresenter<BorrowBookDetailContract.View> implements
        BorrowBookDetailContract.Presenter {

    @Override
    public void getBorrowBookDetail(final long borrowerBookId) {
        Subscription subscription = DataRepository.getInstance().getBorrowBookDetail(borrowerBookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<BorrowBookDetailInfoVo>>() {
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
                    public void onNext(BaseResultEntityVo<BorrowBookDetailInfoVo> borrowBookDetailInfoVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (borrowBookDetailInfoVoBaseResultEntityVo.status == 200
                                    && borrowBookDetailInfoVoBaseResultEntityVo.data != null) {
                                BorrowBookBean bean = new BorrowBookBean();
                                bean.mBorrowerId = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.id;
                                bean.mBook.mCoverImg = ImageUrlUtils.getDownloadOriginalImagePath(borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.image);
                                bean.mAuthor.mName = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.author;
                                bean.mBook.mName = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.bookName;

                                if (null != borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.publishDate && borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.publishDate.length() >= 4) {
                                    bean.mBook.mPublishDate = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.publishDate.substring(0, 4);
                                } else {
                                    bean.mBook.mPublishDate = "暂无数据";
                                }
                                bean.mPress.mName = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.publisher;
                                bean.mBook.mIsbn = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.isbn;
                                bean.mCategory.mName = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.categoryName;
                                bean.mLibrary.mName = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.libName;
                                bean.mBook.mBookId = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.libraryBookId;
                                //是否点赞 点赞1 true  未点赞 0 null -false
                                bean.mIsPraised = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.isPraiseD == 1;
                                bean.mLibrary.mLibStatus = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.libraryStatus;
                                bean.mBorrowDays = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.borrowDays;
                                if (!TextUtils.isEmpty(borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.returnTimeStr)) {
                                    //历史借阅
                                    //借书日期
                                    mView.dismissProgressDialog();
                                    bean.mHistoryBorrowDate = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.borrowTimeStr
                                     + "-" + borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.returnTimeStr;
                                    //还书日期
                                    bean.mHistoryBackDate = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.returnTimeStr;
                                    bean.mIsBuy = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.borrowState == 28;
                                    bean.mIsLost = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.borrowState == 7;
                                    //借阅状态
                                    bean.mBorrowState = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.borrowState;
                                } else {
                                    //当前借阅
                                    bean.mHistoryBorrowDate = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.borrowTimeStr;
                                    String borrowDate = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.borrowTimeStr;
                                    String newAfterDate = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.expirationTimeStr;
                                    bean.mCurrentBookDateInfo = borrowDate + "-" + newAfterDate;
                                    bean.mIsOverdueBuyTip = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.canOverduePay == 1;

                                    //是否有续借功能(1:有,0:无)
                                    boolean canRenew = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.mayRenew == 1;
                                    if (borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.overDue == 1
                                            || borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.overDue == 0) {//1逾期0未知
                                        bean.mIsOverdue = true;
                                        bean.mOneKeyToRenew = false;

                                    } else if (borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.overDue == 2) {//2未逾期
                                        bean.mIsOverdue = false;
                                        if (borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.renewTimes == 0) {//为NULL时可以续借，不为空时已经续借
                                            bean.mOneKeyToRenew = canRenew;
                                        } else {
                                            bean.mOneKeyToRenew = false;
//                                            newAfterDate = DateUtils.getDateAfter(DateUtils.formatNowDate(borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.renewTimes),
//                                                    borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.borrowDays);
//                                            bean.mCurrentBookDateInfo = borrowDate + "-" + newAfterDate;
                                        }
                                        bean.mHasDays = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.daysRemaining;
                                    }
                                    if (bean.mBook.mBookId > 0) {
                                        getReturnLibList(bean.mBook.mBookId);
                                    } else {
                                        mView.dismissProgressDialog();
                                    }
                                }
                                mView.setBookBaseInfo(bean);

                                //读书笔记
                                if (null != borrowBookDetailInfoVoBaseResultEntityVo.data.libraryBorrowerNotes
                                        && borrowBookDetailInfoVoBaseResultEntityVo.data.libraryBorrowerNotes.size() > 0) {
                                    List<ReadNoteBean> readNotes = new ArrayList<>();
                                    for (NoteListItemVo item : borrowBookDetailInfoVoBaseResultEntityVo.data.libraryBorrowerNotes) {
                                        ReadNoteBean readNoteBean = new ReadNoteBean();
                                        Note note = new Note();
                                        note.mId = item.id;
                                        note.mContent = item.readingNote;
                                        note.mModifyDate = DateUtils.formatDate(item.noteDate);

                                        readNoteBean.mNote = note;
                                        readNotes.add(readNoteBean);
                                    }
                                    mView.setUserReadingNote(readNotes);
                                } else {
                                    mView.setUserReadingNoteEmpty();
                                }
                            } else if (borrowBookDetailInfoVoBaseResultEntityVo.status == 401) {
                                if (borrowBookDetailInfoVoBaseResultEntityVo.data.errorCode == 30100) {
                                    mView.pleaseLoginTip();
                                } else {
                                    mView.showNetError();
                                }
                            } else {
                                mView.showNetError();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void getReturnLibList(long bookId) {
        Subscription subscription = DataRepository.getInstance().getReturnLibList(bookId, LocationManager.getInstance().getLngLat())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<LibraryBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (e instanceof ApiException) {
                                if (((ApiException) e).getCode() == 30100) {
                                    mView.pleaseLoginTip();
                                } else {
                                    mView.showNetError();
                                }
                            } else {
                                mView.showNetError();
                            }
                        }
                    }

                    @Override
                    public void onNext(List<LibraryBean> libraryBeans) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (libraryBeans != null) {
                                mView.setLibraryList(libraryBeans);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void renewBorrowBook(final long borrowBookId) {
        String idCard = UserRepository.getInstance().getLoginUserIdCard();
        if (!TextUtils.isEmpty(idCard)) {
            Subscription subscription = DataRepository.getInstance().renewBorrowBook(idCard, borrowBookId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                if (e instanceof ApiException) {
                                    if (((ApiException) e).getCode() == 30100) {
                                        mView.pleaseLoginTip();
                                    } else {
                                        mView.showToastMsg("续借失败！");
                                    }
                                } else {
                                    mView.showToastMsg("续借失败！");
                                }
                            }
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (mView != null) {
                                if (aBoolean) {
                                    mView.showToastMsg("续借成功！");
                                    //详情页的续借成功是可以重新请求数据的
                                    getBorrowBookDetail(borrowBookId);
                                } else {
                                    mView.showToastMsg("续借失败！");
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public void praiseBook(long borrowBookId, long boughtId) {
        //借阅点赞
        if (borrowBookId > 0) {
            Subscription subscription = DataRepository.getInstance().praiseBook(borrowBookId, 1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                if (e instanceof ApiException) {
                                    if (((ApiException) e).getCode() == 30100) {
                                        mView.pleaseLoginTip();
                                    } else {
                                        mView.showToastMsg(R.string.failure);
                                    }
                                } else {
                                    mView.showToastMsg(R.string.failure);
                                }
                            }
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (mView != null) {
                                if (aBoolean) {
                                    mView.operatePraiseSuccess();
                                } else {
                                    mView.showToastMsg(R.string.failure);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }

        //购书点赞
        if (boughtId > 0) {
            Subscription subscription = DataRepository.getInstance().praiseSelfBuyBook(boughtId, 1)
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
                                    if (((ApiException) e).getCode() == 30100) {
                                        mView.pleaseLoginTip();
                                    } else {
                                        mView.showToastMsg(R.string.network_fault);
                                    }
                                } else {
                                    mView.showToastMsg(R.string.network_fault);
                                }
                            }
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (mView != null) {
                                if (aBoolean) {
                                    mView.operatePraiseSuccess();
                                } else {
                                    mView.showToastMsg(R.string.failure);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }

    }

    @Override
    public void getSelfBuyBookShelfDetail(long buyBookId) {
        Subscription subscription = DataRepository.getInstance().getSelfBuyBookDetail(buyBookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BoughtBookBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (e instanceof ApiException) {
                                if (((ApiException) e).getCode() == 30100) {
                                    mView.pleaseLoginTip();
                                } else {
                                    mView.showNetError();
                                }
                            } else {
                                mView.showNetError();
                            }
                        }
                    }

                    @Override
                    public void onNext(BoughtBookBean boughtBookBean) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            if (boughtBookBean != null) {
                                mView.setBuyBookDetail(boughtBookBean);
                                if (boughtBookBean.mNoteList != null
                                        && boughtBookBean.mNoteList.size() > 0) {
                                    mView.setUserReadingNote(boughtBookBean.mNoteList);
                                } else {
                                    mView.setUserReadingNoteEmpty();
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
    public void praiseSelfBuyBook(long buyBookId, final boolean isPraised) {
        Subscription subscription = DataRepository.getInstance().praiseSelfBuyBook(buyBookId, isPraised ? 0 : 1)
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
                                if (((ApiException) e).getCode() == 30100) {
                                    mView.pleaseLoginTip();
                                } else {
                                    mView.showToastMsg(R.string.network_fault);
                                }
                            } else {
                                mView.showToastMsg(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            if (aBoolean) {
                                mView.operatePraiseSuccess();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void delNote(long id) {
        Subscription subscription = DataRepository.getInstance().delNote(id)
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
                                if (((ApiException) e).getCode() == 30100) {
                                    mView.pleaseLoginTip();
                                } else {
                                    mView.showToastMsg(R.string.failure);
                                }
                            } else {
                                mView.showToastMsg("删除失败");
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            if (aBoolean) {
                                mView.delNoteSuccess();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

}
