package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/9.
 */

public class LibListItemVo {
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("deposit")
    @Expose
    public int deposit;
    @SerializedName("distance")
    @Expose
    public int distance;
    @SerializedName("libCode")
    @Expose
    public String libCode;
    @SerializedName("libId")
    @Expose
    public String libId;
    @SerializedName("libName")
    @Expose
    public String libName;
    @SerializedName("lightTime")
    @Expose
    public String lightTime;
    @SerializedName("lighten")
    @Expose
    public String lighten;
    @SerializedName("lngLat")
    @Expose
    public String lngLat;
    @SerializedName("logo")
    @Expose
    public String logo;
    @SerializedName("serviceTime")
    @Expose
    public String serviceTime;
    @SerializedName("bookNum")
    @Expose
    public int bookNum;
    @SerializedName("hotTip")
    @Expose
    public int hotTip;
    @SerializedName("libLevel")
    @Expose
    public String libLevel;
}
