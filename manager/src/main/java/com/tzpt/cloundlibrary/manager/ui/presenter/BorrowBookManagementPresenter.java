package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.bean.ReaderInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ApiException;
import com.tzpt.cloundlibrary.manager.ui.contract.BorrowBookManagementContract;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 借书管理
 * Created by Administrator on 2017/7/7.
 */
public class BorrowBookManagementPresenter extends RxPresenter<BorrowBookManagementContract.View>
        implements BorrowBookManagementContract.Presenter, BaseResponseCode {
    //读者
    private ReaderInfo mReaderInfo;
    //可借数目
    private int mBorrowableSum;

    @Override
    public void getReaderInfo(final String readerId) {
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getReaderInfo(readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReaderInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case ERROR_CODE_KICK_OUT:
                                        mView.setNoLoginPermission(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.setNoLoginPermission(R.string.operate_timeout);
                                        break;
                                    default:
                                        mView.showDialogGetReaderInfoFailed(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showDialogGetReaderInfoFailed(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(ReaderInfo readerInfo) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            if (readerInfo.mNotApplyPenalty > 0) {
                                mView.showDialogGetReaderInfoFailed(R.string.network_fault);
                            } else {
                                mReaderInfo = readerInfo;

                                mView.setReaderNameNumber(readerInfo.mCardName + " " + StringUtils.setIdCardNumberForReader(readerInfo.mIdCard));
                                mBorrowableSum = readerInfo.mBorrowableSum;

                                //押金信息
                                LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
                                if (libraryInfo.mAgreementLevel == 1) {
                                    mView.setDepositOrPenalty("可用共享押金" + MoneyUtils.formatMoney(readerInfo.getPlatformUsableDeposit()));
                                } else if (libraryInfo.mAgreementLevel == 2
                                        || libraryInfo.mAgreementLevel == 3) {
                                    mView.setDepositOrPenalty("共享" + MoneyUtils.formatMoney(readerInfo.getPlatformUsableDeposit())
                                            + " 馆" + MoneyUtils.formatMoney(readerInfo.getOfflineUsableDeposit()));
                                } else {
                                    mView.setDepositOrPenalty("可用馆押金" + MoneyUtils.formatMoney(readerInfo.getOfflineUsableDeposit()));
                                }

                                //逾期未还数量
                                if (readerInfo.mOverdueNum > 0) {
                                    mView.showOverdueNumTipDialog();
                                }
                                getTotalInfo();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void getBookInfo(String barNumber) {
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().getBookInfo(barNumber, mReaderInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookInfoBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case ERROR_CODE_KICK_OUT:
                                        mView.setNoLoginPermission(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.setNoLoginPermission(R.string.operate_timeout);
                                        break;
                                    case ERROR_CODE_1015:
                                        mView.showCancelableDialog(R.string.library_has_stopped);
                                        break;
                                    case ERROR_CODE_2207://书籍不存在
                                        mView.showCancelableDialog(R.string.bar_code_error);
                                        break;
                                    case ERROR_CODE_2208://书籍已在借
                                        mView.showCancelableDialog(R.string.book_already_borrowed);
                                        break;
                                    case ERROR_CODE_2209://书籍已盘亏
                                        mView.showCancelableDialog(R.string.have_dish_deficient);
                                        break;
                                    case ERROR_CODE_2210://书籍已流出
                                        mView.showCancelableDialog(R.string.has_been_out);
                                        break;
                                    case ERROR_CODE_2211://书籍已丢失
                                        mView.showCancelableDialog(R.string.have_lost);
                                        break;
                                    case ERROR_CODE_2212://书籍已剔旧
                                        mView.showCancelableDialog(R.string.have_stuck_between_old);
                                        break;
                                    case ERROR_CODE_2214://书籍已被其他人预约
                                        mView.showCancelableDialog(R.string.have_make_appointment);
                                        break;
                                    case ERROR_CODE_2215://书籍是基藏库
                                        mView.showCancelableDialog(R.string.book_not_borrow);
                                        break;
                                    case ERROR_CODE_2218://书籍已售出
                                        mView.showCancelableDialog(R.string.have_been_sale);
                                        break;
                                    case ERROR_CODE_10000://超限值
                                        mView.showCancelableDialog(R.string.limit_value);
                                        break;
                                    case ERROR_CODE_10001://读者登录异常

                                        break;
                                    case ERROR_CODE_10002://押金不足
                                        mView.showToastTip(R.string.no_more_deposit_tip);
                                        break;
                                    case ERROR_CODE_10003:
                                        mView.showCancelableDialog(R.string.duplicate_entry);
                                        break;
                                    case ERROR_CODE_10004:
                                        mView.showToastTip(R.string.only_platform_deposit);
                                        break;
                                    case ERROR_CODE_UNKNOWN:
                                    case ERROR_CODE_PARSE:
                                    case ERROR_CODE_NETWORK:
                                    case ERROR_CODE_HTTP:
                                        mView.showCancelableDialog(R.string.network_fault);
                                        break;
                                    default:
                                        mView.showCancelableDialog(R.string.bar_code_error);
                                        break;
                                }
                            } else {
                                mView.showCancelableDialog(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(BookInfoBean book) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            if (book != null) {
                                mView.setBook(book);
                                getTotalInfo();
                            } else {
                                mView.showCancelableDialog(R.string.bar_code_error);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }


    @Override
    public void removeBook(BookInfoBean book) {
        DataRepository.getInstance().removeBook(book);
        getTotalInfo();
    }

    @Override
    public void clearBookList() {
        DataRepository.getInstance().clearBookList();
    }

    @Override
    public void delReaderInfo() {
//        DataRepository.getInstance().delReaderInfo();
    }

    @Override
    public void delOrderNumber() {
//        DataRepository.getInstance().delOrderNumberInfo();
    }

    @Override
    public void borrowBook() {
        mView.showProgressLoading();
        Subscription subscription = DataRepository.getInstance().borrowBook(mReaderInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissProgressLoading();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case ERROR_CODE_KICK_OUT:
                                        mView.setNoLoginPermission(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.setNoLoginPermission(R.string.operate_timeout);
                                        break;
                                    case ERROR_CODE_3400://可用押金不足，重新获取用户信息
                                        mView.showDepositDeficiencyDialog();
                                        break;
                                    case ERROR_CODE_2305://存在不能借阅的书籍
                                        List<BookInfoBean> bookList = DataRepository.getInstance().getBookList();
                                        if (bookList != null && bookList.size() > 0) {
                                            StringBuilder buffer = new StringBuilder();
                                            buffer.append("借书失败！\n");
                                            int failedCount = 0;
                                            for (BookInfoBean item : bookList) {
                                                if (item.mColorIsRed) {
                                                    buffer.append(TextUtils.isEmpty(item.mBarNumber)
                                                            ? "" : item.mBarNumber).append(",");
                                                    failedCount++;
                                                }
                                            }
                                            buffer.append("\n");
                                            buffer.append("不可借，请删除！");

                                            if (failedCount > 0) {
                                                mView.showBookLockedDialog(buffer.toString());
                                                mView.refreshBookList(bookList);
                                            } else {
                                                mView.showCancelableDialog(R.string.borrow_book_error);
                                            }

                                        } else {
                                            mView.showCancelableDialog(R.string.borrow_book_error);
                                        }
                                        break;
                                    case ERROR_CODE_2307://批量新增借阅记录失败
                                    case ERROR_CODE_2213://读者账户不存在
                                    case ERROR_CODE_3108://读者不存在
                                    case ERROR_CODE_2304://读者账户不存在
                                    case ERROR_CODE_2303://图书馆账户不存在
                                        mView.showCancelableDialog(R.string.network_fault);
                                        break;
                                    case ERROR_CODE_2313://超出最大借阅数
                                        mView.showCancelableDialog(R.string.limit_value);
                                        break;
                                    case ERROR_CODE_2317://免单申请未审核
                                        mView.showCancelableDialog(R.string.free_penalty_un_check);
                                        DataRepository.getInstance().clearBookList();
                                        mView.clearBookList();
                                        getReaderInfo(mReaderInfo.mReaderId);
                                        break;
                                    case ERROR_CODE_UNKNOWN:
                                    case ERROR_CODE_PARSE:
                                    case ERROR_CODE_NETWORK:
                                    case ERROR_CODE_HTTP:
                                        mView.showCancelableDialog(R.string.network_fault);
                                        break;
                                    default:
                                        mView.showCancelableDialog(R.string.borrow_book_error);
                                        break;
                                }
                            } else {
                                mView.showCancelableDialog(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            mView.borrowBookSuccess();
                        }
                    }
                });
        addSubscrebe(subscription);
    }


    @Override
    public void getBookListFromScan() {
        List<BookInfoBean> bookList = DataRepository.getInstance().getBookList();
        if (bookList.size() > 0) {
            mView.refreshBookList(bookList);
        }
        getTotalInfo();
    }

    @Override
    public void clickScanBtn() {
        if (mReaderInfo != null) {
            mView.turnToScanActivity(mReaderInfo);
        }
    }

    private void getTotalInfo() {
        List<BookInfoBean> bookList = DataRepository.getInstance().getBookList();
        int canBorrowSum = mBorrowableSum - bookList.size();
        mView.setBorrowableSum("可借" + (canBorrowSum < 0 ? 0 : canBorrowSum));
        if (bookList.size() > 0) {
            double totalPrice = 0.00;
            double depositTotalPrice = 0.00;
            double attachPriceTotal = 0.00;
            double attachPriceForDeposit = 0.00;
            for (BookInfoBean info : bookList) {
                totalPrice = MoneyUtils.add(totalPrice, info.mPrice);
                attachPriceTotal = MoneyUtils.add(attachPriceTotal, info.mAttachPrice);
                if (info.mDeposit == 1) {
                    depositTotalPrice = MoneyUtils.add(depositTotalPrice, info.mPrice);
                    attachPriceForDeposit = MoneyUtils.add(attachPriceForDeposit, info.mAttachPrice);
                }
            }

            double takeDeposit = MoneyUtils.add(depositTotalPrice, attachPriceForDeposit);
            if (takeDeposit > 0) {
                mView.setTotalInfoDeposit("数量 " + bookList.size(), "金额 " + MoneyUtils.addToStr(totalPrice, attachPriceTotal),
                        "押金 " + MoneyUtils.formatMoney(takeDeposit));
            } else {
                mView.setTotalInfo("数量 " + bookList.size(), "金额 " + MoneyUtils.addToStr(totalPrice, attachPriceTotal));
            }
            mView.setBookListVisibility(View.VISIBLE);
        } else {
            mView.setTotalInfoDeposit("数量 " + 0, "金额 " + 0.00, "押金 " + 0.00);
            mView.setBookListVisibility(View.GONE);
        }
    }

}
