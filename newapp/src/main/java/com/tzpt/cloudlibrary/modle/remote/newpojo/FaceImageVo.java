package com.tzpt.cloudlibrary.modle.remote.newpojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 人脸图像信息
 * Created by tonyjia on 2018/3/13.
 */
public class FaceImageVo {

    @SerializedName("readerFaceImage")
    @Expose
    public String readerFaceImage;

    @SerializedName("errorCode")
    @Expose
    public int errorCode;
}
