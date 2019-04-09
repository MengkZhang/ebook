package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/4/19.
 */

public class BarCodeReusltVo {
    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("barCodeToken")
    @Expose
    public String barCodeToken;

    @SerializedName("readerId")
    @Expose
    public int readerId;

    @SerializedName("validNum")
    @Expose
    public int validNum;
}
