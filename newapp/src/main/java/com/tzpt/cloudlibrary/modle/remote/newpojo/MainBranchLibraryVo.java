package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 总分馆
 */
public class MainBranchLibraryVo {

    @SerializedName("count")
    @Expose
    public int count;                   //上级馆，一二级分馆数量总和,用于判断是否显示 总分馆模块大于0显示否则不显示
    @SerializedName("supper")
    @Expose
    public LibraryVo supper;            //上级馆
    @SerializedName("oneLevel")
    @Expose
    public List<LibraryVo> oneLevel;    //一级分馆
    @SerializedName("twoLevel")
    @Expose
    public List<LibraryVo> twoLevel;    //二级分馆

    public class LibraryVo {
        @SerializedName("address")
        @Expose
        public String address;          //地址
        @SerializedName("deposit")
        @Expose
        public int deposit;             //押金模式 0 无 1有
        @SerializedName("distance")
        @Expose
        public int distance;            //距离
        @SerializedName("libCode")
        @Expose
        public String libCode;          //馆号
        @SerializedName("libId")
        @Expose
        public String libId;            //馆ID
        @SerializedName("libLevel")
        @Expose
        public String libLevel;         //馆类别
        @SerializedName("libName")
        @Expose
        public String libName;          //馆名称
        @SerializedName("lightTime")
        @Expose
        public String lightTime;        //开关馆时间
        @SerializedName("lighten")
        @Expose
        public String lighten;          //是否显示小灯泡图标
        @SerializedName("lngLat")
        @Expose
        public String lngLat;           //经纬度
        @SerializedName("logo")
        @Expose
        public String logo;             //logo
        @SerializedName("serviceTime")
        @Expose
        public String serviceTime;      //服务时间
        @SerializedName("bookNum")
        @Expose
        public int bookNum;
        @SerializedName("hotTip")
        @Expose
        public int hotTip;
    }
}
