package com.tzpt.cloudlibrary.bean;

/**
 * 菜单模块
 * Created by tonyjia on 2018/10/19.
 */
public class ModelMenu {

    public int mId;             //id
    public String mName;        //名称
    public int mResId;          //资源名称
    public boolean mIsBaseModel;//是否基本类型
    public String mUrl;         //网络地址
    public String mLogoUrl;     //logo地址

    public ModelMenu() {
    }

    public ModelMenu(int id, String name, int resId, boolean isBaseModel) {
        this.mId = id;
        this.mName = name;
        this.mResId = resId;
        this.mIsBaseModel = isBaseModel;
    }

}
