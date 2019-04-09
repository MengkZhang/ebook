package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/19.
 */

public class VerifyIDCardVo {
    @SerializedName("bookSum")
    @Expose
    public int bookSum;

    @SerializedName("cardName")
    @Expose
    public String cardName;

    @SerializedName("createBy")
    @Expose
    public int createBy;

    @SerializedName("createTime")
    @Expose
    public long createTime;

    @SerializedName("gender")
    @Expose
    public String gender;

    @SerializedName("hallCode")
    @Expose
    public String hallCode;

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("idCard")
    @Expose
    public String idCard;

    @SerializedName("idPassword")
    @Expose
    public String idPassword;

    @SerializedName("idcardImage")
    @Expose
    public String idcardImage;

    @SerializedName("image")
    @Expose
    public String image;

    @SerializedName("phone")
    @Expose
    public String phone;

    @SerializedName("priceSum")
    @Expose
    public double priceSum;

    @SerializedName("totalBorrowSum")
    @Expose
    public int totalBorrowSum;

    @SerializedName("type")
    @Expose
    public int type;

    @SerializedName("updateBy")
    @Expose
    public String updateBy;

    @SerializedName("updateTime")
    @Expose
    public double updateTime;

    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    @SerializedName("message")
    @Expose
    public String message;
}
