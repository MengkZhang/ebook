package com.tzpt.cloudlibrary.ui.share;

import android.content.Context;
import android.view.ViewGroup;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.ShareItemBean;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 分享
 * Created by ZhiqiangJia on 2017-02-20.
 */
public class ShareAdapter extends RecyclerArrayAdapter<ShareItemBean> {

    private boolean mLandscape;

    public ShareAdapter(Context context, boolean landScape) {
        super(context);
        this.mLandscape = landScape;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<ShareItemBean>(parent, mLandscape ? R.layout.view_item_land_scape_share : R.layout.view_item_share) {
            @Override
            public void setData(ShareItemBean item) {
                holder.setImageDrawableRes(R.id.share_icon_img, item.icon);
                holder.setText(R.id.share_title_tv, item.name);
            }
        };
    }
}
