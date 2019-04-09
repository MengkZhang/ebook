package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 还书
 * Created by Administrator on 2017/7/10.
 */

public class ReturnBookVo {

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
        @SerializedName("bookId")
        @Expose
        public int bookId;
        @SerializedName("readerId")
        @Expose
        public String readerId;
        @SerializedName("belongLibraryHallCode")
        @Expose
        public String belongLibraryHallCode;
        @SerializedName("barNumber")
        @Expose
        public String barNumber;
        @SerializedName("properTitle")
        @Expose
        public String properTitle;
        @SerializedName("price")
        @Expose
        public double price;
        @SerializedName("penalty")
        @Expose
        public double penalty;      //借阅记录对应的罚金金额
        @SerializedName("penaltyId")
        @Expose
        public long penaltyId;      //借阅记录对应的罚金记录id
        @SerializedName("deposit")
        @Expose
        public double deposit;
        @SerializedName("attachPrice")
        @Expose
        public double attachPrice;
    }

}
