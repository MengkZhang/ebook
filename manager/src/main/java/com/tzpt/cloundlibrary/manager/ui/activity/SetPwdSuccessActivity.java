package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;

import butterknife.OnClick;

/**
 * 设置成功
 * Created by Administrator on 2018/9/26.
 */

public class SetPwdSuccessActivity extends BaseActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SetPwdSuccessActivity.class);
        context.startActivity(intent);
    }


    @OnClick({R.id.titlebar_left_btn, R.id.confirm_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
            case R.id.confirm_btn:
                LoginActivity.startActivity(this);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_pwd_success;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("设置成功");
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            LoginActivity.startActivity(this);
        }
        return false;
    }

}
