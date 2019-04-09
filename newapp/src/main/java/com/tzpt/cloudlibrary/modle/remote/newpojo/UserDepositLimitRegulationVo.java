package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/12/6.
 */

public class UserDepositLimitRegulationVo extends BaseDataResultVo {
    @SerializedName("availablePay")
    @Expose
    public double availablePay;     //最大可充值金额

    @SerializedName("chargeable")
    @Expose
    public int chargeable;          //是否可以充值，0:可以充值；1:不可以充值

    @SerializedName("isLimit")
    @Expose
    public boolean isLimit;         //是否是限制退押金 true:用户不可继续退押金，false:用户可继续退押金

    @SerializedName("limitMessage")
    @Expose
    public String limitMessage;     //不可继续退押金提示语

    @SerializedName("maxAmount")
    @Expose
    public double maxAmount;        //单次退押金最大金额限制
}
