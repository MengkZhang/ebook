package com.tzpt.cloudlibrary.modle.remote.newdownload.core.cause;

/**
 * Created by Administrator on 2018/8/6.
 */

public enum EndCause {
    COMPLETED,
    ERROR,
    CANCELED,
    FILE_BUSY,
    SAME_TASK_BUSY,
    PRE_ALLOCATE_FAILED,
    WIFI_REQUIRE
}
