package com.tzpt.cloudlibrary.ui.video;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.adapter.glide.RoundedCornersTransformation;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.rxbus.event.VideoSetEvent;
import com.tzpt.cloudlibrary.utils.Utils;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 视频列表
 * Created by tonyjia on 2018/6/21.
 */
public class VideoCollectionListAdapter extends RecyclerArrayAdapter<VideoSetBean> {

    protected boolean mIsEditMode = false;
    protected SparseBooleanArray mSparseItemChecked = new SparseBooleanArray();
    protected final RxBus mRxBus;

    public VideoCollectionListAdapter(Context context) {
        super(context);
        mRxBus = CloudLibraryApplication.mRxBus;
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

                //设置收藏视频选中状态
                CheckBox delCB = holder.getView(R.id.video_item_cb);
                if (mIsEditMode) {
                    delCB.setVisibility(View.VISIBLE);
                    delCB.setChecked(mSparseItemChecked.get(holder.getAdapterPosition()));
                } else {
                    delCB.setVisibility(View.GONE);
                    delCB.setChecked(false);
                }
            }
        };
    }

    /**
     * 编辑模式设置选中item
     *
     * @param position
     */
    public void chooseCollectionVideo(int position) {
        mSparseItemChecked.put(position, !mSparseItemChecked.get(position));
        notifyItemChanged(position);
        handleCheckedChanged();
    }

    //检查当前是否可编辑
    public void checkEditorAble() {
        mRxBus.post(new VideoSetEvent(1, getCount() > 0));
    }

    public boolean isEditMode() {
        return mIsEditMode;
    }

    public void setEditMode(boolean editMode) {
        mIsEditMode = editMode;
        if (!mIsEditMode) {
            mSparseItemChecked.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 处理选中事件
     */
    private void handleCheckedChanged() {
        int checkedCount = 0;
        for (int i = 0; i < getCount(); i++) {
            if (mSparseItemChecked.get(i, false)) {
                checkedCount++;
            }
        }
        // 通知 删除数量 更新界面
        mRxBus.post(new VideoSetEvent(0, checkedCount));
    }

    /**
     * 是否全选
     *
     * @param isChecked 全选中和全不选
     */
    public void checkAllOrNone(boolean isChecked) {
        for (int i = 0; i < getCount(); i++) {
            mSparseItemChecked.put(i, isChecked);
        }
        notifyDataSetChanged();
        handleCheckedChanged();
    }

}
