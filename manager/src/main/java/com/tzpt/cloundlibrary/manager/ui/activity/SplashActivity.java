package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.tzpt.cloundlibrary.manager.ManagerApplication;
import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.Constant;

/**
 * 启动页
 * Created by Administrator on 2018/9/26.
 */

public class SplashActivity extends AppCompatActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constant.TO_MESSAGE_VIEW, 1000);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.INVISIBLE);
        }

        int messageFlag = getIntent().getIntExtra(Constant.TO_MESSAGE_VIEW, -1);
        if (TextUtils.isEmpty(ManagerApplication.TOKEN)) {
            LoginActivity.startActivity(this, messageFlag);
            finish();
        } else {
            MainActivity.startActivity(this, messageFlag, false);
            finish();
        }
    }
}
