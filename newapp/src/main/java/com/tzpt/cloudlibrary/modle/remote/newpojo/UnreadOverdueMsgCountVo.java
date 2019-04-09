package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/4/2.
 */

public class UnreadOverdueMsgCountVo extends BaseDataResultVo{
    @SerializedName("messageCount")
    @Expose
    public int messageCount;
}
