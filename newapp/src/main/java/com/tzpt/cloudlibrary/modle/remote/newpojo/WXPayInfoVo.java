package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/1/16.
 */

public class WXPayInfoVo extends BaseDataResultVo {
    @SerializedName("appid")
    @Expose
    public String appid;

    @SerializedName("noncestr")
    @Expose
    public String noncestr;

    @SerializedName("packageName")
    @Expose
    public String packageName;

    @SerializedName("partnerid")
    @Expose
    public String partnerid;

    @SerializedName("prepayid")
    @Expose
    public String prepayid;

    @SerializedName("sign")
    @Expose
    public String sign;

    @SerializedName("timestamp")
    @Expose
    public String timestamp;

    @SerializedName("orderNum")
    @Expose
    public String orderNum;

    @SerializedName("errorData")
    @Expose
    public String errorData;
}
