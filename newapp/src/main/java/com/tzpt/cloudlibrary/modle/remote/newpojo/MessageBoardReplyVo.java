package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2019/1/9.
 */

public class MessageBoardReplyVo {
    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    @SerializedName("commentId")
    @Expose
    public long commentId;      //消息ID

    @SerializedName("content")
    @Expose
    public String content;      //评论内容

    @SerializedName("createTime")
    @Expose
    public String createTime;   //创建时间

    @SerializedName("id")
    @Expose
    public long id;             //回复id

    @SerializedName("imagePaths")
    @Expose
    public List<String> imagePaths;   //图片路径

    @SerializedName("isOwn")
    @Expose
    public int isOwn;           //是不是自己的留言

    @SerializedName("isPraise")
    @Expose
    public int isPraise;        //是否已经点赞

    @SerializedName("libraryId")
    @Expose
    public long libraryId;      //图书馆id

    @SerializedName("praiseCount")
    @Expose
    public int praiseCount;     //点赞数

    @SerializedName("repliedGender")
    @Expose
    public int repliedGender;   //被回复人性别

    @SerializedName("repliedId")
    @Expose
    public long repliedId;      //被回复人ID

    @SerializedName("repliedName")
    @Expose
    public String repliedName;  //被回复人姓名

    @SerializedName("repliedNickName")
    @Expose
    public String repliedNickName;  //被回复人昵称

    @SerializedName("repliedType")
    @Expose
    public int repliedType;     //被回复类型 0:评论 1:平台 2:读者

    @SerializedName("replyGender")
    @Expose
    public int replyGender;     //回复人性别

    @SerializedName("replyId")
    @Expose
    public long replyId;        //回复人ID

    @SerializedName("replyImage")
    @Expose
    public String replyImage;   //回复头像

    @SerializedName("replyName")
    @Expose
    public String replyName;    //回复人姓名

    @SerializedName("replyNickName")
    @Expose
    public String replyNickName;    //回复人昵称

    @SerializedName("replyRepliedId")
    @Expose
    public long replyRepliedId; //被回复的留言板消息ID

    @SerializedName("replyType")
    @Expose
    public int replyType;       //回复类型 1:平台 2:读者
}
