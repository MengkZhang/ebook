package com.tzpt.cloudlibrary.ui.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.camera.ScanCallback;
import com.tzpt.cloudlibrary.utils.ISBNUtil;
import com.tzpt.cloudlibrary.widget.camera.ScanWrapper;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 扫描图书isbn
 */
public class ScanningIsbnActivity extends BaseActivity {

    //图书高级搜索扫描
    public static void startActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ScanningIsbnActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.borrow_book_status)
    TextView mBorrowBookStatus;
    @BindView(R.id.recommend_new_book_camera_preview)
    ScanWrapper mCameraPreview;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                mBorrowBookStatus.setText("");
                mCameraPreview.startPreviewScan();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
        mCameraPreview.openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
        mCameraPreview.releaseCamera();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_scanning_isbn;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("扫描ISBN");
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        mCameraPreview.bindActivity(this);
        mCameraPreview.setScanCallback(new ScanCallback() {
            @Override
            public void onScanResult(String content) {
                if (ISBNUtil.isISBN(content)) {
                    Intent intent = new Intent();
                    intent.putExtra("isbn", content);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    mCameraPreview.pausePreviewScan();
                    enteringBookTips("ISBN错误！");
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
                mCameraPreview.turnLight();
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    //错误提示
    private void enteringBookTips(String msg) {
        mBorrowBookStatus.setText(msg);
        mHandler.sendEmptyMessageDelayed(1000, 1200);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHandler) {
            mHandler.removeMessages(1000);
        }
    }
}
