package com.tzpt.cloudlibrary.ui.account;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;

import java.util.List;

/**
 * Created by Administrator on 2018/11/9.
 */

public class GalleryGridViewAdapter extends RecyclerView.Adapter<GalleryGridViewAdapter.CustomViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<String> mData;
    private OnItemClickListener mOnItemClickListener;

    public GalleryGridViewAdapter(Context context, List<String> list, OnItemClickListener listener) {
        mContext = context;
        mData = list;
        mInflater = LayoutInflater.from(context);
        mOnItemClickListener = listener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.view_gallery_item, parent, false);
        final CustomViewHolder viewHolder = new CustomViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        GlideApp.with(mContext)
                .load(mData.get(position))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView img;

        public CustomViewHolder(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.gallery_photo_item_iv);
        }
    }

    interface OnItemClickListener {
        void onItemClick(int position);
    }
}
