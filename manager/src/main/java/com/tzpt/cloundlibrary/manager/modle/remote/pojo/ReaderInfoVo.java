package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 读者统计
 * Created by ZhiqiangJia on 2017-07-09.
 */

public class ReaderInfoVo {
    @SerializedName("balance")
    @Expose
    public double balance;                  //押金余额

    @SerializedName("borrowCard")
    @Expose
    public String borrowCard;               //借阅证号

    @SerializedName("borrowerCount")
    @Expose
    public int borrowerCount;            //借阅次数

    @SerializedName("cardName")
    @Expose
    public String cardName;                 //读者名称

    @SerializedName("createTime")
    @Expose
    public String createTime;               //建档时间

    @SerializedName("isServiceReader")
    @Expose
    public int isServiceReader;             //是否是服务读者0:注册读者，1:服务读者

    @SerializedName("readerIdCard")
    @Expose
    public String readerIdCard;             //身份证信息

    @SerializedName("readerJiBie")
    @Expose
    public String readerJiBie;              //读者级别
}
