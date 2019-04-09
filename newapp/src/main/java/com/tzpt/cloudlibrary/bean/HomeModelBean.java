package com.tzpt.cloudlibrary.bean;

/**
 * 首页模块
 * Created by tonyjia on 2018/10/19.
 */
public class HomeModelBean {

    public int mId;             //id
    public String mName;        //名称
    public int mResId;          //资源名称
    public int mHomeModelIndex; //位置

    public HomeModelBean(int id, String name, int resId) {
        this.mId = id;
        this.mName = name;
        this.mResId = resId;
    }

}
