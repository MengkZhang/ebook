package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/14.
 */

public class SelfBorrowBookInfoVo {
    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("attachPrice")
    @Expose
    public float attachPrice;

    @SerializedName("barNumber")
    @Expose
    public String barNumber;

    @SerializedName("belongLibraryHallCode")
    @Expose
    public String belongLibraryHallCode;

    @SerializedName("deposit")
    @Expose
    public int deposit;

    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("price")
    @Expose
    public float price;

    @SerializedName("properTitle")
    @Expose
    public String properTitle;

    @SerializedName("stayLibraryHallCode")
    @Expose
    public String stayLibraryHallCode;
    @SerializedName("executeDeposit")
    @Expose
    public int executeDeposit;//是否执行本系统押金流程(1:是,0:否)
    @SerializedName("borrowDepositType")
    @Expose
    public int borrowDepositType;//-1:不需要押金办借,1:只能使用共享押金办借,2:只能用馆押金办借,3:先用共享押金后用馆押金一起
}


