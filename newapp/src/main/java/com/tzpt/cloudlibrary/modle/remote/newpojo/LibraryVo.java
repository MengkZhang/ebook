package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tonyjia on 2018/11/21.
 */

public class LibraryVo {
    @SerializedName("libId")
    @Expose
    public int libId;
    @SerializedName("libName")
    @Expose
    public String libName;
    @SerializedName("libCode")
    @Expose
    public String libCode;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("lightTime")
    @Expose
    public String lightTime;
    @SerializedName("lighten")
    @Expose
    public String lighten;
    @SerializedName("serviceTime")
    @Expose
    public String serviceTime;
    @SerializedName("logo")
    @Expose
    public String logo;
    @SerializedName("distance")
    @Expose
    public int distance;
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
