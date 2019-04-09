package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.ui.contract.OperatorPwdContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.OperatorPwdPresenter;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 管理员密码界面
 * Created by Administrator on 2017/7/3.
 */

public class OperatorPwdActivity extends BaseActivity implements OperatorPwdContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, OperatorPwdActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.operator_name_tv)
    TextView mOperatorNameTv;
    @BindView(R.id.old_pwd_et)
    EditText mOldPwdEt;
    @BindView(R.id.new_pwd_et)
    EditText mNewPwdEt;
    @BindView(R.id.sure_new_pwd_et)
    EditText mSureNewPwdEt;

    private OperatorPwdPresenter mPresenter;

    @OnClick({R.id.confirm_btn, R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confirm_btn:
                changePwd();
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    private void changePwd() {
        mPresenter.changeOperatorPwd(mOldPwdEt.getText().toString().trim(),
                mNewPwdEt.getText().toString().trim(),
                mSureNewPwdEt.getText().toString().trim());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_operator_pwd;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("密码");
    }

    @Override
    public void initDatas() {
        mPresenter = new OperatorPwdPresenter();
        mPresenter.attachView(this);
        mPresenter.getOperatorName();
    }

    @Override
    public void configViews() {
        mSureNewPwdEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    changePwd();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void changePwdSuccess(String msg) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mOldPwdEt.setText("");
                mNewPwdEt.setText("");
                mSureNewPwdEt.setText("");
                finish();
                LoginActivity.startActivity(OperatorPwdActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void changePwdFailed(String msg) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void changePwdFailed(int msgId) {
        changePwdFailed(getString(msgId));
    }

    @Override
    public void noPermissionPrompt(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                LoginActivity.startActivity(OperatorPwdActivity.this);
                finish();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void showLoadingProgress(String msg) {
        showDialog(msg);
    }

    @Override
    public void hideLoadingProgress() {
        dismissDialog();
    }

    @Override
    public void setOperatorName(String name) {
        mOperatorNameTv.setText(name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
