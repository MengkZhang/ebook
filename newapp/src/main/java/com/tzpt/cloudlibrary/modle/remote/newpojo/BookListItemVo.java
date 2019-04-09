package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/8.
 */

public class BookListItemVo {
    @SerializedName("author")
    @Expose
    public String author;
    @SerializedName("bookId")
    @Expose
    public long bookId;
    @SerializedName("bookName")
    @Expose
    public String bookName;
    @SerializedName("id")
    @Expose
    public long id;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("isbn")
    @Expose
    public String isbn;
    @SerializedName("publishDate")
    @Expose
    public String publishDate;
    @SerializedName("publisher")
    @Expose
    public String publisher;
    @SerializedName("storageTime")
    @Expose
    public String storageTime;
    @SerializedName("borrowNum")
    @Expose
    public int borrowNum;
    @SerializedName("contentDescript")
    @Expose
    public String contentDescript;
    @SerializedName("recommendNum")
    @Expose
    public int recommendNum;
    @SerializedName("praiseNum")
    @Expose
    public int praiseNum;
}
