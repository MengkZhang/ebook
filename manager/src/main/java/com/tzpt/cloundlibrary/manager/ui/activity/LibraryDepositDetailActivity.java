package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseListActivity;
import com.tzpt.cloundlibrary.manager.bean.LibraryDepositDetailBean;
import com.tzpt.cloundlibrary.manager.ui.adapter.LibraryDepositDetailAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.LibraryDepositDetailContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.LibraryDepositDetailPresenter;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 本馆押金明细
 */
public class LibraryDepositDetailActivity extends BaseListActivity<LibraryDepositDetailBean> implements
        LibraryDepositDetailContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LibraryDepositDetailActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.library_deposit_total_tv)
    TextView mDepositDetailTotalTv;
    @BindView(R.id.library_deposit_total_ll)
    LinearLayout mDepositDetailTotalLl;

    private LibraryDepositDetailPresenter mPresenter;
    private int mCurrentPage = 1;
    @OnClick({R.id.titlebar_left_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                this.finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_library_deposit_detail;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("明细");
    }

    @Override
    public void initDatas() {
        mPresenter = new LibraryDepositDetailPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {
        initAdapter(LibraryDepositDetailAdapter.class, false, true);
        mPresenter.getLibraryDepositDetailList(1);
    }


    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onLoadMore() {
        mPresenter.getLibraryDepositDetailList(mCurrentPage + 1);
    }

    @Override
    public void setLibraryDepositDetailList(List<LibraryDepositDetailBean> libraryDepositDetailList, int totalCount, boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(libraryDepositDetailList);

    }

    @Override
    public void setLibraryDepositDetailListEmpty(boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void showNetError(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showError();
        } else {
            mAdapter.pauseMore();
        }
    }

    //设置合计金额
    @Override
    public void setLibraryTotalDeposit(String totalDeposit) {
        mDepositDetailTotalLl.setVisibility(View.VISIBLE);
        mDepositDetailTotalTv.setText(totalDeposit);
    }

    @Override
    public void setNoPermissionDialog(int kickedOffline) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(kickedOffline));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                LoginActivity.startActivity(LibraryDepositDetailActivity.this);
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
        mPresenter.detachView();
    }

}
