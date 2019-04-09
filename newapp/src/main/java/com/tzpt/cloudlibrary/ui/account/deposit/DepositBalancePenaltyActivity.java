package com.tzpt.cloudlibrary.ui.account.deposit;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.bean.DepositBalanceBean;
import com.tzpt.cloudlibrary.ui.library.LibraryDetailActivity;
import com.tzpt.cloudlibrary.widget.CustomDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 押金余额+逾期罚金
 * Created by Administrator on 2018/6/22.
 */

public class DepositBalancePenaltyActivity extends BaseListActivity<DepositBalanceBean> {
    private static final String DEPOSIT_BALANCE_LIST = "deposit_balance_list";

    public static void startActivity(Context context, ArrayList<DepositBalanceBean> list) {
        Intent intent = new Intent(context, DepositBalancePenaltyActivity.class);
        intent.putParcelableArrayListExtra(DEPOSIT_BALANCE_LIST, list);
        context.startActivity(intent);
    }

    @BindView(R.id.occupy_deposit_tv)
    TextView mOccupyDepositTv;

    @OnClick({R.id.titlebar_left_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_deposit_balance;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("押金明细");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mAdapter = new DepositBalancePenaltyAdapter(this);
        initAdapter(false, true);
        mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_default);

        ArrayList<DepositBalanceBean> list = getIntent().getParcelableArrayListExtra(DEPOSIT_BALANCE_LIST);
        if (list != null) {
            mAdapter.addAll(list);
            mAdapter.stopMore();
        }
    }

    @Override
    public void configViews() {
        mOccupyDepositTv.setText("逾期罚金");
    }

    @Override
    public void onItemClick(int position) {
        DepositBalanceBean item = mAdapter.getItem(position);
        if (!TextUtils.isEmpty(item.mLibCode)) {
            if (item.mIsUnusual == 1) {
                final CustomDialog dialog = new CustomDialog(DepositBalancePenaltyActivity.this, R.style.DialogTheme, "该馆已停用！");
                dialog.setCancelable(false);
                dialog.hasNoCancel(false);
                dialog.show();
                dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
                    @Override
                    public void onClickOk() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickCancel() {
                        dialog.dismiss();
                    }
                });
                return;
            }
            LibraryDetailActivity.startActivity(DepositBalancePenaltyActivity.this, item.mLibCode, item.mName);
        }
    }
}
