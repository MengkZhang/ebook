package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 赔书统计
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class LostBookStatisticsVo {

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
        public String totalSumPrice;    //金额

        @SerializedName("resultList")
        @Expose
        public List<LostBookVo> resultList;       //书籍列表信息
    }

    public class LostBookVo {
        @SerializedName("barNumber")
        @Expose
        public String barNumber;            //条码号

        @SerializedName("classificationNumber")
        @Expose
        public String classificationNumber; //索书号

        @SerializedName("compensateDate")
        @Expose
        public String compensateDate;       //赔书日期

        @SerializedName("frameCode")
        @Expose
        public String frameCode;            //排架号

        @SerializedName("isbn")
        @Expose
        public String isbn;                 //isbn

        @SerializedName("moneySum")
        @Expose
        public double moneySum;             //金额

        @SerializedName("press")
        @Expose
        public String press;                //出版社

        @SerializedName("price")
        @Expose
        public double price;                //定价

        @SerializedName("properTitle")
        @Expose
        public String properTitle;          //书名

        @SerializedName("returnCode")
        @Expose
        public String returnCode;           //还书单号

        @SerializedName("userName")
        @Expose
        public String userName;             //操作员
//
//
//        @SerializedName("hallCode")
//        @Expose
//        public String hallCode;
//
//        @SerializedName("attachPrice")
//        @Expose
//        public double attachPrice;
    }
}
