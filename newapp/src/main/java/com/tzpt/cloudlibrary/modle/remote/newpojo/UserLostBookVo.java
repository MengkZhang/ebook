package com.tzpt.cloudlibrary.modle.remote.newpojo;

/**
 * 读者赔书信息
 * Created by ZhiqiangJia on 2018-02-03.
 */
public class UserLostBookVo {

    public BaseResultEntityVo<UserDepositModuleVo> mUserDepositModuleVo;
    public BaseResultEntityVo<PlatformBorrowBooksVo> mResultEntityVo;

    public UserLostBookVo(BaseResultEntityVo<UserDepositModuleVo> userDepositModuleVoBaseResultEntityVo, BaseResultEntityVo<PlatformBorrowBooksVo> resultEntityVo) {
        this.mUserDepositModuleVo = userDepositModuleVoBaseResultEntityVo;
        this.mResultEntityVo = resultEntityVo;
    }
}
