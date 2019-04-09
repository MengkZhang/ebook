package com.tzpt.cloudlibrary.ui.account.card;

import android.graphics.Bitmap;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.UserInfoBean;

/**
 * Created by ZhiqiangJia on 2017-10-16.
 */

public interface UserIdentificationContract {

    interface View extends BaseContract.BaseView {

        void setBarCodeBitmap(Bitmap bitmap);

        void setQRBitmap(Bitmap bitmap);

        void showProgressView();

        void showContentView();

        void showError();

        void setUserNickName(String nickName);

        void setUserPhoneNumber(String phone);

        void setUserHeadImage(String imagePath, boolean isMan);

        void pleaseLoginTip();

        void setFaceRecognitionImage(String faceImage);

        void showProgressDialog();

        void dismissProgressDialog();

        void showDialogTip(int msgId);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getLoginInfo();

        void getTokenBar();

        void getUserImgStatus();
    }
}
