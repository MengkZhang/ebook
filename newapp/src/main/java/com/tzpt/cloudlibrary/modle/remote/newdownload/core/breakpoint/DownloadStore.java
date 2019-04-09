package com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadTask;

import java.io.IOException;

/**
 * Created by Administrator on 2018/8/6.
 */

public interface DownloadStore {
    /**
     * 根据下载地址获取下载信息
     *
     * @param url
     * @return
     */
    @Nullable
    BreakpointInfo get(String url);

    /**
     * 创建下载信息
     *
     * @param task
     * @return
     * @throws IOException
     */
    void createAndInsert(@NonNull DownloadTask task, boolean reset);

    /**
     * 更新下载信息文件总大小
     *
     * @param downloadInfo
     */
    void updateContentLength(BreakpointInfo downloadInfo);

    /**
     * 更新下载信息已下载大小
     *
     * @param downloadInfo
     */
    void updateReadBytes(BreakpointInfo downloadInfo);

    /**
     * 删除下载记录
     *
     * @param url
     */
    void del(String url);
}
