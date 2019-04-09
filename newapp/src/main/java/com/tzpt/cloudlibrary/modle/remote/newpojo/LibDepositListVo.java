package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/11/19.
 */

public class LibDepositListVo {
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
    public List<LibDepositItemVo> resultList;

    public class LibDepositItemVo {
        @SerializedName("customerCanDeposit")
        @Expose
        public double customerCanDeposit;

        @SerializedName("customerDeposit")
        @Expose
        public double customerDeposit;

        @SerializedName("customerHallCode")
        @Expose
        public String customerHallCode;

        @SerializedName("customerName")
        @Expose
        public String customerName;
    }
}
