package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/11/17.
 */

public class NoteModifyResultVo {
    @SerializedName("errorCode")
    @Expose
    public int errorCode;

    @SerializedName("noteId")
    @Expose
    public long noteId;

    @SerializedName("message")
    @Expose
    public String message;
}
