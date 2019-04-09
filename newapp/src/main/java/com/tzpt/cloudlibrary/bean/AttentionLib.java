package com.tzpt.cloudlibrary.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/29.
 */

public class AttentionLib implements Serializable {
    public String mName;
    public String mLibCode;

    public AttentionLib(String name, String libCode) {
        mName = name;
        mLibCode = libCode;
    }
}
