package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/10/17.
 */

public class BranchLibVo {
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
        @SerializedName("list")
        @Expose
        public List<BranchLib> libList;
    }

    public class BranchLib {
        @SerializedName("hallCode")
        @Expose
        public String hallCode;

    }
}
