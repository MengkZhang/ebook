package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.ui.contract.SetPwdContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.SetPwdPresenter;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置密码
 * Created by Administrator on 2018/9/26.
 */

public class SetPwdActivity extends BaseActivity implements SetPwdContract.View {
    private static final String HALL_CODE = "hall_code";
    private static final String USER_NAME = "user_name";
    private static final String ID = "id";

    public static void startActivity(Context context, int id, String hallCode, String userName) {
        Intent intent = new Intent(context, SetPwdActivity.class);
        intent.putExtra(HALL_CODE, hallCode);
        intent.putExtra(USER_NAME, userName);
        intent.putExtra(ID, id);
        context.startActivity(intent);
    }

    @BindView(R.id.library_code_tv)
    TextView mLibCodeTv;
    @BindView(R.id.admin_name_tv)
    TextView mAdminNameTv;
    @BindView(R.id.new_pwd_one_et)
    EditText mNewPwdOneEt;
    @BindView(R.id.new_pwd_two_et)
    EditText mNewPwdTwoEt;

    private SetPwdPresenter mPresenter;
    private int mId;

    @OnClick({R.id.titlebar_left_btn, R.id.confirm_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.confirm_btn:
                mPresenter.setNewPwd(mId, mNewPwdOneEt.getText().toString().trim(), mNewPwdTwoEt.getText().toString().trim());
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_pwd;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("设置密码");
    }

    @Override
    public void initDatas() {
        mPresenter = new SetPwdPresenter();
        mPresenter.attachView(this);

        mId = getIntent().getIntExtra(ID, 0);
        String hallCode = getIntent().getStringExtra(HALL_CODE);
        String userName = getIntent().getStringExtra(USER_NAME);

        mLibCodeTv.setText(getString(R.string.reset_pwd_hall_code, hallCode));
        mAdminNameTv.setText(getString(R.string.reset_pwd_admin_name, userName));
    }

    @Override
    public void configViews() {

    }

//    @Override
//    public void noPermissionPrompt(int msgId) {
//
//    }

    @Override
    public void showDialogTip(String msg) {
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
    public void showDialogTip(int msgId) {
        showDialogTip(getString(msgId));
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
    public void resetPwdSuccess() {
        SetPwdSuccessActivity.startActivity(this);
    }


}
