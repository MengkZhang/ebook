package com.tzpt.cloudlibrary;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.tzpt.cloudlibrary.base.BaseActivity;

import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/30.
 */

public class EmptyActivity extends BaseActivity {
    private static final String FROM_TYPE = "from_type";

    public static void startActivity(Context context, String title) {
        Intent intent = new Intent(context, EmptyActivity.class);
        intent.putExtra(FROM_TYPE, title);
        context.startActivity(intent);
    }

    @OnClick({R.id.titlebar_left_btn,})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_empty;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        String title = getIntent().getStringExtra(FROM_TYPE);
        mCommonTitleBar.setTitle(title);
    }

    @Override
    public void configViews() {

    }
}
