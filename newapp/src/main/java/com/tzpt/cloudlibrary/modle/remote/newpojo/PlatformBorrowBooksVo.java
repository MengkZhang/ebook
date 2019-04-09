package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 平台在借书籍信息
 * Created by ZhiqiangJia on 2018-01-09.
 */
public class PlatformBorrowBooksVo {
    @SerializedName("errorCode")
    @Expose
    public int errorCode;
    @SerializedName("list")
    @Expose
    public List<Book> list;

    public class Book {
        @SerializedName("barNumber")
        @Expose
        public String barNumber;
        @SerializedName("belongLibraryHallCode")
        @Expose
        public String belongLibraryHallCode;
        @SerializedName("borrowerId")
        @Expose
        public long borrowerId;
        @SerializedName("price")
        @Expose
        public double price;
        @SerializedName("attachPrice")
        @Expose
        public double attachPrice;
        @SerializedName("deposit")
        @Expose
        public double deposit;
        @SerializedName("properTitle")
        @Expose
        public String properTitle;
    }


}
