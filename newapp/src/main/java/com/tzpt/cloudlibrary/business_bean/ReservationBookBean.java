package com.tzpt.cloudlibrary.business_bean;

/**
 * 预约图书信息
 * Created by Administrator on 2018/10/30.
 */

public class ReservationBookBean extends BaseBookBean {
    public String mFrameCode;            //排架号
    public int mStoreRoom;               //库位
    public String mStoreRoomName;        //库位名称
    public String mRemark;               //注释是否需要身份证
    public String mValidTime;            //预约有效期
    public String mAppointTimeEnd;       //预约结束时间
}
