package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/9.
 */

public class LibOpenTimeVo {
    @SerializedName("lightTime")
    @Expose
    public String lightTime;

    @SerializedName("phone")
    @Expose
    public String phone;

    @SerializedName("superviseTel")
    @Expose
    public String superviseTel;

    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    @SerializedName("message")
    @Expose
    public String message;

}
