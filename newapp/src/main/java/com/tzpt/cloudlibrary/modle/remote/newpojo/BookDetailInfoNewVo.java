package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/12/22.
 */

public class BookDetailInfoNewVo extends BaseDataResultVo {
    @SerializedName("author")
    @Expose
    public String author;

    @SerializedName("authorInfo")
    @Expose
    public String authorInfo;

    @SerializedName("bookId")
    @Expose
    public long bookId;
    @SerializedName("libBookId")
    @Expose
    public long libBookId;
    @SerializedName("bookName")
    @Expose
    public String bookName;

    @SerializedName("catalog")
    @Expose
    public String catalog;

    @SerializedName("categoryId")
    @Expose
    public String categoryId;

    @SerializedName("categoryName")
    @Expose
    public String categoryName;

    @SerializedName("extraInfo")
    @Expose
    public ExtraInfo extraInfo;

    @SerializedName("htmlUrl")
    @Expose
    public String htmlUrl;

    @SerializedName("image")
    @Expose
    public String image;

    @SerializedName("isbn")
    @Expose
    public String isbn;

    @SerializedName("price")
    @Expose
    public double price;

    @SerializedName("publishDate")
    @Expose
    public String publishDate;

    @SerializedName("publisher")
    @Expose
    public String publisher;

    @SerializedName("summary")
    @Expose
    public String summary;

    public class ExtraInfo {
        @SerializedName("borrowNum")
        @Expose
        public int borrowNum;

        @SerializedName("praiseNum")
        @Expose
        public int praiseNum;

        @SerializedName("recommendNum")
        @Expose
        public int recommendNum;
        @SerializedName("shareNum")
        @Expose
        public int shareNum;
    }
}
