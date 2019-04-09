package com.tzpt.cloundlibrary.manager.utils;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * Created by Administrator on 2017/7/6.
 */
public class StringUtils {
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

    private static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 验证邮箱
     *
     * @param eMail
     * @return true是邮箱 false不是邮箱
     */
    public static boolean isEMail(String eMail) {
        return Pattern.matches(REGEX_EMAIL, eMail);
    }

    /**
     * 是否是手机号码或者电话号码
     *
     * @param phoneNumber
     * @return
     */
    public static boolean petternPhoneNumber(String phoneNumber) {
        if (null == phoneNumber || TextUtils.isEmpty(phoneNumber)) {
            return false;
        }
        Pattern pattern = Pattern
                .compile("^((1[3|4|5|7|8]\\d{9})|(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8}))$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    /**
     * 验证是否身份证号码
     *
     * @param idCard
     * @return
     */
    public static boolean verifyIdCardNumber(String idCard) {
        if (TextUtils.isEmpty(idCard) || idCard.length() != 18) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            if (isNumeric(idCard.substring(i, i + 1))) {
                int index = Integer.parseInt(idCard.substring(i, i + 1));
                double x = Math.pow(2, 17 - (double) i);
                int y = (int) (x % 11);
                sum += index * y;
            } else {
                return false;
            }

        }
        String lastIndex = idCard.substring(17, 18);
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
        if (null == str || str.length() == 0) {
            return false;
        }
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * double转换为String
     *
     * @param value
     * @return
     */
    public static String doubleToString(double value) {
        // 这里面的bd是自定义的变量，即最后取得小数点后若干位的数，2表示小数点后两位
        BigDecimal bd = new BigDecimal(String.valueOf(value));
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        String string = bd.toString();
        return string;
    }

    /**
     * String 转换为 double
     *
     * @param value
     * @return
     */
    public static double StringFormatToDouble2bit(String value) {
        if (null == value || TextUtils.isEmpty(value)) {
            return 0.00;
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        double toDouble = bd.doubleValue();
        return toDouble;
    }

    public static String StringFormatToDouble2bit2(String value) {
        if (null == value || TextUtils.isEmpty(value)) {
            return "0.00";
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        String toDouble = bd.toString();
        return toDouble;
    }

    /**
     * 读者身份证信息
     *
     * @param idCard
     * @return
     */
    public static String setIdCardNumberForReader(String idCard) {
        if (TextUtils.isEmpty(idCard) || idCard.length() != 18) {
            return idCard;
        }
        String myIdCard = idCard.substring(0, 12);
        return idCard.replace(myIdCard, "*");
    }


    /**
     * 执行自动补全
     *
     * @param barNumber
     */
    public static String performAutomaticCompletion(String barNumber) {
        if (barNumber.length() >= 5 && barNumber.substring(0, 5).matches("[a-zA-Z]+")) {
            return dealNumber(barNumber, 5);
        } else if (barNumber.length() >= 4 && barNumber.substring(0, 4).matches("[a-zA-Z]+")) {
            return dealNumber(barNumber, 4);
        } else {
            return barNumber;
        }
    }

    /**
     * 处理条码号中的数字
     *
     * @param barNumber 条码号
     * @param index     索引值
     * @return 数字
     */
    private static String dealNumber(String barNumber, int index) {
        String content = barNumber.substring(index);
        String libCode = barNumber.substring(0, index);
        StringBuilder buffer = new StringBuilder();
        if (content.length() < 7) {
            buffer.append(libCode);

            final int length = content.length();
            CharSequence cs = new CharSequence() {
                @Override
                public int length() {
                    return 7 - length;
                }

                @Override
                public char charAt(int index) {
                    return '0';
                }

                @Override
                public CharSequence subSequence(int start, int end) {
                    return null;
                }
            };
            buffer.append(cs);
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

    public static String getLibCode(String barNumber) {
        if (barNumber.length() >= 5 && barNumber.substring(0, 5).matches("[a-zA-Z]+")) {
            return barNumber.substring(0, 5);
        } else if (barNumber.length() >= 4 && barNumber.substring(0, 4).matches("[a-zA-Z]+")) {
            return barNumber.substring(0, 4);
        } else {
            return null;
        }
    }

    public static String getBarCode(String barNumber) {
        if (barNumber.contains("-")) {
            barNumber = barNumber.replace("-", "");
        }
        if (barNumber.length() >= 5 && barNumber.substring(0, 5).matches("[a-zA-Z]+")) {
            return barNumber.substring(5);
        } else if (barNumber.length() >= 4 && barNumber.substring(0, 4).matches("[a-zA-Z]+")) {
            return barNumber.substring(4);
        } else {
            return null;
        }
    }


    /**
     * 验证输入格式
     *
     * @param barNumber
     * @return
     */
    public static boolean isVerifycode(String barNumber) {
        barNumber = barNumber.replace("-", "");
        String regEx4 = "^[A-Z]{4}[0-9]{7}$";
        Pattern pattern4 = Pattern.compile(regEx4);
        Matcher matcher4 = pattern4.matcher(barNumber);
        if (matcher4.matches()) {
            return true;
        } else {
            String regEx5 = "^[A-Z]{5}[0-9]{7}$";
            Pattern pattern5 = Pattern.compile(regEx5);
            Matcher matcher5 = pattern5.matcher(barNumber);
            return matcher5.matches();
        }

    }

    /**
     * 是否金额到小数点2位
     *
     * @param money
     * @return
     */
    public static boolean patternMoney(String money) {
        if (null == money || TextUtils.isEmpty(money)) {
            return false;
        }
        Pattern pattern = Pattern
                .compile("^\\d{1,}$|^\\d{1,}.\\d{1,2}$");
        Matcher matcher = pattern.matcher(money);
        return matcher.matches();
    }

    /**
     * 注册读者身份证号码
     *
     * @param idCard
     * @return
     */
    public static String setIdCardNumberForRegisterReader(String idCard) {
        if (TextUtils.isEmpty(idCard) || idCard.length() != 18) {
            return idCard;
        }
        String myIdCard = idCard.substring(10, 16);
        return idCard.replace(myIdCard, "**");
    }

    /**
     * 设置身份证号码
     *
     * @param idCard
     */
    public static String setIdCardNumber(String idCard) {
        if (TextUtils.isEmpty(idCard) || idCard.length() != 18) {
            return idCard;
        }
        String myIdCard = idCard.substring(0, idCard.length() - 10);
        return idCard.replace(myIdCard, "****");
    }

    /**
     * 精确到小数点N位
     *
     * @param value
     * @param bit
     * @return
     */
    public static String doubleToString(double value, int bit) {
        // 这里面的bd是自定义的变量，即最后取得小数点后若干位的数，2表示小数点后两位
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(bit, BigDecimal.ROUND_HALF_UP);
        String string = bd.toString();
        return string;
    }


    /**
     * 设置图书馆地址信息
     *
     * @param address
     * @return
     */
    public static String setLibraryAddressInfo(String address) {
        if (null == address || TextUtils.isEmpty(address)) {
            return "";
        }
        if (address.length() < 13) {
            return address;
        }
        if (address.contains("(") || address.contains(")")) {
            address = address.replace("(", "").replace(")", "");
        }
        String myAddress = address.substring(6, address.length() - 6);
        return address.replace(myAddress, "*");
    }


}
