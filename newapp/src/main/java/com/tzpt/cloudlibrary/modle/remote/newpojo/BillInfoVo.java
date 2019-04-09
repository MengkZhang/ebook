package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/11/18.
 */

public class BillInfoVo extends BaseDataResultVo {

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

    @SerializedName("resultList")
    @Expose
    public List<BillListItemVo> resultList;

    public class BillListItemVo {
        @SerializedName("comment")
        @Expose
        public String comment;

        @SerializedName("deductionMoney")
        @Expose
        public double deductionMoney;

        @SerializedName("isRefund")
        @Expose
        public int isRefund;

        @SerializedName("payStatus")
        @Expose
        public String payStatus;

        @SerializedName("operOrder")
        @Expose
        public String operOrder;

        @SerializedName("orderNumber")
        @Expose
        public String orderNumber;

        @SerializedName("payRemark")
        @Expose
        public String payRemark;

        @SerializedName("remark")
        @Expose
        public String remark;

        @SerializedName("transMoney")
        @Expose
        public double transMoney;

        @SerializedName("transName")
        @Expose
        public String transName;

        @SerializedName("transTime")
        @Expose
        public long transTime;

        @SerializedName("transStatus")
        @Expose
        public TransStatus transStatus;

        @SerializedName("transType")
        @Expose
        public TransType transType;
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

    public class TransType {
        @SerializedName("index")
        @Expose
        public int index;

        @SerializedName("desc")
        @Expose
        public String desc;
    }
}
