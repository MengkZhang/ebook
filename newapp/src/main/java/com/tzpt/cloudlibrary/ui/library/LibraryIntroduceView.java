package com.tzpt.cloudlibrary.ui.library;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.utils.StringUtils;

/**
 * 本馆介绍
 * Created by tonyjia on 2018/11/20.
 */
public class LibraryIntroduceView extends LinearLayout {

    public LibraryIntroduceView(Context context) {
        this(context, null);
    }

    public LibraryIntroduceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LibraryIntroduceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private TextView mLibIntrocudeTv;
    private TextView mLibOpenTodayTv;
    private TextView mLibDistanceTv;
    private TextView mLibAddressTv;
    private ImageView mLibLogoIv;
    private Context mContext;

    private void initViews(Context context) {
        this.mContext = context;
        inflate(context, R.layout.view_library_introduce, this);
        mLibIntrocudeTv = (TextView) findViewById(R.id.lib_introduce_tv);
        mLibOpenTodayTv = (TextView) findViewById(R.id.lib_open_today_tv);
        mLibDistanceTv = (TextView) findViewById(R.id.lib_distance_tv);
        mLibAddressTv = (TextView) findViewById(R.id.lib_address_tv);
        mLibLogoIv = (ImageView) findViewById(R.id.lib_logo_iv);
    }

    /**
     * 设置今日开放
     *
     * @param todayInfo 今日开放
     */
    public void setOpenToday(String todayInfo) {
        mLibOpenTodayTv.setText(mContext.getString(R.string.open_today, todayInfo));
    }

    /**
     * 设置距离
     */
    public void setLibDistance(int distance) {
        mLibDistanceTv.setText(StringUtils.mToKm(distance));
    }

    /**
     * 设置地址
     *
     * @param address 地址
     */
    public void setAddress(String address) {
        mLibAddressTv.setText(mContext.getString(R.string.lib_address, address));

    }

    /**
     * 获取logo ImageView
     *
     * @return
     */
    public ImageView getLibLogoImageView() {
        return mLibLogoIv;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        mLibIntrocudeTv.setText(title);
    }

    /**
     * 设置营业时间
     *
     * @param todayInfo 营业时间
     */
    public void setOpenTimeToday(String todayInfo) {
        mLibOpenTodayTv.setText(mContext.getString(R.string.open_time_today, todayInfo));
    }
}
