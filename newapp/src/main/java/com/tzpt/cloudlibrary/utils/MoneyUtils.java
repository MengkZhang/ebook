package com.tzpt.cloudlibrary.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZhiqiangJia on 2017-08-23.
 */

public class MoneyUtils {

    /**
     * @param value1
     * @param value2
     * @return
     */
    public static double add(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return b1.add(b2).doubleValue();
    }

    /**
     * @param value1
     * @param value2
     * @return
     */
    public static double sub(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return b1.subtract(b2).doubleValue();
    }

    public static String addToStr(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return formatMoney(b1.add(b2).doubleValue());
    }

    /**
     * 格式化金额
     *
     * @param money
     * @return
     */
    public static String formatMoney(double money) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(money);
    }

    /**
     * 格式化金额
     *
     * @param money
     * @return
     */
    public static String formatMoney(String money) {
        double moneyD = stringToDouble(money);
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(moneyD);
    }

    /**
     * 格式化金额
     *
     * @param money
     * @return
     */
    public static double stringToDouble(String money) {
        if (TextUtils.isEmpty(money)) {
            return 0.00;
        }
        try {
            return Double.valueOf(money);
        } catch (Exception e) {
            return 0.00;
        }
    }

    //金额验证
    public static boolean isMoney(String money) {
        if (money.endsWith(".")) {
            return false;
        }
        // 判断小数点后2位的数字的正则表达式
        Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
        Matcher match = pattern.matcher(money);
        return match.matches();
    }
}
