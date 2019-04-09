package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/1/16.
 */

public class PayResultVo extends BaseDataResultVo {
    @SerializedName("value")
    @Expose
    public boolean value;
}
