package com.tzpt.cloudlibrary.ui.account.selfhelp;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.utils.MoneyUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 验证密码
 */
public class VerifyPasswordActivity extends BaseActivity {

    private static final String MONEY = "MONEY";

    public static void startActivityForResult(Activity activity, double money, int requestCode) {
        Intent intent = new Intent(activity, VerifyPasswordActivity.class);
        intent.putExtra(MONEY, money);
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_bottom);
    }

    @BindView(R.id.self_buy_book_money_et)
    EditText mMoneyEt;
    @BindView(R.id.self_buy_book_psw_et)
    EditText mPswEt;
    @BindView(R.id.self_buy_book_submit_btn)
    Button mSubmitBtn;

    @OnClick({R.id.self_buy_book_submit_btn, R.id.verify_psw_root_view_ll})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.verify_psw_root_view_ll:
                finish();
                break;
            case R.id.self_buy_book_submit_btn:
                String money = mMoneyEt.getText().toString().trim();
                String psw = mPswEt.getText().toString().trim();
                if (TextUtils.isEmpty(psw)) {
                    ToastUtils.showSingleToast(R.string.error_incorrect_password);
                    return;
                }
                if (psw.length() != 6) {
                    ToastUtils.showSingleToast(R.string.error_invalid_password);
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("money", money);
                intent.putExtra("psw", psw);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_verify_password;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        //setSupportSlideBack(false);
        double money = getIntent().getDoubleExtra(MONEY, 0.00);
        mMoneyEt.setText(MoneyUtils.formatMoney(money));
        mPswEt.setFocusable(true);
        mPswEt.setFocusableInTouchMode(true);
        mPswEt.requestFocus();
    }

    @Override
    public void configViews() {
        mPswEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mSubmitBtn.setBackgroundResource(s.length() > 0 ? R.drawable.btn_login : R.drawable.phone_manage_button_bg);
                mSubmitBtn.setEnabled(s.length() > 0);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        KeyboardUtils.hideSoftInput(mPswEt);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_bottom);
    }

}
