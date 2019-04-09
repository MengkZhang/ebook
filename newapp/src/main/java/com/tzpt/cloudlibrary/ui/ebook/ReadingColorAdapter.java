package com.tzpt.cloudlibrary.ui.ebook;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.ReadingColorBean;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;
import com.tzpt.cloudlibrary.zlibrary.core.util.ZLColor;

/**
 * Created by Administrator on 2017/10/24.
 */

public class ReadingColorAdapter extends RecyclerArrayAdapter<ReadingColorBean> {

    public ReadingColorAdapter(Context context) {
        super(context);
    }

    /**
     * 设置主题默认选择
     *
     * @param selected
     */
    public void setSelected(ZLColor selected) {
        int size = getAllData().size();
        for (int i = 0; i < size; i++) {
            getAllData().get(i).colorChose = (selected.equals(getAllData().get(i).mBgColor));
        }
        notifyDataSetChanged();
    }

    /**
     * 设置全部没有选中
     */
    public void setNoSelected(){
        for (ReadingColorBean item : getAllData()){
            item.colorChose = false;
        }
        notifyDataSetChanged();
    }


    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<ReadingColorBean>(parent, R.layout.view_bg_gallery_item) {
            @Override
            public void setData(ReadingColorBean item) {
                if (item != null) {
                    holder.setBackgroundColorRes(R.id.color_tv, item.defaultResourceId);
                    holder.setBackgroundColorRes(R.id.round_border_iv,
                            item.colorChose ? R.mipmap.ic_reading_icon_border_selected : 0);
                    holder.setTextColorRes(R.id.color_tv, item.textColorResource);
                }
            }
        };
    }
}
