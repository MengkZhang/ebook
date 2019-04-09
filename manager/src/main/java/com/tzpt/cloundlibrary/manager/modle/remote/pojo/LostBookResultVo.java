package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 赔书
 * Created by Administrator on 2017/7/11.
 */
public class LostBookResultVo {
    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("data")
    @Expose
    public ResponseData data;

    public class ResponseData {
        @SerializedName("errorCode")
        @Expose
        public int errorCode;

        @SerializedName("errorBorrowerIds")
        @Expose
        public List<Long> errorBorrowerIds;

        @SerializedName("failBorrowerIds")
        @Expose
        public List<Long> failBorrowerIds;

        @SerializedName("successBorrowerIds")
        @Expose
        public List<Long> successBorrowerIds;
    }

}
