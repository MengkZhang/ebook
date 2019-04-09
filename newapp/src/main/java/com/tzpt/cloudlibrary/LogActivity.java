package com.tzpt.cloudlibrary;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloudlibrary.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 日志
 */
public class LogActivity extends BaseActivity {

    @BindView(R.id.log_content_tv)
    TextView logContentTv;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LogActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_log;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("日志");
    }

    @Override
    public void initDatas() {

    }
    @OnClick(R.id.titlebar_left_btn)
    public void onViewClicked(View v) {
        finish();
    }
    @Override
    public void configViews() {
        String logText = CrashHandler.getInstance().getLog();
        logContentTv.setText(logText);
    }

}
