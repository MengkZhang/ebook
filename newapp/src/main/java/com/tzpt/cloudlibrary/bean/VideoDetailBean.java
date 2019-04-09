package com.tzpt.cloudlibrary.bean;

import java.util.List;

/**
 * 视频详情数据
 * Created by tonyjia on 2018/7/24.
 */
public class VideoDetailBean {

    public VideoSetBean mVideoSetBean;
    public List<VideoTOCTree> mVideoTOCTrees;

    public VideoDetailBean(VideoSetBean videoSetBean, List<VideoTOCTree> videoTOCTrees) {
        this.mVideoSetBean = videoSetBean;
        this.mVideoTOCTrees = videoTOCTrees;
    }
}
