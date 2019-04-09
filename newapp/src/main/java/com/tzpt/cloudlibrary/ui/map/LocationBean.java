package com.tzpt.cloudlibrary.ui.map;

import java.io.Serializable;

/**
 * 定位对象
 * Created by ZhiqiangJia on 2017-09-05.
 */
public class LocationBean implements Serializable {
    public String mAddress;             //地址
    public String mLngLat;              //经纬度
    public String mProvince;            //省
    public String mCity;                //城市
    public String mAdCode;              //行政区域码
    public String mDistrict;            //区域
    public String mSelectedArea;        //选择的最小行政单位
    public String mSelectedAreaTitle;   //选择的最小行政单位标题
    public String mSelectAdCode;        //选择的行政区域码
    public int mErrorCode;              //错误代码
    public int mStatus = 2;             //0成功 1没有权限 2定位失败

}
