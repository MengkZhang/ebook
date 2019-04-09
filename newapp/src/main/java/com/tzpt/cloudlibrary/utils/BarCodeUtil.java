package com.tzpt.cloudlibrary.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/7/10.
 */

public class BarCodeUtil {

    public static boolean isBarCode(String result) {
        String regEx = "^[A-Z]{4}-[0-9]{7}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(result);
        return matcher.matches();
    }


    /**
     * 执行自动补全
     *
     * @param barNumber
     */
    public static String performAutomaticCompletion(String barNumber) {
        //1.是否为空
        if (null == barNumber || TextUtils.isEmpty(barNumber)) {
            return barNumber;
        }
        //2.判断前4位是否字母
        if (barNumber.length() <= 4) {
            return barNumber;
        }
        // 3.获取4位以后的数字
        String content = barNumber.substring(4);
        String libCode = barNumber.substring(0, 4);
        StringBuffer buffer = new StringBuffer();
        if (content.length() < 7) {
            buffer.append(libCode);
            int length = content.length();
            for (int i = 0; i < 7 - length; i++) {
                buffer.append("0");
            }
            buffer.append(content);
            return buffer.toString();
        } else {
            //超过7位，保留后7位数字
            buffer.append(libCode);
            String newContent = content.substring(content.length() - 7, content.length());
            buffer.append(newContent);
            return buffer.toString();
        }
    }

    /**
     * 验证输入格式
     *
     * @param barNumber
     * @return
     */
    public static boolean isVerifycode(String barNumber) {
        String regEx = "^[A-Z]{4}[0-9]{7}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(barNumber);
        return matcher.matches();
    }
}
