package com.tzpt.cloudlibrary.ui.bookstore;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.adapter.glide.RoundedCornersTransformation;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 书店适配器
 * Created by ZhiqiangJia on 2017-12-6.
 */
public class BookStoreAdapter extends RecyclerArrayAdapter<LibraryBean> {

    private Drawable mNormalDrawable;
    private Drawable mRedDrawable;
    //    private Drawable mLightOnDrawable;
//    private Drawable mLightOffDrawable;
    private boolean mShowStatus = false;        //是否展示图书馆图书状态
    private boolean mRecommendNewBook = false;  //是否推荐新书图书馆列表
    private boolean mIsNormalColor = true;      //是否显示正常颜色
    private StringBuffer mLibInfoBuffer;

    public BookStoreAdapter(Context context, boolean showStatus, boolean recommendNewBook) {
        super(context);
        this.mShowStatus = showStatus;
        this.mRecommendNewBook = recommendNewBook;
        mNormalDrawable = context.getResources().getDrawable(R.mipmap.ic_home_lib_distance);
        mNormalDrawable.setBounds(0, 0, mNormalDrawable.getMinimumWidth(), mNormalDrawable.getMinimumHeight());
        mRedDrawable = context.getResources().getDrawable(R.mipmap.ic_position_red);
        mRedDrawable.setBounds(0, 0, mRedDrawable.getMinimumWidth(), mRedDrawable.getMinimumHeight());
//        mLightOnDrawable = context.getResources().getDrawable(R.mipmap.ic_light_on);
//        mLightOnDrawable.setBounds(0, 0, mLightOnDrawable.getMinimumWidth(), mLightOnDrawable.getMinimumHeight());
//        mLightOffDrawable = context.getResources().getDrawable(R.mipmap.ic_light_off);
//        mLightOffDrawable.setBounds(0, 0, mLightOffDrawable.getMinimumWidth(), mLightOffDrawable.getMinimumHeight());
    }

    //设置颜色
    public void refreshLibraryDistanceColor(boolean isNormalColor) {
        this.mIsNormalColor = isNormalColor;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<LibraryBean>(parent, R.layout.view_item_book_store) {
            @Override
            public void setData(LibraryBean item) {
                if (null != item) {
                    holder.setText(R.id.book_store_name_tv, item.mLibrary.mName)
                            .setText(R.id.book_store_address_tv, item.mLibrary.mAddress)
                            .setText(R.id.book_store_heat_tv, mContext.getString(R.string.home_library_person_count, StringUtils.formatHeatCount(item.mLibrary.mHeatCount)))
                            .setText(R.id.book_store_book_count_tv, mContext.getString(R.string.home_library_book_count, StringUtils.formatBookCount(item.mLibrary.mBookCount)))
                            .setText(R.id.book_store_distance_tv, StringUtils.mToKm(item.mDistance));
                    //设置图书馆logo
                    GlideApp.with(mContext)
                            .load(item.mLibrary.mLogo)
                            .placeholder(R.drawable.bg_00000000)
                            .error(R.mipmap.ic_default_lib_icon)
                            .transform(new RoundedCornersTransformation(5, RoundedCornersTransformation.CornerType.ALL))
                            .centerInside()
                            .into((ImageView) holder.getView(R.id.book_store_pic_img));
                    //设置距离
                    holder.setTextColor(R.id.book_store_distance_tv, mIsNormalColor ? Color.parseColor("#999999") : Color.RED);
                    //设置边框
                    holder.setCompoundLeftDrawables(R.id.book_store_distance_tv, mIsNormalColor ? mNormalDrawable : mRedDrawable);
//                    //设置是否图书馆开放
//                    if (null != item.mLibrary.mLighten && item.mLibrary.mLighten.equals("1")) {
//                        holder.setCompoundRightDrawables(R.id.book_store_name_tv, item.mIsOpen ? mLightOnDrawable : mLightOffDrawable);
//                    } else {
//                        holder.setCompoundRightDrawables(R.id.book_store_name_tv, null);
//                    }

                    //是否展示图书馆内的图书状态
                    if (mShowStatus) {
                        setVisible(R.id.book_store_status_tv, View.VISIBLE);
                        String inLib = item.mLibrary.mInLib;
                        String outLib = item.mLibrary.mOutLib;
                        if (mLibInfoBuffer == null) {
                            mLibInfoBuffer = new StringBuffer();
                        }
                        mLibInfoBuffer.setLength(0);
                        String inLibInfo = TextUtils.isEmpty(inLib) ? "" : inLib;
                        if (!TextUtils.isEmpty(inLibInfo) && !inLibInfo.equals("0")) {
                            mLibInfoBuffer.append(inLibInfo).append("册 在馆");
                        }
                        String outLibInfo = TextUtils.isEmpty(outLib) ? "" : outLib;
                        if (!TextUtils.isEmpty(outLibInfo) && !outLibInfo.equals("0")) {
                            mLibInfoBuffer.append("  ");
                            mLibInfoBuffer.append(outLibInfo).append("册 在借");
                        }
                        holder.setText(R.id.book_store_status_tv, mLibInfoBuffer.toString());
                    }

                    //设置推荐新书图书馆信息
                    if (mRecommendNewBook) {
                        holder.setVisible(R.id.book_store_recommend_book_flag_tv, item.recommendExist == 1 ? View.VISIBLE : View.GONE);
                    }
                }
            }
        };

    }

}
