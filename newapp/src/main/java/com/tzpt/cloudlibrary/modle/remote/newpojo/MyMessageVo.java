package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 我的消息
 * Created by ZhiqiangJia on 2018-01-18.
 */
public class MyMessageVo {

    @SerializedName("errorCode")
    @Expose
    public int errorCode;
    @SerializedName("totalCount")
    @Expose
    public int totalCount;       //总数量
    @SerializedName("totalPage")
    @Expose
    public int totalPage;        //总页数
    @SerializedName("resultList")
    @Expose
    public List<MessageResultVo> resultList;//回复列表

    public class MessageResultVo {
        @SerializedName("commentId")
        @Expose
        public long commentId;        //评论ID
        @SerializedName("content")
        @Expose
        public String content;          //内容 type=5/6时没有内容
        @SerializedName("createTime")
        @Expose
        public String createTime;       //创建时间
        @SerializedName("gender")
        @Expose
        public int gender;              //性别 type=3/4时性别为0
        @SerializedName("image")
        @Expose
        public String image;            //头像/馆logo
        @SerializedName("name")
        @Expose
        public String name;             //姓名/平台名
        @SerializedName("newsId")
        @Expose
        public long newsId;             //资讯ID
        @SerializedName("replyId")
        @Expose
        public long replyId;          //回复ID	type=5时没有回复ID
        @SerializedName("type")
        @Expose
        public int type;                //消息类型 1:读者回复评论 2:读者回复读者的回复 3:平台回复评论 4:平台回复读者的回复 5:评论点赞 6:回复点赞
        @SerializedName("nickName")
        @Expose
        public String nickName;         //昵称
        @SerializedName("kind")
        @Expose
        public int kind;                //1留言板 0资讯
        @SerializedName("libraryCode")
        @Expose
        public String libraryCode;      //图书馆馆号
        @SerializedName("libraryLevelName")
        @Expose
        public String libraryLevelName; //图书馆级别名称
    }
}
