package com.tzpt.cloudlibrary.ui.share;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloudlibrary.LogActivity;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.ShareBean;
import com.tzpt.cloudlibrary.utils.VersionUtils;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * 分享APP
 */
public class ShareAppActivity extends BaseActivity {

    @BindView(R.id.share_app_version_tv)
    TextView mShareAppVersionTv;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ShareAppActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_share_app;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("分享APP");
    }

    @Override
    public void initDatas() {
    }

    @Override
    public void configViews() {
        mShareAppVersionTv.setText(getString(R.string.new_last_version, VersionUtils.getVersionInfo()));
    }


    @OnLongClick(R.id.share_logo_iv)
    public boolean onViewLongClicked() {
        LogActivity.startActivity(this);
        return false;
    }

    @OnClick({R.id.titlebar_left_btn, R.id.share_app_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.share_app_btn:
                ShareBean shareBean = new ShareBean();
                shareBean.shareTitle = getString(R.string.app_name);
                shareBean.shareContent = "点击链接，开始云图书馆之旅！";
                shareBean.shareUrl = "http://m.ytsg.cn/app/";
                shareBean.shareUrlForWX = "http://a.app.qq.com/o/simple.jsp?pkgname=com.tzpt.cloudlibrary";
                shareBean.shareImagePath = "http://img.ytsg.cn/images/htmlPage/ic_logo.png";
                ShareActivity.startActivity(this, shareBean);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
    }
}
