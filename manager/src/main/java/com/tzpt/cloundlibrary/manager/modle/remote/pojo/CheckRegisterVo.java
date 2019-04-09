package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 检查读者信息
 * Created by Administrator on 2017/7/6.
 */
public class CheckRegisterVo {

    @SerializedName("status")
    @Expose
    public int status;

    @SerializedName("data")
    @Expose
    public ResponseData data;

    public class ResponseData {
        @SerializedName("errorCode")
        @Expose
        public int errorCode;

        @SerializedName("message")
        @Expose
        public String message;

        @SerializedName("errorData")
        public ErrorDataVo errorData;

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("cardName")
        @Expose
        public String cardName;
        @SerializedName("gender")
        @Expose
        public String gender;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("idCard")
        @Expose
        public String idCard;
        @SerializedName("idcardImage")
        @Expose
        public String idcardImage;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("hallCode")
        @Expose
        public String hallCode;
        @SerializedName("totalPenalty")
        @Expose
        public double totalPenalty;         //罚金
        @SerializedName("priceSum")
        @Expose
        public double priceSum;             //系统借书总数(有协议在借和无协议在借之和)
        @SerializedName("needDeposit")
        @Expose
        public int needDeposit;             //1有押金0无押金
        @SerializedName("hasAgreement")
        @Expose
        public int hasAgreement;            //1有协议0无协议

        @SerializedName("maxBookSum")
        @Expose
        public int maxBookSum;              //最大借阅数量
        @SerializedName("platformBookSum")
        @Expose
        public int platformBookSum;         //平台已借书数量
        @SerializedName("platformPriceSum")
        @Expose
        public double platformPriceSum;     //平台已借书押金
        @SerializedName("account")
        @Expose
        public AccountVo account;           //平台押金账户
        @SerializedName("customerDeposit")
        @Expose
        public CustomerDepositVo customerDeposit;//客户押金账户
        @SerializedName("offlineAvailableBalance")
        @Expose
        public String offlineAvailableBalance;  //	读者线下可用余额
        @SerializedName("offlinePlatformBookSum")
        @Expose
        public String offlinePlatformBookSum;   //	读者线下押金占用
        @SerializedName("isUpdateName")
        @Expose
        public int isUpdateName;                //是否需要更新读者信息 0可修改 1不可修改
        @SerializedName("nation")
        @Expose
        public String nation;                   //民族

        @SerializedName("overdueNum")
        @Expose
        public int overdueNum;                  //逾期数量


        public class AccountVo {
            @SerializedName("id")
            @Expose
            public int id;                  //账户id
            @SerializedName("balance")
            @Expose
            public double balance;          //平台总押金
            @SerializedName("frozenAmount")
            @Expose
            public double frozenAmount;     //	读者冻结金额

        }

        public class CustomerDepositVo {
            @SerializedName("bookSum")
            @Expose
            public int bookSum;         //	客户在借总数
            @SerializedName("deposit")
            @Expose
            public double deposit;     //	客户押金
            @SerializedName("priceSum")
            @Expose
            public double priceSum;    // 	客户在借押金占用总数
        }

        public class ErrorDataVo {
            @SerializedName("id")
            @Expose
            public int id;
            @SerializedName("cardName")
            @Expose
            public String cardName;
            @SerializedName("gender")
            @Expose
            public String gender;
            @SerializedName("nation")
            @Expose
            public String nation;
            @SerializedName("idCard")
            @Expose
            public String idCard;
            @SerializedName("phone")
            @Expose
            public String phone;
        }
    }
}
