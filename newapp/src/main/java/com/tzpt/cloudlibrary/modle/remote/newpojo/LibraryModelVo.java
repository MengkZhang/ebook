package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 图书馆模块
 */
public class LibraryModelVo {

    @SerializedName("appLink")
    @Expose
    public String appLink;  //链接地址
    @SerializedName("id")
    @Expose
    public String id;       //主键
    @SerializedName("logoName")
    @Expose
    public String logoName; //名称
    @SerializedName("logoUrl")
    @Expose
    public String logoUrl;  //logo 地址
    @SerializedName("modelName")
    @Expose
    public String modelName;//模块名称
}
