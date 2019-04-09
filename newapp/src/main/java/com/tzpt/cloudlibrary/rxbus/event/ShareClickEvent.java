package com.tzpt.cloudlibrary.rxbus.event;

/**
 * 分享点击事件
 * Created by tonyjia on 2018/11/1.
 */
public class ShareClickEvent {

    public boolean mShareClick = false;
    public int mShareMsgType = -1; //0图书 2电子书

    public ShareClickEvent(boolean shareClick, int shareMsgType) {
        this.mShareClick = shareClick;
        this.mShareMsgType = shareMsgType;
    }
}
