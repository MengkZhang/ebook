package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/1/16.
 */

public class AliPayInfoVo extends BaseDataResultVo {

    @SerializedName("payParam")
    @Expose
    public String payParam;

    @SerializedName("orderNum")
    @Expose
    public String orderNum;

    @SerializedName("errorData")
    @Expose
    public String errorData;
}
