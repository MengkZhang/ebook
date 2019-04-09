package com.tzpt.cloudlibrary.utils;

import android.support.v4.util.ArrayMap;

/**
 * Created by ZhiqiangJia on 2017-08-18.
 */

public class IdCardUtils {


    /**
     * 验证是否身份证号码
     *
     * @param idcard
     * @return
     */
    public static boolean verifyIdCardNumber(String idcard) {
        if (idcard.length() != 18) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            if (isNumeric(idcard.substring(i, i + 1))) {
                int index = Integer.parseInt(idcard.substring(i, i + 1));
                double x = Math.pow(2, 17 - (double) i);
                int y = (int) (x % 11);
                sum += index * y;
            } else {
                return false;
            }

        }
        String lastIndex = idcard.substring(17, 18);
        int x = sum % 11;
        ArrayMap<Integer, String> maps = getIdCardMap();

        if (maps.get(x).equals(lastIndex)) {
            return true;
        }

        return false;
    }

    /**
     * 获取idcard字典
     *
     * @return
     */
    private static ArrayMap<Integer, String> getIdCardMap() {
        ArrayMap<Integer, String> maps = new ArrayMap<Integer, String>();
        maps.clear();
        maps.put(0, "1");
        maps.put(1, "0");
        maps.put(2, "X");
        maps.put(3, "9");
        maps.put(4, "8");
        maps.put(5, "7");
        maps.put(6, "6");
        maps.put(7, "5");
        maps.put(8, "4");
        maps.put(9, "3");
        maps.put(10, "2");
        return maps;
    }
    /**
     * 判断是否数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (null == str) {
            return false;
        }
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
