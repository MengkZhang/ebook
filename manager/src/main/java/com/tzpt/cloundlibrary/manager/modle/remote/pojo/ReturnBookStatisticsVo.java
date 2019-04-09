package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 还书统计
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class ReturnBookStatisticsVo {

    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("data")
    @Expose
    public RequestData data;

    public class RequestData {
        @SerializedName("errorCode")
        @Expose
        public int errorCode;

        @SerializedName("totalSumPrice")
        @Expose
        public String totalSumPrice;     //金额

        @SerializedName("totalCount")
        @Expose
        public int totalCount;          //数量

        @SerializedName("resultList")
        @Expose
        public List<BorrowingBookVo> resultList;   //书籍列表信息
    }

    public class BorrowingBookVo {
        @SerializedName("author")
        @Expose
        public String author;               //著者

        @SerializedName("barNumber")
        @Expose
        public String barNumber;            //条码号

        @SerializedName("classificationNumber")
        @Expose
        public String classificationNumber; //索书号

        @SerializedName("frameCode")
        @Expose
        public String frameCode;            //排架号

        @SerializedName("hallCode")
        @Expose
        public String hallCode;             //馆号

        @SerializedName("isbn")
        @Expose
        public String isbn;                 //isbn

        @SerializedName("press")
        @Expose
        public String press;                //出版社

        @SerializedName("price")
        @Expose
        public double price;                //价格

        @SerializedName("properTitle")
        @Expose
        public String properTitle;          //书名

        @SerializedName("returnNumber")
        @Expose
        public String returnNumber;         //还书单号

        @SerializedName("returnTime")
        @Expose
        public String returnTime;           //还书时间

        @SerializedName("userName")
        @Expose
        public String userName;             //操作员
    }
}
