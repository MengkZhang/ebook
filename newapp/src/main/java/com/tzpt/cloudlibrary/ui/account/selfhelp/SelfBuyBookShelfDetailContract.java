package com.tzpt.cloudlibrary.ui.account.selfhelp;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.business_bean.BoughtBookBean;

/**
 * 购书架详情
 * Created by tonyjia on 2018/8/17.
 */
public interface SelfBuyBookShelfDetailContract {

    interface View extends BaseContract.BaseView {

        void showProgressDialog();

        void dismissProgressDialog();

        void setNetError();

        void setBuyBookDetail(BoughtBookBean bean);

        void showMsgToast(int resId);

        void delNoteSuccess();

        void praiseBuyBookSuccess(boolean isPraised, int resId);

        void pleaseLoginTip();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getSelfBuyBookShelfDetail(long buyBookId);

        void praiseSelfBuyBook(long buyBookId, final boolean isPraised);

        /**
         * 删除笔记
         *
         * @param id 笔记ID
         */
        void delNote(long id);
    }
}
