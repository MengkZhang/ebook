package com.tzpt.cloundlibrary.manager.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.bean.StatisticsClassifyBean;
import com.tzpt.cloundlibrary.manager.bean.StatisticsItem;
import com.tzpt.cloundlibrary.manager.permissions.Permission;
import com.tzpt.cloundlibrary.manager.permissions.PermissionsDialogFragment;
import com.tzpt.cloundlibrary.manager.permissions.RxPermissions;
import com.tzpt.cloundlibrary.manager.ui.adapter.StatisticsResultLineAdapter;
import com.tzpt.cloundlibrary.manager.ui.adapter.StatisticsResultLineItemAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.StatisticsResultListContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.StatisticsResultPresenter;
import com.tzpt.cloundlibrary.manager.utils.DisplayUtils;
import com.tzpt.cloundlibrary.manager.widget.MultiStateLayout;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.OnLoadMoreListener;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by Administrator on 2018/9/3.
 */

public class StatisticsResultListActivity extends BaseActivity implements StatisticsResultListContract.View,
        OnLoadMoreListener {
    private static final String STATISTICS_CLASSIFY = "statistics_classify";

    public static void startActivity(Context context, StatisticsClassifyBean classifyBean) {
        Intent intent = new Intent(context, StatisticsResultListActivity.class);
        intent.putExtra(STATISTICS_CLASSIFY, classifyBean);
        context.startActivity(intent);
    }

    @BindView(R.id.title_ll)
    LinearLayout mTitleLl;
    @BindView(R.id.recycler_content_list)
    EasyRecyclerView mContentRecyclerView;
    @BindView(R.id.statistics_total_info_rl)
    RelativeLayout mStatisticsTotalInfoRl;
    @BindView(R.id.statistics_total_info_one_tv)
    TextView mTotalInfoOneTv;
    @BindView(R.id.statistics_total_info_two_tv)
    TextView mTotalInfoTwoTv;
    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;

    private StatisticsClassifyBean mClassifyBean;

    private StatisticsResultLineAdapter mAdapter;

    private StatisticsResultPresenter mPresenter;

    private int mCurrentPageNum = 1;

    @OnClick({R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_statistics_result;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
    }

    @Override
    public void initDatas() {
        mPresenter = new StatisticsResultPresenter();
        mPresenter.attachView(this);

        mClassifyBean = (StatisticsClassifyBean) getIntent().getSerializableExtra(STATISTICS_CLASSIFY);
        mCommonTitleBar.setTitle(mClassifyBean.getTitle());

        mPresenter.getStatisticsResult(mClassifyBean.getId(), mCurrentPageNum);
    }

    @Override
    public void configViews() {
        mAdapter = new StatisticsResultLineAdapter(this, new StatisticsResultLineItemAdapter.CallPhoneListener() {
            @Override
            public void call(String phone) {
                initCallPhonePermission(phone);
            }
        });

        mContentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mContentRecyclerView.setAdapter(mAdapter);

        mAdapter.setNullMoreView(this);
    }

    @Override
    public void onLoadMore() {
        mPresenter.getStatisticsResult(mClassifyBean.getId(), mCurrentPageNum + 1);
    }

    @Override
    public void showLoadingProgress() {
        mMultiStateLayout.showProgress();
    }

    @Override
    public void setStatisticsResultColumnTitle(List<StatisticsItem> data) {
        for (StatisticsItem item : data) {
            View titleView = LayoutInflater.from(this).inflate(R.layout.view_statistics_list_row_item, null);
            TextView infoTv = (TextView) titleView.findViewById(R.id.info_tv);
            infoTv.setText(item.mContent);
            infoTv.setMaxWidth(DisplayUtils.dp2px(item.mWidth));
            infoTv.setMinWidth(DisplayUtils.dp2px(item.mWidth));
            infoTv.setWidth(DisplayUtils.dp2px(item.mWidth));
            infoTv.setGravity(item.mGravity);
            infoTv.setTextColor(Color.parseColor("#888888"));
            infoTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            mTitleLl.addView(titleView);
        }
    }

    @Override
    public void setStatisticsResultList(List<List<StatisticsItem>> data, int totalSum, boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mCurrentPageNum = 1;
        } else {
            mCurrentPageNum = mCurrentPageNum + 1;
        }

        mAdapter.addAll(data);
        if (mAdapter.getCount() >= totalSum) {
            mAdapter.stopMore();
        }
        mMultiStateLayout.showContentView();
    }

    @Override
    public void setStatisticsResultEmpty() {
        mTitleLl.setVisibility(View.GONE);
        mStatisticsTotalInfoRl.setVisibility(View.GONE);

        mAdapter.clear();
        mMultiStateLayout.showEmpty();
    }

    @Override
    public void setStatisticsResultError(boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mMultiStateLayout.showError();
            mMultiStateLayout.showRetryError(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.getStatisticsResult(mClassifyBean.getId(), mCurrentPageNum);
                }
            });
        } else {
            mAdapter.pauseMore();
        }
    }

    @Override
    public void setTotalInfo(String info1, String info2) {
        mStatisticsTotalInfoRl.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(info1)) {
            mTotalInfoOneTv.setVisibility(View.VISIBLE);
            mTotalInfoOneTv.setText(info1);
        } else {
            mTotalInfoOneTv.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(info2)) {
            mTotalInfoTwoTv.setVisibility(View.VISIBLE);
            mTotalInfoTwoTv.setText(info2);
        } else {
            mTotalInfoTwoTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void noPermissionPrompt(int msgId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                LoginActivity.startActivity(StatisticsResultListActivity.this);
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
        mPresenter.delStatisticsCondition();
        mPresenter.detachView();
    }


    //检查打电话权限
    private void initCallPhonePermission(final String phoneNumber) {
        if (Build.VERSION.SDK_INT < 23) {
            startPhoneCall(phoneNumber);
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(Manifest.permission.CALL_PHONE)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {
                            startPhoneCall(phoneNumber);
                        } else if (permission.shouldShowRequestPermissionRationale) {//下次继续提示(应该显示请求许可理由)

                        } else {
                            showSetPermissionPopUpWindow();
                        }
                    }
                });
    }

    private PermissionsDialogFragment mCallPhoneDialogFragment;

    //展示设置权限弹窗
    private void showSetPermissionPopUpWindow() {
        if (null == mCallPhoneDialogFragment) {
            mCallPhoneDialogFragment = new PermissionsDialogFragment();
        }
        if (mCallPhoneDialogFragment.isAdded()) {
            return;
        }
        mCallPhoneDialogFragment.initPermissionUI(PermissionsDialogFragment.PERMISSION_CALL_PHONE);
        mCallPhoneDialogFragment.show(this.getFragmentManager(), "PermissionsDialogFragment");
    }

    /**
     * 开始拨打电话
     */
    private void startPhoneCall(String phoneNumber) {
        if (null == phoneNumber || TextUtils.isEmpty(phoneNumber)) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) ==
                PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + phoneNumber);
            intent.setData(data);
            startActivity(intent);
        }

    }
}
