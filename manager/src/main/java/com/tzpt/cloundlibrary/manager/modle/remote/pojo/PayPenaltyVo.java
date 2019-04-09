package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 缴罚金
 * Created by Administrator on 2017/7/8.
 */

public class PayPenaltyVo {
    @SerializedName("code")
    @Expose
    public int code;
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("obj")
    @Expose
    public Object obj;
}
