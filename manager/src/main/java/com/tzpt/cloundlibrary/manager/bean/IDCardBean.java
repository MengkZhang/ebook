package com.tzpt.cloundlibrary.manager.bean;

import java.io.Serializable;

/**
 * 读者身份证信息
 * Created by Administrator on 2017/7/4.
 */
public class IDCardBean implements Serializable {
    public String ID;         //读者id
    public String NAME;         //姓名
    public String SEX;          //性别
    public String FOLK;         //民族
    public String BIRTHDAY;     //生日
    public String NUM;          //号码
    public String ADDRESS;      //地址
    public String ISSUE;        //签发机关
    public String PERIOD;       //有效期限
    public String smallHeadPath;//用户头像
    public String mBundleTel;   //绑定的手机号
}
