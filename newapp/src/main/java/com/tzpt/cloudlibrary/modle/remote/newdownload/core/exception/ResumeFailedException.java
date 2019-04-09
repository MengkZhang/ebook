package com.tzpt.cloudlibrary.modle.remote.newdownload.core.exception;

import com.tzpt.cloudlibrary.modle.remote.newdownload.core.cause.ResumeFailedCause;

import java.io.IOException;

/**
 * Created by Administrator on 2018/8/6.
 */

public class ResumeFailedException extends IOException{
    private final ResumeFailedCause resumeFailedCause;

    public ResumeFailedException(ResumeFailedCause cause) {
        super("Resume failed because of " + cause);
        this.resumeFailedCause = cause;
    }

    public ResumeFailedCause getResumeFailedCause() {
        return resumeFailedCause;
    }
}
