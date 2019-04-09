package com.tzpt.cloudlibrary.zlibrary.core.opstions;

/**
 * Created by Administrator on 2017/4/7.
 */

public final class ZLStringOption extends ZLOption{
    public ZLStringOption(String group, String optionName, String defaultValue) {
        super(group, optionName, defaultValue);
    }

    public String getValue() {
        if (mySpecialName != null) {
            return myDefaultStringValue;
        } else {
            return getConfigValue();
        }
    }

    public void setValue(String value) {
        if (value == null) {
            return;
        }
        setConfigValue(value);
    }

    public void saveSpecialValue() {
//        if (mySpecialName != null && Config.Instance().isInitialized()) {
//            Config.Instance().setSpecialStringValue(mySpecialName, getValue());
//        }
    }
}
