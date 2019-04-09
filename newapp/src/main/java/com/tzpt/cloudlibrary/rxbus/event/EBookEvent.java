package com.tzpt.cloudlibrary.rxbus.event;

/**
 * 收藏电子书 消息
 * Created by Administrator on 2018/7/4.
 */
public class EBookEvent {
    private int mMsgType;           //修改类型 0选择数量 1可编辑状态
    private int mCount;             //电子书选择数量
    private boolean mEditorAble;    //是否可编辑

    public EBookEvent(int msgType, int count) {
        mMsgType = msgType;
        mCount = count;
    }

    public EBookEvent(int msgType, boolean editorAble) {
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
