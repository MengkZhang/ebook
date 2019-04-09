package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.log.CrashHelper;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 日志
 */
public class ShowErrorLogActivity extends BaseActivity {

    @BindView(R.id.show_log_tv)
    public TextView mShowLogTv;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ShowErrorLogActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_show_error_log;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("日志");
    }

    @Override
    public void initDatas() {
        StringBuffer builder = new StringBuffer();
        builder.append("日志：\n");
        builder.append(CrashHelper.getCrashInfo());
        mShowLogTv.setText(builder);
    }

    @Override
    public void configViews() {

    }

    @OnClick({R.id.titlebar_left_btn, R.id.show_log_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                this.finish();
                break;
            case R.id.show_log_btn:
                mShowLogTv.setText("");
                CrashHelper.clearCrashInfo();
                break;
        }
    }

}
