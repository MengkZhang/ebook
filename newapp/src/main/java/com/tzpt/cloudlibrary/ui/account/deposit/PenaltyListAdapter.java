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
 * Created by Administrator on 2018/6/21.
 */

public class PenaltyListAdapter extends RecyclerArrayAdapter<DepositBalanceBean> {

    public PenaltyListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<DepositBalanceBean>(parent, R.layout.view_penalty_list_item) {
            @Override
            public void setData(DepositBalanceBean item) {
                holder.setText(R.id.penalty_list_name_tv, item.mName)
                        .setText(R.id.penalty_list_money_tv, MoneyUtils.formatMoney(item.mPenalty));

                holder.itemView.setBackgroundResource(!TextUtils.isEmpty(item.mLibCode) ? R.drawable.bg_item_translate_common : R.drawable.bg_ffffff);

            }
        };
    }
}
