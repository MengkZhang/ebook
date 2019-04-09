package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/8/28.
 */

public class AttentionLibResultVo extends BaseDataResultVo {
    @SerializedName("status")
    @Expose
    public boolean status;
}
