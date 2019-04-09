package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/4/4.
 */

public class BannerDataListVo {
    @SerializedName("url")
    @Expose
    public List<String> url;
}
