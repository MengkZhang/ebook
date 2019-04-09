package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 读者统计
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class ReaderStatisticsVo {

    @SerializedName("status")
    @Expose
    public int status;

    @SerializedName("data")
    @Expose
    public ResponseData data;                   //合计信息

    public class ResponseData {

        @SerializedName("errorCode")
        @Expose
        public int errorCode;

        @SerializedName("totalCount")
        @Expose
        public int totalCount;                    //数量

        @SerializedName("totalSumPrice")
        @Expose
        public String totalSumPrice;               //数量

        @SerializedName("resultList")
        @Expose
        public List<ReaderInfoVo> resultList;       //书籍列表信息
    }
}
