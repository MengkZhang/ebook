package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.FlowManageDetailBookInfoBean;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 流出管理详情
 * Created by ZhiqiangJia on 2017-07-11.
 */
public class FlowManagementDetailAdapter extends RecyclerArrayAdapter<FlowManageDetailBookInfoBean> {

    private boolean mButtonVisible = false;
    private View.OnClickListener mOnClickListener;

    public FlowManagementDetailAdapter(Context context, boolean buttonVisible, View.OnClickListener listener) {
        super(context);
        this.mButtonVisible = buttonVisible;
        this.mOnClickListener = listener;

    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<FlowManageDetailBookInfoBean>(parent, R.layout.view_item_out_flow_detail) {
            @Override
            public void setData(FlowManageDetailBookInfoBean item) {
                if (null != item) {
                    holder.setText(R.id.out_flow_detail_libcode, item.mBookInfoBean.mBelongLibraryHallCode)
                            .setText(R.id.out_flow_detail_barcode, item.mBookInfoBean.mBarNumber)
                            .setText(R.id.out_flow_detail_book_name, item.mBookInfoBean.mProperTitle)
                            .setText(R.id.out_flow_detail_book_price, StringUtils.doubleToString(item.mBookInfoBean.mPrice));

                    holder.setTag(R.id.out_flow_detail_delete_img, item.circulateMapId);
                    holder.setOnClickListener(R.id.out_flow_detail_delete_img, mOnClickListener);
                    setVisible(R.id.out_flow_detail_delete_img, mButtonVisible);
                    setVisible(R.id.out_flow_detail_book_attach_price, false);
                }
            }
        };
    }
}
