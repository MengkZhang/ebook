package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 自助购书结果
 * Created by tonyjia on 2018/8/16.
 */
public class SelfBuyBookResultVo {

    @SerializedName("errorCode")
    @Expose
    public int errorCode;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("errorData")
    @Expose
    public List<ErrorBookIdVo> errorData;

    public class ErrorBookIdVo {

        @SerializedName("code")
        @Expose
        public int code;
        @SerializedName("libraryBookId")
        @Expose
        public int libraryBookId;
    }
}
