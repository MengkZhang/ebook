package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/8.
 */

public class BaseResultEntityVo<T> {
    @SerializedName("data")
    @Expose
    public T data;

    @SerializedName("status")
    @Expose
    public int status;
}
