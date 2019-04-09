package com.tzpt.cloundlibrary.manager.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 开放时间设置
 * Created by Administrator on 2017/6/26.
 */
public class LightLibraryOpenTimeInfo {

    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("dayTime")
    @Expose
    public LibraryDateTime dayTime;
    @SerializedName("week")
    @Expose
    public Integer[] week;
    @SerializedName("weekTime")
    @Expose
    public LibraryDateTime weekTime;

    public class LibraryDateTime {
        @SerializedName("date")
        @Expose
        public String date;
        @SerializedName("am")
        @Expose
        public TimeArea am;
        @SerializedName("pm")
        @Expose
        public TimeArea pm;
    }

    public class TimeArea {
        @SerializedName("begin")
        @Expose
        public String begin;
        @SerializedName("end")
        @Expose
        public String end;
    }
}
