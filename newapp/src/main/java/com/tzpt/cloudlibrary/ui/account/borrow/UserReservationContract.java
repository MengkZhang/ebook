package com.tzpt.cloudlibrary.ui.account.borrow;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.business_bean.ReservationBookBean;

import java.util.List;

/**
 * 我的预约
 * Created by ZhiqiangJia on 2017-10-11.
 */
public interface UserReservationContract {

    interface View extends BaseContract.BaseView {

        void setNetError(boolean refresh);

        void setUserReservationList(List<ReservationBookBean> borrowBookBeanList, int totalCount, boolean refresh);

        void setUserReservationEmpty(boolean refresh);

        void showToastError(String msg);

        void showToastError(int resId);

        void pleaseLoginTip();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getUserReservationList(int pageNum);

        void cancelReservation(String isbn,String libCode);
    }
}
