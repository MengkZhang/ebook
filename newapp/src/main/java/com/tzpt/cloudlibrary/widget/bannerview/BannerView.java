package com.tzpt.cloudlibrary.widget.bannerview;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.ui.main.BannerAdapter;

/**
 * banner
 * Created by tonyjia on 2018/10/22.
 */
public class BannerView extends RelativeLayout {

    public BannerView(@NonNull Context context) {
        this(context, null);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private LoopViewPager mBannerVp;
    private TextView mBannerTitleTv;
    private ImageView mBannerDefaultImg;
    private CircleIndicator mCircleIndicator;
    private BannerAdapter mBannerAdapter;

    private LoopViewPager.OnDispatchTouchEventListener mDispatchOnTouchListener = new LoopViewPager.OnDispatchTouchEventListener() {
        @Override
        public void onDispatchKeyEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mBannerVp.setLooperPic(false);
            } else if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_CANCEL) {
                mBannerVp.setLooperPic(true);
            }
        }

        @Override
        public void onPageChanged(int realPosition, int realCount) {
            if (mBannerAdapter != null && realPosition < mBannerAdapter.getCount()) {
                setBannerTitle(mBannerAdapter.getItemData(realPosition).mTitle);
            }
        }
    };

    private void initViews(Context context) {
        inflate(context, R.layout.view_banner, this);
        mBannerTitleTv = (TextView) findViewById(R.id.banner_title_tv);
        mCircleIndicator = (CircleIndicator) findViewById(R.id.banner_indicator);
        mBannerVp = (LoopViewPager) findViewById(R.id.banner_vp);
        mBannerDefaultImg = (ImageView) findViewById(R.id.banner_default_img);

        mBannerVp.setOnDispatchTouchEventListener(mDispatchOnTouchListener);
        mBannerVp.setLooperPic(true);

    }

    public void setAdapter(BannerAdapter adapter) {
        mBannerAdapter = adapter;
        mBannerVp.setAdapter(mBannerAdapter);
        mCircleIndicator.setViewPager(mBannerVp);
    }

    public void setBannerTitle(String title) {
        mBannerTitleTv.setText(title);
    }

    public void showBannerView() {
        mBannerDefaultImg.setVisibility(View.GONE);
        mBannerVp.setVisibility(View.VISIBLE);
        mBannerTitleTv.setVisibility(View.VISIBLE);
        mCircleIndicator.setVisibility(View.VISIBLE);
    }

    public void hideBannerView() {
        if (mBannerVp.getChildCount() == 0) {
            mBannerDefaultImg.setVisibility(View.VISIBLE);
            mBannerDefaultImg.setBackgroundResource(R.mipmap.ic_information_no_data);
            mBannerVp.setVisibility(View.GONE);
            mBannerTitleTv.setVisibility(View.GONE);
            mCircleIndicator.setVisibility(View.GONE);
        }
    }

    public void releaseBanner() {
        mBannerVp.releaseLoopPicHandler();
        mBannerVp.setOnDispatchTouchEventListener(null);
    }

    public void pauseLoopPicHandler() {
        mBannerVp.setLooperPic(false);
        mBannerVp.releaseLoopPicHandler();
    }

    public void restoreLoopPicHandler() {
        mBannerVp.setLooperPic(true);
        mBannerVp.loopPictureIfNeed();
    }
}
