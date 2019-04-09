package com.tzpt.cloudlibrary.ad.agentweb;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

public final class AgentWeb {

    private Activity mActivity;
    private ViewGroup mViewGroup;

    private WebCreator mWebCreator;

    private WebChromeClient mWebChromeClient;
    private WebViewClient mWebViewClient;

    private WebLifeCycle mWebLifeCycle;

    private AgentWeb(AgentBuilder agentBuilder) {
        this.mActivity = agentBuilder.mActivity;
        this.mViewGroup = agentBuilder.mViewGroup;
        mWebCreator = configWebCreator(agentBuilder.mLayoutParams);
        this.mWebChromeClient = agentBuilder.mWebChromeClient;
        this.mWebViewClient = agentBuilder.mWebViewClient;
        mWebCreator.create().getWebView();
        this.mWebLifeCycle = new DefaultWebLifeCycleImpl(mWebCreator.getWebView());
        init();
    }


    public WebLifeCycle getWebLifeCycle() {
        return this.mWebLifeCycle;
    }


    public static AgentBuilder with(@NonNull Activity activity) {
        return new AgentBuilder(activity);
    }

    public void destroy() {
        this.mWebLifeCycle.onDestroy();
    }

    public static class PreAgentWeb {
        private AgentWeb mAgentWeb;
        private boolean isReady = false;

        PreAgentWeb(AgentWeb agentWeb) {
            this.mAgentWeb = agentWeb;
        }


        public PreAgentWeb ready() {
            if (!isReady) {
                mAgentWeb.ready();
                isReady = true;
            }
            return this;
        }

        public AgentWeb go(@Nullable String url) {
            if (!isReady) {
                ready();
            }
            return mAgentWeb.go(url);
        }
    }

    private WebCreator configWebCreator(ViewGroup.LayoutParams lp) {
        return new DefaultWebCreator(mActivity, mViewGroup, lp);
    }

    private AgentWeb go(String url) {
        mWebCreator.getWebView().loadUrl(url);
        return this;
    }

    private void init() {

    }

    private void ready() {
        AgentWebConfig.initCookiesManager(mActivity.getApplicationContext());
        IAgentWebSettings mAgentWebSettings = AbsAgentWebSettings.getInstance();
        mAgentWebSettings.toSetting(mWebCreator.getWebView());
        if (mWebChromeClient != null){
            mWebCreator.getWebView().setWebChromeClient(mWebChromeClient);
        }
        if (mWebViewClient != null){
            mWebCreator.getWebView().setWebViewClient(mWebViewClient);
        }

    }

    public boolean canGoBack() {
        return mWebCreator.getWebView().canGoBack();
    }

    public void goBack() {
        if (mWebCreator.getWebView() != null && mWebCreator.getWebView().canGoBack()) {
            mWebCreator.getWebView().goBack();
        }
    }

    public boolean canGoForward() {
        return mWebCreator.getWebView().canGoForward();
    }

    public void goForward() {
        if (mWebCreator.getWebView() != null && mWebCreator.getWebView().canGoForward()) {
            mWebCreator.getWebView().goForward();
        }
    }

    public void reload(){
        if (mWebCreator.getWebView() != null) {
            mWebCreator.getWebView().reload();
        }
    }

    public static final class AgentBuilder {
        private Activity mActivity;
        private ViewGroup mViewGroup;
        private ViewGroup.LayoutParams mLayoutParams = null;
        private WebViewClient mWebViewClient;
        private WebChromeClient mWebChromeClient;

        AgentBuilder(@NonNull Activity activity) {
            mActivity = activity;
        }

        public IndicatorBuilder setAgentWebParent(@NonNull ViewGroup v, @NonNull ViewGroup.LayoutParams lp) {
            this.mViewGroup = v;
            this.mLayoutParams = lp;
            return new IndicatorBuilder(this);
        }


        private PreAgentWeb buildAgentWeb() {
            return new PreAgentWeb(new AgentWeb(this));
        }
    }

    public static class IndicatorBuilder {
        private AgentBuilder mAgentBuilder = null;

        IndicatorBuilder(AgentBuilder agentBuilder) {
            this.mAgentBuilder = agentBuilder;
        }

        public CommonBuilder useDefaultIndicator() {
            return new CommonBuilder(mAgentBuilder);
        }
    }


    public static class CommonBuilder {
        private AgentBuilder mAgentBuilder;

        CommonBuilder(AgentBuilder agentBuilder) {
            this.mAgentBuilder = agentBuilder;
        }

        public CommonBuilder setWebChromeClient(@Nullable WebChromeClient webChromeClient) {
            this.mAgentBuilder.mWebChromeClient = webChromeClient;
            return this;

        }

        public CommonBuilder setWebViewClient(@Nullable WebViewClient webChromeClient) {
            this.mAgentBuilder.mWebViewClient = webChromeClient;
            return this;
        }

        public PreAgentWeb createAgentWeb() {
            return this.mAgentBuilder.buildAgentWeb();
        }

    }


}
