package com.tzpt.cloudlibrary.modle.remote.newpojo;

/**
 * Created by Administrator on 2018/1/24.
 */

public class UserInfoMoreVo extends BaseDataResultVo {
    public BaseResultEntityVo<UserInfoVo> userInfo;
    public BaseResultEntityVo<UnreadMsgCountVo> unReadMsgCount;
    public BaseResultEntityVo<UnreadOverdueMsgCountVo> unReadOverdueMsgCount;

    public UserInfoMoreVo(BaseResultEntityVo<UserInfoVo> o1, BaseResultEntityVo<UnreadMsgCountVo> o2,
                          BaseResultEntityVo<UnreadOverdueMsgCountVo> o3) {
        this.userInfo = o1;
        this.unReadMsgCount = o2;
        this.unReadOverdueMsgCount = o3;
    }
}
