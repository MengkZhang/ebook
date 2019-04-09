package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 消息列表
 * Created by Administrator on 2017/7/3.
 */
public class MsgVo {

    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("data")
    @Expose
    public ResponseData data;

    public class ResponseData {
        @SerializedName("errorCode")
        @Expose
        public int errorCode;
         @SerializedName("totalSum")
        @Expose
        public int totalSum;
        @SerializedName("resultList")
        @Expose
        public List<MsgList> resultList;
        @SerializedName("totalCount")
        @Expose
        public int totalCount;
        @SerializedName("totalPage")
        @Expose
        public int totalPage;
    }

    public class MsgList {

        @SerializedName("newsId")
        @Expose
        public long newsId;                  //消息id
        @SerializedName("startDate")
        @Expose
        public String startDate;           //消息开始时间
        @SerializedName("endDate")
        @Expose
        public String endDate;             //消息结束时间
        @SerializedName("status")
        @Expose
        public String status;                 //消息状态1表示已读，0未读
        @SerializedName("content")
        @Expose
        public String content;             //消息状态1表示已读，0未读
    }
}
