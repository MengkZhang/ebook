package com.tzpt.cloudlibrary.rxbus.event;

/**
 * 收藏视频 消息
 * Created by tonyjia on 2018/7/13.
 */
public class VideoSetEvent {

    private int mMsgType;           //修改类型 0选择数量 1可编辑状态
    private int mCount;             //电子书选择数量
    private boolean mEditorAble;    //是否可编辑

    public VideoSetEvent(int msgType, int count) {
        mMsgType = msgType;
        mCount = count;
    }

    public VideoSetEvent(int msgType, boolean editorAble) {
        mMsgType = msgType;
        mEditorAble = editorAble;
    }

    public int getMsgType() {
        return mMsgType;
    }

    public int getCount() {
        return mCount;
    }

    public boolean isEditorAble() {
        return mEditorAble;
    }
}
