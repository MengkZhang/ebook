package com.tzpt.cloudlibrary.ui.share;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.bean.ShareBean;
import com.tzpt.cloudlibrary.bean.ShareItemBean;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 分享
 */
public class ShareActivity extends AppCompatActivity implements ShareAppContract.View {

    private static final String SHARE_BEAN = "share_bean";

    public static void startActivity(Activity activity, ShareBean shareBean) {
        Intent intent = new Intent(activity, ShareActivity.class);
        intent.putExtra(SHARE_BEAN, shareBean);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_bottom);
    }

    public static void startActivityForResult(Activity activity, ShareBean shareBean, int requestCode) {
        Intent intent = new Intent(activity, ShareActivity.class);
        intent.putExtra(SHARE_BEAN, shareBean);
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_bottom);
    }

    @BindView(R.id.share_recycler_view)
    RecyclerView mShareRecyclerView;
    private ShareAdapter mAdapter;
    private ShareAppPresenter mPresenter;
    private Unbinder unbinder;

    @OnClick({R.id.share_cancel_btn, R.id.share_layout_rl})
    public void onViewClicked() {
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        unbinder = ButterKnife.bind(this);

        setStatusBarColor();
        initDatas();
        configViews();
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void initDatas() {
        mPresenter = new ShareAppPresenter();
        mPresenter.attachView(this);

        ShareBean shareBean = (ShareBean) getIntent().getSerializableExtra(SHARE_BEAN);
        mPresenter.setShareInfo(this, shareBean);
    }

    private void configViews() {
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        mShareRecyclerView.setLayoutManager(manager);
        mAdapter = new ShareAdapter(this, false);
        mShareRecyclerView.setAdapter(mAdapter);
        mPresenter.getShareItem();

        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ShareItemBean bean = mAdapter.getItem(position);
                if (null != bean) {
                    switch (bean.type) {
                        case 0:
                            mPresenter.shareWX();
                            finish();
                            break;
                        case 1:
                            mPresenter.shareWXFriends();
                            finish();
                            break;
                        case 2:
                            mPresenter.shareQQ();
                            break;
                        case 3:
                            mPresenter.shareQZone();
                            break;
                        case 4:
                            mPresenter.shareWeiBo();
                            break;
                        case 5:
                            mPresenter.shareForCopy();
                            finish();
                            break;
                        case 6://下载
                            handleAction(true, false);
                            break;
                        case 7://收藏
                            handleAction(false, true);
                            break;
                    }
                }
            }
        });
    }

    private void handleAction(boolean isDownLoad, boolean isCollection) {
        Intent intent = new Intent();
        if (isDownLoad) {
            intent.putExtra("mIsDownLoad", true);
        }
        if (isCollection) {
            intent.putExtra("mIsCollection", true);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void setShareItemList(List<ShareItemBean> shareItemList) {
        mAdapter.clear();
        mAdapter.addAll(shareItemList);
    }

    @Override
    public void shareSuccess() {
        ToastUtils.showSingleToast(R.string.share_success);
        finish();
    }

    @Override
    public void shareCancel() {
        ToastUtils.showSingleToast(R.string.share_cancel);
        finish();
    }

    @Override
    public void shareFailure() {
        ToastUtils.showSingleToast(R.string.share_fail);
        finish();
    }

    @Override
    public void showMsg(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }

    @Override
    public void finishActivity() {
        finish();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mPresenter.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_bottom);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mAdapter) {
            mAdapter.clear();
        }
        if (null != mPresenter) {
            mPresenter.detachView();
            mPresenter = null;
        }
        unbinder.unbind();
    }


    /**
     * 拷贝链接
     *
     * @param title       标题
     * @param description 描述
     * @param url         链接地址
     */
    @Override
    public void clipboardCopyLink(String title, String description, String url) {
        if (null == url || TextUtils.isEmpty(url)) {
            return;
        }
        StringBuilder urlBuilder = new StringBuilder();
        //标题
        if (!TextUtils.isEmpty(title)) {
            urlBuilder.append(title)
                    .append("\n");
        }
        //如果小于30字则加入省略符
        if (!TextUtils.isEmpty(description)) {
            urlBuilder.append(description);
            if (description.length() >= 30) {
                urlBuilder.append("......");
            }
            urlBuilder.append("\n");
        }
        urlBuilder.append(url);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(null, urlBuilder.toString()));
        ToastUtils.showSingleToast("复制成功！");
    }
}
