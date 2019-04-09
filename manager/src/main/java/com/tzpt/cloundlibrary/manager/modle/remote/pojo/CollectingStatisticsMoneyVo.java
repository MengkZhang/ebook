package com.tzpt.cloundlibrary.manager.modle.remote.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 收款统计
 * Created by ZhiqiangJia on 2017-07-09.
 */
public class CollectingStatisticsMoneyVo {

    @SerializedName("inBorrowerPrice")
    @Expose
    public String inBorrowerPrice;  //在借金额

    @SerializedName("idcard")
    @Expose
    public String idcard;           //身份证

    @SerializedName("cardName")
    @Expose
    public String cardName;         //读者姓名

    @SerializedName("bookSum")
    @Expose
    public int bookSum;             //在借数量

    @SerializedName("operDate")
    @Expose
    public String operDate;         //日期

    @SerializedName("operOrder")
    @Expose
    public String operOrder;        //单号

    @SerializedName("operation")
    @Expose
    public String operation;        //项目1:个人充值,2,"个人提现",3,"交罚金4,"交赔金5,"代读者充值6,"代读者提现	string

    @SerializedName("deposit")
    @Expose
    public String deposit;          //金额

    @SerializedName("userName")
    @Expose
    public String userName;         //操作员
}
