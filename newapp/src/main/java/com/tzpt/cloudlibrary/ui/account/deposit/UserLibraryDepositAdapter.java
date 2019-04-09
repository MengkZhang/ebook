package com.tzpt.cloudlibrary.ui.account.deposit;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.UserDepositBean;
import com.tzpt.cloudlibrary.bean.UserLibraryDepositBean;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 资讯列表
 * Created by ZhiqiangJia on 2017-08-17.
 */
public class UserLibraryDepositAdapter extends RecyclerArrayAdapter<UserLibraryDepositBean> {

    public UserLibraryDepositAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<UserLibraryDepositBean>(parent, R.layout.view_library_deposit_item) {
            @Override
            public void setData(UserLibraryDepositBean item) {
                holder.setText(R.id.library_deposit_code, TextUtils.isEmpty(item.hallCode) ? "" : item.hallCode)
                        .setText(R.id.library_deposit_name, TextUtils.isEmpty(item.name) ? "" : item.name)
                        .setText(R.id.library_deposit_money, TextUtils.isEmpty(item.total) ? "" : item.total)
                        .setText(R.id.library_deposit_can_use, TextUtils.isEmpty(item.canUse) ? "" : item.canUse);
            }
        };
    }
}
