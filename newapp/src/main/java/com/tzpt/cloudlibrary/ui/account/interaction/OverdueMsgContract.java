package com.tzpt.cloudlibrary.ui.account.interaction;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.OverdueMsgBean;

import java.util.List;

/**
 * Created by Administrator on 2018/3/30.
 */

public interface OverdueMsgContract {
    interface View extends BaseContract.BaseView {
        void showNetError(boolean refresh);

        void setMyMessageList(List<OverdueMsgBean> list, int totalCount, boolean refresh);

        void showMyMessageListEmpty(boolean refresh);

        void modifyMsgState(int position);

        void pleaseLoginTip();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getOverdueMsg(int pageNum);

        void readOverdueMsg(int id);
    }
}
