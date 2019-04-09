package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.bean.IDCardBean;
import com.tzpt.cloundlibrary.manager.ui.contract.ReaderAuthenticationContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.ReaderAuthenticationPresenter;
import com.tzpt.cloundlibrary.manager.utils.KeyboardUtils;
import com.tzpt.cloundlibrary.manager.widget.MultiStateLayout;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 验证读者身份界面
 */
public class ReaderAuthenticationActivity extends BaseActivity implements
        ReaderAuthenticationContract.View {

    private static final String FROM_TYPE = "from_type";
    private static final String ID_CARD_BEAN = "id_card_bean";

    public static void startActivity(Context context, int fromType, IDCardBean idCardBean) {
        Intent intent = new Intent(context, ReaderAuthenticationActivity.class);
        intent.putExtra(FROM_TYPE, fromType);
        intent.putExtra(ID_CARD_BEAN, idCardBean);
        context.startActivity(intent);
    }

    @BindView(R.id.multi_state_layout)
    MultiStateLayout mEmptyView;
    @BindView(R.id.reader_id_card_number_tv)
    TextView mReaderIdCardNumberTv;
    @BindView(R.id.reader_sex_tv)
    TextView mReaderSexTv;
    @BindView(R.id.reader_nationality_tv)
    TextView mReaderNationalityTv;
    @BindView(R.id.reader_birthday_tv)
    TextView mReaderBirthdayTv;
    @BindView(R.id.reader_name_et)
    EditText mReaderNameEt;
    @BindView(R.id.reader_submit_btn)
    Button mReaderSubmitBtn;
    @BindView(R.id.reader_skip_btn)
    Button mReaderSkipBtn;
    @BindView(R.id.reader_name_edit_btn)
    ImageButton mReaderNameEditBtn;
    @BindView(R.id.reader_nationality_edit_btn)
    ImageButton mReaderNationalityEditBtn;

    private ReaderAuthenticationPresenter mPresenter;
    private IDCardBean mIdCard;
    private int mFromType;
    private boolean mNeedReaderRegister; //是否读者未注册

    @OnClick({R.id.titlebar_left_btn, R.id.reader_name_edit_btn, R.id.reader_nationality_edit_btn,
            R.id.reader_retry_scanning_btn, R.id.reader_skip_btn, R.id.reader_submit_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                this.finish();
                break;
            case R.id.reader_name_edit_btn:         //编辑姓名
                mReaderNameEt.setEnabled(true);
                mReaderNameEt.setFocusable(true);
                mReaderNameEt.setFocusableInTouchMode(true);
                mReaderNameEt.setCursorVisible(true);
                String readerName = mReaderNameEt.getText().toString().trim();
                if (!TextUtils.isEmpty(readerName)) {
                    mReaderNameEt.setSelection(readerName.length());
                    mReaderNameEt.requestFocus();
                }
                KeyboardUtils.showSoftInput(mReaderNameEt);
                break;
            case R.id.reader_nationality_edit_btn:  //选择民族
                ReaderNationSelectActivity.startActivityForResult(this, 1000);
                break;
            case R.id.reader_retry_scanning_btn:    //返回扫描
                ReaderLoginActivity.startActivity(this, mFromType);
                finish();
                break;
            case R.id.reader_skip_btn:              //读者已在平台刷身份证，不需要修改读者信息,执行罚金流程
                if (mFromType == 0
                        || mFromType == 2) {
                    mPresenter.login(mIdCard.ID, true);
                } else {
                    mPresenter.login(mIdCard.ID, false);
                }

                break;
            case R.id.reader_submit_btn:            //读者未在平台刷身份证或读者未注册，不需要修改读者信息,执行罚金流程更新用户信息流程
                if (mNeedReaderRegister) {          //注册
                    readerName = mReaderNameEt.getText().toString().trim();
                    if (TextUtils.isEmpty(readerName)) {
                        showDialogTips(R.string.name_not_empty);
                        return;
                    }
                    ReaderRegisterActivity.startActivity(this, mIdCard, mFromType);
                    finish();
                } else {                            //更新
                    if (mFromType == 0
                            || mFromType == 2) {
                        mPresenter.updateReaderInfo(mIdCard.ID, mReaderIdCardNumberTv.getText().toString().trim(),
                                mReaderNameEt.getText().toString().trim(),
                                mReaderSexTv.getText().toString().trim(),
                                mReaderNationalityTv.getText().toString().trim(), true);
                    } else {
                        mPresenter.updateReaderInfo(mIdCard.ID, mReaderIdCardNumberTv.getText().toString().trim(),
                                mReaderNameEt.getText().toString().trim(),
                                mReaderSexTv.getText().toString().trim(),
                                mReaderNationalityTv.getText().toString().trim(), true);
                    }
                }
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_reader_authentication;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("确认身份信息");
    }

    @Override
    public void initDatas() {
        mPresenter = new ReaderAuthenticationPresenter();
        mPresenter.attachView(this);

        mFromType = getIntent().getIntExtra(FROM_TYPE, 0);
        mIdCard = (IDCardBean) getIntent().getSerializableExtra(ID_CARD_BEAN);
        if (null != mIdCard) {
            mEmptyView.showProgress();
            mPresenter.checkReaderAuthenticationInfo(mIdCard);
        }
    }

    @Override
    public void configViews() {
        mReaderNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && mIdCard != null) {
                    mIdCard.NAME = mReaderNameEt.getText().toString().trim();
                }
            }
        });
    }

    @Override
    public void checkResult(String readerId) {
        switch (mFromType) {
            case 0:
                BorrowBookManagementActivity.startActivity(this, readerId);
                break;
            case 1:
                LostBookActivity.startActivity(this, readerId);
                break;
            case 2:
                RefundDepositActivity.startActivity(this, readerId);
                break;
            case 3:
                ReaderPwdManagementActivity.startActivity(this, readerId);
                break;
            case 4:
                ChargeLibDepositActivity.startActivity(this, readerId);
                break;
        }
        finish();
    }

    @Override
    public void showLoading(String msg) {
        showDialog(msg);
    }

    @Override
    public void dismissLoading() {
        dismissDialog();
    }

    /**
     * 验证读者身份信息失败提示
     *
     * @param msg 消息
     */
    @Override
    public void showDialogScanLoginFailed(String msg) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        dialog.hasNoCancel(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    /**
     * 验证读者身份信息失败提示
     *
     * @param msgId 消息ID
     */
    @Override
    public void showDialogScanLoginFailed(int msgId) {
        showDialogScanLoginFailed(getString(msgId));
    }

    /**
     * 处理出现网络错误
     *
     * @param msgId 消息ID
     */
    @Override
    public void showAutoProcessDepositPenaltyFailed(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.hasNoCancel(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    /**
     * 被踢下线或者登录超时的提示
     *
     * @param msgId 消息ID
     */
    @Override
    public void noPermissionPrompt(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                LoginActivity.startActivity(ReaderAuthenticationActivity.this);
                finish();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    /**
     * 提示需要注册，进入注册界面
     *
     * @param msg 消息
     */
    private void showDialogForFirstScan(String msg) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
                finish();
            }
        });

    }

    /**
     * 提示非身份证读者，需要验证身份信息
     *
     * @param tel 电话号码
     */
    @Override
    public void showDialogForFirstScanToCheckInfo(String tel) {
        mIdCard.mBundleTel = tel;
        showDialogForFirstScan(getString(R.string.not_idcard_need_check));
    }

    /**
     * 该读者为非身份证注册读者，需重新注册！
     */
    @Override
    public void showDialogForFirstScanToRegister() {
        showDialogForFirstScan(getString(R.string.not_idcard_need_register_again));
    }

    /**
     * 提示然后退出
     *
     * @param msgId 消息ID
     */
    @Override
    public void showDialogForFinish(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.hasNoCancel(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    /**
     * 提示
     *
     * @param msgId 消息ID
     */
    @Override
    public void showDialogTips(int msgId) {
        showMessageDialog(getString(msgId));
    }

    /**
     * 设置读者身份信息
     *
     * @param idCard           读者身份信息
     * @param editReaderEnable 是否可以编辑
     */
    @Override
    public void setReaderAuthenticationInfo(IDCardBean idCard, boolean editReaderEnable) {
        mEmptyView.showContentView();
        this.mIdCard = idCard;
        if (editReaderEnable) {
            mReaderNationalityEditBtn.setVisibility(View.VISIBLE);
            mReaderNameEditBtn.setVisibility(View.VISIBLE);
            mReaderSubmitBtn.setVisibility(View.VISIBLE);
            mReaderSkipBtn.setVisibility(View.GONE);
            mReaderNameEt.setEnabled(true);
            mReaderNameEt.setFocusable(false);
            mReaderNameEt.setFocusableInTouchMode(false);
            mReaderNameEt.setCursorVisible(false);
        } else {
            mReaderNationalityEditBtn.setVisibility(View.GONE);
            mReaderNameEditBtn.setVisibility(View.GONE);
            mReaderSubmitBtn.setVisibility(View.GONE);
            mReaderSkipBtn.setVisibility(View.VISIBLE);
            mReaderNameEt.setEnabled(false);
        }
        mReaderIdCardNumberTv.setText(idCard.NUM);
        mReaderNameEt.setText(idCard.NAME);
        mReaderSexTv.setText(idCard.SEX);
        mReaderBirthdayTv.setText(idCard.BIRTHDAY);
        mReaderNationalityTv.setText(idCard.FOLK);
    }

    /**
     * 设置当前读者是否需要注册
     *
     * @param needRegister 需要注册
     */
    @Override
    public void needRegisterReader(boolean needRegister) {
        this.mNeedReaderRegister = needRegister;
    }

    @Override
    public void turnToDealPenalty(String readerId) {
        PenaltyDealActivity.startActivity(this, mFromType, readerId);
        finish();
    }

    /**
     * 返回设置民族信息
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
            String nation = data.getStringExtra("nation");
            mReaderNationalityTv.setText(nation);
            if (mIdCard != null) {
                mIdCard.FOLK = nation;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

}
