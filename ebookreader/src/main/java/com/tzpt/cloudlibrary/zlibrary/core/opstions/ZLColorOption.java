package com.tzpt.cloudlibrary.zlibrary.core.opstions;

import com.tzpt.cloudlibrary.zlibrary.core.util.ZLColor;

/**
 * Created by Administrator on 2017/4/8.
 */

public final class ZLColorOption extends ZLOption {
    private ZLColor myValue;
    private String myStringValue;

    private static String stringColorValue(ZLColor color) {
        return String.valueOf(color != null ? color.intValue() : -1);
    }

    public ZLColorOption(String group, String optionName, ZLColor defaultValue) {
        super(group, optionName, stringColorValue(defaultValue));
    }

    public ZLColor getValue() {
        final String stringValue = getConfigValue();
        if (!stringValue.equals(myStringValue)) {
            myStringValue = stringValue;
            try {
                final int intValue = Integer.parseInt(stringValue);
                myValue = intValue != -1 ? new ZLColor(intValue) : null;
            } catch (NumberFormatException e) {
            }
        }
        return myValue;
    }

    public void setValue(ZLColor value) {
        if (value == null) {
            return;
        }
        myValue = value;
        myStringValue = stringColorValue(value);
        setConfigValue(myStringValue);
    }
}
