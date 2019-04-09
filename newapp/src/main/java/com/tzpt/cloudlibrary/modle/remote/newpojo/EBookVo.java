package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tonyjia on 2018/11/21.
 */

public class EBookVo {
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("author")
    @Expose
    public String author;
    @SerializedName("bookName")
    @Expose
    public String bookName;
    @SerializedName("createDate")
    @Expose
    public String createDate;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("number")
    @Expose
    public int number;
    @SerializedName("summary")
    @Expose
    public String summary;
}
