package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/11.
 */

public class BannerNewListItemVo {
    @SerializedName("image")
    @Expose
    public String image;

    @SerializedName("newsId")
    @Expose
    public String newsId;

    @SerializedName("title")
    @Expose
    public String title;
}
