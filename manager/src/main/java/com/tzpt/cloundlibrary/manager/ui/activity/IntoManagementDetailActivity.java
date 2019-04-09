package com.tzpt.cloundlibrary.manager.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseListActivity;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.FlowManageDetailBookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.IntoManagementListBean;
import com.tzpt.cloundlibrary.manager.ui.adapter.IntoManagementDetailAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.IntoManagementDetailContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.IntoManagementDetailPresenter;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.OnLoadMoreListener;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 流入详情列表
 */
public class IntoManagementDetailActivity extends BaseListActivity<BookInfoBean> implements
        IntoManagementDetailContract.View,
        OnLoadMoreListener {
    private static final String OPERATION_LIST_BEAN = "IntoManageListBean";
    private static final String OPERATION_FROM_TYPE = "IntoManageListBean_from_type";

    /**
     * @param context  上下文
     * @param listBean 对象
     * @param fromType 0 已审核  1 未审核 2 已完成 3 通还
     */
    public static void startActivity(Activity context, IntoManagementListBean listBean, int fromType) {
        Intent intent = new Intent(context, IntoManagementDetailActivity.class);
        intent.putExtra(OPERATION_LIST_BEAN, listBean);
        intent.putExtra(OPERATION_FROM_TYPE, fromType);
        context.startActivityForResult(intent, 1000);
    }

    @BindView(R.id.post_btn)
    public Button mBtnPost;
    @BindView(R.id.into_bottom_show_msg_tv)
    public TextView mIntoBottomShowMsg;
    @BindView(R.id.flow_header_library_info_tv)
    public TextView mFlowHeaderLibraryInfo;
    @BindView(R.id.flow_header_user_info_tv)
    public TextView mFlowHeaderUserInfo;
    @BindView(R.id.statistics_total_info_one_tv)
    public TextView mFlowBottomNumber;
    @BindView(R.id.statistics_total_info_two_tv)
    public TextView mFlowBottomPrice;
    private IntoManagementDetailContract.Presenter mPresenter;
    private int mCurrentPage = 1;
    private int mFromType = -1;

    @OnClick({R.id.titlebar_left_btn, R.id.post_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                this.finish();
                break;
            case R.id.post_btn:// 0签收 1拒收
                if (mFromType == 0) {       //签收
                    showToastDialogForThisSingle("<html><font color='#ff0000'>确定签收本单吗？</font></html>", 0);
                } else if (mFromType == 4) {//拒收
                    showToastDialogForThisSingle("<html><font color='#ff0000'>确定拒收本单吗？</font></html>", 1);
                }
                break;
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_into_management_detail;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("详情");
    }

    @Override
    public void initDatas() {
        mPresenter = new IntoManagementDetailPresenter();
        mPresenter.attachView(this);

        Intent intent = getIntent();
        mFromType = intent.getIntExtra(OPERATION_FROM_TYPE, -1);
        IntoManagementListBean managementListBean = (IntoManagementListBean) intent.getSerializableExtra(OPERATION_LIST_BEAN);
        if (null != managementListBean) {
            mPresenter.setIntoManagementListBeanId(managementListBean.id);
        }
        //设置头部信息
        setHeadInfoByFromType();
        mPresenter.getIntoManageSingDetail(1);
    }

    @Override
    public void setHeadInfo(CharSequence libraryInfo, CharSequence libraryUser) {
        mFlowHeaderLibraryInfo.setText(libraryInfo);
        mFlowHeaderUserInfo.setText(libraryUser);
    }

    /**
     * 设置头部信息
     * <p>
     * fromType 0 已审核  1 未审核 2 已完成 3 通还
     * titleName     标题
     * result        显示结果
     * buttonTxt     按钮文本
     * hasMessageTxt 是否有显示文本
     * hasSendButton 是否有发送按钮
     */
    private void setHeadInfoByFromType() {
        String titleName = "详情";
        String result = "";
        String buttonTxt = "";
        boolean hasMessageTxt = false;
        boolean hasSendButton = false;
        switch (mFromType) {
            case 0://已审核
                titleName = "签收";
                result = "";
                buttonTxt = "确认";
                hasMessageTxt = false;
                hasSendButton = true;
                break;
            case 1://未审核
                titleName = "未审核";
                result = "本单未审核！";
                buttonTxt = "";
                hasMessageTxt = true;
                hasSendButton = false;
                break;
            case 2://已完成
                titleName = "已完成";
                result = "本单已完成！";
                buttonTxt = "";
                hasMessageTxt = true;
                hasSendButton = false;
                break;
            case 3://通还
                titleName = "通还";
                result = "";
                buttonTxt = "";
                hasMessageTxt = false;
                hasSendButton = false;
                break;
            case 4://拒收
                titleName = "拒收";
                result = "";
                buttonTxt = "确认";
                hasMessageTxt = false;
                hasSendButton = true;
                break;
        }
        mCommonTitleBar.setTitle(titleName);
        //是否有发送按钮
        mBtnPost.setVisibility(hasSendButton ? View.VISIBLE : View.GONE);
        //设置按钮名称
        mBtnPost.setText(buttonTxt);
        //设置显示信息
        mIntoBottomShowMsg.setVisibility(hasMessageTxt ? View.VISIBLE : View.GONE);
        mIntoBottomShowMsg.setText(result);
    }

    @Override
    public void configViews() {
        mAdapter = new IntoManagementDetailAdapter(this);
        initAdapter(false, true);
    }

    /**
     * 展示弹出框
     *
     * @param message 消息
     * @param type    0签收 1拒收
     */
    private void showToastDialogForThisSingle(String message, final int type) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, message);
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.setButtonTextConfirmOrYes(true);
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mPresenter.operationThisSingle(type);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.setTextForHtml(message);
        dialog.show();
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
    public void showMsgDialog(int msgId) {
        mRecyclerView.setRefreshing(false);
        showMessageDialog(getString(msgId));
    }

    @Override
    public void onLoadMore() {
        mPresenter.getIntoManageSingDetail(mCurrentPage + 1);
    }

    @Override
    public void setIntoDetailTotalSumInfo(int totalSum, String priceSum) {
        mFlowBottomNumber.setText(String.format(getString(R.string.total_sum_one), String.valueOf(totalSum)));
        mFlowBottomPrice.setText(String.format(getString(R.string.total_price_one), StringUtils.StringFormatToDouble2bit2(priceSum)));
    }

    /**
     * 设置流入详情列表
     *
     * @param bookInfoBeanList 流入列表
     * @param totalSum                     数量
     * @param refresh                      是否刷新
     */
    @Override
    public void setIntoDetailBookInfoList(List<BookInfoBean> bookInfoBeanList, int totalSum, boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(bookInfoBeanList);
        if (mAdapter.getCount() >= totalSum) {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setIntoDetailBookInfoListEmpty(boolean refresh) {
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


    /**
     * 拒收成功
     */
    @Override
    public void rejectionThisSingleSuccess() {
        showToastDialog(getString(R.string.success), true);
    }

    /**
     * 签收成功
     */
    @Override
    public void signThisSingleSuccess() {
        showToastDialog(getString(R.string.success), true);
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
                LoginActivity.startActivity(IntoManagementDetailActivity.this);
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

    /**
     * 展示弹出框
     *
     * @param message 消息
     * @param finish  是否消失界面
     */
    private void showToastDialog(String message, final boolean finish) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, message);
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                if (finish) {
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
        dialog.setText(message);
        dialog.show();
    }
}
