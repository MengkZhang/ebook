package com.tzpt.cloudlibrary.ui.library;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * 我要留言
 * Created by ZhiqiangJia on 2018-01-12.
 */
public interface LibraryToLeaveMessageContract {

    interface View extends BaseContract.BaseView {
        void showMsgDialog(int msgId);

        void showPostDialog();

        void dismissPostDialog();

        void postSuccess();

        void pleaseLoginTip();

        void pleaseLogin();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        /**
         * 设置馆号
         *
         * @param libraryCode 馆号
         */
        void setLibraryCode(String libraryCode);

        /**
         * 发表留言
         *
         * @param contact   联系方式
         * @param content   内容
         * @param imagePath 图片路径
         */
        void pubMsg(String contact, String content, String imagePath);
    }

}
