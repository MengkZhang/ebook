package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/1/23.
 */

public class WithdrawResultVo extends BaseDataResultVo {
    @SerializedName("isSuccess")
    @Expose
    public boolean isSuccess;
}
