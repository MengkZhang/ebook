package com.tzpt.cloudlibrary.ui.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.VideoBean;
import com.tzpt.cloudlibrary.bean.VideoTOCTree;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadStatus;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.rxbus.event.VideoEvent;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/7/10.
 */

public class VideoDownloadChooseAdapter extends RecyclerArrayAdapter<VideoTOCTree> {
    @SuppressLint("UseSparseArrays")
    private LinkedHashMap<Long, Boolean> mItemChecked = new LinkedHashMap<>();//存的是最后的子节点（扩展性不是很好）
    protected final RxBus mRxBus;

    VideoDownloadChooseAdapter(Context context) {
        super(context);
        mRxBus = CloudLibraryApplication.mRxBus;
    }

    @Override
    public int getViewType(int position) {
        return getItem(position).Level;
    }

    @Override
    public void addAll(Collection<? extends VideoTOCTree> collection) {
        super.addAll(collection);
        for (VideoTOCTree item : collection) {
            if (item.hasChildren()) {
                for (VideoTOCTree subItem : item.subtrees()) {
                    if (subItem.getStatus() < 0) {
                        mItemChecked.put(subItem.getId(), false);
                    }
                }
            }
        }
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
            return new BaseViewHolder<VideoTOCTree>(parent, R.layout.view_video_download_sub_item) {
                @Override
                public void setData(final VideoTOCTree item) {
                    holder.setText(R.id.sub_item_chapter_title_tv, item.getName());
                    CheckBox chooseCB = holder.getView(R.id.sub_item_status_ck);
                    switch (item.getStatus()) {
                        case DownloadStatus.COMPLETE:
                            chooseCB.setChecked(true);
                            chooseCB.setEnabled(false);
                            holder.getItemView().setClickable(false);
                            holder.setText(R.id.item_download_status_tv, "已下载");
                            holder.setTextColor(R.id.sub_item_chapter_title_tv,
                                    mContext.getResources().getColor(R.color.color_8a633d));
                            break;
                        case DownloadStatus.DOWNLOADING:
                            holder.setText(R.id.item_download_status_tv, "下载中");
                            chooseCB.setChecked(true);
                            chooseCB.setEnabled(false);
                            holder.getItemView().setClickable(false);
                            holder.setTextColor(R.id.sub_item_chapter_title_tv,
                                    mContext.getResources().getColor(R.color.color_8a633d));
                            break;
                        default:
                            holder.setText(R.id.item_download_status_tv, "");
                            chooseCB.setChecked(mItemChecked.get(item.getId()));
                            chooseCB.setEnabled(true);
                            holder.getItemView().setClickable(true);
                            holder.setTextColor(R.id.sub_item_chapter_title_tv, mItemChecked.get(item.getId()) ?
                                    mContext.getResources().getColor(R.color.color_8a633d) :
                                    mContext.getResources().getColor(R.color.color_333333));
                            holder.getItemView().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mItemChecked.get(item.getId()) != null) {
                                        mItemChecked.put(item.getId(), !mItemChecked.get(item.getId()));
                                    } else {
                                        mItemChecked.put(item.getId(), false);
                                    }
                                    notifyItemChanged(holder.getAdapterPosition());
                                    handleCheckedChanged();
                                }
                            });
                            break;
                    }
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

    private void handleCheckedChanged() {
        int checkedCount = 0;
        for (Long key : mItemChecked.keySet()) {
            if (mItemChecked.get(key)) {
                checkedCount++;
            }
        }
        mRxBus.post(new VideoEvent(checkedCount));
    }

    void checkAllOrNone(boolean isChecked) {
        int count = 0;
        for (Long key : mItemChecked.keySet()) {
            count++;
            mItemChecked.put(key, isChecked);
        }
        if (count > 0) {
            notifyDataSetChanged();
            handleCheckedChanged();
        }
    }

    boolean isAllChoose() {
        for (Long key : mItemChecked.keySet()) {
            if (!mItemChecked.get(key)) {
                return false;
            }
        }
        return true;
    }

    List<Long> getCheckedItem() {
        List<Long> checkedItems = new ArrayList<>();
        for (Long key : mItemChecked.keySet()) {
            if (mItemChecked.get(key)) {
                checkedItems.add(key);
            }
        }
        return checkedItems;
    }

    void clearCheckedItem() {
        for (Long key : getCheckedItem()) {
            if (mItemChecked.get(key)) {
                mItemChecked.remove(key);
            }
        }
    }

    void updateDownloadToComplete(VideoBean videoBean) {
        VideoTOCTree node = find(videoBean.getUrl());
        if (node == null) {
            return;
        }
        node.setStatus(DownloadStatus.COMPLETE);

        int position = getAllData().indexOf(node);
        notifyItemChanged(position);
    }

    void updateDownloadToDownloading(VideoBean videoBean) {
        VideoTOCTree node = find(videoBean.getUrl());
        if (node == null) {
            return;
        }
        node.setStatus(DownloadStatus.DOWNLOADING);

        int position = getAllData().indexOf(node);
        notifyItemChanged(position);
    }

    /**
     * 查找列表中的VideoTOCTree
     *
     * @return VideoTOCTree
     */
    private VideoTOCTree find(String url) {
        for (VideoTOCTree node : getAllData()) {
            if (url.equals(node.getUrl())) {
                return node;
            }
        }
        return null;
    }
}
