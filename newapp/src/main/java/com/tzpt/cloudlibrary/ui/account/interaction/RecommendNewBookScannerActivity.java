package com.tzpt.cloudlibrary.ui.account.interaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.camera.ScanCallback;
import com.tzpt.cloudlibrary.ui.paperbook.BookDetailActivity;
import com.tzpt.cloudlibrary.utils.ISBNUtil;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;
import com.tzpt.cloudlibrary.widget.camera.ScanWrapper;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 推荐新书
 */
public class RecommendNewBookScannerActivity extends BaseListActivity<String> {

    @BindView(R.id.borrow_book_status)
    TextView mBorrowBookStatus;
    @BindView(R.id.recommend_new_book_camera_preview)
    ScanWrapper mScanWrapper;
    @BindView(R.id.progress_layout)
    LoadingProgressView mProgressLayout;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, RecommendNewBookScannerActivity.class);
        context.startActivity(intent);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                mBorrowBookStatus.setText("");
                mScanWrapper.startPreviewScan();
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_recommend_new_book_scanner;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("新书推荐");
    }

    @Override
    public void initDatas() {
        mScanWrapper.bindActivity(this);
    }

    @Override
    public void configViews() {
        mScanWrapper.setScanCallback(new ScanCallback() {
            @Override
            public void onScanResult(String content) {
                if (ISBNUtil.isISBN(content)) {
                    BookDetailActivity.startActivity(RecommendNewBookScannerActivity.this, content, null, null, 3);
                } else {
                    mScanWrapper.pausePreviewScan();
                    mBorrowBookStatus.setText(R.string.ISBN_code_error);
                    mHandler.sendEmptyMessageDelayed(1000, 1200);
                }
            }
        });
    }

    @OnClick({R.id.titlebar_left_btn, R.id.btn_light, R.id.btn_back})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.btn_light:
                mScanWrapper.turnLight();
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
        mScanWrapper.openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
        mScanWrapper.releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHandler) {
            mHandler.removeMessages(1000);
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onItemClick(int position) {

    }
}
