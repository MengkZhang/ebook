package com.tzpt.cloudlibrary.ui.account.deposit;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.DepositBalanceBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 退押金协议
 * Created by ZhiqiangJia on 2017-10-09.
 */
public interface RefundDepositContract {

    interface View extends BaseContract.BaseView {
        void showLoadingDialog();

        void dismissLoadingDialog();

        void showProgressLoading();

        void dismissProgressLoading();

        void setAvailableBalance(String online, String offline);

        void showDialogTip(int resId, boolean finish);

        void showDialogTip(String str, boolean finish);

        void withdrawDepositLimitTip(String deposit, boolean isLimit);

        void showSuccessDialog();

        void pleaseLoginTip();

        void showProcessedPenaltyDialog(String penalty);

        void showHandlePenaltyDialog();

        void setOfflineDeposit(ArrayList<DepositBalanceBean> list);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        /**
         * 请求可退押金信息
         */
        void requestWithdrawInfo();

        /**
         * 退押金
         *
         * @param deposit 金额
         * @param userIp  用户Ip地址
         */
        void submitRefundDeposit(String deposit, String userIp);

        /**
         * 获取线下押金列表
         */
        void getOfflineDepositList();
    }
}
