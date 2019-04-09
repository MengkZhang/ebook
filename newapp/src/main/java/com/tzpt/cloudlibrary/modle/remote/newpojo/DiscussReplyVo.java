package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 回复列表对象
 * Created by ZhiqiangJia on 2018-01-24.
 */
public class DiscussReplyVo {
    @SerializedName("pageCount")
    @Expose
    public int pageCount;

    @SerializedName("pageNo")
    @Expose
    public int pageNo;

    @SerializedName("totalCount")
    @Expose
    public int totalCount;

    @SerializedName("totalPage")
    @Expose
    public int totalPage;

    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    @SerializedName("resultList")
    @Expose
    public List<DiscussReplyItemVo> resultList;

    public class DiscussReplyItemVo {

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("id")
        @Expose
        public long id;

        @SerializedName("content")
        @Expose
        public String content;

        @SerializedName("createTime")
        @Expose
        public String createTime;

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
        public String replyNickName;         //昵称
        @SerializedName("repliedNickName")
        @Expose
        public String repliedNickName;       //昵称
    }
}
