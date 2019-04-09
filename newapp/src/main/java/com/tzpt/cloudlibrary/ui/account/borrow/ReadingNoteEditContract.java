package com.tzpt.cloudlibrary.ui.account.borrow;

import com.tzpt.cloudlibrary.base.BaseContract;

/**
 * 读书笔记
 * Created by ZhiqiangJia on 2017-08-29.
 */
public interface ReadingNoteEditContract {

    interface View extends BaseContract.BaseView {

        void saveUserReadingNoteSuccess(long noteId);

        void showToastTips(int msgId);

//        void setUserReadingNote(int noteId, String noteContent);

        void showProgressDialog();

        void dismissProgressDialog();

//        void editNoteSuccess(String content);

        void pleaseLoginTip();

        void showDialogTips(String tips);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void saveUserReadingNote(long borrowBookId, long buyBookId, long noteId, String content);

//        void getUserNote(int id);
    }

}
