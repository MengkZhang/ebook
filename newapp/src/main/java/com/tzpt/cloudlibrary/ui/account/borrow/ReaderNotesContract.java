package com.tzpt.cloudlibrary.ui.account.borrow;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.business_bean.ReadNoteGroupBean;

import java.util.List;

/**
 * 读书笔记
 * Created by ZhiqiangJia on 2017-08-24.
 */
public interface ReaderNotesContract {

    interface View extends BaseContract.BaseView {

        void setNetError();

        void setReaderNotesList(List<ReadNoteGroupBean> readerNotesList, int totalBookCount, int totalNoteCount, boolean refresh);

        void setReaderNotesEmpty(boolean refresh);

        void pleaseLoginTip();

        void showToastMsg(String msg);

        void showToastMsg(int msgId);

        void delNoteSuccess();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getReaderNotesList(int pageNum);

        void delNote(long noteId);
    }
}
