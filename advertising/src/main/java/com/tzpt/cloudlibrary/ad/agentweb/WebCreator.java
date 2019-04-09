package com.tzpt.cloudlibrary.ad.agentweb;

import android.webkit.WebView;
import android.widget.FrameLayout;

public interface WebCreator{
    WebCreator create();

    WebView getWebView();
}
