package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 自助购书图书信息
 * <p>
 * Created by tonyjia on 2018/8/15.
 */

public class SelfBuyBookVo {

    @SerializedName("id")
    @Expose
    public int id;                          //图书ID
    @SerializedName("barNumber")
    @Expose
    public String barNumber;                //条码号
    @SerializedName("belongLibraryHallCode")
    @Expose
    public String belongLibraryHallCode;    //所属馆号
    @SerializedName("stayLibraryHallCode")
    @Expose
    public String stayLibraryHallCode;      //所在馆号
    @SerializedName("properTitle")
    @Expose
    public String properTitle;              //书名
    @SerializedName("discountPrice")
    @Expose
    public double discountPrice;            //折后价
    @SerializedName("price")
    @Expose
    public double price;                    //原价
    @SerializedName("attachPrice")
    @Expose
    public double attachPrice;              //溢价

    @SerializedName("errorCode")
    @Expose
    public int errorCode;
}
