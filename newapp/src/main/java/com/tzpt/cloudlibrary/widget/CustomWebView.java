package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tzpt.cloudlibrary.utils.CheckInternetUtil;

/**
 * 自定义webView 网络视图
 * Created by ZhiqiangJia on 2017-05-15.
 */
public class CustomWebView extends WebView {

    public CustomWebView(Context context) {
        super(context);
        initWebSettings();
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWebSettings();
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWebSettings();
    }

    /**
     * 配置webView
     */
    private void initWebSettings() {
        WebSettings settings = getSettings();
        settings.setSupportZoom(false);                 //WebView是否支持使用屏幕上的缩放控件和手势进行缩放，默认值true。
        settings.setBuiltInZoomControls(false);         //是否使用内置的缩放机制
        settings.setDisplayZoomControls(false);         //使用内置的缩放机制时是否展示缩放控件，默认值true
        settings.setJavaScriptEnabled(true);            //设置WebView是否允许执行JavaScript脚本，默认false，不允许
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//让JavaScript自动打开窗口，默认false
        settings.setDomStorageEnabled(true);            //DOM存储API是否可用，默认false
        //重写使用缓存的方式，默认值LOAD_DEFAULT
//        if (CheckInternetUtil.isInternetAvailable()) {
//            settings.setCacheMode(WebSettings.LOAD_DEFAULT);            //默认模式
//        } else {
//            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//缓存模式
//        }
        //不使用缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setUserAgentString("androidYtsg");
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//设置布局，会引起WebView的重新布局（relayout）,默认值NARROW_COLUMNS
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);    //不建议调整线程优先级，未来版本不会支持这样做
        settings.setAppCacheEnabled(true);                              //应用缓存API是否可用，默认值false
        settings.setAppCacheMaxSize(1024 * 1024 * 8);                   //设置缓存大小
        settings.setDefaultTextEncodingName("utf-8");                   //设置应用缓存内容的最大值
        settings.setLoadsImagesAutomatically(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            settings.setUseWideViewPort(true);                          //WebView是否支持HTML的“viewport”标签或者使用wide viewport。
            settings.setLoadWithOverviewMode(true);                     //是否允许WebView度超出以概览的方式载入页面，默认false
        }
        //在Android 5.0上 WebView 设置允许加载 Http 与 Https 混合内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //当一个安全的来源（origin）试图从一个不安全的来源加载资源时配置WebView的行为
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //
        this.setWebViewClient(new CustomWebViewClient());
        this.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (title.contains("404")) {
                    view.stopLoading();
                    if (null != mLoadingListener) {
                        mLoadingListener.onPageLoadingError();
                    }
                }
            }
        });
    }

    /**
     * 加载网页
     *
     * @param url
     */
    public void loadWebUrl(String url) {
        this.loadUrl(url);
    }

    private boolean mScrollAble = false;

    public void setScrollAble(boolean able) {
        this.mScrollAble = able;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.resumeTimers();

    }

    @Override
    public void onPause() {
        super.onPause();
        this.pauseTimers();
    }

    /**
     * 销毁webView
     */
    public void destroyWebView() {
        if (!mIsLoaded) {
            removeCallbacks(runnable);
        }
        loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        clearHistory();
        loadUrl("about:blank");
        stopLoading();
        setWebChromeClient(null);
        setWebViewClient(null);
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(false);
        removeAllViewsInLayout();
        removeAllViews();
        destroyDrawingCache();
        destroy();
    }

    private boolean mIsLoaded = false;

    private class CustomWebViewClient extends WebViewClient {

        CustomWebViewClient() {
            super();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (null != mLoadingListener) {
                mLoadingListener.onPageLoadingError();
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (null != mLoadingListener) {
                mLoadingListener.onPageLoadingError();
            }
        }

        /**
         * 多页面在同一个WebView中打开，就是不新建activity或者调用系统浏览器打开
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //Android8.0以下的需要返回true 并且需要loadUrl；8.0之后效果相反
            if (Build.VERSION.SDK_INT < 26) {
                view.loadUrl(url);
                return true;
            }
            return false;
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mIsLoaded = false;
            // 当webView加载30秒后强制回馈完成-设置为超时时间
            view.postDelayed(runnable, 30000);
            if (null != mLoadingListener) {
                mLoadingListener.onPageStarted();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //加载完成，则延时加载error 失效
            if (null != mLoadingListener) {
                if (!CheckInternetUtil.isInternetAvailable()) {
                    mLoadingListener.onPageLoadingError();
                    mIsLoaded = false;
                    return;
                }
                mLoadingListener.onPageFinished(true);
                mIsLoaded = true;
                view.removeCallbacks(runnable);
            }
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mIsLoaded) {
                return;
            }
            if (null != mLoadingListener) {
                mLoadingListener.onPageLoadingError();
            }
        }
    };
    private CallbackWebViewLoading mLoadingListener;

    public void setLoadingListener(CallbackWebViewLoading loadingListener) {
        this.mLoadingListener = loadingListener;
    }

    public interface CallbackWebViewLoading {

        void onPageStarted();

        void onPageFinished(boolean hasContent);

        void onPageLoadingError();
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        invalidate();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 使WebView不可滚动
     */
    @Override
    public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY,
                                int scrollRangeX, int scrollRangeY, int maxOverScrollX,
                                int maxOverScrollY, boolean isTouchEvent) {
        if (mScrollAble) {
            return super.overScrollBy(0, deltaY, scrollX, scrollY, scrollRangeX,
                    scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
        }
        return false;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(0, 0);
    }

    /**
     * 清除WebView 缓存
     */
    public void clearWebCache() {
        clearHistory();
        clearCache(true);
    }

}
