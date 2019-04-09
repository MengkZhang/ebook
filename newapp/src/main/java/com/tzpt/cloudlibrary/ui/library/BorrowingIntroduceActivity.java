package com.tzpt.cloudlibrary.ui.library;

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
 * 图书馆详情借阅须知
 * Created by tonyjia on 2018/8/27.
 */
public class BorrowingIntroduceActivity extends BaseActivity implements
        BorrowingIntroduceContract.View {

    private static final String LIB_CODE = "lib_code";

    public static void startActivity(Context context, String libCode) {
        Intent intent = new Intent(context, BorrowingIntroduceActivity.class);
        intent.putExtra(LIB_CODE, libCode);
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
            getBorrowingIntroduce();
        }
    };
    private CustomWebView.CallbackWebViewLoading mCallbackWebViewLoading = new CustomWebView.CallbackWebViewLoading() {
        @Override
        public void onPageStarted() {
        }

        @Override
        public void onPageFinished(boolean hasContent) {
            mMultiStateLayout.showContentView();
        }

        @Override
        public void onPageLoadingError() {
            mMultiStateLayout.showError();
            mMultiStateLayout.showRetryError(mRetryClickListener);
        }
    };
    private BorrowingIntroducePresenter mPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_borrowing_instructions;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("借阅须知");
    }

    @Override
    public void initDatas() {
        mPresenter = new BorrowingIntroducePresenter();
        mPresenter.attachView(this);
        getBorrowingIntroduce();

    }

    private void getBorrowingIntroduce() {
        String libCode = getIntent().getStringExtra(LIB_CODE);
        mPresenter.getLibIntroduce(libCode);
    }

    @Override
    public void configViews() {

    }

    @Override
    public void showProgress() {
        mMultiStateLayout.showProgress();
    }

    @Override
    public void setNetError() {
        mMultiStateLayout.showError();
        mMultiStateLayout.showRetryError(mRetryClickListener);
    }

    @Override
    public void setLibIntroduce(String url) {
        mCustomWebView.loadWebUrl(url);
        mCustomWebView.setLoadingListener(mCallbackWebViewLoading);
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
        mPresenter.detachView();
        mPresenter = null;
    }
}
