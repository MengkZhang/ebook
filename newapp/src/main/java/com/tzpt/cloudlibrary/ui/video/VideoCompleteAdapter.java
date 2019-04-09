package com.tzpt.cloudlibrary.ui.video;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.VideoBean;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;

/**
 * Created by Administrator on 2018/7/4.
 */

public class VideoCompleteAdapter extends BaseVideoDLAdapter {
    public VideoCompleteAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<VideoBean>(parent, R.layout.view_video_complete_list_item) {
            @Override
            public void setData(VideoBean item) {
                holder.setText(R.id.video_complete_name_tv, item.getName())
                        .setText(R.id.video_complete_load_total_tv, StringUtils.convertStorageNoB(item.getTotalBytes()));

                CheckBox delCB = holder.getView(R.id.video_complete_del_cb);
                if (mIsEditMode) {
                    delCB.setVisibility(View.VISIBLE);
                    delCB.setChecked(mSparseItemChecked.get(holder.getAdapterPosition() - getHeaderCount()));
                } else {
                    delCB.setVisibility(View.GONE);
                    delCB.setChecked(false);
                }
//                delCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        handleCheckedChanged(holder.getAdapterPosition(), isChecked);
//                    }
//                });

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
}
