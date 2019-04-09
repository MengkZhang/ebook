package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/8.
 */

public class LoginInfoVo {

    @SerializedName("cardName")
    @Expose
    public String cardName;
    @SerializedName("gender")
    @Expose
    public int gender;
    @SerializedName("idCard")
    @Expose
    public String idCard;

    @SerializedName("image")
    @Expose
    public String image;

    @SerializedName("phone")
    @Expose
    public String phone;

    @SerializedName("readerId")
    @Expose
    public String readerId;

    @SerializedName("token")
    @Expose
    public String token;

    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("libCode")
    @Expose
    public String libCode;

    @SerializedName("libId")
    @Expose
    public long libId;

    @SerializedName("libName")
    @Expose
    public String libName;
    @SerializedName("nickName")
    @Expose
    public String nickName;
}
