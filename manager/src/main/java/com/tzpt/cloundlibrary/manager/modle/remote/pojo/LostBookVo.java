package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 赔书
 * Created by Administrator on 2017/7/11.
 */
public class LostBookVo {

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

        @SerializedName("list")
        @Expose
        public List<BookInfoVo> list;
    }

    public class BookInfoVo {
        @SerializedName("libraryBookId")
        @Expose
        public long libraryBookId;

        @SerializedName("borrowId")
        @Expose
        public long borrowId;

        @SerializedName("borrowHallCode")
        @Expose
        public String borrowHallCode;

        @SerializedName("attachPrice")
        @Expose
        public double attachPrice;          //溢价

        @SerializedName("barNumber")
        @Expose
        public String barNumber;

        @SerializedName("belongLibraryHallCode")
        @Expose
        public String belongLibraryHallCode;

        @SerializedName("properTitle")
        @Expose
        public String properTitle;

        @SerializedName("price")
        @Expose
        public double price;

        @SerializedName("usedDepositType")
        @Expose
        public int usedDepositType;

        @SerializedName("deposit")
        @Expose
        public double deposit;
    }
}
