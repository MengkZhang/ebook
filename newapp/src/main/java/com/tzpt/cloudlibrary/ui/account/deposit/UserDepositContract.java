package com.tzpt.cloudlibrary.ui.account.deposit;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.DepositBalanceBean;
import com.tzpt.cloudlibrary.bean.UserDepositBean;
import com.tzpt.cloudlibrary.business_bean.DepositCategoryBean;

import java.util.List;

/**
 * Created by ZhiqiangJia on 2017-08-23.
 */
public interface UserDepositContract {

    interface View extends BaseContract.BaseView {

        void setNetError();

        void setUserDepositBottomInfo(double penalty, double depositBalance, double occupyDeposit, List<DepositBalanceBean> list, List<DepositBalanceBean> penaltyList);

        void setUserDepositList(List<UserDepositBean> userDepositBeanList, int totalCount, boolean refresh);

        void setUserDepositListEmpty(boolean refresh);

        void pleaseLoginTip();

        void showDialogTip(int resId, boolean finish);

        void setDepositCategory(List<DepositCategoryBean> list);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getUserDepositInfo(int pageNum, DepositCategoryBean depositCategory);
    }
}
