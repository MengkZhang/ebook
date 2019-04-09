package com.tzpt.cloudlibrary.ui.account;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.UserHeadBean;

import java.util.List;

/**
 * 用户头像
 * Created by ZhiqiangJia on 2017-08-22.
 */
public class UserHeadContract {

    interface View extends BaseContract.BaseView {

        void showProgressDialog();

        void dismissProgressDialog();

        void changeUserHeadSuccess();

        void showErrorMessage(int msgId);

        void showHeadImageError();

        void setUserHeadList(List<UserHeadBean> userHeadList);

        void setUserHeadListEmpty();

        void pleaseLoginTip();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getUserHeadList();

        void submitUserHead(String imagePath);
    }
}
