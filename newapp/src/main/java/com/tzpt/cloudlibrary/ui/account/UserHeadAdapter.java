package com.tzpt.cloudlibrary.ui.account;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.UserHeadBean;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 用户头像适配器
 * Created by ZhiqiangJia on 2017-08-22.
 */

public class UserHeadAdapter extends RecyclerArrayAdapter<UserHeadBean> {

    private SparseBooleanArray mChooseArray = new SparseBooleanArray();

    public UserHeadAdapter(Context context) {
        super(context);
    }

    public void setChooseImage(int position) {
        mChooseArray.clear();
        for (int i = 0; i < getCount(); i++) {
            mChooseArray.put(i, position == i);
        }
        notifyDataSetChanged();
    }

    public void setCurrentImage(String currentImage) {
        if (!TextUtils.isEmpty(currentImage)) {
            mChooseArray.clear();
            for (int i = 0; i < getCount(); i++) {
                mChooseArray.put(i, currentImage.equals(getAllData().get(i).image));
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<UserHeadBean>(parent, R.layout.view_user_head_item) {
            @Override
            public void setData(UserHeadBean item) {
                GlideApp.with(mContext)
                        .load(item.image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .into((ImageView) holder.getView(R.id.item_user_head_img));
                holder.setVisible(R.id.item_user_head_choose, mChooseArray.get(getAdapterPosition()));
            }
        };
    }


}
