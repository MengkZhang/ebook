package com.tzpt.cloundlibrary.manager.utils;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字母大写过滤工具
 * Created by ZhiqiangJia on 2017-11-27.
 */
public class CapitalLettersInputFilter implements InputFilter {
    private Pattern mPattern;

    public CapitalLettersInputFilter(boolean hasNumber) {
        if (hasNumber) {
            this.mPattern = Pattern.compile("([A-Za-z]{0,2})\\d{0,10}");//只能输入字母和数字
        } else {
            this.mPattern = Pattern.compile("([A-Za-z]{0,10})");//只能输入字母
        }
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String sourceText = source.toString();
        if (TextUtils.isEmpty(sourceText)) {
            return "";
        }
        Matcher matcher = mPattern.matcher(source);
        if (!matcher.matches()) {
            return "";
        }
        for (int i = start; i < end; i++) {
            if (Character.isLowerCase(source.charAt(i))) {
                char[] v = new char[end - start];
                TextUtils.getChars(source, start, end, v, 0);
                String s = new String(v).toUpperCase();

                if (source instanceof Spanned) {
                    SpannableString sp = new SpannableString(s);
                    TextUtils.copySpansFrom((Spanned) source,
                            start, end, null, sp, 0);
                    return sp;
                } else {
                    return s;
                }
            }
        }

        return null; // keep original
    }
}
