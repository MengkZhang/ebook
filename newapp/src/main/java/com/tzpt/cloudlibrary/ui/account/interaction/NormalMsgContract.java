package com.tzpt.cloudlibrary.ui.account.interaction;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.CommentBean;

import java.util.List;

/**
 * Created by Administrator on 2018/3/29.
 */

public interface NormalMsgContract {
    interface View extends BaseContract.BaseView {

        void showMsgDialog(int msgId);

        void showNetError(boolean refresh);

        void setMyMessageList(List<CommentBean> beanList, int totalCount, boolean refresh);

        void setMyMessageListEmpty(boolean refresh);

        void pleaseLoginTip();

        void showSendProgressDialog();

        void dismissSendProgressDialog();

        /**
         * 回复成功
         */
        void publishMsgSuccess();

        void publishMsgFailure();

        void showNoCommentDialog();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        boolean isLogin();

        /**
         * 获取消息列表
         *
         * @param pageNo 页码
         */
        void getMyMessageList(int pageNo);

        /**
         * 发布评论或者留言
         *
         * @param libCode 图书馆馆号
         * @param content 内容
         * @param replyId 回复ID
         * @param libCode 图书馆馆号
         */
        void publishMessage(String libCode, String content, long replyId);
    }
}
