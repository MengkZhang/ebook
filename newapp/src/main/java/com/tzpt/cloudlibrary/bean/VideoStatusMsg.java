package com.tzpt.cloudlibrary.bean;

/**
 * 当前视频播放器状态
 * Created by tonyjia on 2018/6/14.
 */
public class VideoStatusMsg {

    public boolean mIsPause = false;    //是否暂停

    public String mVideoImage;          //视频图书

    public int mCellClick = -1;         //1下载 2 收藏
}
