package com.tzpt.cloudlibrary.ad.agentweb;

import android.webkit.WebView;

public interface IAgentWebSettings<T extends android.webkit.WebSettings> {

    IAgentWebSettings toSetting(WebView webView);

    T getWebSettings();

}
