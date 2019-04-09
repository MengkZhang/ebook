package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.adapter.recyclerview.EasyRVAdapter;
import com.tzpt.cloundlibrary.manager.base.adapter.recyclerview.EasyRVHolder;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.utils.MoneyUtils;

import java.util.List;

/**
 * 图书列表适配器
 * Created by Administrator on 2017/7/9.
 */
public class BookListAdapter extends EasyRVAdapter<BookInfoBean> {
    private View.OnClickListener mOnClickListener;

    public BookListAdapter(Context context, List<BookInfoBean> list, View.OnClickListener listener) {
        super(context, list, R.layout.view_borrow_book_list_item);
        mOnClickListener = listener;
    }

    @Override
    protected void onBindData(EasyRVHolder viewHolder, int position, BookInfoBean item) {
        viewHolder.setText(R.id.library_number_tv, TextUtils.isEmpty(item.mBelongLibraryHallCode) ? "" : item.mBelongLibraryHallCode)
                .setText(R.id.code_number_tv, TextUtils.isEmpty(item.mBarNumber) ? "" : item.mBarNumber)
                .setText(R.id.book_name_tv, TextUtils.isEmpty(item.mProperTitle) ? "" : item.mProperTitle)
                .setText(R.id.book_price_tv, ((item.mDeposit == 0) ? "" : "押") + MoneyUtils.formatMoney(item.mPrice));

        viewHolder.setTextColor(R.id.code_number_tv, item.mColorIsRed ? Color.RED : Color.parseColor("#333333"));
        if (item.mAttachPrice > 0) {
            viewHolder.setText(R.id.attach_price_tv, "溢" + MoneyUtils.formatMoney(item.mAttachPrice));
            viewHolder.setVisible(R.id.attach_price_tv, View.VISIBLE);
        } else {
            viewHolder.setVisible(R.id.attach_price_tv, View.GONE);
        }
        viewHolder.setTag(R.id.del_book_iv, position);
        viewHolder.setOnClickListener(R.id.del_book_iv, mOnClickListener);
    }
}
