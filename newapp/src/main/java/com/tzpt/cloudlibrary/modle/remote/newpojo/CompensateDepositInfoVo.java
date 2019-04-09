package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/12/5.
 */

public class CompensateDepositInfoVo extends BaseDataResultVo {
    @SerializedName("handleType")
    @Expose
    public int handleType;    //处理类型(1:可直接处理,2:可充值共享押金处理,3:只能充值馆押金处理)

    @SerializedName("lackAmount")
    @Expose
    public double lackAmount;    //欠缺金额

    @SerializedName("offlineAvailableBalance")
    @Expose
    public double offlineAvailableBalance;    //线下可用押金

    @SerializedName("onLineAvailableBalance")
    @Expose
    public double onLineAvailableBalance;    //线上可用押金
}
