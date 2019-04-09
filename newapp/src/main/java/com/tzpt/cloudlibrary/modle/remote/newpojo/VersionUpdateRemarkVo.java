package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/12/7.
 */

public class VersionUpdateRemarkVo {
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("subtitle")
    @Expose
    public String subtitle;
    @SerializedName("content")
    @Expose
    public List<String> content;
}
