package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/10.
 */

public class BookInLibraryItemVo {
    @SerializedName("barNumber")
    @Expose
    public String barNumber;
    @SerializedName("belongLibraryCode")
    @Expose
    public String belongLibraryCode;
    @SerializedName("callNumber")
    @Expose
    public String callNumber;
    @SerializedName("deposit")
    @Expose
    public int deposit;
    @SerializedName("frameCode")
    @Expose
    public String frameCode;
    @SerializedName("inLib")
    @Expose
    public int inLib;
    @SerializedName("libBookId")
    @Expose
    public String libBookId;
    @SerializedName("outLib")
    @Expose
    public int outLib;
    @SerializedName("stayLibraryCode")
    @Expose
    public String stayLibraryCode;
    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("storeRoom")
    @Expose
    public int storeRoom;
    @SerializedName("storeRoomName")
    @Expose
    public String storeRoomName;
    @SerializedName("canAppoint")
    @Expose
    public int canAppoint;//是否有预约功能(1:有,0:无)
}
