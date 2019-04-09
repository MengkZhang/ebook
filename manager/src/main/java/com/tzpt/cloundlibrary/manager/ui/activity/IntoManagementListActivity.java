package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseListActivity;
import com.tzpt.cloundlibrary.manager.bean.IntoManagementListBean;
import com.tzpt.cloundlibrary.manager.ui.adapter.IntoManagementListAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.IntoManagementListContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.IntoManagementListPresenter;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.OnLoadMoreListener;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.swipe.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 流入管理列表
 * Created by ZhiqiangJia on 2017-07-10.
 */
public class IntoManagementListActivity extends BaseListActivity<IntoManagementListBean> implements
        IntoManagementListContract.View,
        OnLoadMoreListener {
    @BindView(R.id.library_number)
    public TextView mLibraryNumber;
    @BindView(R.id.recycler_view)
    public EasyRecyclerView mRecyclerView;
    @BindView(R.id.statistics_total_info_one_tv)
    public TextView mFlowBottomNumber;
    @BindView(R.id.statistics_total_info_two_tv)
    public TextView mFlowBottomPrice;
    @BindView(R.id.flow_parent_layout)
    public LinearLayout mFlowParentLayout;
    private int mCurrentPage = 1;
    private IntoManagementListContract.Presenter mPresenter;

    @OnClick({R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                this.finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_into_management_list;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("流入列表");
    }

    @Override
    public void initDatas() {
        mLibraryNumber.setText("流出馆");
        mPresenter = new IntoManagementListPresenter();
        mPresenter.attachView(this);

        mPresenter.getIntoManageList(1);
    }

    @Override
    public void configViews() {
        initAdapter(IntoManagementListAdapter.class, false, true);

        if (null != mAdapter && mAdapter instanceof IntoManagementListAdapter) {
            IntoManagementListAdapter adapter = (IntoManagementListAdapter) mAdapter;
            adapter.setInToManagementListener(new IntoManagementListAdapter.CallbackChangeIntoOperation() {
                @Override
                public void callbackChangeIntoManage(IntoManagementListBean item) {
                    //2未审核4已审核6已完成-1通还
                    switch (item.inState) {
                        case 4://已审核-签收
                            IntoManagementDetailActivity.startActivity(IntoManagementListActivity.this, item, 0);
                            break;
                        case 2://未审核-详情
                            IntoManagementDetailActivity.startActivity(IntoManagementListActivity.this, item, 1);
                            break;
                        case 6://已完成-详情
                            IntoManagementDetailActivity.startActivity(IntoManagementListActivity.this, item, 2);
                            break;
                        case -1://通还-详情
                            IntoManagementDetailActivity.startActivity(IntoManagementListActivity.this, item, 3);
                            break;
                    }
                }

                @Override
                public void callbackRefuseAcceptIntoManage(IntoManagementListBean item) {
                    if (item.inState == 4) {//已审核-拒收
                        IntoManagementDetailActivity.startActivity(IntoManagementListActivity.this, item, 4);
                    }
                }
            });
        }
    }

    @Override
    public void showError(boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showError();
            mRecyclerView.setRetryRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mPresenter.getIntoManageList(1);
                }
            });
        } else {
            mAdapter.pauseMore();
        }
    }

    @Override
    public void onLoadMore() {
        mPresenter.getIntoManageList(mCurrentPage + 1);
    }

    /**
     * 设置合计信息
     *
     * @param totalAllSum   数量
     * @param totalAllPrice 价格
     */
    @Override
    public void setIntoManageTotalInfo(int totalAllSum, String totalAllPrice) {
        mFlowBottomNumber.setText(String.format(getString(R.string.total_sum_one), String.valueOf(totalAllSum)));
        mFlowBottomPrice.setText(String.format(getString(R.string.total_price_one), StringUtils.StringFormatToDouble2bit2(totalAllPrice)));
    }

    /**
     * 设置流入列表
     *
     * @param flowManageListBeanList 流入列表
     * @param totalAllSum            数量
     * @param refresh                是否刷新
     */
    @Override
    public void setIntoManagementList(List<IntoManagementListBean> flowManageListBeanList, int totalAllSum, boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(flowManageListBeanList);
        if (mAdapter.getCount() >= totalAllSum) {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setIntoManagementListEmpty(boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void complete() {
        mRecyclerView.setRefreshing(false);
    }

    @Override
    public void setNoLoginPermission(int kickedOffline) {
        mRecyclerView.setRefreshing(false);
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
                LoginActivity.startActivity(IntoManagementListActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }


    @Override
    public void onItemClick(int position) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            mPresenter.getIntoManageList(1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.clear();
        mPresenter.detachView();
    }
}
