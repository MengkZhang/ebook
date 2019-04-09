package com.tzpt.cloudlibrary.zlibrary.core.opstions;

/**
 * 自定义Integer类型的数据类型
 * Created by Administrator on 2017/4/7.
 */

public final class ZLIntegerRangeOption extends ZLOption {
    private static int valueInRange(int value, int min, int max) {
        return Math.min(max, Math.max(min, value));
    }

    public final int MinValue;
    public final int MaxValue;

    private int myValue;
    private String myStringValue;

    public ZLIntegerRangeOption(String group, String optionName, int minValue, int maxValue, int defaultValue) {
        super(group, optionName, String.valueOf(valueInRange(defaultValue, minValue, maxValue)));
        MinValue = minValue;
        MaxValue = maxValue;
    }

    public int getValue() {
        final String stringValue = getConfigValue();
        if (!stringValue.equals(myStringValue)) {
            myStringValue = stringValue;
            try {
                myValue = valueInRange(Integer.parseInt(stringValue), MinValue, MaxValue);
            } catch (NumberFormatException e) {
            }
        }
        return myValue;
    }

    public void setValue(int value) {
        value = valueInRange(value, MinValue, MaxValue);
        myValue = value;
        myStringValue = String.valueOf(value);
        setConfigValue(myStringValue);
    }
}
