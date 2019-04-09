package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.bean.LostBookInfo;
import com.tzpt.cloundlibrary.manager.bean.UsedDepositType;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ApiException;
import com.tzpt.cloundlibrary.manager.ui.contract.LostBookContact;
import com.tzpt.cloundlibrary.manager.utils.MD5Util;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 赔书
 * Created by Administrator on 2017/7/11.
 */
public class LostBookPresenter extends RxPresenter<LostBookContact.View> implements
        LostBookContact.Presenter, BaseResponseCode {

    private LostBookInfo mLostBookInfo;

    private boolean mIsUseDeposit;

    private double mPayMoney;

    @Override
    public void getReturnBookInfo(String readerId) {
        if (TextUtils.isEmpty(readerId)) {
            return;
        }
        mView.showLoading();
        Subscription subscription = DataRepository.getInstance().getBorrowingBookList(readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LostBookInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissLoading();
                            dealException(e);
                        }
                    }

                    @Override
                    public void onNext(LostBookInfo lostBookInfo) {
                        if (mView != null) {
                            mView.dismissLoading();
                            mLostBookInfo = lostBookInfo;
                            mView.setReaderNameNumber(lostBookInfo.mReaderInfo.mCardName + " "
                                    + StringUtils.setIdCardNumberForReader(lostBookInfo.mReaderInfo.mIdCard));

                            mView.setBorrowableSum(lostBookInfo.mReaderInfo.mBorrowingNum < 0 ? 0 : lostBookInfo.mReaderInfo.mBorrowingNum);

                            //押金信息
                            LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
                            if (libraryInfo.mAgreementLevel == 1) {
                                mView.setDepositOrPenalty("可用共享押金" + MoneyUtils.formatMoney(lostBookInfo.mReaderInfo.getPlatformUsableDeposit()));
                            } else if (libraryInfo.mAgreementLevel == 2
                                    || libraryInfo.mAgreementLevel == 3) {
                                mView.setDepositOrPenalty("共享" + MoneyUtils.formatMoney(lostBookInfo.mReaderInfo.getPlatformUsableDeposit())
                                        + " 馆" + MoneyUtils.formatMoney(lostBookInfo.mReaderInfo.getOfflineUsableDeposit()));
                            } else {
                                mView.setDepositOrPenalty("可用馆押金" + MoneyUtils.formatMoney(lostBookInfo.mReaderInfo.getOfflineUsableDeposit()));
                            }

                            if (lostBookInfo.mBookList.size() > 0) {
                                mView.refreshBookList(lostBookInfo.mBookList);
                            } else {
                                mView.setEmptyBookList();
                            }
                            refreshTotalInfo(lostBookInfo.mBookList);
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void setBookStatus(int position, boolean checked) {
        mLostBookInfo.mBookList.get(position).mCompensateChoosed = checked;
        refreshTotalInfo(mLostBookInfo.mBookList);
    }

    @Override
    public void compensateBook(String pwd) {
        if (mLostBookInfo.mReaderInfo == null || TextUtils.isEmpty(mLostBookInfo.mReaderInfo.mIdCard)) {
            mView.showDialog1BtnFinish(R.string.network_fault);
            return;
        }
        mView.showLoading();
        if (!mIsUseDeposit) {
            LibraryInfo library = DataRepository.getInstance().getLibraryInfo();
            if (library.mAgreementLevel == 2) {
                Subscription subscription = DataRepository.getInstance().checkOperatorPsw(MD5Util.MD5(pwd))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                if (mView != null) {
                                    mView.dismissLoading();
                                    mView.clearPswText();
                                    dealException(e);
                                }
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                if (mView != null) {
                                    mView.dismissLoading();
                                    if (aBoolean) {
                                        mView.turnToDaishouPenalty(mLostBookInfo.mReaderInfo.mReaderId, mPayMoney);
                                    } else {
                                        mView.clearPswText();
                                        mView.showDialogMsg(R.string.psw_error);
                                    }
                                }
                            }
                        });
                addSubscrebe(subscription);
            } else if (library.mAgreementLevel == 3
                    || library.mAgreementLevel == 4) {
                Subscription subscription = DataRepository.getInstance().useCashCompensateBook(MD5Util.MD5(pwd),
                        mLostBookInfo.mReaderInfo.mReaderId, mPayMoney, mLostBookInfo.mBookList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                if (mView != null) {
                                    mView.dismissLoading();
                                    mView.clearPswText();
                                    dealException(e);
                                }
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                if (mView != null) {
                                    mView.dismissLoading();
                                    if (aBoolean) {
                                        mView.compensateBookSuccess(mLostBookInfo.mReaderInfo.mReaderId);
                                    } else {
                                        mView.clearPswText();
                                        mView.showDialogMsg(R.string.compensate_book_failed);
                                    }
                                }
                            }
                        });
                addSubscrebe(subscription);
            }
        } else {
            Subscription subscription = DataRepository.getInstance().useDepositCompensateBook(mLostBookInfo.mReaderInfo.mIdCard,
                    MD5Util.MD5(pwd), mLostBookInfo.mReaderInfo.mReaderId, mLostBookInfo.mBookList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.clearPswText();
                                mView.dismissLoading();
                                dealException(e);
                            }
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (mView != null) {
                                mView.dismissLoading();
                                if (aBoolean) {
                                    mView.compensateBookSuccess(mLostBookInfo.mReaderInfo.mReaderId);
                                } else {
                                    mView.clearPswText();
                                    mView.showDialogMsg(R.string.compensate_book_failed);
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public void compensateBookDirect() {
        mView.showLoading();
        Subscription subscription = DataRepository.getInstance().compensateBookDirect(mLostBookInfo.mReaderInfo.mReaderId,
                mLostBookInfo.mBookList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.clearPswText();
                            mView.dismissLoading();
                            dealException(e);
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            mView.dismissLoading();
                            if (aBoolean) {
                                mView.compensateBookSuccess(mLostBookInfo.mReaderInfo.mReaderId);
                            } else {
                                mView.clearPswText();
                                mView.showDialogMsg(R.string.compensate_book_failed);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void refreshTotalInfo(List<BookInfoBean> bookList) {
        //所有书籍赔偿总价
        double totalReturnDeposit;
        if (bookList.size() > 0) {
            //书籍总价
            double totalPrice = 0.00;
            //总溢价
            double attachPriceTotal = 0.00;
            int count = 0;

            //一、二级协议，占共享押金的书籍直接用占押金，判断条件：可用共享押金>所有书籍占馆押金+无押金（书价+溢价）
            //三级协议：占（共享/馆）押金的书直接用占押金，判断条件：可用共享押金>无押金（书价+溢价） || 可用馆押金>无押金（书价+溢价）
            //四级协议：占馆押金的书籍直接用占押金，判断条件：可用馆押金>无押金（书价+溢价）
            LibraryInfo library = DataRepository.getInstance().getLibraryInfo();
            double compensatePrice = 0;//用来判断的金额
            if (library.mAgreementLevel == 1
                    || library.mAgreementLevel == 2) {
                for (BookInfoBean info : bookList) {
                    if (info.mCompensateChoosed) {
                        if (info.mUsedDepositType != UsedDepositType.PLATFORM_DEPOSIT) {
                            compensatePrice = MoneyUtils.add(compensatePrice, MoneyUtils.add(info.mPrice, info.mAttachPrice));
                        }
                        totalPrice = MoneyUtils.add(totalPrice, info.mPrice);
                        attachPriceTotal = MoneyUtils.add(attachPriceTotal, info.mAttachPrice);
                        count++;
                    }
                }
            } else if (library.mAgreementLevel == 3) {
                for (BookInfoBean info : bookList) {
                    if (info.mCompensateChoosed) {
                        if (info.mUsedDepositType == UsedDepositType.NO_DEPOSIT) {
                            compensatePrice = MoneyUtils.add(compensatePrice, MoneyUtils.add(info.mPrice, info.mAttachPrice));
                        }
                        totalPrice = MoneyUtils.add(totalPrice, info.mPrice);
                        attachPriceTotal = MoneyUtils.add(attachPriceTotal, info.mAttachPrice);
                        count++;
                    }
                }
            } else {
                for (BookInfoBean info : bookList) {
                    if (info.mCompensateChoosed) {
                        if (info.mUsedDepositType == UsedDepositType.NO_DEPOSIT) {
                            compensatePrice = MoneyUtils.add(compensatePrice, MoneyUtils.add(info.mPrice, info.mAttachPrice));
                        }
                        totalPrice = MoneyUtils.add(totalPrice, info.mPrice);
                        attachPriceTotal = MoneyUtils.add(attachPriceTotal, info.mAttachPrice);
                        count++;
                    }
                }
            }

            if (count > 0) {
                totalReturnDeposit = MoneyUtils.add(totalPrice, attachPriceTotal);
                //合计信息
                mView.setTotalInfo(count, MoneyUtils.formatMoney(totalReturnDeposit));

                if (library.mAgreementLevel == 1) {
                    if (mLostBookInfo.mReaderInfo.getPlatformUsableDeposit() >= compensatePrice) {
                        mIsUseDeposit = true;
                        mView.showLostBookUI(totalReturnDeposit);
                    } else {
                        mView.showUnableLostBookUI(library.mAgreementLevel, 0);
                    }
                } else if (library.mAgreementLevel == 2) {
                    if (mLostBookInfo.mReaderInfo.getPlatformUsableDeposit() >= compensatePrice) {
                        mIsUseDeposit = true;
                        mView.showLostBookUI(totalReturnDeposit);
                    } else {
                        mIsUseDeposit = false;
                        mPayMoney = MoneyUtils.sub(compensatePrice, mLostBookInfo.mReaderInfo.getPlatformUsableDeposit());
                        mView.showUnableLostBookUI(library.mAgreementLevel, mPayMoney);
                    }
                } else if (library.mAgreementLevel == 3) {
                    if (mLostBookInfo.mReaderInfo.getPlatformUsableDeposit() >= compensatePrice) {
                        mIsUseDeposit = true;
                        mView.showLostBookUI(totalReturnDeposit);
                    } else if (mLostBookInfo.mReaderInfo.getOfflineUsableDeposit() >= compensatePrice) {
                        mIsUseDeposit = true;
                        mView.showLostBookUI(totalReturnDeposit);
                    } else {
                        mIsUseDeposit = false;
                        mPayMoney = MoneyUtils.sub(compensatePrice, mLostBookInfo.mReaderInfo.getOfflineUsableDeposit());
                        mView.showUnableLostBookUI(library.mAgreementLevel, mPayMoney);
                    }
                } else {
                    if (mLostBookInfo.mReaderInfo.getOfflineUsableDeposit() >= compensatePrice) {
                        mIsUseDeposit = true;
                        mView.showLostBookUI(totalReturnDeposit);
                    } else {
                        mIsUseDeposit = false;
                        mPayMoney = MoneyUtils.sub(compensatePrice, mLostBookInfo.mReaderInfo.getOfflineUsableDeposit());
                        mView.showUnableLostBookUI(library.mAgreementLevel, mPayMoney);
                    }
                }
            } else {
                mView.hideLostBookUI();
                mView.setTotalInfo(0, MoneyUtils.formatMoney(0.00));
            }
        } else {
            mView.hideLostBookUI();
            mView.setTotalInfo(0, MoneyUtils.formatMoney(0.00));
        }
    }

    private void dealException(Throwable e) {
        if (e instanceof ApiException) {
            switch (((ApiException) e).getCode()) {
                case ERROR_CODE_KICK_OUT:
                    mView.noPermissionDialog(R.string.kicked_offline);
                    break;
                case ERROR_CODE_1006:
                    mView.noPermissionDialog(R.string.operate_timeout);
                    break;
                case CODE_SERVICE_ERROR:
                    mView.showDialog1BtnFinish(R.string.error_code_500);
                    break;
                case ERROR_CODE_3103:
                    mView.showDialogMsg(R.string.psw_error);
                    break;
                case ERROR_CODE_3108://读者不存在
                case ERROR_CODE_2303://图书馆账户不存在
                case ERROR_CODE_2304://读者账户不存在
                    mView.showDialog1BtnFinish(R.string.network_fault);
                    break;
                case ERROR_CODE_3400://余额不足
                    mView.showDialogMsg(R.string.deposit_not_enough);
                    //刷新用户数据
                    getReturnBookInfo(mLostBookInfo.mReaderInfo.mReaderId);
                    break;
                case ERROR_CODE_2311://存在本协议下不可赔偿的书籍
                case ERROR_CODE_2312://批量更新借阅记录和书籍状态失败
                    mView.showDialog1BtnFinish(R.string.network_fault);
                    break;
                case ERROR_CODE_6112://交押金金额超限值
                    mView.showDialogMsg(R.string.amount_the_deposit_exceeds_the_limit);
                    break;
                case ERROR_CODE_10005:
                    mView.showDialog1BtnFinish(R.string.no_compensate_book_info);
                    break;
                case ERROR_CODE_10006:
                    mView.showDialogMsg(R.string.password_cannot_be_empty);
                    break;
                default:
                    mView.showDialog1BtnFinish(R.string.network_fault);
                    break;
            }
        } else {
            mView.showDialog1BtnFinish(R.string.network_fault);
        }
    }
}
