package com.tzpt.cloudlibrary.zlibrary.core.opstions;

/**
 * 自定义Boolean类型的数据类型
 * Created by Administrator on 2017/4/8.
 */

public class ZLBooleanOption extends ZLOption{
    private final boolean myDefaultValue;

    public ZLBooleanOption(String group, String optionName, boolean defaultValue) {
        super(group, optionName, defaultValue ? "true" : "false");
        myDefaultValue = defaultValue;
    }

    public boolean getValue() {
        if (mySpecialName != null) {
            return myDefaultValue;
        } else {
            return "true".equals(getConfigValue());
        }
    }

    public void setValue(boolean value) {
        setConfigValue(value ? "true" : "false");
    }

}
