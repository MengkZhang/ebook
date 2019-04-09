package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tonyjia on 2018/11/21.
 */

public class ActivityVo {
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("startDate")
    @Expose
    public String startDate;
    @SerializedName("endDate")
    @Expose
    public String endDate;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("summary")
    @Expose
    public String summary;
    @SerializedName("isJoin")
    @Expose
    public int isJoin;
}
