package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.PenaltyBean;

import java.util.List;

/**
 * Created by Administrator on 2018/12/14.
 */

public interface PenaltyDealContract {
    interface View extends BaseContract.BaseView {
        void showProgressLoading();

        void dismissProgressLoading();

        void showDialogTipAndFinish(int msgId);

        void showDialogTip(String msg);

        void showDialogTip(int msgId);

        void setRightBtnStatus(int agreementLevel);

        void dealPenaltySuccess(String readerId);

        void applyChargeFreePenaltySuccess(String readerId);

        void setReaderNameNumber(String info);

        void setNoBackSum(String info);

        void setDepositInfo(String info);

        void setPenaltyList(List<PenaltyBean> list);

        void setTotalInfo(int sumInfo, double totalPrice,  double totalPenalty);

        void setNoLoginPermission(int kickedOffline);

        void exitDealPenalty();

        void showSubstituteChargePenalty(double penalty);

        void showCashChargePenalty(double penalty);

        void turnToSubstituteChargePenalty(String readerId, double money);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        /**
         * 获取登录图书馆信息
         */
        void getLibraryInfo();

        /**
         * 获取读者罚金列表
         *
         * @param readerId 读者ID
         */
        void getPenaltyList(String readerId);

        /**
         * 申请罚金免单
         */
        void applyPenaltyFree(String applyRemark, String pwd);

        /**
         * 收罚金
         */
        void chargePenalty(String pwd);

        void clickRightBtn();
    }
}
