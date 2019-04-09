package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.BookInfoBean;
import com.tzpt.cloundlibrary.manager.bean.FlowManageDetailBookInfoBean;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 流出管理详情
 * Created by ZhiqiangJia on 2017-07-11.
 */
public class IntoManagementDetailAdapter extends RecyclerArrayAdapter<BookInfoBean> {

    public IntoManagementDetailAdapter(Context context) {
        super(context);

    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<BookInfoBean>(parent, R.layout.view_item_out_flow_detail) {
            @Override
            public void setData(BookInfoBean item) {
                if (null != item) {
                    holder.setText(R.id.out_flow_detail_libcode, item.mBelongLibraryHallCode)
                            .setText(R.id.out_flow_detail_barcode, item.mBarNumber)
                            .setText(R.id.out_flow_detail_book_name, item.mProperTitle)
                            .setText(R.id.out_flow_detail_book_price, StringUtils.doubleToString(item.mPrice));

                    setVisible(R.id.out_flow_detail_delete_img, false);
                    setVisible(R.id.out_flow_detail_book_attach_price, false);
                }
            }
        };
    }
}
