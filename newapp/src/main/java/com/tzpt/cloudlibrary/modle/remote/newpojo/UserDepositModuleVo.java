package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/18.
 */

public class UserDepositModuleVo extends BaseDataResultVo {
    @SerializedName("availableBalance")
    @Expose
    public double availableBalance;

    @SerializedName("usedDeposit")
    @Expose
    public double usedDeposit;

    @SerializedName("totalPenalty")
    @Expose
    public double totalPenalty;

    @SerializedName("offlineAvailableBalance")
    @Expose
    public double offlineAvailableBalance;

    @SerializedName("onLineAvailableBalance")
    @Expose
    public double onLineAvailableBalance;

    @SerializedName("isLimit")
    @Expose
    public boolean isLimit;

    @SerializedName("limitMessage")
    @Expose
    public String limitMessage;

    @SerializedName("maxAmount")
    @Expose
    public double maxAmount;

    @SerializedName("availablePay")
    @Expose
    public double availablePay;

    @SerializedName("type")
    @Expose
    public int type;        //0非限制(正式读者),1 限制(非正式读者)

    @SerializedName("chargeable")
    @Expose
    public int chargeable; //是否可以充值，0:可以充值；1:不可以充值
}
