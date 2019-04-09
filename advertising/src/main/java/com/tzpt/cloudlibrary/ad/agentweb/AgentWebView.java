package com.tzpt.cloudlibrary.ad.agentweb;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.reflect.Field;

public class AgentWebView extends WebView {
    private boolean mIsInited;

    public AgentWebView(Context context) {
        this(context, null);
    }

    public AgentWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mIsInited = true;
    }

    @Override
    public final void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(client);
    }

    @Override
    public final void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(client);
    }

    @Override
    public void destroy() {
        setVisibility(View.GONE);
        removeAllViewsInLayout();
        fixedStillAttached();
        releaseConfigCallback();
        if (mIsInited) {
            super.destroy();
        }
    }

    @Override
    public void clearHistory() {
        if (mIsInited) {
            super.clearHistory();
        }
    }

    @Override
    public void setOverScrollMode(int mode) {
        super.setOverScrollMode(mode);
    }

    @Override
    public boolean isPrivateBrowsingEnabled() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && getSettings() == null) {
            return false; // getSettings().isPrivateBrowsingEnabled()
        } else {
            return super.isPrivateBrowsingEnabled();
        }
    }

    // Activity在onDestory时调用webView的destroy，可以停止播放页面中的音频
    private void fixedStillAttached() {
        ViewParent parent = getParent();
        if (parent instanceof ViewGroup) { // 由于自定义webView构建时传入了该Activity的context对象，因此需要先从父容器中移除webView，然后再销毁webView；
            ViewGroup mWebViewContainer = (ViewGroup) getParent();
            mWebViewContainer.removeAllViewsInLayout();
        }
    }

    // 解决WebView内存泄漏问题；
    private void releaseConfigCallback() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) { // JELLY_BEAN
            try {
                Field field = WebView.class.getDeclaredField("mWebViewCore");
                field = field.getType().getDeclaredField("mBrowserFrame");
                field = field.getType().getDeclaredField("sConfigCallback");
                field.setAccessible(true);
                field.set(null, null);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();

            } catch (IllegalAccessException e) {
                e.printStackTrace();

            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { // KITKAT
            try {
                Field sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
                if (sConfigCallback != null) {
                    sConfigCallback.setAccessible(true);
                    sConfigCallback.set(null, null);
                }
            } catch (NoSuchFieldException e) {

                e.printStackTrace();

            } catch (ClassNotFoundException e) {

                e.printStackTrace();

            } catch (IllegalAccessException e) {

                e.printStackTrace();

            }
        }
    }
}