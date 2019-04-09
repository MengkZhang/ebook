package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/14.
 */

public class UserHeadListItemVo {
    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("image")
    @Expose
    public String image;
}
