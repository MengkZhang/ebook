package com.tzpt.cloudlibrary.bean;

import java.io.Serializable;
import java.util.List;

/**
 * APP版本
 * Created by ZhiqiangJia on 2017-09-12.
 */
public class AppVersionBean implements Serializable {
    public int mForceUpdate;        //是否强制更新1 是 0 否
    public String mHref;            //更新地址
    public long mId;                //
    public String mTitle;           //标题
    public String mSubTitle;        //子标题
    public List<String> mContents;  //内容提示
    public long mUpdateTime;        //更新时间
    public String mVersion;         //版本号
}
