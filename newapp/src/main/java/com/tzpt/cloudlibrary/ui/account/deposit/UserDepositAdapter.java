package com.tzpt.cloudlibrary.ui.account.deposit;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.UserDepositBean;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 资讯列表
 * Created by ZhiqiangJia on 2017-08-17.
 */
public class UserDepositAdapter extends RecyclerArrayAdapter<UserDepositBean> {

    public UserDepositAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<UserDepositBean>(parent, R.layout.view_item_deposit) {
            @Override
            public void setData(UserDepositBean item) {
                String operationAndRemark;
                operationAndRemark = TextUtils.isEmpty(item.mOperation) ? "" : item.mOperation
                            + (TextUtils.isEmpty(item.mRemark) ? "" : "-" + item.mRemark);
                holder.setText(R.id.item_deposit_date_tv, TextUtils.isEmpty(item.mOperationDate) ? "" : item.mOperationDate)
                        .setText(R.id.item_deposit_project_tv, TextUtils.isEmpty(operationAndRemark) ? "" : operationAndRemark)
                        .setText(R.id.item_deposit_money_tv, TextUtils.isEmpty(item.mMoney) ? "" : item.mMoney)
                        .setText(R.id.item_deposit_status_tv, TextUtils.isEmpty(item.mStatus) ? "" : item.mStatus);
            }
        };
    }
}
