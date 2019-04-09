package com.tzpt.cloundlibrary.manager.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.ui.contract.SetOperatorFirstPswContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.SetOperatorFirstPswPresenter;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 操作员首次登录修改密码
 */
public class SetOperatorFirstPswActivity extends BaseActivity implements
        SetOperatorFirstPswContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SetOperatorFirstPswActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.new_pwd_et)
    EditText mNewPwdEt;
    @BindView(R.id.sure_new_pwd_et)
    EditText mSureNewPwdEt;
    private SetOperatorFirstPswPresenter mPresenter;

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

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_operator_first_psw;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("密码");
    }

    @Override
    public void initDatas() {
        mPresenter = new SetOperatorFirstPswPresenter();
        mPresenter.attachView(this);
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

    private void changePwd() {
        mPresenter.saveOperatorPwd(mNewPwdEt.getText().toString().trim(),
                mSureNewPwdEt.getText().toString().trim());
    }

    @Override
    public void changePwdSuccess() {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, "修改成功！");
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
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
        dialog.show();
    }

    @Override
    public void changePwdFailed(int resId) {
        showMessageDialog(getString(resId));
    }

    @Override
    public void showLoadingProgress() {
        showDialog("发送中...");
    }

    @Override
    public void hideLoadingProgress() {
        dismissDialog();
    }

    @Override
    public void pleaseLoginTip(int resId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(resId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.delOperatorToken();
        mPresenter.detachView();
    }
}
