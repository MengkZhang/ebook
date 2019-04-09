package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/11/16.
 */

public class ReservationBookListVo {
    @SerializedName("pageCount")
    @Expose
    public int pageCount;

    @SerializedName("pageNo")
    @Expose
    public int pageNo;

    @SerializedName("totalCount")
    @Expose
    public int totalCount;

    @SerializedName("totalPage")
    @Expose
    public int totalPage;

    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("resultList")
    @Expose
    public List<ReservationBookItemVo> resultList;

    public class ReservationBookItemVo {
        @SerializedName("appointTime")
        @Expose
        public long appointTime;

        @SerializedName("appointTimeEnd")
        @Expose
        public long appointTimeEnd;

        @SerializedName("author")
        @Expose
        public String author;

        @SerializedName("bookId")
        @Expose
        public String bookId;

        @SerializedName("bookName")
        @Expose
        public String bookName;

        @SerializedName("id")
        @Expose
        public int id;

        @SerializedName("image")
        @Expose
        public String image;

        @SerializedName("isbn")
        @Expose
        public String isbn;

        @SerializedName("publishYear")
        @Expose
        public String publishYear;

        @SerializedName("publisher")
        @Expose
        public String publisher;
        @SerializedName("libCode")
        @Expose
        public String libCode;
        @SerializedName("libName")
        @Expose
        public String libName;

        @SerializedName("barNumber")
        @Expose
        public String barNumber;
        @SerializedName("callNumber")
        @Expose
        public String callNumber;
        @SerializedName("frameCode")
        @Expose
        public String frameCode;
        @SerializedName("isNeedIdCard")
        @Expose
        public int isNeedIdCard;//借阅时是否需要身份证	0:不需要 1:需要
        @SerializedName("storeRoom")
        @Expose
        public int storeRoom;
        @SerializedName("storeRoomName")
        @Expose
        public String storeRoomName;


    }
}
