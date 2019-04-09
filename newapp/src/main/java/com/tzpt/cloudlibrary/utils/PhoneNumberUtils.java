package com.tzpt.cloudlibrary.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZhiqiangJia on 2017-08-18.
 */

public class PhoneNumberUtils {
    /**
     * 验证手机号是否合法
     * <p/>
     * 中国移动号段：134、135、136、137、138、139、150、151、152、157、158、159、147、182、183、184、187、188、1705、178
     * 中国联通号段：130、131、132、145（145属于联通无线上网卡号段）、155、156、185、186 、176、1709、
     * 中国电信号段：133 、153 、180 、181 、189、1700、177
     *
     * @param mobiles
     * @return boolean
     */
    public static boolean isMobileNumber(String mobiles) {
        Pattern pattern = Pattern
                .compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(mobiles);
        return matcher.matches();
    }


    /**
     * 设置电话号码
     *
     * @param phone
     */
    public static String setPhoneNumberInfo(String phone) {
        if (!TextUtils.isEmpty(phone) && phone.length() >= 11) {
            String myNumber = phone.substring(3, 7);
            return phone.replace(myNumber, "***");
        }
        return phone;
    }
}
