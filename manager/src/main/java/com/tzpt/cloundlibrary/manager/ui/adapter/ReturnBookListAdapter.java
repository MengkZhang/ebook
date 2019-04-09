package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.adapter.recyclerview.EasyRVAdapter;
import com.tzpt.cloundlibrary.manager.base.adapter.recyclerview.EasyRVHolder;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;

import java.util.List;

/**
 * 还书
 * Created by Administrator on 2017/7/10.
 */
public class ReturnBookListAdapter extends EasyRVAdapter<BookInfoBean> {

    public ReturnBookListAdapter(Context context, List<BookInfoBean> list) {
        super(context, list, R.layout.view_return_book_list_item);
    }

    @Override
    protected void onBindData(EasyRVHolder viewHolder, int position, BookInfoBean item) {
        viewHolder.setText(R.id.return_book_lib_code, item.mBelongLibraryHallCode)
                .setText(R.id.return_book_bar_number, item.mBarNumber)
                .setText(R.id.return_book_name, item.mProperTitle)
                .setText(R.id.return_book_price, ((item.mDeposit == 0) ? "" : "押") + MoneyUtils.formatMoney(item.mPrice))
                .setText(R.id.return_book_penalty, MoneyUtils.formatMoney(item.mPenalty));

        viewHolder.setTextColor(R.id.return_book_penalty, item.mHandlePenalty ? Color.parseColor("#333333") : Color.RED);
        if (item.mAttachPrice > 0) {
            viewHolder.setText(R.id.return_book_attach_price, "溢" + MoneyUtils.formatMoney(item.mAttachPrice));
            viewHolder.setVisible(R.id.return_book_attach_price, View.VISIBLE);
        } else {
            viewHolder.setVisible(R.id.return_book_attach_price, View.GONE);
        }
    }
}
