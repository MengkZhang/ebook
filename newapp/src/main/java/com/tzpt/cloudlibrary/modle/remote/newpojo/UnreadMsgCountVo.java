package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/1/24.
 */

public class UnreadMsgCountVo extends BaseDataResultVo{
    @SerializedName("unreadCount")
    @Expose
    public int unreadCount;
}
