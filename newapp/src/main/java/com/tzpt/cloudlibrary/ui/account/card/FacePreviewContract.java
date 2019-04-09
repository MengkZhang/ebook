package com.tzpt.cloudlibrary.ui.account.card;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * 设置人脸识别-上传图片
 * Created by tonyjia on 2018/3/13.
 */
public interface FacePreviewContract {

    interface View extends BaseContract.BaseView {

        void showUploadProgressDialog();

        void dismissUploadProgressDialog();

        void uploadSuccess();

        void uploadFail();

        void pleaseLoginTip();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void setLocalImagePath(String localImagePath);

        void updateLoadFaceImage();

    }

}
