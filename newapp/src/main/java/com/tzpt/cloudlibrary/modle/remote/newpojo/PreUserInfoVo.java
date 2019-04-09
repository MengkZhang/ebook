package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * author：ZhiQiangJia
 * create time: 2018/3/6 15:13
 * description:验证用户信息
 */
public class PreUserInfoVo extends BaseDataResultVo {

    @SerializedName("baseInfo")
    @Expose
    public UserBaseInfoVo baseInfo;

    public class UserBaseInfoVo {

        @SerializedName("activityCount")
        @Expose
        public int activityCount;               //我的报名数量
        @SerializedName("appointCount")
        @Expose
        public int appointCount;                //当前预约数量
        @SerializedName("borrowOverdueIsExist")
        @Expose
        public int borrowOverdueIsExist;        //当前借阅是否存在即将逾期 0:不存在 1: 存在
        @SerializedName("borrowOverdueSum")
        @Expose
        public int borrowOverdueSum;            //当前借阅逾期数量
        @SerializedName("borrowSum")
        @Expose
        public int borrowSum;                   //当前借阅数量
        @SerializedName("idCard")
        @Expose
        public String idCard;                   //身份证号
        @SerializedName("isScan")
        @Expose
        public int isScan;                      //是否通过扫描注册 0:否 1:是
        @SerializedName("noteCount")
        @Expose
        public int noteCount;                   //读书笔记数量
        @SerializedName("totalBorrowSum")
        @Expose
        public int totalBorrowSum;              //历史借阅数量
    }

}
