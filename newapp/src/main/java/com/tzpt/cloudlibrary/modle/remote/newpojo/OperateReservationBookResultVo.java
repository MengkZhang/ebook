package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/16.
 */

public class OperateReservationBookResultVo {
    @SerializedName("errorCode")
    @Expose
    public int errorCode;
    @SerializedName("needIdCard")
    @Expose
    public int needIdCard;

    @SerializedName("appointStatus")
    @Expose
    public int appointStatus;

    @SerializedName("message")
    @Expose
    public String message;
}
