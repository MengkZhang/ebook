package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 视频列表
 * Created by tonyjia on 2018/6/25.
 */
public class VideoListVo {

    @SerializedName("id")
    @Expose
    public long id;             //id
    @SerializedName("content")
    @Expose
    public String content;      //内容
    @SerializedName("image")
    @Expose
    public String image;        //	图片
    @SerializedName("name")
    @Expose
    public String name;         //	视频集名称
    @SerializedName("watchTotalNum")
    @Expose
    public int watchTotalNum;   //	观看次数
}
