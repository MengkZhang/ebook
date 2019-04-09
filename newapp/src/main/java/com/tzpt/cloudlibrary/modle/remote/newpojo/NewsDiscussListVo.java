package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/11/14.
 */

public class NewsDiscussListVo {
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

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("resultList")
    @Expose
    public List<DiscussListItemVo> resultList;

    public class DiscussListItemVo {
        @SerializedName("content")
        @Expose
        public String content;

        @SerializedName("createTime")
        @Expose
        public String createTime;

        @SerializedName("gender")
        @Expose
        public int gender;

        @SerializedName("id")
        @Expose
        public long id;

        @SerializedName("image")
        @Expose
        public String image;

        @SerializedName("isOwn")
        @Expose
        public int isOwn;

        @SerializedName("isPraise")
        @Expose
        public int isPraise;

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("praiseCount")
        @Expose
        public int praiseCount;

        @SerializedName("replies")
        @Expose
        public List<DiscussReplyListItemVo> replies;

        @SerializedName("replyCount")
        @Expose
        public int replyCount;

        @SerializedName("nickName")
        @Expose
        public String nickName;
    }

    public class DiscussReplyListItemVo {
        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("id")
        @Expose
        public String id;

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
        public String replyNickName;

        @SerializedName("repliedNickName")
        @Expose
        public String repliedNickName;
    }
}
