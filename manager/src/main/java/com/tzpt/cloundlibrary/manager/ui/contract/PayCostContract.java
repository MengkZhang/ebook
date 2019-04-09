package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;

/**
 * 交押金,罚金
 * Created by Administrator on 2017/7/8.
 */
public interface PayCostContract {
    interface View extends BaseContract.BaseView {
        void payCostSuccess();

        void showDepositMsg(int msgId, String maxMoney);

        void showErrorMsg(String msg);

        void showErrorMsg(int msgId);

        void setNoLoginPermission(int kickedOffline);

        void clearPswText();

        void applyPenaltyFreeSuccess();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        boolean checkPenaltyFreePermission();

        void payCost(int flag, double payMoney, String pwd);

        void applyFreeCharge(String applyRemark, String pwd);
    }
}
