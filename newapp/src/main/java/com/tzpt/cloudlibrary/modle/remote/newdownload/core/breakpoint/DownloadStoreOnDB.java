package com.tzpt.cloudlibrary.modle.remote.newdownload.core.breakpoint;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tzpt.cloudlibrary.modle.local.db.DBManager;
import com.tzpt.cloudlibrary.modle.local.db.DownInfoColumns;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadTask;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2018/8/9.
 */

public class DownloadStoreOnDB implements DownloadStore {

    @Nullable
    @Override
    public BreakpointInfo get(String url) {
        DownInfoColumns downInfoColumns = DBManager.getInstance().getDownInfo(url);
        if (downInfoColumns != null) {
            BreakpointInfo downloadInfo = new BreakpointInfo(downInfoColumns.getUrl(),
                    new File(downInfoColumns.getSavePath()),
                    downInfoColumns.getName());
            downloadInfo.increaseCurrentOffset(downInfoColumns.getReadLength(), false);
            downloadInfo.setContentLength(downInfoColumns.getCountLength());
            return downloadInfo;
        }
        return null;
    }

    @Override
    public void createAndInsert(@NonNull DownloadTask task, boolean reset){
        DownInfoColumns downInfoColumns = new DownInfoColumns();
        downInfoColumns.setUrl(task.getUrl());
        downInfoColumns.setName(task.getFilename());
        downInfoColumns.setFileType(task.getFileType());
        downInfoColumns.setSavePath(task.getParentFile().getPath() + "/");
        downInfoColumns.setCountLength(0);
        downInfoColumns.setReadLength(0);
        DBManager.getInstance().insertDownInfo(downInfoColumns, reset);
    }

    @Override
    public void updateContentLength(BreakpointInfo downloadInfo) {
        DBManager.getInstance().updateDownInfoTotalBytes(downloadInfo.getUrl(), downloadInfo.getTotalLength());
    }

    @Override
    public void updateReadBytes(BreakpointInfo downloadInfo) {
        DBManager.getInstance().updateDownInfoReadBytes(downloadInfo.getUrl(), downloadInfo.getTotalOffset());
    }

    @Override
    public void del(String url) {
        DBManager.getInstance().deleteDownloadInfo(url);
    }
}
