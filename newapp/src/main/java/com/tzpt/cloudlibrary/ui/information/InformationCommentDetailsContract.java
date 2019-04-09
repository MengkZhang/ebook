package com.tzpt.cloudlibrary.ui.information;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.CommentBean;

import java.util.List;

/**
 * 评论详情
 * Created by ZhiqiangJia on 2018-01-23.
 */
public interface InformationCommentDetailsContract {
    interface View extends BaseContract.BaseView {

        void setNetError(boolean refresh);

        void pleaseLoginTip();

        void setDiscussCommentList(List<CommentBean> replyBeanList, int totalCount, boolean refresh);

        void setDiscussCommentListEmpty(boolean refresh);

        void showMsgProgressDialog(String msg);

        void dismissMsgProgressDialog();

        void showToastMsg(int msgId);

        void delReaderMsgSuccess(boolean isOwn, long replyId);

        void publishReaderMsgSuccess(int resId);

        void setCommentPraiseStatus(long replyId, int praisedCount);

        void setDiscussLocationAndCurrentPage(int discussIndex, int currentPage);

        /**
         * 没有评论内容提示
         *
         * @param replyId 回复ID
         * @param finish  finish界面
         */
        void showNoCommentDialog(long replyId, boolean finish);

        void showCommentView();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        /**
         * 获取评论及回复列表
         *
         * @param isLibMsgBoard 是否留言板
         * @param commentId     评论ID
         * @param replyId       回复ID
         * @param pageNo        页码
         */
        void getCommentDetailList(boolean isLibMsgBoard, long commentId, long replyId, int pageNo);

        /**
         * 删除自己的评论
         *
         * @param isLibMsgBoard 是否留言板
         * @param commentId     评论ID
         */
        void delOwnMsg(boolean isLibMsgBoard, long commentId);

        /**
         * 删除回复列表中的自己的回复
         *
         * @param isLibMsgBoard 是否留言板
         * @param replyId       回复id
         */
        void delReplyOwnMsg(boolean isLibMsgBoard, long replyId);

        /**
         * 回复评论
         *
         * @param isLibMsgBoard 是否留言板
         * @param libCode       图书馆馆号
         * @param commentId     评论ID
         * @param content       回复内容
         */
        void replyComment(boolean isLibMsgBoard, String libCode, long commentId, String content);

        /**
         * 回复评论的回复
         *
         * @param isLibMsgBoard 是否留言板
         * @param libCode       图书馆馆号
         * @param commentId     评论ID
         * @param replyId       回复ID
         * @param content       回复内容
         */
        void replyRepliedComment(boolean isLibMsgBoard, String libCode, long commentId, long replyId, String content);

        /**
         * 点赞头部评论
         *
         * @param isLibMsgBoard 是否留言板
         * @param commentId     评论ID
         * @param praisedCount  点赞数量
         */
        void praiseReaderMsg(boolean isLibMsgBoard, long commentId, final int praisedCount);

        /**
         * 回复读者点赞，获取取消点赞
         *
         * @param isLibMsgBoard 是否留言板
         * @param replyId       回复id
         * @param praisedCount  点赞数量
         */
        void replyReaderPraise(boolean isLibMsgBoard, long replyId, final int praisedCount);

        boolean isLogin();

        long getNewsId();
    }
}
