package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.adapter.recyclerview.EasyRVAdapter;
import com.tzpt.cloundlibrary.manager.base.adapter.recyclerview.EasyRVHolder;
import com.tzpt.cloundlibrary.manager.bean.PenaltyBean;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/12/15.
 */

public class PenaltyListAdapter extends EasyRVAdapter<PenaltyBean> {

    public PenaltyListAdapter(Context context, List<PenaltyBean> list) {
        super(context, list, R.layout.view_return_book_list_item);
    }

    @Override
    protected void onBindData(EasyRVHolder viewHolder, int position, PenaltyBean item) {
        viewHolder.setText(R.id.return_book_lib_code, item.mBelongLibraryHallCode)
                .setText(R.id.return_book_bar_number, item.mBarNumber)
                .setText(R.id.return_book_name, item.mProperTitle)
                .setText(R.id.return_book_price, MoneyUtils.formatMoney(item.mPrice))
                .setText(R.id.return_book_penalty, MoneyUtils.formatMoney(item.mPenalty));

        viewHolder.setTextColor(R.id.return_book_penalty, Color.RED);
        if (item.mAttachPrice > 0) {
            viewHolder.setText(R.id.return_book_attach_price, "溢" + MoneyUtils.formatMoney(item.mAttachPrice));
            viewHolder.setVisible(R.id.return_book_attach_price, View.VISIBLE);
        } else {
            viewHolder.setVisible(R.id.return_book_attach_price, View.GONE);
        }
    }
}
