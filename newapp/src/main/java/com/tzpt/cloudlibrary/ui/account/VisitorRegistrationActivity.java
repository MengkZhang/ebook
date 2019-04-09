package com.tzpt.cloudlibrary.ui.account;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 读友会未注册读者登记
 */
public class VisitorRegistrationActivity extends BaseActivity implements
        VisitorRegistrationContract.View {

    private final static String ACTION_ID = "action_id";

    public static void startActivity(Context context, int actionId) {
        Intent intent = new Intent(context, VisitorRegistrationActivity.class);
        intent.putExtra(ACTION_ID, actionId);
        context.startActivity(intent);
    }

    @BindView(R.id.progress_layout)
    LoadingProgressView mProgressLayout;
    @BindView(R.id.register_name_et)
    EditText mRegisterNameEt;
    @BindView(R.id.register_delete_box1)
    ImageView mRegisterDeleteBox1;
    @BindView(R.id.register_id_card_et)
    EditText mRegisterIdCardEt;
    @BindView(R.id.register_delete_box2)
    ImageView mRegisterDeleteBox2;
    @BindView(R.id.register_phone_et)
    EditText mRegisterPhoneEt;
    @BindView(R.id.register_delete_box3)
    ImageView mRegisterDeleteBox3;

    private VisitorRegistrationPresenter mPresenter;
    private int mActionID;

    @OnClick({R.id.titlebar_left_btn, R.id.register_btn, R.id.register_delete_box1,
            R.id.register_delete_box2, R.id.register_delete_box3})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.register_btn:
                mPresenter.startVisitorRegistration(mActionID, mRegisterNameEt.getText().toString().trim(),
                        mRegisterIdCardEt.getText().toString().trim(),
                        mRegisterPhoneEt.getText().toString().trim());
                break;
            case R.id.register_delete_box1:
                mRegisterNameEt.setText("");
                break;
            case R.id.register_delete_box2:
                mRegisterIdCardEt.setText("");
                break;
            case R.id.register_delete_box3:
                mRegisterPhoneEt.setText("");
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_visitor_registration;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("未注册读者登记");
    }

    @Override
    public void initDatas() {
        mActionID = getIntent().getIntExtra(ACTION_ID, -1);
        if (mActionID != -1) {
            mPresenter = new VisitorRegistrationPresenter();
            mPresenter.attachView(this);
        }
    }

    @Override
    public void configViews() {
        mRegisterNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mRegisterDeleteBox1.setVisibility((s.length() == 0) ? View.INVISIBLE : View.VISIBLE);
            }
        });
        mRegisterIdCardEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mRegisterDeleteBox2.setVisibility((s.length() == 0) ? View.INVISIBLE : View.VISIBLE);
            }
        });
        mRegisterPhoneEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mRegisterDeleteBox3.setVisibility((s.length() == 0) ? View.INVISIBLE : View.VISIBLE);
            }
        });
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
        if (null != mPresenter) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    @Override
    public void registerSuccess() {
        ToastUtils.showSingleToast("报名成功！");
        finish();
    }

    @Override
    public void showToastMsg(String msg) {
        ToastUtils.showSingleToast(msg);
    }

    @Override
    public void showToastMsg(int resId) {
        ToastUtils.showSingleToast(resId);
    }
}
