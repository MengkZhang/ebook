package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/10/8.
 */

public class PenaltyFreeStatisticsVo {
    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("data")
    @Expose
    public ResponseData data;            //合计信息

    public class ResponseData {
        @SerializedName("errorCode")
        @Expose
        public int errorCode;

        @SerializedName("totalCount")
        @Expose
        public int totalCount;          //数量

        @SerializedName("totalSumPrice")
        @Expose
        public String totalSumPrice;    //金额

        @SerializedName("resultList")
        @Expose
        public List<PenaltyFreeStatisticsInfoVo> resultList;   //列表信息
    }


}
