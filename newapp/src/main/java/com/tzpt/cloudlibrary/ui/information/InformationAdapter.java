package com.tzpt.cloudlibrary.ui.information;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.adapter.glide.RoundedCornersTransformation;
import com.tzpt.cloudlibrary.bean.InformationBean;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 资讯列表
 * Created by ZhiqiangJia on 2017-08-17.
 */
public class InformationAdapter extends RecyclerArrayAdapter<InformationBean> {

    public InformationAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<InformationBean>(parent, R.layout.view_information_item) {
            @Override
            public void setData(InformationBean item) {
                holder.setText(R.id.information_title_tv, TextUtils.isEmpty(item.mTitle) ? "" : item.mTitle)
                        .setText(R.id.information_time_tv, TextUtils.isEmpty(item.mCreateTime) ? "" : item.mCreateTime)
                        .setText(R.id.information_preview_count_tv, String.valueOf(item.mReadCount));

                GlideApp.with(mContext)
                        .load(item.mImage)
                        .dontAnimate()
                        .placeholder(R.drawable.bg_circle_eeeeee)
                        .error(R.mipmap.ic_video_error_image)
                        .centerCrop()
                        .transform(new RoundedCornersTransformation(3, RoundedCornersTransformation.CornerType.ALL))
                        .into((ImageView) holder.getView(R.id.information_pic_iv));

                if (!TextUtils.isEmpty(item.mVideoUrl)) {
                    holder.setVisible(R.id.shadow_cover_iv, View.VISIBLE);
                    holder.setVisible(R.id.information_video_play_time_tv, View.VISIBLE);
                    holder.setText(R.id.information_video_play_time_tv, TextUtils.isEmpty(item.mVideoDuration) ? "" : item.mVideoDuration);
                } else {
                    holder.setVisible(R.id.shadow_cover_iv, View.GONE);
                    holder.setVisible(R.id.information_video_play_time_tv, View.GONE);
                }
            }
        };
    }
}
