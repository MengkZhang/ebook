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
public class CapitalLettersTypeInputFilter implements InputFilter {
    private Pattern mPattern;

    /**
     * @param inputType
     */
    public CapitalLettersTypeInputFilter(int inputType) {
        if (inputType == 0) {   //0借书1还书
            this.mPattern = Pattern.compile("^[J|j]$|^[J|j][S|s][0-9]{0,8}$|^[0-9]{0,8}");
        } else {
            this.mPattern = Pattern.compile("(^[H|h]$)|(^[H|h][S|s]?\\d{0,8}$)");
        }
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String sourceText = source.toString();
        String destText = dest.toString();

        if (TextUtils.isEmpty(sourceText)) {
            return "";
        }

        StringBuilder sb = new StringBuilder(destText);
        sb.insert(dstart, sourceText);
        Matcher matcher = mPattern.matcher(sb.toString());
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
