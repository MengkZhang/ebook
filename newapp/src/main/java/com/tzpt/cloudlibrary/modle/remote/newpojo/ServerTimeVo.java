package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/8/16.
 */

public class ServerTimeVo {
    @SerializedName("data")
    @Expose
    public Data data;

    @SerializedName("status")
    @Expose
    public int status;

    public class Data {
        @SerializedName("time")
        @Expose
        public long time;
    }
}
