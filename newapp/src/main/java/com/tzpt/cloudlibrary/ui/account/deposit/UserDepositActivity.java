package com.tzpt.cloudlibrary.ui.account.deposit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.bean.DepositBalanceBean;
import com.tzpt.cloudlibrary.bean.UserDepositBean;
import com.tzpt.cloudlibrary.business_bean.DepositCategoryBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.utils.MoneyUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.CustomPopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 账单列表
 */
public class UserDepositActivity extends BaseListActivity<UserDepositBean> implements
        UserDepositContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, UserDepositActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.user_deposit_penalty_tv)
    TextView mUserDepositPenaltyTv;
    @BindView(R.id.user_deposit_balance_tv)
    TextView mUserDepositBalanceTv;

    private UserDepositPresenter mPresenter;

    private int mCurrentPage = 1;

    private ArrayList<DepositBalanceBean> mDepositBalanceList = new ArrayList<>();
    private ArrayList<DepositBalanceBean> mPenaltyList = new ArrayList<>();


    private CustomPopupWindow mTypeListPopupWindow;
    private DepositCategoryAdapter mCategoryAdapter;
    private List<DepositCategoryBean> mCategoryData = new ArrayList<>();
    private DepositCategoryBean mCategory = null;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                if (mTypeListPopupWindow == null) {
                    createPopupWindow();
                }

                if (Build.VERSION.SDK_INT >= 24) {
                    mTypeListPopupWindow.showAtLocation(mCommonTitleBar, Gravity.TOP, 0, 0);
                } else {
                    mTypeListPopupWindow.showAsDropDown(mCommonTitleBar, 0, -(int) DpPxUtils.dipToPx(UserDepositActivity.this, 48f));
                }

            }
        }
    };


    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_txt_btn, R.id.user_deposit_balance_tv,
            R.id.user_deposit_penalty_tv})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_txt_btn:
                if (mTypeListPopupWindow == null || !mTypeListPopupWindow.isShowing()) {
                    mHandler.sendEmptyMessageDelayed(100, 100);
                }
                break;
            case R.id.user_deposit_balance_tv:
                if (mDepositBalanceList != null
                        && mDepositBalanceList.size() > 0) {
                    DepositBalanceActivity.startActivity(this, mDepositBalanceList, 0);
                }
                break;
            case R.id.user_deposit_penalty_tv:
                if (mPenaltyList != null
                        && mPenaltyList.size() > 0) {
                    PenaltyListActivity.startActivity(this, mPenaltyList);
                }
                break;
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_deposit;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("账单");
    }

    @Override
    public void initDatas() {
        mPresenter = new UserDepositPresenter();
        mPresenter.attachView(this);
        mPresenter.getUserDepositInfo(1, mCategory);

        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        mAdapter = new UserDepositAdapter(this);
        initAdapter(false, true);
        mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_default);
    }

    @Override
    public void onRefresh() {
        mPresenter.getUserDepositInfo(1, mCategory);
    }

    @Override
    public void onLoadMore() {
        mPresenter.getUserDepositInfo(mCurrentPage + 1, mCategory);
    }

    @Override
    public void onItemClick(int position) {
        UserDepositBean item = mAdapter.getItem(position);
        if (item != null) {
            BillDetailActivity.startActivity(this,
                    item.mMoney,
                    item.mOperation,
                    item.mIsRefund ? "退款方式" : "付款方式",
                    item.mPayRemark,
                    item.mOperationDate,
                    item.mOrderNum,
                    item.mOperateOrder,
                    item.mDeductionMoney,
                    item.mComment,
                    item.mTransactionType);
        }
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
    public void setUserDepositBottomInfo(double penalty, double depositBalance, double occupyDeposit, List<DepositBalanceBean> list, List<DepositBalanceBean> penaltyList) {
        if (penalty > 0) {
            mUserDepositPenaltyTv.setVisibility(View.VISIBLE);
            mUserDepositPenaltyTv.setText(getString(R.string.overdue_fines, MoneyUtils.formatMoney(penalty)));
        } else {
            mUserDepositPenaltyTv.setVisibility(View.GONE);
        }

        if (depositBalance > 0 || occupyDeposit > 0) {
            Drawable drawable = getResources().getDrawable(R.mipmap.ic_orange_arrow);
            mUserDepositBalanceTv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            mUserDepositBalanceTv.setVisibility(View.VISIBLE);
            mUserDepositBalanceTv.setEnabled(true);
            mUserDepositBalanceTv.setText(getString(R.string.deposit_balance_occupy_deposit, MoneyUtils.formatMoney(depositBalance), MoneyUtils.formatMoney(occupyDeposit)));
        } else {
            mUserDepositBalanceTv.setEnabled(false);
            if (mAdapter.getCount() > 0) {
                mUserDepositBalanceTv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                mUserDepositBalanceTv.setVisibility(View.VISIBLE);
                mUserDepositBalanceTv.setText(getString(R.string.deposit_balance_occupy_deposit, MoneyUtils.formatMoney(0), MoneyUtils.formatMoney(0)));
            } else {
                mUserDepositBalanceTv.setVisibility(View.GONE);
            }
        }

        mDepositBalanceList.clear();
        mDepositBalanceList.addAll(list);
        mPenaltyList.clear();
        mPenaltyList.addAll(penaltyList);
    }

    @Override
    public void setUserDepositList(List<UserDepositBean> userDepositBeanList, int totalCount, boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(userDepositBeanList);
        mRecyclerView.setRefreshing(false);
        if (mAdapter.getCount() >= totalCount) {
            mAdapter.stopMore();
        }
        if (userDepositBeanList.size() > 0) {
            mUserDepositBalanceTv.setVisibility(View.VISIBLE);
        } else {
            mUserDepositBalanceTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void setUserDepositListEmpty(boolean refresh) {
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

    }

    @Override
    public void showDialogTip(int resId, final boolean finish) {
        final CustomDialog customDialog = new CustomDialog(this, R.style.DialogTheme);
        customDialog.setCancelable(false);
        customDialog.hasNoCancel(false);
        customDialog.setButtonTextConfirmOrYes(true);
        customDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                customDialog.dismiss();
                if (finish) {
                    finish();
                }
            }

            @Override
            public void onClickCancel() {
                customDialog.dismiss();
            }
        });
        customDialog.setText(getText(resId));
        customDialog.show();
    }

    @Override
    public void setDepositCategory(List<DepositCategoryBean> list) {
        mCategoryData.addAll(list);
        mCategory = mCategoryData.get(0);

        mCommonTitleBar.setRightBtnText(mCategory.mDesc);
        mCommonTitleBar.setRightBtnTextIcon(R.mipmap.ic_title_bar_category);
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

        //取消事件订阅
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receviceLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }


    private void createPopupWindow() {
        //设置选中状态
        for (DepositCategoryBean bean : mCategoryData) {
            bean.mIsSelected = false;
        }
        int selectIndex = 0;
        int classifySize = mCategoryData.size();
        for (int i = 0; i < classifySize; i++) {
            if (mCategoryData.get(i).mValue.equals(mCategory.mValue)) {
                selectIndex = i;
                break;
            }
        }
        mCategoryData.get(selectIndex).mIsSelected = true;

        mTypeListPopupWindow = new CustomPopupWindow(mContext);
        mCategoryAdapter = new DepositCategoryAdapter(mContext, mCategoryData);
        View view = View.inflate(mContext, R.layout.ppw_titlebar_category, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTypeListPopupWindow.dismiss();
            }
        });
        ListView lv = (ListView) view.findViewById(R.id.category_lv);
        lv.setAdapter(mCategoryAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DepositCategoryBean item = mCategoryData.get(position);

                if (mCategory.mValue.equals(item.mValue)) {
                    mTypeListPopupWindow.dismiss();
                    return;
                }
                mCategory = item;

                for (DepositCategoryBean bean : mCategoryData) {
                    bean.mIsSelected = false;
                }
                mCategoryData.get(position).mIsSelected = true;
                mCategoryAdapter.notifyDataSetChanged();

                mCommonTitleBar.setRightBtnText(mCategory.mDesc);

                mAdapter.clear();
                mRecyclerView.showProgress();
                mPresenter.getUserDepositInfo(1, mCategory);
                mTypeListPopupWindow.dismiss();
            }
        });
        mTypeListPopupWindow.setContentView(view);
        mTypeListPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mTypeListPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mTypeListPopupWindow.setTouchable(true);
        mTypeListPopupWindow.setOutsideTouchable(true);
        mTypeListPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }
}
