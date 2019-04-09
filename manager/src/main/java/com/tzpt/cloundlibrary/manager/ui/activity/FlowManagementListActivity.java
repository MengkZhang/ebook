package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseListActivity;
import com.tzpt.cloundlibrary.manager.bean.FlowManageListBean;
import com.tzpt.cloundlibrary.manager.ui.adapter.FlowManagementListAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.FlowManagementcontract;
import com.tzpt.cloundlibrary.manager.ui.presenter.FlowManagementListPresenter;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;
import com.tzpt.cloundlibrary.manager.widget.popupwindow.FlowDeleteSinglePPW;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.OnLoadMoreListener;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.swipe.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 流出管理列表
 */
public class FlowManagementListActivity extends BaseListActivity<FlowManageListBean> implements
        FlowManagementcontract.View,
        OnLoadMoreListener {

    @BindView(R.id.recycler_view)
    public EasyRecyclerView mRecyclerView;
    @BindView(R.id.statistics_total_info_one_tv)
    public TextView mFlowBottomNumber;
    @BindView(R.id.statistics_total_info_two_tv)
    public TextView mFlowBottomPrice;
    @BindView(R.id.flow_parent_layout)
    public LinearLayout mFlowParentLayout;

    private FlowManagementListPresenter mPresenter;
    private int mCurrentPage = 1;

    @OnClick({R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                this.finish();
                break;
        }
    }

    /**
     * 操作流出管理，修改，详情，通还，撤回，删除
     */
    private FlowManagementListAdapter.CallbackChangeFlowOperation mListener = new FlowManagementListAdapter.CallbackChangeFlowOperation() {
        /**
         * 进入编辑 新增0，修改1，清点删单2 ，直接删单3，撤回4
         * 进入详情 已批准0，已完成1，通还2
         *
         *
         * 已发送(撤回)
         * @param item
         */
        @Override
        public void callbackChangeFlowManage(FlowManageListBean item) {
            //1未发送，2已发送，3.被否决 4.已批准5.被拒绝6.已完成 -1通还
            if (item.onlyRead) {
                FlowManagementDetailActivity.startActivity(FlowManagementListActivity.this, item, item.outState);
                return;
            }
            switch (item.outState) {
                case 1://未发送 -修改
                    FlowManagementOperationActivity.startActivity(FlowManagementListActivity.this, item, 1);
                    break;
                case 2://已发送 -撤回
                    FlowManagementOperationActivity.startActivity(FlowManagementListActivity.this, item, 4);
                    break;
                case 3://被否决 -修改
                    FlowManagementOperationActivity.startActivity(FlowManagementListActivity.this, item, 1);
                    break;
                case 4://已批准 -详情
                    FlowManagementDetailActivity.startActivity(FlowManagementListActivity.this, item, 4);
                    break;
                case 5://被拒收 -修改
                    FlowManagementOperationActivity.startActivity(FlowManagementListActivity.this, item, 1);
                    break;
                case 6://已完成 -详情
                    FlowManagementDetailActivity.startActivity(FlowManagementListActivity.this, item, 6);
                    break;
                case -1://通还   -详情
                    FlowManagementDetailActivity.startActivity(FlowManagementListActivity.this, item, -1);
                    break;
            }
        }

        /**
         * 删除
         * @param item 清点删单2 ，直接删单3
         */
        @Override
        public void callbackDeleteFlowManage(FlowManageListBean item) {
            if (item.onlyRead) {
                return;
            }
            //1未发送，2已发送，3.被否决 4.已批准5.被拒绝6.已完成 -1通还
            switch (item.outState) {
                case 1://未发送  -删单
                case 3://被否决  -删单
                case 5://被拒收  -删单
                    new FlowDeleteSinglePPW(FlowManagementListActivity.this, mFlowParentLayout, item, new FlowDeleteSinglePPW.CallbackOutflowDelete() {
                        //直接删单
                        @Override
                        public void callbackDirectDelete(FlowManageListBean item) {
                            FlowManagementOperationActivity.startActivity(FlowManagementListActivity.this, item, 3);
                        }

                        //清点删单
                        @Override
                        public void callbackSingleDelete(FlowManageListBean item) {
                            FlowManagementOperationActivity.startActivity(FlowManagementListActivity.this, item, 2);
                        }
                    });
                    break;
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_flow_management_list;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("流出列表");
    }

    @Override
    public void initDatas() {
        mPresenter = new FlowManagementListPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {
        initAdapter(FlowManagementListAdapter.class, false, true);
        if (null != mAdapter && mAdapter instanceof FlowManagementListAdapter) {
            FlowManagementListAdapter adapter = (FlowManagementListAdapter) mAdapter;
            adapter.setFlowManagementListener(mListener);
        }

        mPresenter.getFlowManagementList(1);
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void complete() {
        mRecyclerView.setRefreshing(false);
    }

    @Override
    public void showError(boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showError();
            mRecyclerView.setRetryRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mPresenter.getFlowManagementList(1);
                }
            });
        } else {
            mAdapter.pauseMore();
        }
    }

    @Override
    public void onLoadMore() {
        mPresenter.getFlowManagementList(mCurrentPage + 1);
    }

    /**
     * 设置合计信息
     *
     * @param totalSum   数量
     * @param totalPrice 金额
     */
    @Override
    public void setFlowManageTotalInfo(int totalSum, String totalPrice) {
        mFlowBottomNumber.setText(String.format(getString(R.string.total_sum_one), String.valueOf(totalSum)));
        mFlowBottomPrice.setText(String.format(getString(R.string.total_price_one), StringUtils.StringFormatToDouble2bit2(totalPrice)));
    }

    /**
     * 设置流出管理列表
     *
     * @param flowManageListBeanList 流出管理列表
     * @param refresh                是否刷新
     */
    @Override
    public void setFlowManageListBeanList(List<FlowManageListBean> flowManageListBeanList, int totalSum, boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(flowManageListBeanList);
        if (mAdapter.getCount() >= totalSum) {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setFlowManageListBeanListEmpty(boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
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
                LoginActivity.startActivity(FlowManagementListActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    /**
     * 直接删单后刷新数据-再议
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == 1001) {
            boolean refresh = data.getBooleanExtra("refresh", false);
            if (refresh) {
                mPresenter.getFlowManagementList(1);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.clear();
        mPresenter.delStatisticsCondition();
        mPresenter.detachView();
    }
}
