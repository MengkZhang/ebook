package com.tzpt.cloudlibrary.ui.account;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * Created by tonyjia on 2018/11/6.
 */
public interface UserInfoContract {

    interface View extends BaseContract.BaseView {

        void setUserIdCard(String idCard);

        void setUserCardName(String cardName);

        void setUserNickName(String nickName);

        void setUserHeadImage(String headImage, boolean isMan);

        void setUserPhone(String phone);

        void showLoadingProgress();

        void dismissLoadingProgress();

        void changeUserHeadSuccess();

        void changeUserHeadFailed();

        void pleaseLoginTips();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getUserInfo();

        void getUserPhone();

        void submitUserHead(String imagePath);
    }
}
