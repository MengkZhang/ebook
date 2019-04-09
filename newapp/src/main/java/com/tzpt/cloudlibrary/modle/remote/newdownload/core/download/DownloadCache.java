package com.tzpt.cloudlibrary.modle.remote.newdownload.core.download;

import android.support.annotation.NonNull;

import com.tzpt.cloudlibrary.modle.remote.newdownload.core.Util;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.cause.ResumeFailedCause;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception.FileBusyAfterRunException;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception.InterruptException;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception.NetworkPolicyException;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception.PreAllocateException;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception.ResumeFailedException;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception.ServerCanceledException;
import com.tzpt.cloudlibrary.modle.remote.newdownload.core.file.MultiPointOutputStream;

import java.io.IOException;
import java.net.SocketException;

/**
 * Created by Administrator on 2018/8/6.
 */

public class DownloadCache {
    //重定向地址
    private String mRedirectLocation;
    private final MultiPointOutputStream mOutputStream;

    private volatile boolean mPreconditionFailed;
    private volatile boolean mUserPause;
    private volatile boolean mServerCanceled;
    private volatile boolean mUnknownError;
    private volatile boolean mIsWIFIRequire;
    private volatile boolean mFileBusyAfterRun;
    private volatile boolean mPreAllocateFailed;
    private volatile IOException realCause;


    DownloadCache(@NonNull MultiPointOutputStream outputStream) {
        this.mOutputStream = outputStream;
    }

    @NonNull
    MultiPointOutputStream getOutputStream() {
        return mOutputStream;
    }

    void setRedirectLocation(String redirectLocation) {
        this.mRedirectLocation = redirectLocation;
    }

    String getRedirectLocation() {
        return mRedirectLocation;
    }

    boolean isPreconditionFailed() {
        return mPreconditionFailed;
    }

    public boolean isUserCanceled() {
        return mUserPause;
    }

    boolean isServerCanceled() {
        return mServerCanceled;
    }

    boolean isUnknownError() {
        return mUnknownError;
    }

    boolean isFileBusyAfterRun() {
        return mFileBusyAfterRun;
    }

    boolean isPreAllocateFailed() {
        return mPreAllocateFailed;
    }

    boolean isWifiRequire() {
        return mIsWIFIRequire;
    }

    IOException getRealCause() {
        return realCause;
    }

    public boolean isInterrupt() {
        return mPreconditionFailed
                || mUserPause
                || mServerCanceled
                || mUnknownError
                || mFileBusyAfterRun
                || mPreAllocateFailed;
    }

    private void setPreconditionFailed(IOException realCause) {
        this.mPreconditionFailed = true;
        this.realCause = realCause;
    }

    void setUserCanceled() {
        this.mUserPause = true;
    }

    private void setFileBusyAfterRun() {
        this.mFileBusyAfterRun = true;
    }

    private void setServerCanceled(IOException realCause) {
        this.mServerCanceled = true;
        this.realCause = realCause;
    }

    void setUnknownError(IOException realCause) {
        this.mUnknownError = true;
        this.realCause = realCause;
    }

    private void setPreAllocateFailed(IOException realCause) {
        this.mPreAllocateFailed = true;
        this.realCause = realCause;
    }

    private void setIsWifiRequire(IOException realCause) {
        this.mIsWIFIRequire = true;
        this.realCause = realCause;
    }

    public void catchException(IOException e) {
        if (isUserCanceled())
            return; // ignored

        if (e instanceof ResumeFailedException) {
            setPreconditionFailed(e);
        } else if (e instanceof ServerCanceledException) {
            setServerCanceled(e);
        } else if (e == FileBusyAfterRunException.SIGNAL) {
            setFileBusyAfterRun();
        } else if (e instanceof PreAllocateException) {
            setPreAllocateFailed(e);
        } else if (e instanceof NetworkPolicyException) {
            setIsWifiRequire(e);
        } else if (e != InterruptException.SIGNAL) {
            setUnknownError(e);
        }
    }

    static class PreError extends DownloadCache {
        PreError(IOException realCause) {
            super(null);
            setUnknownError(realCause);
        }
    }
}
