package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 资讯的评论详情
 * Created by ZhiqiangJia on 2018-01-25.
 */

public class CommentInfoVo {
    @SerializedName("errorCode")
    @Expose
    public int errorCode;
    @SerializedName("content")
    @Expose
    public String content;           //评论内容
    @SerializedName("createTime")
    @Expose
    public String createTime;        //评论时间
    @SerializedName("gender")
    @Expose
    public int gender;               //性别
    @SerializedName("id")
    @Expose
    public int id;                  //评论ID
    @SerializedName("image")
    @Expose
    public String image;            //读者头像
    @SerializedName("isOwn")
    @Expose
    public int isOwn;               //是否是自己发表的评论 即自己是否可以删除此评论
    @SerializedName("isPraise")
    @Expose
    public int isPraise;            //自己是否已经点赞
    @SerializedName("name")
    @Expose
    public String name;             //读者姓名
    @SerializedName("praiseCount")
    @Expose
    public int praiseCount;         //点赞数量
    @SerializedName("newsId")
    @Expose
    public long newsId;             //资讯id
    @SerializedName("nickName")
    @Expose
    public String nickName;         //昵称

}
