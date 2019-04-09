package com.tzpt.cloudlibrary.business_bean;

import com.tzpt.cloudlibrary.bean.CommentBean;

import java.util.List;

/**
 * 资讯评论详情对象
 * Created by Administrator on 2019/1/8.
 */

public class InformationCommentDetailBean {
    public int mTotalCount;
    public List<CommentBean> mReplyCommentList;
    public int mTargetIndex = -1;
    public int mCurrentPage = 0;
}
