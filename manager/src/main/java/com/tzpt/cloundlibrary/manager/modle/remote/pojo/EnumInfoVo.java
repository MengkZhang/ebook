package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/12/17.
 */

public class EnumInfoVo {
    @SerializedName("desc")
    @Expose
    public String desc;

    @SerializedName("index")
    @Expose
    public int index;

    @SerializedName("name")
    @Expose
    public String name;
}
