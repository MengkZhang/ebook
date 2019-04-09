package com.tzpt.cloundlibrary.manager.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 身份证验证
 * Created by ZhiqiangJia on 2017-11-27.
 */
public class IdCardInputFilter implements InputFilter {

    private Pattern mPattern;
    public static final String REGEX_ID_CARD = "^(\\d{17}[X|x]?)|(\\d{1,18})$";

    public IdCardInputFilter() {
        mPattern = Pattern.compile(REGEX_ID_CARD);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String sourceText = source.toString().toUpperCase();
        if (TextUtils.isEmpty(sourceText)) {
            return "";
        }
        String content = dest.toString() + sourceText;
        Matcher matcher = mPattern.matcher(content);
        if (!matcher.matches()) {
            return "";
        }
        return dest.subSequence(dstart, dend) + sourceText;
    }
}
