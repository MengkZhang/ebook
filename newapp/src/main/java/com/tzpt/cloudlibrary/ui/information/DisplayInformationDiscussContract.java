package com.tzpt.cloudlibrary.ui.information;

import com.tzpt.cloudlibrary.base.BaseContract;
import com.tzpt.cloudlibrary.bean.CommentBean;
import com.tzpt.cloudlibrary.bean.InformationBean;

import java.util.List;

/**
 * 资讯评论协议
 * Created by ZhiqiangJia on 2017-10-12.
 */

public interface DisplayInformationDiscussContract {

    interface View extends BaseContract.BaseView {
        /**
         * 设置资讯详情
         *
         * @param information 资讯详情
         */
        void setInformationDetail(InformationBean information);

        /**
         * 详情界面加载
         */
        void showDetailLoadProgress();

        /**
         * 详情界面加载异常
         */
        void showDetailLoadError();

        /**
         * 展示评论列表
         *
         * @param discussList 评论列表
         * @param totalCount  评论总数
         * @param refresh     是不是刷新处理
         */
        void setDiscussList(List<CommentBean> discussList, int totalCount, boolean refresh);

        /**
         * 评论列表为空
         *
         * @param refresh 是不是刷新处理
         */
        void setDiscussEmpty(boolean refresh);

        /**
         * 评论列表异常
         */
        void setDiscussError();

        /**
         * 当前展示评论的ID
         *
         * @param newsId       资讯ID
         * @param currentCount 当前列表总数
         */
        void setNewsIdCurrentCount(long newsId, int currentCount);

        /**
         * 成功请求下一页评论列表
         * 设置请求的资讯列表
         *
         * @param beanList 资讯列表
         */
        void setLoadInfoListSuccess(List<InformationBean> beanList);

        /**
         * Toast提示
         *
         * @param resId 提示内容
         */
        void showToastMsg(int resId);

        void showDialogMsg(int resId);

        /**
         * 改变点赞按钮状态
         */
        void setPraiseBtnStatus(boolean isPraised);

        /**
         * 改变点赞数量
         */
        void changePraiseCount(boolean isPraise);

        /**
         * 登录提示
         */
        void pleaseLoginTip();

        /**
         * 设置发表评论按钮状态，
         *
         * @param canSend true可以发送，false不可发送
         */
        void setSendDiscussBtnStatus(boolean canSend);

        /**
         * 发布评论成功
         */
        void publishDiscussSuccess();

        void showMsgProgressDialog(String msg);

        void dismissMsgProgressDialog();

        void publishReaderMsgSuccess();

        void delReaderMsgSuccess(int position);

        void setCommentPraiseStatus(int position, int praisedCount);

        void setDiscussLocationAndCurrentPage(int discussIndex, int currentPage);

        void showNoCommentDialog(boolean noDiscuss, long commentId);

        void setCommentReplyInfo(CommentBean childBean, long commentId);

    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        /**
         * 获取资讯ID
         *
         * @param position 索引值
         */
        void getInfoId(int position);

        /**
         * 获取指定id的资讯详情
         *
         * @param newsId     资讯ID
         * @param fromSearch 1表示搜索结果
         */
        void getInformationDetail(long newsId, int fromSearch);

        /**
         * 获取评论列表
         *
         * @param commentId 评论ID
         * @param pageNum   页数
         * @param newsId    资讯ID
         */
        void getDiscussList(final long commentId, final int pageNum, final long newsId);

        /**
         * 获取资讯列表
         *
         * @param keyword     搜索关键字
         * @param libraryCode 馆号
         * @param source      来源
         * @param title       类型
         * @param pageNum     页数
         */
        void getInfoList(String keyword, String libraryCode, String source, String title, String categoryId, String industryId, int pageNum);

        /**
         * 点赞/取消点赞操作
         *
         * @param newsId   资讯ID
         * @param isPraise true点赞 false取消点赞
         */
        void operateNewsPraise(long newsId, boolean isPraise);

        /**
         * 发表评论
         *
         * @param newsId  资讯ID
         * @param content 评论内容
         */
        void publishDiscuss(long newsId, String content);

        /**
         * 判断是否登录
         *
         * @return true登录 false未登录
         */
        boolean isLogin();

        /**
         * 回复读者评论
         *
         * @param newsId    资讯ID
         * @param commentId 评论id
         * @param content   内容
         */
        void replyReaderMsg(long newsId, long commentId, String content);

        /**
         * 删除读者评论
         *
         * @param commentId 评论id
         */
        void delOwnReaderMsg(long commentId, int position);

        /**
         * 点赞读者评论
         *
         * @param commentId    评论id
         * @param position     位置
         * @param praisedCount 点赞数量
         */
        void praiseReaderMsg(long commentId, int position, int praisedCount);
    }
}
