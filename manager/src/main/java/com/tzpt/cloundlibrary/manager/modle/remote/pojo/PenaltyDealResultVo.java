package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/12/12.
 */

public class PenaltyDealResultVo {

    @SerializedName("status")
    @Expose
    public int status;

    @SerializedName("data")
    @Expose
    public ResponseData data;

    public class ResponseData{
        @SerializedName("errorCode")
        @Expose
        public int errorCode;

        @SerializedName("errorPenaltyIds")
        @Expose
        public List<Long> errorPenaltyIds;//因为其他未知原因处理失败的罚金记录id集合

        @SerializedName("failPenalty")
        @Expose
        public double failPenalty;//处理失败的罚金金额

        @SerializedName("failPenaltyIds")
        @Expose
        public List<Long> failPenaltyIds;//因为余额不足处理失败的罚金记录id集合

        @SerializedName("succeedPenalty")
        @Expose
        public double succeedPenalty;//处理成功的罚金金额

        @SerializedName("succeedPenaltyIds")
        @Expose
        public List<Long> succeedPenaltyIds;//处理成功的罚金记录id集合
    }

}
