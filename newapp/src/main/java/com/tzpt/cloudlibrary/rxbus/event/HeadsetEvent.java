package com.tzpt.cloudlibrary.rxbus.event;

/**
 * 耳机拔插事件
 */
public class HeadsetEvent {

    public boolean mIsInsert;   //耳机是否插入

    public HeadsetEvent(boolean isInsert) {
        mIsInsert = isInsert;
    }

}
