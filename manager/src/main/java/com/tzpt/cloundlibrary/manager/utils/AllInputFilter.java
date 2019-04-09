package com.tzpt.cloundlibrary.manager.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 过滤用户输入可以输入所有类型
 * Created by Administrator on 2017/7/13.
 */

public class AllInputFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        return source;
    }
}
