package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/13.
 */

public class EBookDetailInfoVo {
    @SerializedName("author")
    @Expose
    public String author;
    @SerializedName("bookName")
    @Expose
    public String bookName;
    @SerializedName("categoryId")
    @Expose
    public int categoryId;
    @SerializedName("categoryName")
    @Expose
    public String categoryName;
    @SerializedName("createDate")
    @Expose
    public long createDate;
    @SerializedName("file")
    @Expose
    public String file;
    @SerializedName("htmlUrl")
    @Expose
    public String htmlUrl;
    @SerializedName("id")
    @Expose
    public long id;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("isbn")
    @Expose
    public String isbn;
    @SerializedName("number")
    @Expose
    public int number;
    @SerializedName("price")
    @Expose
    public float price;
    @SerializedName("publishDate")
    @Expose
    public String publishDate;
    @SerializedName("publisher")
    @Expose
    public String publisher;
    @SerializedName("summary")
    @Expose
    public String summary;
    @SerializedName("shareNum")
    @Expose
    public int shareNum;
    @SerializedName("collectNum")
    @Expose
    public int collectNum;

}
