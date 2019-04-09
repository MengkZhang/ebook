package com.tzpt.cloudlibrary.bean;

import java.io.Serializable;

/**
 * 分享APP
 * Created by ZhiqiangJia on 2017-08-22.
 */

public class ShareBean implements Serializable {

    public String shareTitle;           //分享标题
    public String shareContent;         //分享内容
    public String shareUrl;             //分享链接
    public String shareUrlForWX;        //分享到微信到链接
    public String shareImagePath;       //分享图片

    public boolean mNeedDownload;       //是否需要下载功能
    public boolean mNeedCopy;           //是否需要复制功能
    public boolean mNeedCollection;     //是否需要收藏功能
    public boolean mIsCollection;       //是否已收藏
}
