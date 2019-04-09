package com.tzpt.cloudlibrary.ui.account.deposit;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.base.data.Library;
import com.tzpt.cloudlibrary.bean.DepositBalanceBean;
import com.tzpt.cloudlibrary.ui.library.LibraryDetailActivity;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 押金明细（押金余额+占用押金）/线下押金明细（可退押金+占用押金）
 * Created by Administrator on 2018/5/25.
 */

public class DepositBalanceActivity extends BaseListActivity<DepositBalanceBean> {
    private static final String DEPOSIT_BALANCE_LIST = "deposit_balance_list";
    private static final String DEPOSIT_TYPE = "";

    public static void startActivity(Context context, ArrayList<DepositBalanceBean> list, int depositType) {
        Intent intent = new Intent(context, DepositBalanceActivity.class);
        intent.putExtra(DEPOSIT_TYPE, depositType);
        intent.putParcelableArrayListExtra(DEPOSIT_BALANCE_LIST, list);
        context.startActivity(intent);
    }

    @BindView(R.id.deposit_balance_money_tv)
    TextView mDepositTitleTv;

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
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        int depositType = getIntent().getIntExtra(DEPOSIT_TYPE, 0);
        if (depositType == 0) {
            mDepositTitleTv.setText("押金余额");
            mCommonTitleBar.setTitle("押金明细");
        } else {
            mDepositTitleTv.setText("可退押金");
            mCommonTitleBar.setTitle("线下押金明细");
        }

        mAdapter = new DepositBalanceAdapter(this);
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

    }

    @Override
    public void onItemClick(int position) {
        DepositBalanceBean item = mAdapter.getItem(position);
        if (!TextUtils.isEmpty(item.mLibCode)) {
            if (item.mIsUnusual == 1) {
                final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, "该馆已停用！");
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
            LibraryDetailActivity.startActivity(DepositBalanceActivity.this, item.mLibCode, item.mName);
        }
    }
}
