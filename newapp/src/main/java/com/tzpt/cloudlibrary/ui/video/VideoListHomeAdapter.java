package com.tzpt.cloudlibrary.ui.video;

import android.content.Context;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.adapter.glide.RoundedCornersTransformation;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.EasyRVAdapter;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.EasyRVHolder;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.utils.Utils;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;

/**
 * 视频列表
 * Created by tonyjia on 2018/6/21.
 */
public class VideoListHomeAdapter extends EasyRVAdapter<VideoSetBean> {

    public VideoListHomeAdapter(Context context) {
        super(context, R.layout.view_home_video_list_item);
        setHasStableIds(true);
    }

    @Override
    protected void onBindData(EasyRVHolder holder, int position, VideoSetBean item) {
        GlideApp.with(mContext)
                .load(item.getCoverImg())
                .dontAnimate()
                .placeholder(R.drawable.bg_eeeeee)
                .error(R.mipmap.ic_video_error_image)
                .centerCrop()
                .transform(new RoundedCornersTransformation(3, RoundedCornersTransformation.CornerType.ALL))
                .into((ImageView) holder.getView(R.id.video_item_image_iv));

        holder.setText(R.id.video_item_title_tv, item.getTitle())
                .setText(R.id.video_item_play_times_tv, mContext.getString(R.string.video_watch_times, Utils.formatWatchTimes(item.getWatchTimes())));
    }
}
