package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 借书统计
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class BorrowBookStatisticsVo {

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

        @SerializedName("belongLibraryHallCode")
        @Expose
        public String belongLibraryHallCode;    //馆号

        @SerializedName("barNumber")
        @Expose
        public String barNumber;                //条码号

        @SerializedName("borrowNumber")
        @Expose
        public String borrowNumber;             //借阅单号

        @SerializedName("borrowTime")
        @Expose
        public String borrowTime;               //借书时间

        @SerializedName("classificationNumber")
        @Expose
        public String classificationNumber;     //索书号

        @SerializedName("frameCode")
        @Expose
        public String frameCode;                //排架号

//        @SerializedName("hallCode")
//        @Expose
//        public String hallCode;                 //馆号

        @SerializedName("isbn")
        @Expose
        public String isbn;                     //isbn

        @SerializedName("press")
        @Expose
        public String press;                    //出版社

        @SerializedName("price")
        @Expose
        public double price;                    //定价

        @SerializedName("properTitle")
        @Expose
        public String properTitle;              //书名

        @SerializedName("userName")
        @Expose
        public String userName;                 //操作员
//
//        @SerializedName("cardName")
//        @Expose
//        public String cardName;
//        @SerializedName("idCard")
//        @Expose
//        public String idCard;
//        @SerializedName("phone")
//        @Expose
//        public String phone;

    }
}
