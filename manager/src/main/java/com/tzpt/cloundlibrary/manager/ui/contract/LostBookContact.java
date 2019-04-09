package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;

import java.util.List;

/**
 * 赔书协议
 * Created by Administrator on 2017/7/11.
 */
public interface LostBookContact {
    interface View extends BaseContract.BaseView {
        void showLoading();

        void dismissLoading();

        void setReaderNameNumber(String info);

        void setBorrowableSum(int count);

        void setDepositOrPenalty(String info);

        void setTotalInfo(int count, String moneyInfo);

        void refreshBookList(List<BookInfoBean> list);

        void setEmptyBookList();

        void setOperateTips(int pwdHintStrId, int btnStrId, String payMoney, int editType, boolean editAble);

        void showDialogMsg(int msgId);

        void compensateBookSuccess(String readerId);

        void showMoneyDialogTips(String money);

        void showDialog1BtnFinish(int msgId);

        void noPermissionDialog(int kickedOffline);

        void clearPswText();

        void showLostBookUI(double payMoney);

        void showUnableLostBookUI(int agreementLevel, double payMoney);

        void hideLostBookUI();

        void turnToDaishouPenalty(String readerId, double money);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getReturnBookInfo(String readerId);

        void setBookStatus(int position, boolean checked);

        void compensateBook(String pwd);

        void compensateBookDirect();
    }
}
