package com.tzpt.cloudlibrary.ui.main;

import android.content.Context;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.EasyRVAdapter;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.EasyRVHolder;
import com.tzpt.cloudlibrary.bean.ModelMenu;

/**
 * 图书馆模块适配器
 * Created by tonyjia on 2018/10/19.
 */
public class LibMenuModelListAdapter extends EasyRVAdapter<ModelMenu> {

    public LibMenuModelListAdapter(Context context) {
        super(context, R.layout.view_lib_model_item);
    }

    @Override
    protected void onBindData(EasyRVHolder viewHolder, int position, ModelMenu item) {
        viewHolder.setText(R.id.home_model_name_tv, item.mName);
        if (item.mIsBaseModel) {
            viewHolder.setImageDrawableRes(R.id.home_model_logo_iv, item.mResId);
        } else {
            viewHolder.setImageUrl(R.id.home_model_logo_iv, item.mLogoUrl);
        }
    }
}
