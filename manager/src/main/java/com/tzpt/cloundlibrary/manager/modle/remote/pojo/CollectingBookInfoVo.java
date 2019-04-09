package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 藏书统计
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class CollectingBookInfoVo {
    @SerializedName("author")
    @Expose
    public String author;                   //著者

    @SerializedName("barNumber")
    @Expose
    public String barNumber;                //条码号

    @SerializedName("classificationNumber")
    @Expose
    public String classificationNumber;     //索书号

    @SerializedName("frameCode")
    @Expose
    public String frameCode;                //排架号

//    @SerializedName("belongLibraryHallCode")
//    @Expose
//    public String belongLibraryHallCode;    //所在馆号

    @SerializedName("isbn")
    @Expose
    public String isbn;                     //ISBN

    @SerializedName("press")
    @Expose
    public String press;                    //出版社

    @SerializedName("price")
    @Expose
    public double price;                    //价格

//    @SerializedName("attachPrice")
//    @Expose
//    public double attachPrice;              //溢价

    @SerializedName("properTitle")
    @Expose
    public String properTitle;              //书名

    @SerializedName("publishDateYear")
    @Expose
    public String publishDateYear;          //出版年

    @SerializedName("belongLibraryHallCode")
    @Expose
    public String belongLibraryHallCode;       //馆号

    @SerializedName("bookState")
    @Expose
    public BookState bookState;

    public class BookState {
        @SerializedName("index")
        @Expose
        public int index;       //图书状态 1在馆 2在借 25流出
        @SerializedName("desc")
        @Expose
        public String desc;
        @SerializedName("name")
        @Expose
        public String name;
    }

}
