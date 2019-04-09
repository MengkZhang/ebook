package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/10/8.
 */

public class PenaltyFreeStatisticsInfoVo {
    @SerializedName("applyTime")
    @Expose
    public long applyTime;           //时间戳

    @SerializedName("operCode")
    @Expose
    public String operCode;          //单号

    @SerializedName("readerName")
    @Expose
    public String readerName;       //读者

    @SerializedName("idCard")
    @Expose
    public String idCard;           //身份证号

    @SerializedName("amount")
    @Expose
    public double amount;           //金额

    @SerializedName("status")
    @Expose
    public Status status;           //状态

    @SerializedName("statusDesc")
    @Expose
    public String statusDesc;       //状态

    @SerializedName("auditRemark")
    @Expose
    public String auditRemark;      //驳回理由

    @SerializedName("applyName")
    @Expose
    public String applyName;        //操作员

    public class Status{
        @SerializedName("index")
        @Expose
        public int index;           //状态

        @SerializedName("desc")
        @Expose
        public String desc;           //状态

        @SerializedName("name")
        @Expose
        public String name;           //状态
    }
}
