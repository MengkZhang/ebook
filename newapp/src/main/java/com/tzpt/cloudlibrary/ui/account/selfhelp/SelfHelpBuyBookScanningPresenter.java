package com.tzpt.cloudlibrary.ui.account.selfhelp;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.SelfBookInfoBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.utils.MoneyUtils;

import java.util.List;

/**
 * 自助购书
 * Created by tonyjia on 2018/8/14.
 */
public class SelfHelpBuyBookScanningPresenter extends RxPresenter<SelfHelpBuyBookScanningContract.View> implements
        SelfHelpBuyBookScanningContract.Presenter {

    @Override
    public void getBookTotalInfo() {
//        if (null != mView) {
//            List<SelfBookInfoBean> bookList = DataRepository.getInstance().getSelfBookList();
//            if (null != bookList && bookList.size() > 0) {
//                double totalPrice = 0.00;
//                for (SelfBookInfoBean info : bookList) {
//                    if (info.discountPrice > 0) {
//                        totalPrice += info.discountPrice;
//                    } else {
//                        totalPrice += (info.price + info.attachPrice);
//                    }
//                }
//                mView.updateBookTotalInfo("数量 " + bookList.size(), "金额 " + MoneyUtils.formatMoney(totalPrice));
//            } else {
//                mView.updateBookTotalInfo("数量 0", "金额 0.00");
//            }
//        }
    }
}
