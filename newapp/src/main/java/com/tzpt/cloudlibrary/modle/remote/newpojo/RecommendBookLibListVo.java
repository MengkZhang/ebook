package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/11/16.
 */

public class RecommendBookLibListVo {


    @SerializedName("list")
    @Expose
    public List<RecommendLibraryListVo> list;
    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    public class RecommendLibraryListVo {
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("bookExist")
        @Expose
        public int bookExist;
        @SerializedName("deposit")
        @Expose
        public String deposit;
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
        @SerializedName("bookNum")
        @Expose
        public int bookNum;
        @SerializedName("hotTip")
        @Expose
        public int hotTip;
    }
}
