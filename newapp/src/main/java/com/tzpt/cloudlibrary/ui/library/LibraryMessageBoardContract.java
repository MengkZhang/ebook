package com.tzpt.cloudlibrary.ui.library;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.CommentBean;

import java.util.List;

/**
 * 图书馆留言板
 * Created by ZhiqiangJia on 2018-01-10.
 */
public interface LibraryMessageBoardContract {

    interface View extends BaseContract.BaseView {

        void setLibraryMessageBoardList(List<CommentBean> messageBoardList, int totalCount, boolean refresh);

        void setLibraryMessageBoardListEmpty(boolean refresh);

        void setNetError(boolean refresh);

        void showProgressDialog();

        void dismissDelProgressDialog();

        void showMessageTips(int msgId);

        void pleaseLoginTip();

        void praiseSuccess(int position);

        void replyMessageSuccess(CommentBean commentBean, long msgId);

        /**
         * 删除留言
         *
         * @param msgId 留言ID
         */
        void removeMsgBoard(long msgId);

        /**
         * 设置留言高亮
         *
         * @param targetIndex 高亮下标
         * @param currentPage 当前页数
         */
        void setMessageHighLight(int targetIndex, int currentPage);

        /**
         * 留言被删除
         *
         * @param msgId 留言ID
         */
        void showNoCommentDialog(long msgId, boolean finish);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        boolean isLogin();

        /**
         * 获取留言板列表
         *
         * @param libraryCode 图书馆馆号
         * @param pageNum     页码
         * @param msgId       留言ID
         */
        void getLibraryMessageBoardList(String libraryCode, int pageNum, long msgId);

        /**
         * 删除留言
         *
         * @param libraryCode 图书馆馆号
         * @param msgId       留言ID
         */
        void delMsg(String libraryCode, long msgId);

        /**
         * 点赞留言
         *
         * @param msgId    留言ID
         * @param position 下标
         */
        void praise(long msgId, int position);

        /**
         * 回复留言
         *
         * @param libraryCode 图书馆馆号
         * @param msgId       留言ID
         * @param content     内容
         */
        void replyMessage(String libraryCode, long msgId, String content);

        /**
         * 重新刷新留言板列表
         *
         * @param libraryCode 图书馆馆号
         */
        void refreshMessageBoardList(String libraryCode);
    }

}
