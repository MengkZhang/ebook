package com.tzpt.cloudlibrary.ui.account.selfhelp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.camera.ScanCallback;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.utils.MoneyUtils;
import com.tzpt.cloudlibrary.widget.camera.ScanWrapper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 自助购书扫描
 */
public class SelfHelpBuyBookScanningActivity extends BaseActivity {

    private static final String FROM_TYPE = "from_type";
    private static final String BAR_NUMBER = "bar_number";
    private static final String BOUGHT_NUM = "bought_num";
    private static final String BOOK_TOTAL_PRICE = "book_total_price";

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SelfHelpBuyBookScanningActivity.class);
        intent.putExtra(FROM_TYPE, 0);
        context.startActivity(intent);
    }

    public static void startActivityForResult(Activity context, int boughtNum, double bookTotalPrice, int requestCode) {
        Intent intent = new Intent(context, SelfHelpBuyBookScanningActivity.class);
        intent.putExtra(FROM_TYPE, 1);
        intent.putExtra(BOUGHT_NUM, boughtNum);
        intent.putExtra(BOOK_TOTAL_PRICE, bookTotalPrice);
        context.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.self_book_camera_preview)
    ScanWrapper mSelfBookCameraPreview;
    @BindView(R.id.self_book_money_tv)
    TextView mSelfBookMoneyTv;
    @BindView(R.id.self_book_sum_tv)
    TextView mBorrowBookSumTv;
    private int mFromType = 0;

    @OnClick({R.id.titlebar_left_btn, R.id.btn_light, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.btn_light:
                mSelfBookCameraPreview.turnLight();
                break;
            case R.id.btn_back:
                if (mFromType == 0) {//进入图书列表
                    SelfHelpBuyBookActivity.startActivity(this, null);
                }
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_shelf_help_buy_book_scanning;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("自助购书");
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);

        mFromType = getIntent().getIntExtra(FROM_TYPE, -1);
        int boughtNum = getIntent().getIntExtra(BOUGHT_NUM, 0);
        double bookTotalPrice = getIntent().getDoubleExtra(BOOK_TOTAL_PRICE, 0);
        if (boughtNum > 0) {
            updateBookTotalInfo("数量 " + boughtNum, "金额 " + MoneyUtils.formatMoney(bookTotalPrice));
        } else {
            updateBookTotalInfo("数量 0", "金额 0.00");
        }
    }

    @Override
    public void configViews() {
        mSelfBookCameraPreview.bindActivity(this);
        mSelfBookCameraPreview.setScanCallback(new ScanCallback() {
            @Override
            public void onScanResult(String barNumber) {
                if (!TextUtils.isEmpty(barNumber)) {
                    switch (mFromType) {
                        case 0://进入图书列表
                            SelfHelpBuyBookActivity.startActivity(SelfHelpBuyBookScanningActivity.this, barNumber);
                            finish();
                            break;
                        case 1://返回图书列表
                            Intent data = new Intent();
                            data.putExtra(BAR_NUMBER, barNumber);
                            setResult(RESULT_OK, data);
                            finish();
                            break;
                    }
                }
            }
        });
    }

    public void updateBookTotalInfo(String sum, String money) {
        mBorrowBookSumTv.setText(sum);
        mSelfBookMoneyTv.setText(money);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginOut(AccountMessage loginMessage) {
        if (loginMessage.mIsLoginOut) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
        mSelfBookCameraPreview.openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
        mSelfBookCameraPreview.releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
