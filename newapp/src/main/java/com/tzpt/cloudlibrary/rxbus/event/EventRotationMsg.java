package com.tzpt.cloudlibrary.rxbus.event;

/**
 * 旋转方向
 * Created by tonyjia on 2018/8/20.
 */
public class EventRotationMsg {

    public boolean isRotationEnable() {
        return mIsRotationEnable;
    }
    private boolean mIsRotationEnable;

    public EventRotationMsg(boolean isRotationEnable) {
        this.mIsRotationEnable = isRotationEnable;
    }
}
