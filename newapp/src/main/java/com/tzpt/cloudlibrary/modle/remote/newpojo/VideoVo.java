package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tonyjia on 2018/11/21.
 */

public class VideoVo {
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("content")
    @Expose
    public String content;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("watchTotalNum")
    @Expose
    public int watchTotalNum;
}
