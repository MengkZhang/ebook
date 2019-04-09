package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 留言板
 * Created by ZhiqiangJia on 2018-01-11.
 */
public class MessageBoardVo extends BaseDataResultVo {

    @SerializedName("totalCount")
    @Expose
    public int totalCount;

    @SerializedName("pageCount")
    @Expose
    public int pageCount;

    @SerializedName("pageNo")
    @Expose
    public int pageNo;

    @SerializedName("totalPage")
    @Expose
    public int totalPage;

    @SerializedName("resultList")
    @Expose
    public List<MessageVo> resultList;

    public class MessageVo {
        @SerializedName("content")
        @Expose
        public String content;        //留言内容

        @SerializedName("createTime")
        @Expose
        public String createTime;    //创建时间

        @SerializedName("id")
        @Expose
        public long id;            //留言ID

        @SerializedName("imagePaths")
        @Expose
        public List<String> imagePaths;   //图片列表

        @SerializedName("isOwn")
        @Expose
        public int isOwn;            //是否是自己的留言 即是否可以删除 1是自己 0其他

        @SerializedName("isPraise")
        @Expose
        public int isPraise;        //是否点赞

        @SerializedName("libraryId")
        @Expose
        public long libraryId;      //图书馆ID

        @SerializedName("libraryLevelName")
        @Expose
        public String libraryLevelName; //图书馆级别名称

        @SerializedName("nickName")
        @Expose
        public String nickName;     //读者昵称

        @SerializedName("praiseCount")
        @Expose
        public int praiseCount;     //点赞数量

        @SerializedName("readerGender")
        @Expose
        public int readerGender;    //读者性别	string	0:未知 1:男 2:女

        @SerializedName("readerIcon")
        @Expose
        public String readerIcon;    //读者头像

        @SerializedName("readerName")
        @Expose
        public String readerName;    //读者姓名

        @SerializedName("replies")
        @Expose
        public List<MessageBoardReplyVo> replies;  //留言回复

        @SerializedName("replyCount")
        @Expose
        public int replyCount;      //回复数量

    }
}
