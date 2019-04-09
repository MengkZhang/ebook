package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/14.
 */

public class UserInfoVo extends BaseDataResultVo {
    @SerializedName("buyCount")
    @Expose
    public int buyCount;
    @SerializedName("videosCollectionNum")
    @Expose
    public int videosCollectionNum;
    @SerializedName("ebookCollectionNum")
    @Expose
    public int ebookCollectionNum;

    @SerializedName("activityCount")
    @Expose
    public int activityCount;
    @SerializedName("appointCount")
    @Expose
    public int appointCount;
    @SerializedName("borrowOverdueSum")
    @Expose
    public int borrowOverdueSum;
    @SerializedName("borrowOverdueUnReadSum")
    @Expose
    public int borrowOverdueUnReadSum;
    @SerializedName("borrowSum")
    @Expose
    public int borrowSum;
    @SerializedName("noteCount")
    @Expose
    public int noteCount;
    @SerializedName("totalBorrowSum")
    @Expose
    public int totalBorrowSum;
    @SerializedName("borrowOverdueIsExist")
    @Expose
    public int borrowOverdueIsExist;
    @SerializedName("upcomingOverdueBookNumber")
    @Expose
    public int upcomingOverdueBookNumber;
    @SerializedName("userInfo")
    @Expose
    public UserBaseInfoVo userInfo;

    public class UserBaseInfoVo {
        @SerializedName("readerId")
        @Expose
        public int readerId;
        @SerializedName("idCard")
        @Expose
        public String idCard;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("gender")
        @Expose
        public int gender;
        @SerializedName("cardName")
        @Expose
        public String cardName;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("nickName")
        @Expose
        public String nickName;
    }
}
