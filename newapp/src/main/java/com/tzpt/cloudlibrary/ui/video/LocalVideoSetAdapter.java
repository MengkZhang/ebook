package com.tzpt.cloudlibrary.ui.video;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.rxbus.RxBus;
import com.tzpt.cloudlibrary.rxbus.event.VideoEvent;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * Created by Administrator on 2018/7/12.
 */

public class LocalVideoSetAdapter extends RecyclerArrayAdapter<VideoSetBean> {
    private boolean mIsEditMode = false;
    protected SparseBooleanArray mSparseItemChecked = new SparseBooleanArray();
    protected final RxBus mRxBus;

    LocalVideoSetAdapter(Context context) {
        super(context);
        mRxBus = CloudLibraryApplication.mRxBus;
    }

    boolean isEditMode() {
        return mIsEditMode;
    }

    void setEditMode(boolean editMode) {
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
        mRxBus.post(new VideoEvent(checkedCount));
    }

    public void checkAllOrNone(boolean isChecked) {
        for (int i = 0; i < getCount(); i++) {
            mSparseItemChecked.put(i, isChecked);
        }
        notifyDataSetChanged();
        handleCheckedChanged();
    }

    int getCheckedCount() {
        int checkedCount = 0;
        for (int i = 0; i < getCount(); i++) {
            if (mSparseItemChecked.get(i, false)) {
                checkedCount++;
            }
        }
        return checkedCount;
    }

    boolean isAllChoose() {
        int checkedCount = 0;
        for (int i = 0; i < getCount(); i++) {
            if (mSparseItemChecked.get(i, false)) {
                checkedCount++;
            }
        }
        return checkedCount == getCount();
    }


    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<VideoSetBean>(parent, R.layout.view_local_set_item) {
            @Override
            public void setData(VideoSetBean item) {
                holder.setText(R.id.video_cache_name_tv, item.getTitle())
                        .setText(R.id.video_set_complete_total_info_tv,
                                mContext.getString(R.string.complete_video_set_info,
                                        item.getCount(), StringUtils.convertStorageNoB(item.getTotalBytes())));
                holder.setImageUrl(R.id.video_set_cover_iv, item.getCoverImg());

//                GlideApp.with(mContext).load(item.getCoverImg()).dontAnimate().centerCrop().apply(RequestOptions.bitmapTransform(new GlideRoundTransform(3, GlideRoundTransform.CornerType.ALL))).into((ImageView) holder.getView(R.id.video_set_cover_iv));
////                GlideApp.with(mContext).load(item.getCoverImg()).centerCrop().transform(new GlideRoundTransform(3, 100, GlideRoundTransform.CornerType.ALL)).into((ImageView) holder.getView(R.id.video_set_cover_one_iv));
////                GlideApp.with(mContext).load(item.getCoverImg()).centerCrop().transform(new GlideRoundTransform(3, 50, GlideRoundTransform.CornerType.ALL)).into((ImageView) holder.getView(R.id.video_set_cover_two_iv));

                CheckBox delCB = holder.getView(R.id.video_set_edit_cb);
                if (mIsEditMode) {
                    delCB.setChecked(mSparseItemChecked.get(holder.getAdapterPosition()));
                    delCB.setVisibility(View.VISIBLE);
                } else {
                    delCB.setChecked(false);
                    delCB.setVisibility(View.GONE);
                }

                holder.getItemView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mIsEditMode) {
                            mSparseItemChecked.put(holder.getAdapterPosition(), !mSparseItemChecked.get(holder.getAdapterPosition()));
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
