package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseListActivity;
import com.tzpt.cloundlibrary.manager.bean.FlowManageDetailBookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.FlowManageListBean;
import com.tzpt.cloundlibrary.manager.ui.adapter.FlowManagementDetailAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.FlowManagementDetailContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.FlowManagementDetailPresenter;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.OnLoadMoreListener;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 流出管理详情-已批准0，已完成1，通还2
 */
public class FlowManagementDetailActivity extends BaseListActivity<FlowManageDetailBookInfoBean> implements
        FlowManagementDetailContract.View,
        OnLoadMoreListener {
    private static final String FLOW_MANAGE_LIST_BEAN = "FlowManageListBean";
    private static final String FLOW_MANAGE_FROM_TYPE = "FlowManageListBean_from_type";

    /**
     * @param context  上下文
     * @param listBean 对象
     * @param fromType 已批准0，已完成1，通还2 未发送3（只读状态下）
     *                 1未发送，2已发送，3.被否决 4.已批准5.被拒收6.已完成 -1通还
     */
    public static void startActivity(Context context, FlowManageListBean listBean, int fromType) {
        Intent intent = new Intent(context, FlowManagementDetailActivity.class);
        intent.putExtra(FLOW_MANAGE_LIST_BEAN, listBean);
        intent.putExtra(FLOW_MANAGE_FROM_TYPE, fromType);
        context.startActivity(intent);
    }

    @BindView(R.id.flow_header_library_info_tv)
    public TextView mFlowHeaderLibraryInfo;
    @BindView(R.id.flow_header_user_info_tv)
    public TextView mFlowHeaderUserInfo;
    @BindView(R.id.flow_bottom_show_msg_tv)
    public TextView mFlowBottomShowMsg;
    @BindView(R.id.statistics_total_info_one_tv)
    public TextView mFlowBottomNumber;
    @BindView(R.id.statistics_total_info_two_tv)
    public TextView mFlowBottomPrice;
    private int mCurrentPage = 1;
    private FlowManagementDetailContract.Presenter mPresenter;

    private int mFromType;//来自类型  1未发送，2已发送，3.被否决 4.已批准5.被拒绝6.已完成 -1通还

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
        return R.layout.activity_flow_management_detail;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("详情");
    }

    @Override
    public void initDatas() {
        mPresenter = new FlowManagementDetailPresenter();
        mPresenter.attachView(this);
        Intent intent = getIntent();
        //1.管理列表对象
        FlowManageListBean flowManageListBean = (FlowManageListBean) intent.getSerializableExtra(FLOW_MANAGE_LIST_BEAN);
        if (null != flowManageListBean) {
            mPresenter.setFlowManageListBeanIdAndState(flowManageListBean.id, flowManageListBean.outState);
        }
        //2.1未发送，2已发送，3.被否决 4.已批准5.被拒绝6.已完成 -1通还
        mFromType = intent.getIntExtra(FLOW_MANAGE_FROM_TYPE, -1);
        //4.设置标题和状态信息
        setTitleAndDetailStatus(flowManageListBean);
        //5.请求列表数据
        mPresenter.getFlowManageDetail(1);
    }

    @Override
    public void setHeadInfo(CharSequence libraryText, CharSequence conPersonInfo, String auditContactName, String auditDate) {
        //设置 图书馆信息和用户信息
        mFlowHeaderLibraryInfo.setText(libraryText);
        mFlowHeaderUserInfo.setText(conPersonInfo);
        //如果状态为已审核，则显示审核人信息
        if (mFromType == 4) {
            StringBuffer message = new StringBuffer()
                    .append(auditContactName)
                    .append("于")
                    .append(auditDate)
                    .append("批准本单，等待签收!");
            mFlowBottomShowMsg.setVisibility(View.VISIBLE);
            mFlowBottomShowMsg.setText(message);
        }
    }

    /**
     * 设置头部标题和本单状态信息
     * 1未发送，2已发送，3.被否决 4.已批准5.被拒收6.已完成 -1通还
     */
    private void setTitleAndDetailStatus(FlowManageListBean bean) {
        String title = "详情";
        String message = "";
        switch (mFromType) {
            case 1://未发送（只读状态下）
                title = "未发送";
                message = "本单未发送！";
                break;
            case 2:
                title = "已发送";
                message = "本单已发送！";
                break;
            case 3:
                title = "被否决";
                message = "本单被否决！";
                break;
            case 4://已批准4
                title = "已批准";
                message = "";
                break;
            case 5:
                title = "被拒收";
                message = "本单被拒收！";
                break;
            case 6://已完成6
                title = "已完成";
                message = "本单已收货！";
                break;
            case -1://通还-1
                title = "通还";
                message = "";
                break;

        }
        mCommonTitleBar.setTitle(title);
        if (TextUtils.isEmpty(message)) {
            mFlowBottomShowMsg.setVisibility(View.GONE);
        } else {
            mFlowBottomShowMsg.setText(message);
        }
    }

    @Override
    public void configViews() {
        mAdapter = new FlowManagementDetailAdapter(this, false, null);
        initAdapter(false, true);
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void showProgressLoading() {
        showDialog(getString(R.string.loading));
    }

    @Override
    public void dismissProgressLoading() {
        dismissDialog();
    }

    @Override
    public void showMsgDialog(String msg) {
        showMessageDialog(msg);
        mRecyclerView.setRefreshing(false);
    }

    @Override
    public void showMsgDialog(int msgId) {
        showMsgDialog(getString(msgId));
    }

    @Override
    public void complete() {
        mRecyclerView.setRefreshing(false);
    }

    @Override
    public void onLoadMore() {
        mPresenter.getFlowManageDetail(mCurrentPage + 1);
    }


    /**
     * 设置合计信息
     *
     * @param totalSum 数量
     * @param priceSum 金额
     */
    @Override
    public void setFlowDetailTotalSumInfo(int totalSum, String priceSum) {
        mFlowBottomNumber.setText(String.format(getString(R.string.total_sum_one), String.valueOf(totalSum)));
        mFlowBottomPrice.setText(String.format(getString(R.string.total_price_one), StringUtils.StringFormatToDouble2bit2(priceSum)));
    }

    /**
     * 流出管理详情列表
     *
     * @param flowManageDetailBookInfoList 图书列表
     * @param totalSum                     数量
     * @param refresh                      是否刷新
     */
    @Override
    public void setFlowDetailBookInfoList(List<FlowManageDetailBookInfoBean> flowManageDetailBookInfoList, int totalSum, boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(flowManageDetailBookInfoList);
        if (mAdapter.getCount() >= totalSum) {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setFlowDetailBookInfoListEmpty(boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void noPermissionPrompt(int kickedOffline) {
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
                LoginActivity.startActivity(FlowManagementDetailActivity.this);
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
        mAdapter.clear();
        mPresenter.detachView();
    }

}
