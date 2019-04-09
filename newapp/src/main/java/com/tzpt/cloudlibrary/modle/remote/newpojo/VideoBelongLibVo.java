package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/10.
 */

public class VideoBelongLibVo {
    @SerializedName("address")
    @Expose
    public String address;

    @SerializedName("bookExist")
    @Expose
    public int bookExist;

    @SerializedName("deposit")
    @Expose
    public int deposit;

    @SerializedName("distance")
    @Expose
    public int distance;

    @SerializedName("inLib")
    @Expose
    public int inLib;

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

    @SerializedName("outLib")
    @Expose
    public int outLib;

    @SerializedName("recommondExist")
    @Expose
    public int recommondExist;

    @SerializedName("serviceTime")
    @Expose
    public String serviceTime;

    @SerializedName("libBookId")
    @Expose
    public String libBookId;

}
