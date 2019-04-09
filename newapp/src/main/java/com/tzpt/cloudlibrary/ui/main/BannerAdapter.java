package com.tzpt.cloudlibrary.ui.main;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.bean.BannerInfo;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;

import java.util.ArrayList;
import java.util.List;

/**
 * banner 适配器
 * Created by Administrator on 2017/6/12.
 */

public class BannerAdapter extends PagerAdapter {
    private Context mContext;
    private List<BannerInfo> mData = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;


    public BannerAdapter(Context context) {
        mContext = context;
    }

    public void addAllData(List<BannerInfo> list) {
        mData.clear();
        mData.addAll(list);
    }

    public void clearBannerData() {
        if (null != mData) {
            mData.clear();
        }
    }

    public BannerInfo getItemData(int realPosition) {
        if (mData != null && !mData.isEmpty()) {
            return mData.get(realPosition);
        }
        return null;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_banner_item, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.banner_img_iv);
        container.addView(view);
        if (null != mData) {
            GlideApp.with(mContext).load(mData.get(position).mImgUrl).centerInside().error(R.mipmap.ic_banner_no_pic).centerCrop().into(imageView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onClick(mData.get(position));
                }
            });
        }
        return view;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(BannerInfo item);
    }
}
