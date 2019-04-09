package com.tzpt.cloudlibrary.ui.account.selfhelp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.SelfBookInfoBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.deposit.PayDepositActivity;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.PermissionsDialogFragment;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;
import com.tzpt.cloudlibrary.widget.recyclerview.decoration.DividerDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 自助购书
 */
public class SelfHelpBuyBookActivity extends BaseActivity implements
        SelfHelpBuyBookContract.View {

    private static final String BAR_NUMBER = "bar_number";
    private static final int SCANNER_REQUEST_CODE = 1000;
    private static final int PAY_REQUEST_CODE = 1001;
    private static final int PSW_REQUEST_CODE = 1002;

    public static void startActivity(Context context, String barNumber) {
        Intent intent = new Intent(context, SelfHelpBuyBookActivity.class);
        intent.putExtra(BAR_NUMBER, barNumber);
        context.startActivity(intent);
    }

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.self_book_sum_tv)
    TextView mSelfBookSumTv;
    @BindView(R.id.self_book_money_tv)
    TextView mSelfBookMoneyTv;
    @BindView(R.id.self_book_scanner_btn)
    Button mSelfBookScannerBtn;
    @BindView(R.id.book_operation_tv)
    TextView mSelfBookOperationTv;
    @BindView(R.id.self_book_total_layout)
    LinearLayout mSelfBookTotalLayout;
    @BindView(R.id.progress_layout)
    LoadingProgressView mProgressLayout;
    @BindView(R.id.self_book_confirm_btn)
    Button mSelfBookConfirmBtn;

    private SelfHelpBuyBookAdapter mAdapter;
    private SelfHelpBuyBookPresenter mPresenter;

    private double mTotalBookPrice;
    private boolean mIsBuyBookResultStatus = false;

    @OnClick({R.id.titlebar_left_btn, R.id.self_book_confirm_btn, R.id.self_book_scanner_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.self_book_confirm_btn:
                if (mIsBuyBookResultStatus) {
                    //购书结果，有未接图书->退出
                    finish();
                } else {
                    //退出
                    if (mAdapter.getCount() == 0) {
                        finish();
                        return;
                    }
                    //如果当前不是提交购书结果状态，则点击展示确认密码弹出框
                    VerifyPasswordActivity.startActivityForResult(this, mTotalBookPrice, PSW_REQUEST_CODE);
                }
                break;
            case R.id.self_book_scanner_btn:
                initCameraPermission();
                break;
        }
    }

    View.OnClickListener mDelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            mPresenter.removeDataByIndex(position);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_shelf_help_buy_book;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("自助购书");
    }

    @Override
    public void initDatas() {
        mPresenter = new SelfHelpBuyBookPresenter();
        mPresenter.attachView(this);
        //如果扫描了条码，则请求图书信息
        String barNumber = getIntent().getStringExtra(BAR_NUMBER);
        if (!TextUtils.isEmpty(barNumber)) {
            mSelfBookScannerBtn.setText("继续扫码");
            mPresenter.getBookInfo(barNumber);
        } else {
            mSelfBookScannerBtn.setText("点击扫码");
        }
    }

    @Override
    public void configViews() {
        mAdapter = new SelfHelpBuyBookAdapter(this, mDelClickListener);
        mAdapter.setEditStatus(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_rv_vertical_two));
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showProgressDialog() {
        mProgressLayout.showProgressLayout();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressLayout.hideProgressLayout();
    }

    @Override
    public void showDialogTips(int resId) {
        final CustomDialog customDialog = new CustomDialog(this,
                R.style.DialogTheme, getString(resId));
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(false);
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }

    /**
     * 平台押金不足，请先充值
     *
     * @param resId
     */
    @Override
    public void showChargeDialogTips(int resId) {
        final CustomDialog customDialog = new CustomDialog(this,
                R.style.DialogTheme, getString(resId));
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(true);
        customDialog.setOkText("充值");
        customDialog.setCancelText("取消");
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
                PayDepositActivity.startActivityForResult(SelfHelpBuyBookActivity.this, PAY_REQUEST_CODE);
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }

    /**
     * 退出登录
     */
    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
        finish();
    }

    /**
     * 设置列表
     *
     * @param bookList
     */
    @Override
    public void setBookList(List<SelfBookInfoBean> bookList) {
        mAdapter.clear();
        mAdapter.addAll(bookList);
        mSelfBookConfirmBtn.setText("确定");
    }

    /**
     * 列表设置为空
     */
    @Override
    public void setBookEmpty() {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();

        mSelfBookTotalLayout.setVisibility(View.GONE);
        mSelfBookConfirmBtn.setText("退出");
    }

    /**
     * 展示合计信息
     *
     * @param bookSum
     * @param totalPrice
     * @param totalBookPrice
     */
    @Override
    public void showBookTotalInfo(String bookSum, String totalPrice, double totalBookPrice) {
        mSelfBookTotalLayout.setVisibility(View.VISIBLE);
        mSelfBookSumTv.setText(bookSum);
        mSelfBookMoneyTv.setText(totalPrice);
        mTotalBookPrice = totalBookPrice;
    }

    /**
     * 购书成功
     *
     * @param successBookSum 成功购买书籍数量
     */
    @Override
    public void selfBuyBookSuccess(int successBookSum) {
        mIsBuyBookResultStatus = true;
        mSelfBookOperationTv.setText("状态");
        mAdapter.setEditStatus(false);
        mAdapter.updateBuyBookSuccess();
        //设置借书合计结果
        mSelfBookTotalLayout.setVisibility(View.VISIBLE);
        if (successBookSum > 0) {
            mSelfBookSumTv.setText(getString(R.string.book_status_success_sum, successBookSum));
        } else {
            mSelfBookSumTv.setText("");
        }
        mSelfBookMoneyTv.setText("");
        //隐藏扫描控件
        mSelfBookScannerBtn.setVisibility(View.GONE);
        mSelfBookConfirmBtn.setText("退出");

        //自定义对话框
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.success = true;
        EventBus.getDefault().post(accountMessage);
    }

    @Override
    public void setEditStatus(boolean editStatus) {
        mAdapter.setEditStatus(editStatus);
    }

    @Override
    public void turnToScan(int boughtNum, double totalPrice) {
        SelfHelpBuyBookScanningActivity.startActivityForResult(SelfHelpBuyBookActivity.this,
                boughtNum, totalPrice, SCANNER_REQUEST_CODE);
    }

    //初始化摄像头权限
    private void initCameraPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            mPresenter.getBookInfoTurnToScan();
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(Manifest.permission.CAMERA)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {//权限已授权
                            mPresenter.getBookInfoTurnToScan();
                        } else if (permission.shouldShowRequestPermissionRationale) {//下次继续提示(应该显示请求许可理由)

                        } else {//没有权限,不能使用权限模块-去设置权限
                            showCameraPermissionPopUpWindow();
                        }
                    }
                });
    }

    PermissionsDialogFragment mSetCameraDialogFragment;

    //展示设置权限弹窗
    private void showCameraPermissionPopUpWindow() {
        if (null == mSetCameraDialogFragment) {
            mSetCameraDialogFragment = new PermissionsDialogFragment();
        }
        if (mSetCameraDialogFragment.isAdded()) {
            return;
        }
        mSetCameraDialogFragment.initPermissionUI(PermissionsDialogFragment.PERMISSION_CAMERA);
        mSetCameraDialogFragment.show(getFragmentManager(), "PermissionsDialogFragment");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case SCANNER_REQUEST_CODE://扫描后获取书籍信息
                    String barNumber = data.getStringExtra(BAR_NUMBER);
                    mPresenter.getBookInfo(barNumber);
                    mSelfBookScannerBtn.setText("继续扫码");
                    break;
                case PAY_REQUEST_CODE://支付成功后请求可用押金信息,没有回调
                    break;
                case PSW_REQUEST_CODE:
                    String money = data.getStringExtra("money");
                    String password = data.getStringExtra("psw");
                    mPresenter.confirmReaderPsw(money, password);
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter.getCount() > 0) {
            mAdapter.clear();
        }
        mPresenter.clearTempBook();
        mPresenter.detachView();
    }

}
