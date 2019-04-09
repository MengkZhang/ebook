package com.tzpt.cloudlibrary.ui.library;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.adapter.glide.RoundedCornersTransformation;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.EasyRVAdapter;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.EasyRVHolder;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;

/**
 * 图书馆适配器
 * Created by ZhiqiangJia on 2017-08-02.
 */
public class LibraryHomeAdapter extends EasyRVAdapter<LibraryBean> {

    private Drawable mNormalDrawable;
    private Drawable mRedDrawable;
//    private Drawable mLightOnDrawable;
//    private Drawable mLightOffDrawable;

    private boolean mIsNormalColor = true;      //是否显示正常颜色

    public LibraryHomeAdapter(Context context) {
        super(context, R.layout.view_item_library_home);
        setHasStableIds(true);
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
    protected void onBindData(EasyRVHolder holder, int position, LibraryBean item) {
        holder.setText(R.id.library_name_tv, item.mLibrary.mName)
                .setText(R.id.library_address_tv, item.mLibrary.mAddress)
                .setText(R.id.library_heat_tv, mContext.getString(R.string.home_library_person_count, StringUtils.formatHeatCount(item.mLibrary.mHeatCount)))
                .setText(R.id.library_distance_tv, StringUtils.mToKm(item.mDistance));

        holder.setText(R.id.library_book_count_tv, mContext.getString(R.string.home_library_book_count, StringUtils.formatBookCount(item.mLibrary.mBookCount)));

        //设置图书馆logo
        GlideApp.with(mContext)
                .load(item.mLibrary.mLogo)
                .transform(new RoundedCornersTransformation(5, RoundedCornersTransformation.CornerType.ALL))
                .placeholder(R.drawable.bg_00000000)
                .error(R.mipmap.ic_default_lib_icon)
                .centerInside()
                .into((ImageView) holder.getView(R.id.library_pic_img));
        //设置距离
        holder.setTextColor(R.id.library_distance_tv, mIsNormalColor ? Color.parseColor("#999999") : Color.RED);
        //设置边框
        holder.setCompoundLeftDrawables(R.id.library_distance_tv, mIsNormalColor ? mNormalDrawable : mRedDrawable);
//        //设置是否图书馆开放
//        if (null != item.mLibrary.mLighten && item.mLibrary.mLighten.equals("1")) {
//            holder.setCompoundRightDrawables(R.id.library_name_tv, item.mIsOpen ? mLightOnDrawable : mLightOffDrawable);
//        } else {
//            holder.setCompoundRightDrawables(R.id.library_name_tv, null);
//        }
    }
}
