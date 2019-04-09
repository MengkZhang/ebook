package com.tzpt.cloudlibrary.bean;

import java.io.Serializable;

/**
 * Created by tonyjia on 2019/1/7.
 */
public class CommentChildBean implements Serializable {

    public String mReplyName;       //回复名称
    public String mRepliedName;     //被回复名称
    public String mReplyContent;    //回复内容

    public CommentChildBean() {
    }

    public CommentChildBean(String replyName, String repliedName, String replyContent) {
        this.mReplyName = replyName;
        this.mRepliedName = repliedName;
        this.mReplyContent = replyContent;
    }
}
