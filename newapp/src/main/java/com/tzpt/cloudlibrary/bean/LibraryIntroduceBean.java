package com.tzpt.cloudlibrary.bean;

import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibIntroduceVo;

/**
 * 图书馆介绍
 */
public class LibraryIntroduceBean {

    public BaseResultEntityVo<LibIntroduceVo> mLibIntroduceVo;
    public BaseResultEntityVo<LibInfoVo> mLibInfoVo;

    public LibraryIntroduceBean(BaseResultEntityVo<LibIntroduceVo> libIntroduceVo,
                                BaseResultEntityVo<LibInfoVo> libInfoVo) {
        this.mLibIntroduceVo = libIntroduceVo;
        this.mLibInfoVo = libInfoVo;
    }
}
