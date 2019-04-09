package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/3/30.
 */

public class OverdueMsgListVo {
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
    public List<OverdueMsgVo> resultList;

    public class OverdueMsgVo {
        @SerializedName("borrowerId")
        @Expose
        public long borrowerId;

        @SerializedName("createTime")
        @Expose
        public long createTime;

        @SerializedName("nowTime")
        @Expose
        public long nowTime;

        @SerializedName("id")
        @Expose
        public int id;

        @SerializedName("idCard")
        @Expose
        public String idCard;

        @SerializedName("message")
        @Expose
        public String message;

        @SerializedName("messageCount")
        @Expose
        public int messageCount;

        @SerializedName("status")
        @Expose
        public int status;//1未查看,2查看
        @SerializedName("borrowState")
        @Expose
        public int borrowState;//借阅状态
    }
}
