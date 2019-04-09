package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.widget.CustomWebView;
import com.tzpt.cloundlibrary.manager.widget.MultiStateLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 充值协议
 */
public class LibraryHelpWebDetailActivity extends BaseActivity {

    private static final String HELP_TITLE = "help_web_title";
    private static final String HELP_URL = "help_web_url";

    public static void startActivity(Context context, String title, String url) {
        Intent intent = new Intent(context, LibraryHelpWebDetailActivity.class);
        intent.putExtra(HELP_TITLE, title);
        intent.putExtra(HELP_URL, url);
        context.startActivity(intent);
    }
    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.custom_webview)
    CustomWebView mCustomWebView;

    @OnClick({R.id.titlebar_left_btn})
    public void onViewClicked() {
        finish();
    }
    View.OnClickListener mRetryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            initDatas();
        }
    };
    private CustomWebView.CallbackWebViewLoading mCallbackWebViewLoading = new CustomWebView.CallbackWebViewLoading(){
        @Override
        public void onPageStarted() {
            if (null != mMultiStateLayout) {
                mMultiStateLayout.showProgress();
            }
        }

        @Override
        public void onPageFinished(boolean hasContent) {
            if (null != mMultiStateLayout) {
                mMultiStateLayout.showContentView();
            }
        }

        @Override
        public void onPageLoadingError() {
            if (null != mMultiStateLayout) {
                mMultiStateLayout.showError();
                mMultiStateLayout.showRetryError(mRetryClickListener);
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_library_help_web;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);

    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        String title = intent.getStringExtra(HELP_TITLE);
        String url = intent.getStringExtra(HELP_URL);
        mCommonTitleBar.setTitle("帮助 - " + title);

        mCustomWebView.loadWebUrl(url);
        mCustomWebView.setLoadingListener(mCallbackWebViewLoading);
    }

    @Override
    public void configViews() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mCustomWebView) {
            mCustomWebView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mCustomWebView) {
            mCustomWebView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mCustomWebView) {
            mCustomWebView.destroyWebView();
            mCustomWebView = null;
        }
    }
}
