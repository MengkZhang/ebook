package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 留言详情
 * Created by Administrator on 2019/1/9.
 */
public class MessageBoardDetailVo {

    @SerializedName("id")
    @Expose
    public long id;                  //留言ID
    @SerializedName("contactWay")
    @Expose
    public String contactWay;       //联系方式
    @SerializedName("content")
    @Expose
    public String content;          //留言内容
    @SerializedName("createTime")
    @Expose
    public String createTime;       //创建时间
    @SerializedName("imagePaths")
    @Expose
    public List<String> imagePaths;       //图片路径
    @SerializedName("isOwn")
    @Expose
    public int isOwn;               //是否点赞
    @SerializedName("isPraise")
    @Expose
    public int isPraise;             //图片路径
    @SerializedName("libraryId")
    @Expose
    public int libraryId;           //图书馆id
    @SerializedName("libraryLevelName")
    @Expose
    public String libraryLevelName; //图书馆id
    @SerializedName("nickName")
    @Expose
    public String nickName;         //昵称
    @SerializedName("praiseCount")
    @Expose
    public int praiseCount;         //点赞数量
    @SerializedName("readerGender")
    @Expose
    public int readerGender;        //读者性别
    @SerializedName("readerIcon")
    @Expose
    public String readerIcon;      //读者头像
    @SerializedName("readerName")
    @Expose
    public String readerName;      //读者姓名
    @SerializedName("replyCount")
    @Expose
    public int replyCount;      //回复数量

    @SerializedName("errorCode")
    @Expose
    public int errorCode;
}
