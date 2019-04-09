package com.tzpt.cloundlibrary.manager.ui.presenter;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.bean.ReaderInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ApiException;
import com.tzpt.cloundlibrary.manager.ui.contract.ReturnBookManagementScanContract;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/12/28.
 */

public class ReturnBookManagementScanPresenter extends RxPresenter<ReturnBookManagementScanContract.View>
        implements ReturnBookManagementScanContract.Presenter, BaseResponseCode {
    private String mReadId;

    @Override
    public void getReturnBookList(ReaderInfo readerInfo) {
        if (readerInfo != null) {
            dealView(readerInfo);
            getTotalInfo(readerInfo.mNotApplyPenalty);
        }
    }

    @Override
    public void returnBook(String barNumber, String readerId) {
        Subscription subscription = DataRepository.getInstance().returnBook(barNumber, readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case ERROR_CODE_KICK_OUT:
                                        mView.showNoPermissionDialog(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.showNoPermissionDialog(R.string.operate_timeout);
                                        break;
                                    case ERROR_CODE_3101:
                                        mView.showMsgDialog(R.string.return_book_failed);
                                        break;
                                    case ERROR_CODE_2217://书籍已在馆
                                        mView.showCancelableDialog(R.string.book_already_in_library);
                                        break;
                                    case ERROR_CODE_2218://书籍已售出
                                        mView.showCancelableDialog(R.string.have_been_sale);
                                        break;
                                    case ERROR_CODE_2209://书籍已盘亏
                                        mView.showCancelableDialog(R.string.have_dish_deficient);
                                        break;
                                    case ERROR_CODE_2211://书籍已丢失
                                        mView.showCancelableDialog(R.string.have_lost);
                                        break;
                                    case ERROR_CODE_2212://书籍已剔旧
                                        mView.showCancelableDialog(R.string.have_stuck_between_old);
                                        break;
                                    case ERROR_CODE_2210://书籍已流出
                                        mView.showCancelableDialog(R.string.has_been_out);
                                        break;
                                    case ERROR_CODE_2214://书籍已被其他人预约
                                        mView.showCancelableDialog(R.string.have_make_appointment);
                                        break;
                                    case ERROR_CODE_2315://借书馆和还书馆协议不同
                                    case ERROR_CODE_2408://不在同一个流通范围
                                        mView.showCancelableDialog(R.string.not_pass_also_pavilion_book);
                                        break;
                                    case ERROR_CODE_2309://限制库不可异馆还书
                                        mView.showCancelableDialog(R.string.library_limit_book_circulation);
                                        break;
                                    case ERROR_CODE_2207:
                                        mView.showCancelableDialog(R.string.bar_code_error);
                                        break;
                                    case ERROR_CODE_2308:
                                    case ERROR_CODE_3108:
                                    case ERROR_CODE_2304:
                                        mView.showCancelableDialog(R.string.network_fault);
                                        break;
                                    case ERROR_CODE_10003:
                                        mView.showCancelableDialog(R.string.book_already_in_library);
                                        break;
                                    default:
                                        mView.showCancelableDialog(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showMsgDialog(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(String aString) {
                        if (mView != null) {
                            mReadId = aString;
                            handlePenalties(aString);
                        }
                    }
                });
        addSubscrebe(subscription);
    }


    //自动处理罚金接口
    private void handlePenalties(final String readerId) {
        Subscription subscription = DataRepository.getInstance().autoDealReturnBookPenalty(readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case ERROR_CODE_KICK_OUT:
                                        mView.showNoPermissionDialog(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.showNoPermissionDialog(R.string.operate_timeout);
                                        break;
                                    default:
                                        refreshReaderInfo();
                                        break;
                                }
                            } else {
                                refreshReaderInfo();
                            }
                        }
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        if (null != mView) {
                            refreshReaderInfo();
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void refreshReaderInfo() {
        Subscription subscription = DataRepository.getInstance().getReaderInfo(mReadId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReaderInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case ERROR_CODE_KICK_OUT:
                                        mView.showNoPermissionDialog(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.showNoPermissionDialog(R.string.operate_timeout);
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
                            dealView(readerInfo);
                            getTotalInfo(readerInfo.mNotApplyPenalty);

                            mView.returnBookSuccess(readerInfo);
                        }
                    }
                });
        addSubscrebe(subscription);
    }


    /**
     * 处理UI显示
     *
     * @param readerInfo 用户信息
     */
    private void dealView(ReaderInfo readerInfo) {
        if (null != readerInfo) {
            mView.setReaderNameNumber(readerInfo.mCardName + " " + StringUtils.setIdCardNumberForReader(readerInfo.mIdCard));
            mView.setNoBackBookSum("未还" + readerInfo.mBorrowingNum);
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
        } else {
            mView.setReaderNameNumber("");
            mView.setNoBackBookSum("");
            mView.setDepositOrPenalty("");
        }
    }


    /**
     * 计算合计信息
     *
     * @param underPenalty 用户欠罚金(用户应收多少现金)
     */
    private void getTotalInfo(double underPenalty) {
        List<BookInfoBean> bookList = DataRepository.getInstance().getBookList();
        if (bookList.size() > 0) {
            double totalPrice = 0.00;
            double attachPriceTotal = 0.00;
            double penalty = 0.00;
            for (BookInfoBean info : bookList) {
                totalPrice = MoneyUtils.add(totalPrice, info.mPrice);
                attachPriceTotal = MoneyUtils.add(attachPriceTotal, info.mAttachPrice);
                penalty = MoneyUtils.add(penalty, info.mPenalty);
            }
            //显示欠罚金
            mView.setTotalInfoDeposit("数量 " + bookList.size(), "金额 " + MoneyUtils.addToStr(totalPrice, attachPriceTotal),
                    "罚金 " + MoneyUtils.formatMoney(penalty), underPenalty);
        } else {
            mView.setTotalInfoDeposit("数量 " + 0, "金额 0.00", "罚金 0.00", 0.00);
        }
    }
}
