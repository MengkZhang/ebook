package com.tzpt.cloudlibrary.ui.account.deposit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.bean.UserLibraryDepositBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.utils.DpPxUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 馆押金明细
 * Created by ZhiqiangJia on 2017-08-24.
 */
public class UserLibraryDepositActivity extends BaseListActivity<UserLibraryDepositBean> implements
        UserLibraryDepositContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, UserLibraryDepositActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.library_deposit_code)
    TextView mLibraryDepositCode;
    @BindView(R.id.library_deposit_name)
    TextView mLibraryDepositName;
    @BindView(R.id.library_deposit_money)
    TextView mLibraryDepositMoney;
    @BindView(R.id.library_deposit_can_use)
    TextView mLibraryDepositCanUse;
    @BindView(R.id.library_deposit_parent)
    RelativeLayout mLibraryDepositParent;

    private int mCurrentPage = 1;
    private UserLibraryDepositPresenter mPresenter;


    @Override
    public int getLayoutId() {
        return R.layout.activity_user_library_deposit;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("单位押金明细");
    }

    @Override
    public void initDatas() {
        mPresenter = new UserLibraryDepositPresenter();
        mPresenter.attachView(this);
        mPresenter.getUserLibraryDepositList(1);
    }

    @Override
    public void configViews() {
        mLibraryDepositCode.setTextColor(Color.parseColor("#989898"));
        mLibraryDepositName.setTextColor(Color.parseColor("#989898"));
        mLibraryDepositMoney.setTextColor(Color.parseColor("#989898"));
        mLibraryDepositCanUse.setTextColor(Color.parseColor("#989898"));
        mAdapter = new UserLibraryDepositAdapter(this);
        initAdapter(false, true);
        mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_default);
    }

    @Override
    public void onRefresh() {
        mPresenter.getUserLibraryDepositList(1);
    }

    @Override
    public void onLoadMore() {
        mPresenter.getUserLibraryDepositList(mCurrentPage + 1);
    }

    @Override
    public void onItemClick(int position) {

    }

    @OnClick(R.id.titlebar_left_btn)
    public void onViewClicked(View v) {
        finish();
    }

    @Override
    public void setNetError() {
        mRecyclerView.setRefreshing(false);
        if (mAdapter.getCount() < 1) {
            mAdapter.clear();
            mRecyclerView.showError();
            mRecyclerView.setRetryRefreshListener(this);
        } else {
            mAdapter.pauseMore();
        }
    }

    @Override
    public void setLibraryDepositList(List<UserLibraryDepositBean> libraryDepositBeanList, int totalCount, boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mLibraryDepositParent.setVisibility(View.VISIBLE);
        mAdapter.addAll(libraryDepositBeanList);
        mRecyclerView.setRefreshing(false);
        if (mAdapter.getCount() >= totalCount) {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setLibraryDepositListEmpty(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);

        finish();
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
        if (null != mPresenter) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }
}
