package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/1/5.
 */

public class SearchHotResultVo {
    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("value")
    @Expose
    public String value;
    @SerializedName("libraryLevelName")
    @Expose
    public String libraryLevelName;
}
