package com.tzpt.cloudlibrary.ui.library;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.adapter.glide.RoundedCornersTransformation;
import com.tzpt.cloudlibrary.bean.LibraryMainBranchBean;
import com.tzpt.cloudlibrary.utils.StringUtils;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 总分馆
 */
public class LibraryMainBranchAdapter extends RecyclerArrayAdapter<LibraryMainBranchBean> {

    private Drawable mNormalDrawable;
    private Drawable mRedDrawable;
//    private Drawable mLightOnDrawable;
//    private Drawable mLightOffDrawable;
    private boolean mIsNormalColor = true;      //是否显示正常颜色

    public LibraryMainBranchAdapter(Context context) {
        super(context);
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
    public int getViewType(int position) {
        if (getItem(position).mLevel == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        //group title
        if (viewType == 0) {
            return new BaseViewHolder<LibraryMainBranchBean>(parent, R.layout.view_item_library_group_title) {
                @Override
                public void setData(LibraryMainBranchBean item) {
                    holder.setText(R.id.group_title_tv, !TextUtils.isEmpty(item.mGroupTitle) ? item.mGroupTitle : "");
                    holder.setVisible(R.id.group_line_v, getAdapterPosition() != 0);
                }
            };
        } else {
            //library info
            return new BaseViewHolder<LibraryMainBranchBean>(parent, R.layout.view_item_branch_library) {
                @Override
                public void setData(LibraryMainBranchBean item) {
                    if (null != item.mLibraryBean) {
                        holder.setText(R.id.library_name_tv, item.mLibraryBean.mLibrary.mName)
                                .setText(R.id.library_address_tv, item.mLibraryBean.mLibrary.mAddress)
                                .setText(R.id.library_heat_tv, mContext.getString(R.string.home_library_person_count, StringUtils.formatHeatCount(item.mLibraryBean.mLibrary.mHeatCount)))
                                .setText(R.id.library_book_count_tv, mContext.getString(R.string.home_library_book_count, StringUtils.formatBookCount(item.mLibraryBean.mLibrary.mBookCount)))
                                .setText(R.id.library_distance_tv, StringUtils.mToKm(item.mLibraryBean.mDistance));
                        //设置距离
                        holder.setTextColor(R.id.library_distance_tv, mIsNormalColor ? Color.parseColor("#999999") : Color.RED);
                        //设置边框
                        holder.setCompoundLeftDrawables(R.id.library_distance_tv, mIsNormalColor ? mNormalDrawable : mRedDrawable);
                        //设置图书馆logo
                        GlideApp.with(mContext)
                                .load(item.mLibraryBean.mLibrary.mLogo)
                                .placeholder(R.drawable.bg_00000000)
                                .error(R.mipmap.ic_default_lib_icon)
                                .transform(new RoundedCornersTransformation(5, RoundedCornersTransformation.CornerType.ALL))
                                .centerInside()
                                .into((ImageView) holder.getView(R.id.library_pic_img));
//                        //设置是否图书馆开放
//                        if (null != item.mLibraryBean.mLibrary.mLighten && item.mLibraryBean.mLibrary.mLighten.equals("1")) {
//                            holder.setCompoundRightDrawables(R.id.library_name_tv, item.mLibraryBean.mIsOpen ? mLightOnDrawable : mLightOffDrawable);
//                        } else {
//                            holder.setCompoundRightDrawables(R.id.library_name_tv, null);
//                        }
                        holder.setVisible(R.id.library_line_v, getAdapterPosition() + 1 == getCount() ? View.GONE : View.VISIBLE);
                    }
                }
            };
        }

    }
}
