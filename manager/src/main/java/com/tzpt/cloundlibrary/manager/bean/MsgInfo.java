package com.tzpt.cloundlibrary.manager.bean;

import java.io.Serializable;

/**
 * 消息列表
 * Created by Administrator on 2017/7/3.
 */
public class MsgInfo implements Serializable {

    public long mId;
    public String mMsg;
    public String mDateInfo;

    public boolean mDateValid = true;
    public boolean mIsRead = true;

}
