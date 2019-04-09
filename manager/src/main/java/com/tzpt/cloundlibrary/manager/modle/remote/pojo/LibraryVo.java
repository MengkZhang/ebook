package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/6/21.
 */

public class LibraryVo {
    @SerializedName("agreement")
    @Expose
    public int agreement;
    @SerializedName("areaAddress")
    @Expose
    public String areaAddress;
    @SerializedName("borrowDays")
    @Expose
    public int borrowDays;
    @SerializedName("borrowNum")
    @Expose
    public int borrowNum;
    @SerializedName("customer_id")
    @Expose
    public int customer_id;
    @SerializedName("deposit")
    @Expose
    public int deposit;
    @SerializedName("hallCode")
    @Expose
    public String hallCode;
    @SerializedName("libraryLevel")
    @Expose
    public String libraryLevel;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("rent")
    @Expose
    public float rent;

}
