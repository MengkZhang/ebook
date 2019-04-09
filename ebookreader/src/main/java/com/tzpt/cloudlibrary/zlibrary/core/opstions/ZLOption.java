package com.tzpt.cloudlibrary.zlibrary.core.opstions;

import android.text.TextUtils;

/**
 * Created by Administrator on 2017/4/7.
 */

abstract class ZLOption {
    String myDefaultStringValue;
    String mySpecialName;
    private String mOptionName;

    ZLOption(String group, String optionName, String defaultStringValue) {
        myDefaultStringValue = defaultStringValue != null ? defaultStringValue : "";
        mOptionName = optionName;
    }

    final String getConfigValue() {
        String configStr = SharedPreferencesUtil.getInstance().getString(mOptionName);
        return TextUtils.isEmpty(configStr) ? myDefaultStringValue : configStr;
    }

    final void setConfigValue(String value) {
        //使用SharePreference保存
        SharedPreferencesUtil.getInstance().putString(mOptionName, value);
    }
}
