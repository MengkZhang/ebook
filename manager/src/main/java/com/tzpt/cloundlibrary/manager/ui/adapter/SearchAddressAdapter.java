package com.tzpt.cloundlibrary.manager.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.bean.SearchAddressBean;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.BaseViewHolder;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.RecyclerArrayAdapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 搜索地址类
 * Created by ZhiqiangJia on 2017-10-23.
 */
public class SearchAddressAdapter extends RecyclerArrayAdapter<SearchAddressBean> {

    private String mKeyWord = "";

    public SearchAddressAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<SearchAddressBean>(parent, R.layout.view_search_address_item) {

            @Override
            public void setData(SearchAddressBean item) {
                if (null != item) {
                    holder.setText(R.id.address_location_tv, item.mAddressLocation);
                    if (!TextUtils.isEmpty(item.mAddressName)) {
                        if (!TextUtils.isEmpty(mKeyWord)) {
                            holder.setText(R.id.address_name_tv, setKeyWordColor(item.mAddressName, mKeyWord));
                        } else {
                            holder.setText(R.id.address_name_tv, item.mAddressName);
                        }
                    } else {
                        holder.setText(R.id.address_name_tv, "");
                    }
                }
            }
        };
    }

    //设置搜索内容
    public void setSearchContent(String content) {
        this.mKeyWord = content;
        notifyDataSetChanged();
    }

    private SpannableString setKeyWordColor(String content, String keyword) {
        SpannableString s = new SpannableString(content);
        Pattern p = Pattern.compile(keyword);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(Color.parseColor("#9e724d")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }
}
