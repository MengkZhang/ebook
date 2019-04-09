package com.tzpt.cloundlibrary.manager.bean;

import android.support.annotation.NonNull;

/**
 * Created by Administrator on 2018/9/10.
 */

public class SingleSelectionConditionBean {
    @NonNull
    private final String mKey;
    @NonNull
    private final String mValue;
    @NonNull
    private final String mName;

    public SingleSelectionConditionBean(@NonNull String key, @NonNull String value, @NonNull String name) {
        mKey = key;
        mValue = value;
        mName = name;
    }

    public String getKey() {
        return mKey;
    }

    public String getValue() {
        return mValue;
    }

    public String getName() {
        return mName;
    }
}
