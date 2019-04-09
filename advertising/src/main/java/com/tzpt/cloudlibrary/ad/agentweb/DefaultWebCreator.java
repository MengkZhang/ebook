package com.tzpt.cloudlibrary.ad.agentweb;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.tzpt.cloudlibrary.ad.R;

public class DefaultWebCreator implements WebCreator {

    private Activity mActivity;
    private ViewGroup mViewGroup;
    private ViewGroup.LayoutParams mLayoutParams = null;
    private boolean mIsCreated = false;
    private WebView mWebView = null;

    DefaultWebCreator(@NonNull Activity activity, @Nullable ViewGroup viewGroup, ViewGroup.LayoutParams lp) {
        this.mActivity = activity;
        this.mViewGroup = viewGroup;
        this.mLayoutParams = lp;
    }

    @Override
    public DefaultWebCreator create() {
        if (mIsCreated) {
            return this;
        }
        mIsCreated = true;
        ViewGroup mViewGroup = this.mViewGroup;
        if (mViewGroup == null) {
            mViewGroup =  createLayout();
            mActivity.setContentView(mViewGroup);
        } else {
            mViewGroup.addView(createLayout(), mLayoutParams);
        }
        return this;
    }

    @Override
    public WebView getWebView() {
        return mWebView;
    }


    private ViewGroup createLayout() {
        Activity mActivity = this.mActivity;
        WebParentLayout mFrameLayout = new WebParentLayout(mActivity);
        mFrameLayout.setId(R.id.web_parent_layout_id);
        mFrameLayout.setBackgroundColor(Color.WHITE);
        View target = this.mWebView = createWebView();
        FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(-1, -1);
        mFrameLayout.addView(target, mLayoutParams);
        mFrameLayout.bindWebView(this.mWebView);
        if (this.mWebView instanceof AgentWebView) {
            AgentWebConfig.WEBVIEW_TYPE = AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE;
        }
        ViewStub mViewStub = new ViewStub(mActivity);
        mViewStub.setId(R.id.mainframe_error_viewsub_id);
        mFrameLayout.addView(mViewStub, new FrameLayout.LayoutParams(-1, -1));
        return mFrameLayout;
    }

    private WebView createWebView() {
        WebView mWebView;
        if (this.mWebView != null) {
            mWebView = this.mWebView;
            AgentWebConfig.WEBVIEW_TYPE = AgentWebConfig.WEBVIEW_CUSTOM_TYPE;
        } else if (AgentWebConfig.IS_KITKAT_OR_BELOW_KITKAT) {
            mWebView = new AgentWebView(mActivity);
            AgentWebConfig.WEBVIEW_TYPE = AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE;
        } else {
            mWebView = new WebView(mActivity);
            AgentWebConfig.WEBVIEW_TYPE = AgentWebConfig.WEBVIEW_DEFAULT_TYPE;
        }
        return mWebView;
    }
}
