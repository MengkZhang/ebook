package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.adapter.recyclerview.EasyRVAdapter;
import com.tzpt.cloundlibrary.manager.base.adapter.recyclerview.EasyRVHolder;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/7/11.
 */

public class RefundDepositBookListAdapter extends EasyRVAdapter<BookInfoBean> {


    public RefundDepositBookListAdapter(Context context, List<BookInfoBean> list) {
        super(context, list, R.layout.view_refund_deposit_book_list_item);
    }

    @Override
    protected void onBindData(EasyRVHolder viewHolder, int position, BookInfoBean item) {
        viewHolder.setText(R.id.library_number_tv, item.mBelongLibraryHallCode)
                .setText(R.id.code_number_tv, item.mBarNumber)
                .setText(R.id.book_name_tv, item.mProperTitle)
                .setText(R.id.book_price_tv, (item.mDeposit == 1 ? "押" : "") + MoneyUtils.formatMoney(item.mPrice));

        if (item.mAttachPrice > 0) {
            viewHolder.setText(R.id.attach_price_tv, "溢" + MoneyUtils.formatMoney(item.mAttachPrice));
            viewHolder.setVisible(R.id.attach_price_tv, View.VISIBLE);
        } else {
            viewHolder.setVisible(R.id.attach_price_tv, View.GONE);
        }
    }
}
