package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 评论回复对象
 * Created by ZhiqiangJia on 2018-01-31.
 */
public class CommentReplyVo {
    @SerializedName("errorCode")
    @Expose
    public int errorCode;
    @SerializedName("content")
    @Expose
    public String content;
    @SerializedName("createTime")
    @Expose
    public String createTime;
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("isOwn")
    @Expose
    public int isOwn;
    @SerializedName("isPraise")
    @Expose
    public int isPraise;
    @SerializedName("praiseCount")
    @Expose
    public int praiseCount;
    @SerializedName("repliedGender")
    @Expose
    public int repliedGender;
    @SerializedName("repliedName")
    @Expose
    public String repliedName;
    @SerializedName("repliedType")
    @Expose
    public int repliedType;
    @SerializedName("replyGender")
    @Expose
    public int replyGender;
    @SerializedName("replyImage")
    @Expose
    public String replyImage;
    @SerializedName("replyName")
    @Expose
    public String replyName;
    @SerializedName("replyType")
    @Expose
    public int replyType;
    @SerializedName("replyNickName")
    @Expose
    public String replyNickName;
    @SerializedName("repliedNickName")
    @Expose
    public String repliedNickName;

}
