package com.tzpt.cloudlibrary.ui.video;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.adapter.glide.RoundedCornersTransformation;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.utils.Utils;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 视频列表
 * Created by tonyjia on 2018/6/21.
 */
public class VideoListAdapter extends RecyclerArrayAdapter<VideoSetBean> {

    public VideoListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<VideoSetBean>(parent, R.layout.view_video_list_item) {
            @Override
            public void setData(VideoSetBean item) {
                GlideApp.with(mContext)
                        .load(item.getCoverImg())
                        .dontAnimate()
                        .placeholder(R.drawable.bg_circle_eeeeee)
                        .error(R.mipmap.ic_video_error_image)
                        .centerCrop()
                        .transform(new RoundedCornersTransformation(3, RoundedCornersTransformation.CornerType.ALL))
                        .into((ImageView) holder.getView(R.id.video_item_image_iv));

                holder.setText(R.id.video_item_title_tv, item.getTitle())
                        .setText(R.id.video_item_play_times_tv, mContext.getString(R.string.video_watch_times, Utils.formatWatchTimes(item.getWatchTimes())));
            }
        };
    }

}
