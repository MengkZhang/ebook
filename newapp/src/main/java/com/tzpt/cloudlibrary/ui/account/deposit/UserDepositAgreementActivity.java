package com.tzpt.cloudlibrary.ui.account.deposit;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.widget.CustomWebView;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 押金协议
 */
public class UserDepositAgreementActivity extends BaseActivity {

    private static final String AGREEMENT_TITLE = "agreement_title";
    private static final String AGREEMENT_URL = "agreement_url";

    public static void startActivity(Context context, String title, String url) {
        Intent intent = new Intent(context, UserDepositAgreementActivity.class);
        intent.putExtra(AGREEMENT_TITLE, title);
        intent.putExtra(AGREEMENT_URL, url);
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

    private View.OnClickListener mRetryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            initDatas();
        }
    };
    private CustomWebView.CallbackWebViewLoading mCallbackWebViewLoading = new CustomWebView.CallbackWebViewLoading() {
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
        return R.layout.activity_user_deposit_agreement;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        String title = getIntent().getStringExtra(AGREEMENT_TITLE);
        mCommonTitleBar.setTitle(title);
    }

    @Override
    public void initDatas() {
        String url = getIntent().getStringExtra(AGREEMENT_URL);
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
