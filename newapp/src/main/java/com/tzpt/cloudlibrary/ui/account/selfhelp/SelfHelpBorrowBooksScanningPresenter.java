package com.tzpt.cloudlibrary.ui.account.selfhelp;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.business_bean.SelfHelpBookInfoBean;
import com.tzpt.cloudlibrary.business_bean.SelfHelpReaderBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.utils.MoneyUtils;

import java.util.List;

/**
 * 自助借书
 */
public class SelfHelpBorrowBooksScanningPresenter extends RxPresenter<SelfHelpBorrowBooksScanningContract.View> implements
        SelfHelpBorrowBooksScanningContract.Presenter {
    /**
     * 检查是否有数据列表
     */
    @Override
    public void checkReaderAndBookInfo() {
//        SelfHelpReaderBean readerInfo = DataRepository.getInstance().getReaderInfoBean();
//        if (null != mView && null != readerInfo) {
//            mView.setBorrowableBookSum(readerInfo.mCanBorrowBookSum);
//            if (!readerInfo.mIsDepositType) {
//                mView.setPenaltyOrDepositInfo("欠逾期罚金 " + MoneyUtils.formatMoney(readerInfo.mPenalty));
//            } else {
//                mView.setPenaltyOrDepositInfo("可用押金 " + MoneyUtils.formatMoney(readerInfo.mUsableLibDeposit + readerInfo.mUsablePlatformDeposit));
//            }
//            //获取合计信息
//            getTotalInfo();
//        }
    }


    //设置合计信息
//    private void getTotalInfo() {
////        List<SelfHelpBookInfoBean> bookList = DataRepository.getInstance().getSelfBookList();
//        if (null != bookList && bookList.size() > 0) {
//            //数量
//            //金额
//            //占押金
//            double totalPrice = 0.00;
//            double depositTotalPrice = 0.00;
//            double attachPriceTotal = 0.00;
//            double attachPriceForDeposit = 0.00;
//            for (SelfHelpBookInfoBean info : bookList) {
//                totalPrice += info.mBook.mPrice;
//                attachPriceTotal += info.mAttachPrice;
//                if (info.mDeposit == 1) {
//                    depositTotalPrice += info.mBook.mPrice;
//                    attachPriceForDeposit += info.mAttachPrice;
//                }
//            }
//
//            boolean isNoAgreementAndDeposit = MoneyUtils.add(depositTotalPrice, attachPriceForDeposit) > 0;
//
//            mView.updateBookTotalInfo("数量 " + bookList.size(),
//                    "金额 " + MoneyUtils.formatMoney(totalPrice + attachPriceTotal),
//                    "押金 " + MoneyUtils.formatMoney(depositTotalPrice + attachPriceForDeposit),
//                    isNoAgreementAndDeposit);
////            DataRepository.getInstance().setTakeDeposit(MoneyUtils.add(depositTotalPrice, attachPriceForDeposit));
//        } else {
////            boolean isNoAgreementAndDeposit = !DataRepository.getInstance().getReaderInfoBean().mIsAgreementLib
////                    && !DataRepository.getInstance().getReaderInfoBean().mIsDepositType;
////            DataRepository.getInstance().setTakeDeposit(0);
//            mView.updateBookTotalInfo("数量 0", "金额 0.00", "押金 0.00", false);
//        }
//    }
}
