package com.tzpt.cloudlibrary.ui.video;

import android.content.Context;
import android.support.v4.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.VideoTOCTree;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.rxbus.event.VideoEvent;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

import java.util.Collection;

/**
 * 视频二级目录
 * Created by tonyjia on 2018/6/27.
 */
public class VideoCatalogAdapter extends RecyclerArrayAdapter<VideoTOCTree> {

    private LongSparseArray<Boolean> mItemChecked = new LongSparseArray<>();
    private final RxBus mRxBus;

    public VideoCatalogAdapter(Context context) {
        super(context);
        this.mRxBus = CloudLibraryApplication.mRxBus;
    }

    /**
     * 添加数据，并指定第一个数据为选中
     *
     * @param collection  列表集合
     * @param playVideoId 第一个视频ID
     */
    public void addAll(Collection<? extends VideoTOCTree> collection, long playVideoId) {
        super.addAll(collection);
        for (VideoTOCTree item : collection) {
            mItemChecked.put(item.getId(), item.getId() == playVideoId);
        }
    }

    @Override
    public int getViewType(int position) {
        return getItem(position).Level;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new BaseViewHolder<VideoTOCTree>(parent, R.layout.view_video_catalog_group_item) {
                @Override
                public void setData(final VideoTOCTree item) {
                    holder.setText(R.id.group_item_title_tv, item.getName());
                    holder.getItemView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isOpen(item)) {
                                toggle(item);
                                holder.setImageResource(R.id.group_item_arrow_iv, R.mipmap.ic_video_catalog_group_item_arrow);
                            } else {
                                expand(item);
                                holder.setImageResource(R.id.group_item_arrow_iv, R.mipmap.ic_video_catalog_group_item_up_arrow);
                            }
                        }
                    });
                }
            };
        } else {
            return new BaseViewHolder<VideoTOCTree>(parent, R.layout.view_video_catalog_sub_item) {
                @Override
                public void setData(final VideoTOCTree item) {
                    holder.setText(R.id.sub_item_chapter_title_tv, item.getName());
                    CheckBox cb = holder.getView(R.id.sub_item_status_ck);
                    cb.setChecked(mItemChecked.get(item.getId()));

                    holder.setTextColor(R.id.sub_item_chapter_title_tv, mItemChecked.get(item.getId()) ?
                            mContext.getResources().getColor(R.color.color_8a633d) :
                            mContext.getResources().getColor(R.color.color_333333));

                    holder.getItemView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notifyChooseDataChanged(item.getId(), item.getUrl());
                        }
                    });
                }
            };
        }
    }

    private void toggle(VideoTOCTree item) {
        int index = getAllData().indexOf(item);
        removeAll(index + 1, item.subtrees());
    }

    private void expand(VideoTOCTree item) {
        int index = getAllData().indexOf(item);
        addAll(index + 1, item.subtrees());
    }

    private boolean isOpen(VideoTOCTree item) {
        return getAllData().contains(item.subtrees().get(0));
    }

    /**
     * 更新视频目录选中状态
     *
     * @param chooseId 选中视频ID
     * @param playUrl  视频播放地址
     */
    public void notifyChooseDataChanged(long chooseId, String playUrl) {
        notifyChooseData(chooseId);
        //发送视频播放消息
        VideoEvent event = new VideoEvent();
        event.setMsgType(1);
        event.setVideoPlayId(chooseId);
        event.setVideoPlayUrl(playUrl);
        mRxBus.post(event);
    }

    /**
     * 更新视频目录选中状态
     *
     * @param chooseId 选中视频ID
     */
    public void notifyChooseData(long chooseId) {
        for (VideoTOCTree item : getAllData()) {
            mItemChecked.put(item.getId(), item.getId() == chooseId);
        }
        notifyDataSetChanged();
    }

}
