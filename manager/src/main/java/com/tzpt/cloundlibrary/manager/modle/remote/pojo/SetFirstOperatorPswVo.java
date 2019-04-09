package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 操作员首次登录修改密码
 * Created by tonyjia on 2018/12/11.
 */
public class SetFirstOperatorPswVo {

    @SerializedName("status")
    @Expose
    public int status;

    @SerializedName("data")
    @Expose
    public OperatorPswVo data;

    public class OperatorPswVo {

        @SerializedName("errorCode")
        @Expose
        public int errorCode;
    }
}
