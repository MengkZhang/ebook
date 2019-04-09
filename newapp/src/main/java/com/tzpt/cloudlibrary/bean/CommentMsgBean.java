package com.tzpt.cloudlibrary.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 评论回复传递消息
 * Created by ZhiqiangJia on 2018-01-30.
 */
public class CommentMsgBean implements Serializable {

    public long mMsgId;                         //消息ID
    public boolean mIsChangePraisedCount;       //是否修改点赞数据
    public boolean mIsChangeCommentData;        //是否修改回复评论数据
    public int mPraisedCount;                   //点赞数量
    public boolean mIsOwnPraised;               //是否自己点赞
    public List<CommentBean> mReplyList;        //回复评论列表
    public int mReplyCount;                     //回复数量
}
