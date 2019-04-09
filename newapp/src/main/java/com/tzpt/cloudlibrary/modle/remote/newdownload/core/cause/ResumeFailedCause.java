package com.tzpt.cloudlibrary.modle.remote.newdownload.core.cause;

/**
 * Created by Administrator on 2018/8/6.
 */

public enum  ResumeFailedCause {
    INFO_DIRTY,
    FILE_NOT_EXIST,
    OUTPUT_STREAM_NOT_SUPPORT,
    RESPONSE_ETAG_CHANGED,
    RESPONSE_PRECONDITION_FAILED,
    RESPONSE_CREATED_RANGE_NOT_FROM_0,
    RESPONSE_RESET_RANGE_NOT_FROM_0,
    CONTENT_LENGTH_CHANGED,
}
