package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/7/8.
 */

public class DeductPlatDepositVo {
    @SerializedName("code")
    @Expose
    public int code;
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("obj")
    @Expose
    public Obj obj;

    public class Obj {
        @SerializedName("idCard")
        @Expose
        public String idCard; //身份证号
        @SerializedName("cardName")
        @Expose
        public String cardName; //姓名
        @SerializedName("bookSum")
        @Expose
        public int bookSum;//当前借书数量
        @SerializedName("priceSum")
        @Expose
        public double priceSum;//当前借书数量总金额
        @SerializedName("penalty")
        @Expose
        public double penalty;//逾期罚金
        @SerializedName("platformDeposit")
        @Expose
        public double platformDeposit; //平台押金
        @SerializedName("userDeposit")
        @Expose
        public double userDeposit; //客户押金
        @SerializedName("shouldPenalty")
        @Expose
        public double shouldPenalty;//读者应交罚金金额
    }
}
