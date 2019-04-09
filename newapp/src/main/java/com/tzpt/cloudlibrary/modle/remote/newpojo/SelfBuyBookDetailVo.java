package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 购书架图书详情
 * Created by tonyjia on 2018/8/17.
 */
public class SelfBuyBookDetailVo {
    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    @SerializedName("author")
    @Expose
    public String author;       //	作者

    @SerializedName("bookId")
    @Expose
    public long bookId;

    @SerializedName("buyPrice")
    @Expose
    public double buyPrice;     //	购买单价

    @SerializedName("buyTime")
    @Expose
    public String buyTime;      //	购买时间

    @SerializedName("categoryName")
    @Expose
    public String categoryName; //	分类

    @SerializedName("fixedPrice")
    @Expose
    public double fixedPrice;   //	洋码

    @SerializedName("id")
    @Expose
    public long id;             //	购书ID

    @SerializedName("image")
    @Expose
    public String image;        //	图片url 路径

    @SerializedName("isPraise")
    @Expose
    public int isPraise;        //	是否点赞	是否点赞0：否，1：是

    @SerializedName("isbn")
    @Expose
    public String isbn;         //	ISBN

    @SerializedName("levelName")
    @Expose
    public String levelName;    //	图书馆级别名称

    @SerializedName("libBookId")
    @Expose
    public int libBookId;       //	图书ID

    @SerializedName("libName")
    @Expose
    public String libName;      //	所属图书馆

    @SerializedName("press")
    @Expose
    public String press;        //	出版社

    @SerializedName("properTitle")
    @Expose
    public String properTitle;  //	书名

    @SerializedName("publishDate")
    @Expose
    public String publishDate;  //	出版年

    @SerializedName("readingNoteDtoList")
    @Expose
    public List<NoteListItemVo> libraryBorrowerNotes;

}
