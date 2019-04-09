package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 藏书统计
 * Created by ZhiqiangJia on 2017-07-15.
 */
public class CollectionBookStatisticsAdapter extends RecyclerArrayAdapter<BookInfoBean> {
    public CollectionBookStatisticsAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<BookInfoBean>(parent, R.layout.view_collecting_book_item_statistics) {
            @Override
            public void setData(BookInfoBean item) {
                if (null != item) {
                    holder.setText(R.id.library_number, item.mBelongLibraryHallCode)
                            .setText(R.id.code_number, item.mBarNumber)
                            .setText(R.id.book_name, item.mProperTitle)
                            .setText(R.id.book_status, item.mBookState)
                            .setText(R.id.book_price, StringUtils.doubleToString(item.mPrice));
                    setVisible(R.id.book_attach_price, false);
                }
            }
        };
    }
}
