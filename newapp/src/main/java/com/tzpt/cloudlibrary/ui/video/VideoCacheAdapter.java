package com.tzpt.cloudlibrary.ui.video;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.VideoBean;
import com.tzpt.cloudlibrary.modle.remote.newdownload.DownloadStatus;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;

/**
 * Created by Administrator on 2018/7/2.
 */

public class VideoCacheAdapter extends BaseVideoDLAdapter {

    public VideoCacheAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<VideoBean>(parent, R.layout.view_video_cache_list_item) {
            @Override
            public void setData(VideoBean item) {
                holder.setText(R.id.video_cache_name_tv, item.getName());
                ProgressBar pb = holder.getView(R.id.video_cache_pb);
                pb.setMax((int) item.getTotalBytes());
                pb.setProgress((int) item.getLoadBytes());
                switch (item.getStatus()) {
                    case DownloadStatus.WAIT:
                        holder.setVisible(R.id.video_cache_pb, View.VISIBLE);
                        holder.setVisible(R.id.video_cache_speed_tv, View.GONE);
                        holder.setVisible(R.id.video_cache_status_tv, View.VISIBLE);
                        holder.setText(R.id.video_cache_status_tv, "等待下载");
                        if (item.getTotalBytes() > 0) {
                            holder.setVisible(R.id.video_cache_load_total_tv, View.VISIBLE);
                        } else {
                            holder.setVisible(R.id.video_cache_load_total_tv, View.GONE);
                        }
                        holder.setText(R.id.video_cache_load_total_tv, mContext.getString(R.string.download_load_total_size,
                                StringUtils.convertStorageNoB(item.getLoadBytes()),
                                StringUtils.convertStorageNoB(item.getTotalBytes())));
                        break;
                    case DownloadStatus.CONNECTING:
                        holder.setVisible(R.id.video_cache_speed_tv, View.GONE);
                        holder.setVisible(R.id.video_cache_status_tv, View.VISIBLE);
                        holder.setText(R.id.video_cache_status_tv, "正在连接");
                        if (item.getTotalBytes() > 0) {
                            holder.setVisible(R.id.video_cache_load_total_tv, View.VISIBLE);
                        } else {
                            holder.setVisible(R.id.video_cache_load_total_tv, View.GONE);
                        }
                        holder.setText(R.id.video_cache_load_total_tv, mContext.getString(R.string.download_load_total_size,
                                StringUtils.convertStorageNoB(item.getLoadBytes()),
                                StringUtils.convertStorageNoB(item.getTotalBytes())));
                        break;
                    case DownloadStatus.START:
                        holder.setVisible(R.id.video_cache_pb, View.VISIBLE);
                        holder.setVisible(R.id.video_cache_speed_tv, View.GONE);
                        holder.setVisible(R.id.video_cache_status_tv, View.VISIBLE);
                        holder.setText(R.id.video_cache_status_tv, "开始下载");
                        if (item.getTotalBytes() > 0) {
                            holder.setVisible(R.id.video_cache_load_total_tv, View.VISIBLE);
                        } else {
                            holder.setVisible(R.id.video_cache_load_total_tv, View.GONE);
                        }
                        holder.setText(R.id.video_cache_load_total_tv, mContext.getString(R.string.download_load_total_size,
                                StringUtils.convertStorageNoB(item.getLoadBytes()),
                                StringUtils.convertStorageNoB(item.getTotalBytes())));
                        break;
                    case DownloadStatus.DOWNLOADING:
                        holder.setVisible(R.id.video_cache_pb, View.VISIBLE);
                        holder.setVisible(R.id.video_cache_speed_tv, View.VISIBLE);
                        holder.setVisible(R.id.video_cache_status_tv, View.GONE);
                        holder.setText(R.id.video_cache_speed_tv, item.getLoadSpeed());
                        if (item.getTotalBytes() > 0) {
                            holder.setVisible(R.id.video_cache_load_total_tv, View.VISIBLE);
                        } else {
                            holder.setVisible(R.id.video_cache_load_total_tv, View.GONE);
                        }
                        holder.setText(R.id.video_cache_load_total_tv, mContext.getString(R.string.download_load_total_size,
                                StringUtils.convertStorageNoB(item.getLoadBytes()),
                                StringUtils.convertStorageNoB(item.getTotalBytes())));
                        break;
                    case DownloadStatus.ERROR:
                        holder.setVisible(R.id.video_cache_pb, View.GONE);
                        holder.setVisible(R.id.video_cache_speed_tv, View.GONE);
                        holder.setVisible(R.id.video_cache_status_tv, View.VISIBLE);
                        holder.setVisible(R.id.video_cache_load_total_tv, View.GONE);
                        holder.setText(R.id.video_cache_status_tv, "下载失败");
                        break;
                    case DownloadStatus.MOBILE_NET_ERROR:
                        holder.setVisible(R.id.video_cache_pb, View.GONE);
                        holder.setVisible(R.id.video_cache_speed_tv, View.GONE);
                        holder.setVisible(R.id.video_cache_status_tv, View.VISIBLE);
                        holder.setVisible(R.id.video_cache_load_total_tv, View.GONE);
                        holder.setText(R.id.video_cache_status_tv, "运营商网络暂停下载");
                        break;
                    case DownloadStatus.NO_NET_ERROR:
                        holder.setVisible(R.id.video_cache_pb, View.GONE);
                        holder.setVisible(R.id.video_cache_speed_tv, View.GONE);
                        holder.setVisible(R.id.video_cache_status_tv, View.VISIBLE);
                        holder.setVisible(R.id.video_cache_load_total_tv, View.GONE);
                        holder.setText(R.id.video_cache_status_tv, "搜索网络...");
                        break;
                    case DownloadStatus.STOP:
                        holder.setVisible(R.id.video_cache_pb, View.VISIBLE);
                        holder.setVisible(R.id.video_cache_speed_tv, View.GONE);
                        holder.setVisible(R.id.video_cache_status_tv, View.VISIBLE);
                        holder.setText(R.id.video_cache_status_tv, "暂停");
                        if (item.getTotalBytes() > 0) {
                            holder.setVisible(R.id.video_cache_load_total_tv, View.VISIBLE);
                        } else {
                            holder.setVisible(R.id.video_cache_load_total_tv, View.GONE);
                        }
                        holder.setText(R.id.video_cache_load_total_tv, mContext.getString(R.string.download_load_total_size,
                                StringUtils.convertStorageNoB(item.getLoadBytes()),
                                StringUtils.convertStorageNoB(item.getTotalBytes())));
                        break;
                }

                CheckBox delCB = holder.getView(R.id.video_cache_cancel_cb);
                if (mIsEditMode) {
                    delCB.setVisibility(View.VISIBLE);
                    delCB.setChecked(mSparseItemChecked.get(holder.getAdapterPosition() - getHeaderCount()));
                } else {
                    delCB.setVisibility(View.GONE);
                    delCB.setChecked(false);
                }

                holder.getItemView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mIsEditMode) {
                            mSparseItemChecked.put(holder.getAdapterPosition() - getHeaderCount(), !mSparseItemChecked.get(holder.getAdapterPosition() - getHeaderCount()));
                            handleCheckedChanged();
                            notifyItemChanged(holder.getAdapterPosition());
                        } else {
                            if (mItemClickListener != null) {
                                mItemClickListener.onItemClick(holder.getAdapterPosition() - getHeaderCount());
                            }
                        }
                    }
                });
            }
        };
    }

    public void updateDownload(VideoBean videoBean) {
        VideoBean videoItem = find(videoBean.getUrl());
        if (videoItem == null) {
            return;
        }
        videoItem.setLoadBytes(videoBean.getLoadBytes());
        videoItem.setTotalBytes(videoBean.getTotalBytes());
        videoItem.setStatus(videoBean.getStatus());
//        videoItem.setLoadSpeed(fileInfo.getSpeed());

        int position = getAllData().indexOf(videoItem);
        notifyItemChanged(position);
        //下载完成和取消下载两个状态要从列表中移除，不能在刷新列表的时候调用remove方法，所以在这里移除。
        if (videoItem.getStatus() == DownloadStatus.COMPLETE
                || videoItem.getStatus() == DownloadStatus.CANCEL) {
            removeChecked(position);
            remove(position);
        }
    }

    /**
     * 查找列表中的VideoBean
     *
     * @return VideoBean
     */
    private VideoBean find(String url) {
        for (VideoBean info : getAllData()) {
            if (url.equals(info.getUrl())) {
                return info;
            }
        }
        return null;
    }

}
