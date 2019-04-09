package com.tzpt.cloudlibrary.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 评论和留言
 * Created by tonyjia on 2019/1/4.
 */
public class CommentBean implements Serializable{

    public String mCommentImage;                    //头像
    public String mCommentName;                     //名称
    public boolean mIsMan;                          //true男性 false女性
    public boolean mIsOwn;                          //是否是自己发表的评论

    public long mId;                                //ID
    public String mContent;                         //读者评论内容
    public String mPublishTime;                     //读者发布时间
    public int mReplyCount;                         //回复数量
    public String mImagePath;                       //图片

    public boolean mIsPraised;                      //是否点赞
    public int mPraisedCount;                       //点赞数量
    public boolean mIsLight;                        //是否高亮


    public long mRepliedID;                         //被回复ID
    public String mRepliedName;                     //被回复名称
//    public CommentBean mRepliedCommentBean;         //被回复评论  应该使用该对象，后续优化

    public List<CommentBean> mReplyContentList;     //二级回复信息列表

    public long mNewsId;                            //资讯ID
    public String mLibraryCode;                     //图书馆馆号
    public boolean mIsBookStore;                    //是否书店

    public int mType;                               //消息类型 1:读者回复评论 2:读者回复读者的回复 3:平台回复评论 4:平台回复读者的回复 5:评论点赞 6:回复点
}
