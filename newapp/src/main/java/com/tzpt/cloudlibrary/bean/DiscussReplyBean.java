package com.tzpt.cloudlibrary.bean;

import android.text.SpannableString;

/**
 * Created by Administrator on 2017/11/14.
 */
public class DiscussReplyBean {
    public String mCommentId;           //评论id -对象为评论
    public String mReplyId;             //回复id
    public String mReaderName;          //读者名称
    public boolean mIsMan;              //读者性别
    public SpannableString mReplyTitle; //回复title(读者名称+回复评论需要加上被回复者的名称)
    public SpannableString mReplyContent; //回复content(读者名称+回复评论需要加上被回复者的名称)
    public String mReplyTime;           //读者发布时间
    public String mContent;             //读者评论内容
    public String mReplyImage;          //读者或者平台图片
    public boolean mIsOwn;              //是否是自己发表的评论
    public boolean mIsPraised;          //是否点赞
    public int mPraisedCount;           //点赞数量
    public int mTotalCount;             //总回复数量 -对象为评论
    public boolean mIsLight;            //是否高亮

}
