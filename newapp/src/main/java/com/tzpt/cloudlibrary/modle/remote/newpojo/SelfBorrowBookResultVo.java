package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/11/21.
 */

public class SelfBorrowBookResultVo {
    @SerializedName("errorCode")
    @Expose
    public int errorCode;
    @SerializedName("successNum")
    @Expose
    public int successNum;
    @SerializedName("failNum")
    @Expose
    public int failNum;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("resultList")
    @Expose
    public List<BookResultVo> resultList;

    public class BookResultVo {

        @SerializedName("attachPrice")
        @Expose
        public double attachPrice;          //附加值
        @SerializedName("barNumber")
        @Expose
        public String barNumber;            //条码号
        @SerializedName("belongLibraryHallCode")
        @Expose
        public String belongLibraryHallCode;//所属馆
        //@SerializedName("bookSource")
        //@Expose
        //public int bookSource;              //书籍来源(index:1本系统,2图创,3智慧三千)
        @SerializedName("borrowStatus")
        @Expose
        public BorrowStatusVo borrowStatus; //办借状态(index:0成功,其余均失败取desc失败原因)
        @SerializedName("deposit")
        @Expose
        public int deposit;                 //是否需要押金
        @SerializedName("executeDeposit")
        @Expose
        public int executeDeposit;          //是否执行本系统押金流程
        @SerializedName("id")
        @Expose
        public long id;                     //书籍id
        @SerializedName("price")
        @Expose
        public double price;                //书籍定价
        @SerializedName("properTitle")
        @Expose
        public String properTitle;          //书籍标题
        @SerializedName("stayLibraryHallCode")
        @Expose
        public String stayLibraryHallCode;  //书籍所在馆

        public class BorrowStatusVo {
            //办借状态(index:0成功,其余均失败取desc失败原因)
            @SerializedName("index")
            @Expose
            public int index;
            @SerializedName("desc")
            @Expose
            public String desc;
        }
    }
}
