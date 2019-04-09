package com.tzpt.cloundlibrary.manager.ui.presenter;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.bean.ReaderInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ApiException;
import com.tzpt.cloundlibrary.manager.ui.contract.BorrowBookManagementScanContract;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 借书扫描
 * Created by Administrator on 2017/7/13.
 */
public class BorrowBookManagementScanPresenter extends RxPresenter<BorrowBookManagementScanContract.View>
        implements BorrowBookManagementScanContract.Presenter, BaseResponseCode {

    @Override
    public void getReaderInfo(ReaderInfo readerInfo) {
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        mView.setReaderNameNumber(readerInfo.mCardName + " " + StringUtils.setIdCardNumberForReader(readerInfo.mIdCard));
        List<BookInfoBean> bookList = DataRepository.getInstance().getBookList();
        int canBorrowSum = readerInfo.mBorrowableSum - bookList.size();
        mView.setBorrowableSum("可借" + (canBorrowSum < 0 ? 0 : canBorrowSum));
        //押金信息
        if (libraryInfo.mAgreementLevel == 1) {
            mView.setDepositOrPenalty("可用共享押金" + MoneyUtils.formatMoney(readerInfo.getPlatformUsableDeposit()));
        } else if (libraryInfo.mAgreementLevel == 2
                || libraryInfo.mAgreementLevel == 3) {
            mView.setDepositOrPenalty("共享" + MoneyUtils.formatMoney(readerInfo.getPlatformUsableDeposit())
                    + " 馆" + MoneyUtils.formatMoney(readerInfo.getOfflineUsableDeposit()));
        } else {
            mView.setDepositOrPenalty("可用馆押金" + MoneyUtils.formatMoney(readerInfo.getOfflineUsableDeposit()));
        }

        getTotalInfo();
    }

    @Override
    public void getBookInfo(String barNumber, ReaderInfo readerInfo) {
        Subscription subscription = DataRepository.getInstance().getBookInfo(barNumber, readerInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookInfoBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case ERROR_CODE_KICK_OUT:
                                        mView.setNoLoginPermission(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.setNoLoginPermission(R.string.operate_timeout);
                                        break;
                                    case ERROR_CODE_1015:
                                        mView.enteringBookTips(R.string.library_has_stopped);
                                        break;
                                    case ERROR_CODE_2207://书籍不存在
                                        mView.enteringBookTips(R.string.bar_code_error);
                                        break;
                                    case ERROR_CODE_2208://书籍已在借
                                        mView.enteringBookTips(R.string.book_already_borrowed);
                                        break;
                                    case ERROR_CODE_2209://书籍已盘亏
                                        mView.enteringBookTips(R.string.have_dish_deficient);
                                        break;
                                    case ERROR_CODE_2210://书籍已流出
                                        mView.enteringBookTips(R.string.has_been_out);
                                        break;
                                    case ERROR_CODE_2211://书籍已丢失
                                        mView.enteringBookTips(R.string.have_lost);
                                        break;
                                    case ERROR_CODE_2212://书籍已剔旧
                                        mView.enteringBookTips(R.string.have_stuck_between_old);
                                        break;
                                    case ERROR_CODE_2214://书籍已被其他人预约
                                        mView.enteringBookTips(R.string.have_make_appointment);
                                        break;
                                    case ERROR_CODE_2215://书籍是基藏库
                                        mView.enteringBookTips(R.string.book_not_borrow);
                                        break;
                                    case ERROR_CODE_2218://书籍已售出
                                        mView.enteringBookTips(R.string.have_been_sale);
                                        break;
                                    case ERROR_CODE_10000://超限值
                                        mView.enteringBookTips(R.string.limit_value);
                                        break;
                                    case ERROR_CODE_10001://读者登录异常

                                        break;
                                    case ERROR_CODE_10002://押金不足
                                        mView.enteringBookTips(R.string.no_more_deposit_tip);
                                        break;
                                    case ERROR_CODE_10003:
                                        mView.enteringBookTips(R.string.duplicate_entry);
                                        break;
                                    case ERROR_CODE_10004:
                                        mView.enteringBookTips(R.string.only_platform_deposit);
                                        break;
                                    case ERROR_CODE_UNKNOWN:
                                    case ERROR_CODE_PARSE:
                                    case ERROR_CODE_NETWORK:
                                    case ERROR_CODE_HTTP:
                                        mView.enteringBookTips(R.string.network_fault);
                                        break;
                                    default:
                                        mView.enteringBookTips(R.string.bar_code_error);
                                        break;
                                }
                            } else {
                                mView.enteringBookTips(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(BookInfoBean book) {
                        if (mView != null) {
                            if (book != null) {
                                mView.enteringBookTips(R.string.empty_content);
                            } else {
                                mView.enteringBookTips(R.string.bar_code_error);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void getTotalInfo() {
        List<BookInfoBean> bookList = DataRepository.getInstance().getBookList();
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
        } else {
            mView.setTotalInfoDeposit("数量 0", "金额 0.00", "押金 0.00");
        }
    }
}
