package com.tzpt.cloundlibrary.manager.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.utils.ActivityManager;
import com.tzpt.cloundlibrary.manager.widget.TitleBarView;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomLoadingDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/6/20.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public TitleBarView mCommonTitleBar;
    Unbinder unbinder;
    private CustomLoadingDialog mLoadingDialog;//进度条
    public Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActivityManager.getInstance().addActivity(this);

        setContentView(getLayoutId());
        mContext = this;
        unbinder = ButterKnife.bind(this);

        mCommonTitleBar = ButterKnife.findById(this, R.id.common_toolbar);
        if (mCommonTitleBar != null) {
            initToolBar();
        }
        initDatas();
        configViews();
    }

    public abstract int getLayoutId();

    public abstract void initToolBar();

    public abstract void initDatas();

    public abstract void configViews();


    // mLoadingDialog
    private CustomLoadingDialog getDialog(String tips) {
        if (mLoadingDialog == null) {
            mLoadingDialog = CustomLoadingDialog.instance(this, tips);
            mLoadingDialog.setCancelable(false);
        }
        return mLoadingDialog;
    }

    public void hideDialog() {
        if (mLoadingDialog != null)
            mLoadingDialog.hide();
    }

    public void showDialog(String tips) {
        if (!getDialog(tips).isShowing()) {
            getDialog(tips).show();
        }
    }

    public void dismissDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    /**
     * 提示信息
     *
     * @param msg
     */
    public void showMessageDialog(String msg) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
//        ActivityManager.getInstance().finishActivity(this);
    }
}
