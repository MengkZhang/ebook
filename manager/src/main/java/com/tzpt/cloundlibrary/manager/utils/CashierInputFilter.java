package com.tzpt.cloundlibrary.manager.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 过滤用户输入只能为金额格式
 * Created by Administrator on 2017/7/13.
 */

public class CashierInputFilter implements InputFilter {
    private Pattern mPattern;

    /**
     * 金额输入过滤器
     * @param hasNav 是否有负数
     */
    public CashierInputFilter(boolean hasNav) {
        if (hasNav) {
            mPattern = Pattern.compile("(^(-?(\\d{0,7}$)|(^-?(\\d{0,7}\\.\\d{0,2})))$)");
        } else {
            mPattern = Pattern.compile("(^\\d{1,7}$)|(^\\d{1,7}\\.\\d{0,2}$)");
        }
    }

    /**
     * @param source 新输入的字符串
     * @param start  新输入的字符串起始下标，一般为0
     * @param end    新输入的字符串终点下标，一般为source长度-1
     * @param dest   输入之前文本框内容
     * @param dstart 原内容起始坐标，一般为0
     * @param dend   原内容终点坐标，一般为dest长度-1
     * @return 输入内容
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String sourceText = source.toString();
        String destText = dest.toString();

        //验证删除等按键
        if (TextUtils.isEmpty(sourceText)) {
            return "";
        }
        StringBuilder sb = new StringBuilder(destText);
        sb.insert(dstart, sourceText);
        Matcher matcher = mPattern.matcher(sb.toString());
        if (!matcher.matches()) {
            return "";
        }
        return dest.subSequence(dstart, dend) + sourceText;
    }
}
