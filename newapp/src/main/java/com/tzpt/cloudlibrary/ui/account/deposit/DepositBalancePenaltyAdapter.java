package com.tzpt.cloudlibrary.ui.account.deposit;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.DepositBalanceBean;
import com.tzpt.cloudlibrary.utils.MoneyUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by Administrator on 2018/6/22.
 */

public class DepositBalancePenaltyAdapter extends RecyclerArrayAdapter<DepositBalanceBean> {

    public DepositBalancePenaltyAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<DepositBalanceBean>(parent, R.layout.view_item_deposit_balance) {
            @Override
            public void setData(DepositBalanceBean item) {
                holder.setText(R.id.deposit_balance_name_tv, item.mName)
                        .setText(R.id.deposit_balance_money_tv, MoneyUtils.formatMoney(item.mUsableDeposit))
                        .setText(R.id.occupy_deposit_tv, MoneyUtils.formatMoney(item.mPenalty));

                holder.itemView.setBackgroundResource(!TextUtils.isEmpty(item.mLibCode) ? R.drawable.bg_item_translate_common : R.drawable.bg_ffffff);
            }
        };
    }
}
