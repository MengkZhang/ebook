package com.tzpt.cloundlibrary.manager.ui.contract;

import com.tzpt.cloundlibrary.manager.base.BaseContract;
import com.tzpt.cloundlibrary.manager.bean.MsgInfo;

import java.util.List;

/**
 * 消息
 * Created by Administrator on 2017/7/3.
 */
public interface MessageContract {
    interface View extends BaseContract.BaseView {

        void showMsgDialog(int msgId);

        void setNoLoginPermission(int msgId);

        void showMsgDialog(int msgId, boolean refresh);

        void showMsgList(List<MsgInfo> list, int totalCount, boolean refresh);

        void showMsgListEmpty(boolean refresh);

        void complete();

        void setReadMsgStatus(int position);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void getMsgFromRemote(int pageNum);

        void setReadMsgStatus(long newsId, int position);

    }
}
