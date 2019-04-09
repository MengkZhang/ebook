package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 押金处理罚金的返回数据
 * Created by ZhiqiangJia on 2017-07-25.
 */
public class AutoProcessDepositPenaltyVo {

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
    }
}
