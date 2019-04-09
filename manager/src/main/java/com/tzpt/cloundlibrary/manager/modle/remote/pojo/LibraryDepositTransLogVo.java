package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 本馆押金明细
 * Created by ZhiqiangJia on 2017-11-21.
 */
public class LibraryDepositTransLogVo {

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
        @SerializedName("totalCount")
        @Expose
        public int totalCount;
        @SerializedName("totalSumPrice")
        @Expose
        public double totalSumPrice;
        @SerializedName("resultList")
        @Expose
        public List<LibraryDepositTransLog> resultList;
    }

    public class LibraryDepositTransLog {
        @SerializedName("cause")
        @Expose
        public String cause;

        @SerializedName("transMoney")
        @Expose
        public double transMoney;

        @SerializedName("operator")
        @Expose
        public String operator;

        @SerializedName("transName")
        @Expose
        public String transName;

        @SerializedName("transTime")
        @Expose
        public long transTime;

        @SerializedName("payStatus")
        @Expose
        public String payStatus;

        @SerializedName("orderNumber")
        @Expose
        public String orderNumber;

        @SerializedName("remark")
        @Expose
        public String remark;

        @SerializedName("transStatus")
        @Expose
        public TransStatus transStatus;

    }

    public class TransStatus {
        @SerializedName("index")
        @Expose
        public int index;

        @SerializedName("desc")
        @Expose
        public String desc;

        @SerializedName("name")
        @Expose
        public String name;
    }
}
