package com.tzpt.cloudlibrary.utils;

/**
 * ISBN验证
 * Created by ZhiqiangJia on 2017-03-23.
 */
public class ISBNUtil {

    public static boolean isISBN(String isbn) {
        String frontStr = isbn.substring(0, isbn.length() - 1);
        String backStr = isbn.substring(isbn.length() - 1);
        boolean isNum = frontStr.matches("[0-9]+");
        if (!isNum || !(frontStr.length() == 9 || frontStr.length() == 12)) {
            return false;
        }
        char[] tmp = frontStr.toCharArray();
        int sum = 0;
        int count = 10;
        if (frontStr.length() == 9) {//验证10位的ISBN
            for (int i = 0; i < 9; i++) {
                int dd = Integer.parseInt(tmp[i] + "");
                sum = sum + count * dd;
                count--;
            }
            int n = 11 - sum % 11;
            String s = "";
            if (n == 11) {
                s = "0";
            } else if (n == 10) {
                s = "x";
            } else {
                s = "" + n;
            }
            if (backStr.toLowerCase().equals(s)) {
                return true;
            } else {
                return false;
            }
        } else if (frontStr.length() == 12) {//验证13位的ISBN
            String str = isbn.substring(0, 3);
            if (!(str.equals("979") || str.equals("978"))) {
                return false;
            }
            for (int i = 0; i < 12; i++) {
                int dd = Integer.parseInt(tmp[i] + "");
                if (i % 2 == 0) {
                    sum = sum + 1 * dd;
                } else {
                    sum = sum + 3 * dd;
                }
            }
            String s = "" + (10 - sum % 10);
            if (backStr.equals("0") && s.equals("10")) {
                return true;
            }
            if (backStr.equals(s)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}