package com.tzpt.cloudlibrary.app.ebook.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.app.ebook.R;
import com.tzpt.cloudlibrary.app.ebook.books.model.ChoosedColor;
import com.tzpt.cloudlibrary.app.ebook.utils.HelpUtils;


/**
 * 选择颜色
 * Created by ZhiqiangJia on 2016-07-05.
 */
public class ReadingChooseColorAdapter extends BaseRecyclerAdapter<ChoosedColor> {

    private int mGralleyWidth;

    public ReadingChooseColorAdapter(Context context) {
        mGralleyWidth = (HelpUtils.getScreenWidth(context) - HelpUtils.dip2px(context, 20)) / 5;
    }

    /**
     * 设置主题默认选择
     *
     * @param selected
     */
    public void setSelected(int selected) {
        int size = getDatas().size();
        for (int i = 0; i < size; i++) {
            getDatas().get(i).colorChoosed = (selected == i);
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_item_bg_gallery, null);
        return new ChoosedColorViewHolder(view);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, ChoosedColor data) {
        if (viewHolder instanceof ChoosedColorViewHolder) {
            ChoosedColorViewHolder holder = (ChoosedColorViewHolder) viewHolder;
            holder.itemView.setTag(RealPosition);
            holder.mGalleryParent.setLayoutParams(new GridLayoutManager.LayoutParams(
                    mGralleyWidth, ViewGroup.LayoutParams.MATCH_PARENT));//设置宽高
            if (null != data) {
                holder.mRoundImageView.setBackgroundResource(data.defaultResourseId);
                holder.mImageBorder.setBackgroundResource(data.colorChoosed ?
                        R.mipmap.ic_reading_icon_border_selected
                        : 0);
                holder.mTextViewColor.setTextColor(Color.parseColor(data.textColorStringResourse));
            }
        }
    }


    private class ChoosedColorViewHolder extends BaseRecyclerAdapter.Holder {

        private RelativeLayout mGalleryParent;
        private ImageView mRoundImageView;
        private TextView mTextViewColor;
        private ImageView mImageBorder;

        private ChoosedColorViewHolder(View itemView) {
            super(itemView);
            mGalleryParent = (RelativeLayout) itemView.findViewById(R.id.mGalleryParent);
            mRoundImageView = (ImageView) itemView.findViewById(R.id.round_imageview);
            mTextViewColor = (TextView) itemView.findViewById(R.id.text_color);
            mImageBorder = (ImageView) itemView.findViewById(R.id.mImageBorder);

        }
    }
}
