package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/12/12.
 */

public class ReaderLoginInfoVo {

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

        @SerializedName("borrowCard")
        @Expose
        public String borrowCard;       //借阅证号

        @SerializedName("canBorrowNum")
        @Expose
        public int canBorrowNum;        //可借数量

        @SerializedName("currentBorrowNum")
        @Expose
        public int currentBorrowNum;    //在借数量

        @SerializedName("cardName")
        @Expose
        public String cardName;         //身份证名字

        @SerializedName("gender")
        @Expose
        public String gender;

        @SerializedName("hallCode")
        @Expose
        public String hallCode;

        @SerializedName("id")
        @Expose
        public String id;               //读者id

        @SerializedName("idCard")
        @Expose
        public String idCard;           //身份证号

        @SerializedName("idcardImage")
        @Expose
        public String idcardImage;

        @SerializedName("image")
        @Expose
        public String image;

        @SerializedName("isScan")
        @Expose
        public int isScan;

        @SerializedName("isServiceReader")
        @Expose
        public int isServiceReader;

        @SerializedName("maxBookSum")
        @Expose
        public int maxBookSum;              //最大借阅数量

        @SerializedName("needDeposit")
        @Expose
        public int needDeposit;             //是否需要押金(1:需要,0:不需要)

        @SerializedName("overdueNum")
        @Expose
        public int overdueNum;              //逾期数量

        @SerializedName("phone")
        @Expose
        public String phone;

        @SerializedName("readerDeposit")
        @Expose
        public ReaderDepositVo readerDeposit;

        @SerializedName("totalBorrowSum")
        @Expose
        public int totalBorrowSum;  //历史借书总数

        public class ReaderDepositVo {
            @SerializedName("applyPenalty")
            @Expose
            public double applyPenalty;     //已申请免单未审核的罚金金额

            @SerializedName("libraryDeposit")
            @Expose
            public DepositInfoVo libraryDeposit;//当前馆下的馆押金

            @SerializedName("notApplyPenalty")
            @Expose
            public double notApplyPenalty;      //未申请免单的罚金金额

            @SerializedName("offlineDeposit")
            @Expose
            public DepositInfoVo offlineDeposit;//线下押金(即当前馆对应客户下的所有馆押金)

            @SerializedName("onlineDeposit")
            @Expose
            public DepositInfoVo onlineDeposit; //线上押金(即共享押金)

            public class DepositInfoVo {
                @SerializedName("availableBalance")
                @Expose
                public double availableBalance;     //可用余额

                @SerializedName("balance")
                @Expose
                public double balance;     //余额

                @SerializedName("usedDeposit")
                @Expose
                public double usedDeposit;     //占用押金
            }
        }
    }
}
