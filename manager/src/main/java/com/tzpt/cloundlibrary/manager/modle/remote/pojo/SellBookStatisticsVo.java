package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/3/6.
 */

public class SellBookStatisticsVo {

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
        @SerializedName("totalCount")
        @Expose
        public int totalCount;          //数量

        @SerializedName("totalSumPrice")
        @Expose
        public double totalSumPrice;    //金额

        @SerializedName("totalPracticalMoney")
        @Expose
        public double totalPracticalMoney;

        @SerializedName("resultList")
        @Expose
        public List<SellBookVo> resultList;       //书籍列表信息
    }


    public class SellBookVo {
        @SerializedName("amount")
        @Expose
        public double amount;                       //金额

        @SerializedName("attachPrice")
        @Expose
        public double attachPrice;                  //书籍附加值

        @SerializedName("barNumber")
        @Expose
        public String barNumber;                    //条码号

        @SerializedName("classificationNumber")
        @Expose
        public String classificationNumber;         //索书号

        @SerializedName("createBy")
        @Expose
        public long createBy;                       //创建人id

        @SerializedName("createTime")
        @Expose
        public String createTime;                   //创建时间

        @SerializedName("frameCode")
        @Expose
        public String frameCode;                    //排架号

        @SerializedName("isbn")
        @Expose
        public String isbn;                         //isbn

        @SerializedName("press")
        @Expose
        public String press;                        //出版社

        @SerializedName("price")
        @Expose
        public double price;                        //价格

        @SerializedName("properTitle")
        @Expose
        public String properTitle;                  //书名

        @SerializedName("userName")
        @Expose
        public String userName;                     //操作员
    }
}
