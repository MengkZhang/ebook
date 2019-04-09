package com.tzpt.cloudlibrary.ui.account.borrow;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.BorrowBookBean;
import com.tzpt.cloudlibrary.business_bean.PenaltyDealResultBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BorrowBookDetailInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.CompensateDepositInfoVo;
import com.tzpt.cloudlibrary.utils.ImageUrlUtils;
import com.tzpt.cloudlibrary.utils.MD5Utils;
import com.tzpt.cloudlibrary.utils.MoneyUtils;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by tonyjia on 2018/5/25.
 */

public class UserCompensateBookPresenter extends RxPresenter<UserCompensateBookContract.View> implements
        UserCompensateBookContract.Presenter {

    private String mLibCode;            //图书馆code
    private double mUsedDeposit;        //书籍占押金
    private double mCompensatePrice;    //赔价
    private boolean mIsOverdue;         //当前书籍是否逾期
    private int mUsedDepositType;       //押金使用类型 1:共享押金,2:馆押金,-1:没有


    @Override
    public void getBookInfo(final long borrowId) {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId)) {
            mView.showProgressDialog();
            Subscription subscription = DataRepository.getInstance().dealUserPenalty(readerId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<PenaltyDealResultBean>() {
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
                        public void onNext(PenaltyDealResultBean penaltyDealResultBean) {
                            if (mView != null) {
                                getLostBookInfo(borrowId);
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public void getUserDepositInfo(final boolean firstLoading) {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (!TextUtils.isEmpty(readerId) && !TextUtils.isEmpty(mLibCode)) {
            Subscription subscription = DataRepository.getInstance().getCompensateDepositInfo(mCompensatePrice, mLibCode, readerId, mUsedDepositType)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResultEntityVo<CompensateDepositInfoVo>>() {
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
                                        if (firstLoading) {
                                            mView.showNetError();
                                        } else {
                                            mView.showErrorMsg(R.string.network_fault);
                                        }
                                    }
                                } else {
                                    if (firstLoading) {
                                        mView.showNetError();
                                    } else {
                                        mView.showErrorMsg(R.string.network_fault);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNext(BaseResultEntityVo<CompensateDepositInfoVo> userDepositModuleVo) {
                            if (mView != null) {
                                if (userDepositModuleVo.data != null) {
                                    //设置可用押金信息
                                    mView.setDepositInfo(MoneyUtils.addToStr(userDepositModuleVo.data.offlineAvailableBalance,
                                            userDepositModuleVo.data.onLineAvailableBalance),
                                            MoneyUtils.formatMoney(mUsedDeposit));

                                    switch (userDepositModuleVo.data.handleType) {
                                        case 1:
                                            mView.setCompensateBookUI();    //进行赔书UI
                                            break;
                                        case 2:
                                            mView.setChargeDepositUI(userDepositModuleVo.data.lackAmount);     //充值UI
                                            break;
                                        case 3:
                                            mView.setLibraryDepositUI();    //图书馆处理UI
                                            break;
                                        case 4:
                                            mView.setPlatformDepositUI();   //平台处理处理UI
                                            break;
                                    }
                                    mView.setContentView();
                                } else {
                                    if (firstLoading) {
                                        mView.showNetError();
                                    } else {
                                        mView.showErrorMsg(R.string.network_fault);
                                    }
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        } else {
            if (null != mView) {
                if (firstLoading) {
                    mView.showNetError();
                } else {
                    mView.showErrorMsg(R.string.network_fault);
                }
            }
        }
    }
    //</editor-fold>

    /**
     * 获取赔书书籍详情
     *
     * @param borrowId 借阅ID
     */
    private void getLostBookInfo(long borrowId) {
        if (borrowId == -1) {
            mView.showNetError();
            return;
        }
        Subscription subscription = DataRepository.getInstance().getBorrowBookDetail(borrowId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<BorrowBookDetailInfoVo>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
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
                                bean.mBook.mName = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.bookName;
                                bean.mBook.mIsbn = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.isbn;
                                bean.mBook.mBookId = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.libraryBookId;
                                if (null != borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.publishDate && borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.publishDate.length() >= 4) {
                                    bean.mBook.mPublishDate = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.publishDate.substring(0, 4);
                                } else {
                                    bean.mBook.mPublishDate = "暂无数据";
                                }

                                bean.mAuthor.mName = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.author;

                                bean.mPress.mName = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.publisher;

                                bean.mCategory.mName = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.categoryName;

                                mLibCode = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.libCode;
                                bean.mLibrary.mCode = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.libCode;
                                bean.mLibrary.mName = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.libName;

                                bean.mLibrary.mLibStatus = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.libraryStatus;

                                //是否点赞 点赞1 true  未点赞 0 null -false
                                bean.mIsPraised = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.isPraiseD == 1;

                                //当前借阅
//                                bean.mIsHistory = false;
                                String borrowDate = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.borrowTimeStr;
                                String newAfterDate = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.expirationTimeStr;
                                bean.mHistoryBorrowDate = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.borrowTimeStr;
                                bean.mCurrentBookDateInfo = borrowDate + "-" + newAfterDate;
                                bean.mIsOverdueBuyTip = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.canOverduePay == 1;

                                bean.mBorrowDays = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.borrowDays;
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
//                                        newAfterDate = DateUtils.getDateAfter(DateUtils.formatNowDate(borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.renewTimes),
//                                                borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.borrowDays);
//                                        bean.mCurrentBookDateInfo = borrowDate + "-" + newAfterDate;
                                    }
                                    bean.mHasDays = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.daysRemaining;
                                }
                                mIsOverdue = bean.mIsOverdue;
                                mView.setLostBookInfo(bean);
                                //书籍占用押金
                                mUsedDeposit = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.usedDeposit;
                                //显示赔价
                                mCompensatePrice = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.compensatePrice;

                                mUsedDepositType = borrowBookDetailInfoVoBaseResultEntityVo.data.BorrowerBook.usedDepositType;
                                mView.setCompensatePrice(MoneyUtils.formatMoney(mCompensatePrice));
                                //获取用户押金信息
                                getUserDepositInfo(true);
                            } else if (borrowBookDetailInfoVoBaseResultEntityVo.status == 401) {
                                if (borrowBookDetailInfoVoBaseResultEntityVo.data.errorCode == 30100) {
                                    mView.setContentView();
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

    //</editor-fold>

    //<editor-fold desc="开始赔书">

    @Override
    public void compensateBook(String password, long borrowId) {
        if (TextUtils.isEmpty(password)) {
            mView.showErrorMsg(R.string.error_incorrect_password);
            return;
        }
        String readerId = UserRepository.getInstance().getLoginReaderId();
        String idCard = UserRepository.getInstance().getLoginUserIdCard();
        if (!TextUtils.isEmpty(readerId) && !TextUtils.isEmpty(idCard)) {
            mView.postingProgressDialog();
            Subscription subscription = DataRepository.getInstance().userCompensateBooks(readerId, idCard, borrowId, MD5Utils.MD5(password))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.dismissProgressDialog();
                                if (e instanceof ApiException) {
                                    switch (((ApiException) e).getCode()) {
                                        case 3103:
                                            mView.showErrorMsg(R.string.password_error);
                                            break;
                                        case 2318://余额不足,请去附近图书馆处理赔书!
                                            mView.showErrorMsgFinish(R.string.please_to_platform_dialog);
                                            break;
                                        case 3400://平台押金不足，请先充值押金!
                                            mView.showErrorMsgFinish(R.string.please_charge);
                                            break;
                                        case 3401://无协议客户押金不足，请至图书馆处理！
                                            mView.showErrorMsgFinish(R.string.please_to_library_dialog);
                                            break;
                                        case 30100:
                                            mView.pleaseLoginTip();
                                            break;
                                        default:
                                            mView.showErrorMsgFinish(R.string.already_lost_book);
                                            break;
                                    }
                                } else {
                                    mView.showErrorMsg(R.string.network_fault);
                                }
                            }
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (mView != null) {
                                mView.dismissProgressDialog();
                                if (aBoolean) {
                                    //减去当前借阅数量
                                    UserRepository.getInstance().readBorrowSum();
                                    if (mIsOverdue) {//如果当前是逾期，减去逾期数量
                                        UserRepository.getInstance().readBorrowOverdueSum();
                                    }
//                                    DataRepository.getInstance().updateUserInfo();
                                    UserRepository.getInstance().refreshUserInfo();
                                    mView.compensateBooksSuccess();
                                } else {
                                    mView.showErrorMsgFinish(R.string.already_lost_book);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }
    //</editor-fold>
}
