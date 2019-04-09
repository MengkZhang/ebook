package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/9.
 */

public class LibInfoVo {
    @SerializedName("bookNum")
    @Expose
    public int bookNum;

    @SerializedName("ebookNum")
    @Expose
    public int ebookNum;

    @SerializedName("libraryBranch")
    @Expose
    public int libraryBranch;

    @SerializedName("libraryDesc")
    @Expose
    public String libraryDesc;

    @SerializedName("librarySupNum")
    @Expose
    public int librarySupNum;

    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    @SerializedName("message")
    @Expose
    public String message;


    @SerializedName("videosNum")
    @Expose
    public int videosNum;

    @SerializedName("registeredReaderNum")
    @Expose
    public int registeredReaderNum;

    @SerializedName("serviceReaderNum")
    @Expose
    public int serviceReaderNum;

    @SerializedName("accessReadersNum")
    @Expose
    public int accessReadersNum;


}
