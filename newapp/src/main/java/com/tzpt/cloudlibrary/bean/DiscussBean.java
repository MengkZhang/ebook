package com.tzpt.cloudlibrary.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 评论
 * Created by ZhiqiangJia on 2017-10-12.
 */
public class DiscussBean implements Serializable {
    public long mId;                    //评论ID
    public String mReaderImagePath;     //读者头像
    public String mReaderName;          //读者名称
    public String mPublishTime;         //读者发布时间
    public String mContent;             //读者评论内容
    public List<CharSequence> mReplyContentList;
    public int mReplyCount;             //回复数量
    public boolean mIsMan;              //true男性 false女性
    public boolean mIsOwn;              //是否是自己发表的评论
    public boolean mIsPraised;          //是否点赞
    public int mPraisedCount;           //点赞数量
    public boolean mIsLight;            //是否高亮

}
