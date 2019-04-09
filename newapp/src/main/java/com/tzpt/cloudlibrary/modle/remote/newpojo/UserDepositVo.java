package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/11/18.
 */

public class UserDepositVo extends BaseDataResultVo {

    @SerializedName("accountPenalty")
    @Expose
    public double accountPenalty;   //可以用共享押金账户处理的总罚金

    @SerializedName("activeDeposit")
    @Expose
    public double activeDeposit;    //可用押金

//    @SerializedName("deposit")
//    @Expose
//    public double deposit;          //总押金
//
//    @SerializedName("usedDeposit")
//    @Expose
//    public double usedDeposit;      //占用押金
//
//    @SerializedName("totalPenalty")
//    @Expose
//    public double totalPenalty;     //罚金

    @SerializedName("isScan")
    @Expose
    public int isScan;

//    @SerializedName("type")
//    @Expose
//    public int type;

//    @SerializedName("offlineAvailableBalance")
//    @Expose
//    public double offlineAvailableBalance; //线下可用余额
//
//    @SerializedName("onLineAvailableBalance")
//    @Expose
//    public double onLineAvailableBalance; //线上可用余额


    @SerializedName("needHandlePenalty")
    @Expose
    public double needHandlePenalty;

    @SerializedName("needHandleFreePenalty")
    @Expose
    public double needHandleFreePenalty;

    @SerializedName("depositInfo")
    @Expose
    public List<DepositInfoVo> depositInfo;

    public class DepositInfoVo {
        @SerializedName("activeDeposit")
        @Expose
        public double activeDeposit;    //可用押金

        @SerializedName("code")
        @Expose
        public String code;             //馆号

        @SerializedName("deposit")
        @Expose
        public double deposit;          //总押金

        @SerializedName("name")
        @Expose
        public String name;             //客户名称或平台押金

        @SerializedName("penalty")
        @Expose
        public double penalty;          //罚金

        @SerializedName("penaltyHandleType")
        @Expose
        public int penaltyHandleType;   //罚金支持的处理方式1:只能用共享押金处理,2:可用共享押金或馆押金处理,3:只能用馆押金处理

        @SerializedName("usedDeposit")
        @Expose
        public double usedDeposit;      //占用押金
        @SerializedName("isUnusual")
        @Expose
        public int isUnusual;           //馆是否不正常
    }
}
