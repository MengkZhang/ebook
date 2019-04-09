package com.tzpt.cloudlibrary.ui.account.deposit;

import android.content.Context;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.DepositBalanceBean;
import com.tzpt.cloudlibrary.utils.MoneyUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by Administrator on 2018/6/21.
 */

public class RefundDepositOfflineAdapter extends RecyclerArrayAdapter<DepositBalanceBean> {
    public RefundDepositOfflineAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<DepositBalanceBean>(parent, R.layout.view_refund_deposit_offline_item) {
            @Override
            public void setData(DepositBalanceBean item) {
                holder.setText(R.id.refund_deposit_lib_code_tv, item.mLibCode)
                        .setText(R.id.refund_deposit_lib_name_tv, item.mName)
                        .setText(R.id.refund_deposit_money_tv, MoneyUtils.formatMoney(item.mOccupyDeposit));
            }
        };
    }
}
