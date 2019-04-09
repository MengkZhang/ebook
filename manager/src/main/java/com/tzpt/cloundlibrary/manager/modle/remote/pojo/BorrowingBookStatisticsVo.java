package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 在借统计
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class BorrowingBookStatisticsVo {

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
        public double totalSumPrice;     //金额
        @SerializedName("totalCount")
        @Expose
        public int totalCount;          //数量
        @SerializedName("resultList")
        @Expose
        public List<BorrowingBookVo> resultList;   //书籍列表信息
    }

    public class BorrowingBookVo {

        @SerializedName("barNumber")
        @Expose
        public String barNumber;            //条码号

        @SerializedName("borrowTime")
        @Expose
        public String borrowTime;           //借书时间

        @SerializedName("cardName")
        @Expose
        public String cardName;             //读者姓名

        @SerializedName("hallCode")
        @Expose
        public String hallCode;             //馆号

        @SerializedName("idCard")
        @Expose
        public String idCard;               //读者身份证

        @SerializedName("isOverdue")
        @Expose
        public int isOverdue;            //是否逾期1:未逾期，2:逾期,3:超逾期

        @SerializedName("isbn")
        @Expose
        public String isbn;                 //isbn

        @SerializedName("phone")
        @Expose
        public String phone;                //手机号

        @SerializedName("press")
        @Expose
        public String press;                //出版社

        @SerializedName("price")
        @Expose
        public double price;                //定价

        @SerializedName("properTitle")
        @Expose
        public String properTitle;          //书名

    }
}
