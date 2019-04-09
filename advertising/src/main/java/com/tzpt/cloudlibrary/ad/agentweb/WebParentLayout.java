package com.tzpt.cloudlibrary.ad.agentweb;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class WebParentLayout extends FrameLayout {
    private WebView mWebView;

    WebParentLayout(@NonNull Context context) {
        this(context, null);
    }

    WebParentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    WebParentLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("WebParentLayout context must be activity or activity sub class .");
        }
    }

    void bindWebView(WebView view) {
        if (this.mWebView == null) {
            this.mWebView = view;
        }
    }
}
