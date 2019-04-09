package com.tzpt.cloudlibrary.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.utils.StatusBarUtil;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.touchimageview.TouchImageView;
import com.tzpt.cloudlibrary.widget.touchimageview.TouchViewPager;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 图片预览界面
 */
public class GalleyActivity extends BaseActivity {

    private static final String IMAGE_LIST = "image_list";
    private static final String IMAGE_LEFT = "image_left";
    private static final String IMAGE_TOP = "image_top";
    private static final String IMAGE_WIDTH = "image_width";
    private static final String IMAGE_HEIGHT = "image_height";

    public static void startActivity(Activity activity, ArrayList<String> imageList, int left, int top, int width, int height) {
        Intent intent = new Intent(activity, GalleyActivity.class);
        intent.putExtra(IMAGE_LIST, imageList);
        intent.putExtra(IMAGE_LEFT, left);
        intent.putExtra(IMAGE_TOP, top);
        intent.putExtra(IMAGE_WIDTH, width);
        intent.putExtra(IMAGE_HEIGHT, height);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    @BindView(R.id.touch_view_pager)
    TouchViewPager mTouchViewPager;

    TouchImageView[] mTouchImageViews;

    private View.OnClickListener mPicClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (((TouchImageView) v).isDraging()) {
                return;
            }
            ((TouchImageView) v).executeFinishAnimation();
        }
    };

    private TouchImageView.GalleyCallback mCallback = new TouchImageView.GalleyCallback() {
        @Override
        public void callbackGalley() {
            finish();
        }
    };

    @Override
    public int getLayoutId() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_galley;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        StatusBarUtil.transparencyBar(this);
    }

    @Override
    public void configViews() {
        ArrayList<String> imageList = getIntent().getStringArrayListExtra(IMAGE_LIST);
        if (null != imageList && imageList.size() > 0) {
            mTouchImageViews = new TouchImageView[imageList.size()];
            for (int i = 0; i < imageList.size(); i++) {
                mTouchImageViews[i] = (TouchImageView) View.inflate(this, R.layout.view_galley_pic_item, null);
                mTouchImageViews[i].setImageClickListener(mCallback);
                mTouchImageViews[i].setOnClickListener(mPicClickListener);
            }

            mTouchViewPager.setAdapter(new TouchPagerAdapter(mTouchImageViews, imageList));
        }

        mTouchViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                mTouchViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Intent intent = getIntent();

                int left = intent.getIntExtra(IMAGE_LEFT, 0);
                int top = intent.getIntExtra(IMAGE_TOP, 0);
                int width = intent.getIntExtra(IMAGE_WIDTH, 0);
                int height = intent.getIntExtra(IMAGE_HEIGHT, 0);
                mTouchImageViews[0].executeScaleAnimation(left, top, width, height);
            }
        });
    }

    private static class TouchPagerAdapter extends PagerAdapter {
        private TouchImageView[] mTouchImageViews;
        private ArrayList<String> mImageList;


        TouchPagerAdapter(TouchImageView[] touchImageViews, ArrayList<String> imageList) {
            this.mTouchImageViews = touchImageViews;
            this.mImageList = imageList;
        }

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            GlideApp.with(container.getContext())
                    .load(mImageList.get(position))
                    .error(R.mipmap.ic_nopicture)
                    .centerInside()
                    .into(mTouchImageViews[position]);

            container.addView(mTouchImageViews[position]);
            return mTouchImageViews[position];
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mTouchImageViews[position]);
        }
    }

    @Override
    public void onBackPressed() {
        mTouchImageViews[0].executeFinishAnimation();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
