package com.tzpt.cloundlibrary.manager.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseListActivity;
import com.tzpt.cloundlibrary.manager.bean.FlowManageDetailBookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.FlowManageListBean;
import com.tzpt.cloundlibrary.manager.ui.adapter.FlowManagementDetailAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.FlowManagementOperationContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.FlowManagementOperationPresenter;
import com.tzpt.cloundlibrary.manager.utils.AllCapTransformationMethod;
import com.tzpt.cloundlibrary.manager.utils.KeyboardUtils;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.OnLoadMoreListener;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 流出录入操作界面
 */
public class FlowManagementOperationActivity extends BaseListActivity<FlowManageDetailBookInfoBean> implements
        FlowManagementOperationContract.View,
        OnLoadMoreListener {
    private static final String FLOW_OPERATION_LIST_BEAN = "FlowManageListBean";
    private static final String FLOW_OPERATION_FROM_TYPE = "FlowManageListBean_from_type";

    /**
     * @param context  上下文
     * @param listBean 对象
     * @param fromType 新增0，修改1，清点删单2 ，直接删单3，撤回4
     */
    public static void startActivity(Activity context, FlowManageListBean listBean, int fromType) {
        Intent intent = new Intent(context, FlowManagementOperationActivity.class);
        intent.putExtra(FLOW_OPERATION_LIST_BEAN, listBean);
        intent.putExtra(FLOW_OPERATION_FROM_TYPE, fromType);
        context.startActivityForResult(intent, 1000);
    }

    @BindView(R.id.flow_header_library_info_tv)
    public TextView mFlowHeaderLibraryInfo;
    @BindView(R.id.flow_header_user_info_tv)
    public TextView mFlowHeaderUserInfo;
    @BindView(R.id.statistics_total_info_one_tv)
    public TextView mFlowBottomNumber;
    @BindView(R.id.statistics_total_info_two_tv)
    public TextView mFlowBottomPrice;
    @BindView(R.id.edit_code_et)
    public EditText mEditCodeEt;
    @BindView(R.id.post_btn)
    public Button mBtnPost;
    @BindView(R.id.flow_book_delete)
    public TextView mFlowBookDelete;
    @BindView(R.id.edit_code_rl)
    public RelativeLayout mEditCodeLayout;
    @BindView(R.id.operation_bottom_total_layout)
    public RelativeLayout mOperationBottomTotalLayout;

    private int mCurrentPage = 1;
    private int mFromType;//来自类型  新增0，修改1，清点删单2 ，直接删单3，撤回4
    private FlowManagementOperationPresenter mPresenter;

    @OnClick({R.id.scan_code_btn, R.id.post_btn, R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.scan_code_btn:        //进入扫描界面
                Intent intent = new Intent(FlowManagementOperationActivity.this, FlowManagementOperationsScanActivity.class);
                intent.putExtra(FLOW_OPERATION_FROM_TYPE, mFromType);
                startActivityForResult(intent, 1002);
                break;
            case R.id.post_btn:             //发送
                mPresenter.operationFlowManageSingle(mFromType);
                break;
            case R.id.titlebar_left_btn:    //返回
                refreshFlowManageListAfterFinish();
                break;
        }
    }

    /**
     * 执行删除按钮-删除图书
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String id = (String) v.getTag();
            if (null != id) {
                mPresenter.deleteFlowManageBookInfo(mFromType, id);
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_flow_management_operation;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle(getString(R.string.out_of_entry));
    }

    @Override
    public void initDatas() {
        mPresenter = new FlowManagementOperationPresenter();
        mPresenter.attachView(this);

        Intent intent = getIntent();
        //1.管理列表对象
        FlowManageListBean flowManageListBean = (FlowManageListBean) intent.getSerializableExtra(FLOW_OPERATION_LIST_BEAN);
        //2.新增0，修改1，清点删单2 ，直接删单3，撤回4
        mFromType = intent.getIntExtra(FLOW_OPERATION_FROM_TYPE, -1);
        mPresenter.setFromType(mFromType);
        //3.设置头部用户图书馆信息
        mPresenter.setManageListBean(flowManageListBean);
        //5.请求列表数据-0新增不需要获取详情
        if (mFromType != 0) {
            mPresenter.getFlowManageDetail(1);
        } else {
            setHeadInfoForAdd(flowManageListBean);
        }
    }

    //设置新增头部
    private void setHeadInfoForAdd(FlowManageListBean flowManageListBean) {
        if (null != flowManageListBean) {
            String inHallCode = flowManageListBean.inHallCode;
            String libraryName = flowManageListBean.name;
            String conPerson = flowManageListBean.conperson;
            String phone = flowManageListBean.phone;
            //图书馆信息
            StringBuilder libraryText = new StringBuilder()
                    .append(TextUtils.isEmpty(inHallCode) ? "" : inHallCode)
                    .append(" ")
                    .append(TextUtils.isEmpty(libraryName) ? "" : libraryName);
            //用户信息
            StringBuilder conPersonInfo = new StringBuilder();
            if (!TextUtils.isEmpty(conPerson)) {
                if (conPerson.length() > 3) {
                    conPerson = conPerson.substring(0, 3) + "...";
                }
            } else {
                conPerson = "";
            }
            conPersonInfo.append(conPerson);
            conPersonInfo.append(" ").append(TextUtils.isEmpty(phone) ? "" : phone);
            setHeadInfo(libraryText, conPersonInfo);
        }
    }

    @Override
    public void configViews() {
        setTitleAndDetailStatus();

        mEditCodeEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                KeyboardUtils.hideSoftInput(mEditCodeEt);
                String barNumber = mEditCodeEt.getText().toString().trim();
                //执行自动补全
//                String allBarNumber = StringUtils.performAutomaticCompletion(barNumber);
//                mEditCodeEt.setText(allBarNumber);
//                mEditCodeEt.setSelection(allBarNumber.length());
                mPresenter.operationFlowManageEditValueByFromType(barNumber, mFromType);
                return false;
            }
        });
    }

    @Override
    public void setHeadInfo(CharSequence libraryText, CharSequence conPersonInfo) {
        mFlowHeaderLibraryInfo.setText(libraryText);
        mFlowHeaderUserInfo.setText(conPersonInfo);
    }


    @Override
    public void onItemClick(int position) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002 && resultCode == RESULT_OK) {
            mPresenter.getBookList();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.delFlowManageListBean();
            mPresenter.delOrderNumber();
            mPresenter.delDetailBookList();
            mAdapter.clear();
            mPresenter.detachView();
        }
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
        showMessageDialog(getString(msgId));
        mRecyclerView.setRefreshing(false);
    }

    @Override
    public void complete() {
        mRecyclerView.setRefreshing(false);
    }

    @Override
    public void setCodeEditTextHint(String hint) {
        mEditCodeEt.setHint(hint);
    }


    /**
     * 撤回弹出框
     *
     * @param circulateId
     */
    @Override
    public void showReCallDialog(final String circulateId) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(R.string.sure_to_withdraw_this_list));
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mPresenter.reCallSingleByCirculateId();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onLoadMore() {
        mPresenter.getFlowManageDetail(mCurrentPage + 1);
    }

    /**
     * 设置标题和详情状态
     */
    private void setTitleAndDetailStatus() {
        int titleId = R.string.out_of_entry;
        int buttonNameId = R.string.send;
        boolean hasSendButton = true;
        boolean hasOperationButton = true;
        boolean hasInputLayout = true; //是否有输入布局
        switch (mFromType) {
            case 0://新增0
                titleId = R.string.out_of_entry;
                buttonNameId = R.string.send;
                hasSendButton = false;
                hasOperationButton = true;
                hasInputLayout = true;
                //新增开始不显示合计信息
                showTotalLayout(false, false);
                break;
            case 1://修改1
                titleId = R.string.out_of_entry;
                buttonNameId = R.string.send;
                hasSendButton = false;
                hasOperationButton = true;
                hasInputLayout = true;
                break;
            case 2://清点删单2
                titleId = R.string.counting_the_delete_single;
                buttonNameId = 0;
                hasSendButton = false;
                hasOperationButton = false;
                hasInputLayout = true;
                break;
            case 3://直接删单3
                titleId = R.string.direct_delete;
                buttonNameId = R.string.delete_a_single;
                hasSendButton = true;
                hasOperationButton = false;
                hasInputLayout = false;
                break;
            case 4://撤回4
                titleId = R.string.to_withdraw;
                buttonNameId = R.string.confirm;
                hasSendButton = true;
                hasOperationButton = false;
                hasInputLayout = false;
                break;
        }
        //设置标题
        mCommonTitleBar.setTitle(getString(titleId));
        //是否有发送按钮
        mBtnPost.setVisibility(hasSendButton ? View.VISIBLE : View.GONE);
        //设置按钮名称
        mBtnPost.setText(buttonNameId == 0 ? "" : getString(buttonNameId));
        //是否有操作按钮
        mFlowBookDelete.setVisibility(hasOperationButton ? View.VISIBLE : View.GONE);
        //列表展示是否展示删除按钮
        mAdapter = new FlowManagementDetailAdapter(this, hasOperationButton, hasOperationButton ? mOnClickListener : null);
        initAdapter(false, true);
        //是否显示输入布局
        if (!hasInputLayout) {
            mEditCodeLayout.setVisibility(View.GONE);
        }
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

    @Override
    public void showTotalLayout(boolean showTotalLayout, boolean showBtn) {
        mOperationBottomTotalLayout.setVisibility(showTotalLayout ? View.VISIBLE : View.GONE);
        mBtnPost.setVisibility(showBtn ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置书籍列表信息
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
        //如果当前是新增或者修改书籍列表，则不显示加载更多
        if (mFromType == 0 || mFromType == 1) {
            mAdapter.stopMore();
        }
    }

    @Override
    public void setFlowDetailBookInfoListEmpty(boolean refresh, boolean deleteSingleCountBook) {
        if (refresh) {
            mAdapter.clear();
            if (mFromType == 2 && deleteSingleCountBook) {//清点删单，没有发现0条数据
                mRecyclerView.setEmptyView(R.layout.common_footer_nomore_view);
            } else {
                mRecyclerView.setEmptyView(R.layout.common_footer_empty_view);
            }
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    /**
     * 发送清单成功-如果是修改，需刷新之前列表
     */
    @Override
    public void sendSuccess() {
        if (mFromType == 0 || mFromType == 1) {
            showToastAndFinishView(getString(R.string.success));
        }
    }

    /**
     * 发送成功，关闭界面
     *
     * @param msg
     */
    public void showToastAndFinishView(String msg) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                //销毁以后，回去刷新流出管理列表
                refreshFlowManageListAfterFinish();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    /**
     * 直接删单成功
     */
    @Override
    public void directDeleteSingleSuccess() {
        //销毁以后，回去刷新流出管理列表
        showToastAndFinishView(getString(R.string.success));
    }

    /**
     * 撤回成功
     */
    @Override
    public void reCallSuccess() {
        //销毁以后，回去刷新流出管理列表
        showToastAndFinishView(getString(R.string.success));
    }

    /***
     * 销毁以后，回去刷新流出管理列表
     */
    private void refreshFlowManageListAfterFinish() {
        if (null != mPresenter && mPresenter.isRefresh()) {
            Intent intent = new Intent();
            intent.putExtra("refresh", true);
            setResult(1001, intent);
        }
        this.finish();
    }

    //流出管理新增没有新书
    @Override
    public void setNoFlowManageNewBookInfo() {
        showMessageDialog(getString(R.string.bar_code_error));
    }

    @Override
    public void refreshBookList(List<FlowManageDetailBookInfoBean> list) {
        mAdapter.clear();
        mAdapter.addAll(list);
        mAdapter.notifyDataSetChanged();
        mAdapter.stopMore();
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
                LoginActivity.startActivity(FlowManagementOperationActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 如果按了返回键
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            refreshFlowManageListAfterFinish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
