package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 交罚金结果
 */
public class PayPenaltyResultVo {

    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("errorPenaltyIds")
    @Expose
    public List<Double> errorPenaltyIds;    //因为其他未知原因处理失败的罚金记录id集合

    @SerializedName("failPenalty")
    @Expose
    public double failPenalty;              //处理失败的罚金金额

    @SerializedName("failPenaltyIds")
    @Expose
    public List<Double> failPenaltyIds;     //因为余额不足处理失败的罚金记录id集合

    @SerializedName("succeedPenalty")
    @Expose
    public double succeedPenalty;           //处理成功的罚金金额

    @SerializedName("succeedPenaltyIds")
    @Expose
    public List<Double> succeedPenaltyIds;  //处理成功的罚金记录id集合

}
