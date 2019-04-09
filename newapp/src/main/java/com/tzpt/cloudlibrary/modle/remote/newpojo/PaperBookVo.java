package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 图书信息
 * Created by tonyjia on 2018/11/21.
 */
public class PaperBookVo {
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("bookId")
    @Expose
    public String bookId;
    @SerializedName("bookName")
    @Expose
    public String bookName;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("author")
    @Expose
    public String author;
    @SerializedName("isbn")
    @Expose
    public String isbn;
}
