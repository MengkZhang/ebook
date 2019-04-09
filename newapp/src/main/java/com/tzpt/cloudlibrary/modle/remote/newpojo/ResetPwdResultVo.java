package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/19.
 */

public class ResetPwdResultVo {
    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    @SerializedName("message")
    @Expose
    public String message;
}
