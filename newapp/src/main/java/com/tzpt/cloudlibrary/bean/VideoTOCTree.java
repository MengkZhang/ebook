package com.tzpt.cloudlibrary.bean;

import com.tzpt.cloudlibrary.zlibrary.core.tree.ZLTree;

/**
 * Created by Administrator on 2018/7/10.
 */

public class VideoTOCTree extends ZLTree<VideoTOCTree> {
    private long mId;
    private String mName;
    private int mStatus = -1;
    private String mUrl;

    public VideoTOCTree() {
        super();
    }

    public VideoTOCTree(VideoTOCTree parent) {
        super(parent);
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }
}
