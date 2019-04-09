package com.tzpt.cloudlibrary.ui.ebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.ui.account.LoginActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 限制读者阅读类
 */
public class EBookReadLimitActivity extends AppCompatActivity {

    public static void startActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, EBookReadLimitActivity.class);
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_bottom);
    }

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_limit);
        unbinder = ButterKnife.bind(this);
    }

    @OnClick({R.id.reader_limit_login_btn, R.id.reader_limit_root_rl, R.id.reader_limit_del_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.reader_limit_login_btn:
                LoginActivity.startActivityForResult(this, 1000);
                break;
            case R.id.reader_limit_del_btn:
            case R.id.reader_limit_root_rl:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000
                && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_bottom);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
