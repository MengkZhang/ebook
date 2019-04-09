package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.LibraryDepositDetailBean;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 本馆押金明细适配器
 * Created by ZhiqiangJia on 2017-10-24.
 */
public class LibraryDepositDetailAdapter extends RecyclerArrayAdapter<LibraryDepositDetailBean> {

    public LibraryDepositDetailAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<LibraryDepositDetailBean>(parent, R.layout.view_library_deposit_detail_item) {
            @Override
            public void setData(LibraryDepositDetailBean item) {
                if (null != item) {
                    holder.setText(R.id.deposit_detail_date_tv, TextUtils.isEmpty(item.mDate) ? "" : item.mDate)
                            .setText(R.id.deposit_detail_project_tv, TextUtils.isEmpty(item.mProjectAndOperator) ? "" : item.mProjectAndOperator)
                            .setText(R.id.deposit_detail_money_tv, TextUtils.isEmpty(item.mMoney) ? "" : item.mMoney);
                    if (TextUtils.isEmpty(item.mStatus)) {
                        holder.setVisible(R.id.deposit_detail_status_tv, View.INVISIBLE);
                    } else {
                        holder.setVisible(R.id.deposit_detail_status_tv, View.VISIBLE);
                        holder.setText(R.id.deposit_detail_status_tv, item.mStatus);
                    }
                    if (TextUtils.isEmpty(item.mRemark)) {
                        holder.setVisible(R.id.deposit_detail_remark_tv, View.GONE);
                    } else {
                        holder.setVisible(R.id.deposit_detail_remark_tv, View.VISIBLE);
                        holder.setText(R.id.deposit_detail_remark_tv, item.mRemark);
                    }
                }
            }
        };
    }
}
