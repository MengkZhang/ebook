package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/12/14.
 */

public class PenaltyListVo {
    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("data")
    @Expose
    public ResponseData data;            //合计信息

    public class ResponseData{
        @SerializedName("errorCode")
        @Expose
        public int errorCode;

        @SerializedName("message")
        @Expose
        public String message;

        @SerializedName("resultList")
        @Expose
        public List<PenaltyItemVo> resultList;
    }

    public class PenaltyItemVo{
        @SerializedName("attachPrice")
        @Expose
        public double attachPrice;  //书籍溢价

        @SerializedName("barNumber")
        @Expose
        public String barNumber;    //书籍条码号

        @SerializedName("belongLibraryHallCode")
        @Expose
        public String belongLibraryHallCode;    //书籍所属馆

        @SerializedName("borrowHallCode")
        @Expose
        public String borrowHallCode;   //借书馆号

        @SerializedName("borrowId")
        @Expose
        public long borrowId;           //借阅记录id

        @SerializedName("borrowTime")
        @Expose
        public long borrowTime;         //借阅时间

        @SerializedName("handleOrderNumber")
        @Expose
        public String handleOrderNumber;    //罚金处理的流水号

        @SerializedName("isHandle")
        @Expose
        public int  isHandle;           //罚金是否已处理

        @SerializedName("libraryBookId")
        @Expose
        public long libraryBookId;      //书籍id

        @SerializedName("penalty")
        @Expose
        public double penalty;          //罚金金额

        @SerializedName("penaltyId")
        @Expose
        public long penaltyId;          //罚金记录id

        @SerializedName("price")
        @Expose
        public double price;            //书籍定价

        @SerializedName("properTitle")
        @Expose
        public String properTitle;      //书籍正题名

        @SerializedName("readerId")
        @Expose
        public long readerId;           //读者id

        @SerializedName("returnHallCode")
        @Expose
        public String returnHallCode;   //还书馆号

        @SerializedName("returnTime")
        @Expose
        public long returnTime;         //还书时间


    }
}
