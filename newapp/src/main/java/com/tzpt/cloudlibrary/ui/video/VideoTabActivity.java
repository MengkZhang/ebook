package com.tzpt.cloudlibrary.ui.video;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.ClassifyTwoLevelBean;
import com.tzpt.cloudlibrary.bean.TabMenuBean;
import com.tzpt.cloudlibrary.ui.library.MyPagerAdapter;
import com.tzpt.cloudlibrary.ui.search.SearchActivity;
import com.tzpt.cloudlibrary.widget.ClassifyTwoLevelSelectLayout;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;
import com.tzpt.cloudlibrary.widget.tablayout.RecyclerTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 视频
 */
public class VideoTabActivity extends BaseActivity implements
        VideoTabContract.View {

    private static final String LIB_CODE = "lib_code";
    private static final String LIB_TITLE = "lib_title";

    public static void startActivity(Context context, String libCode, String title) {
        Intent intent = new Intent(context, VideoTabActivity.class);
        intent.putExtra(LIB_CODE, libCode);
        intent.putExtra(LIB_TITLE, title);
        context.startActivity(intent);
    }

    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.recycler_tab_layout)
    RecyclerTabLayout mRecyclerTabLayout;
    @BindView(R.id.classify_two_level_layout)
    ClassifyTwoLevelSelectLayout mClassifySelectLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    private VideoTabPresenter mPresenter;
    private MyPagerAdapter mPagerAdapter;
    private List<Fragment> mVideoFragmentList = new ArrayList<>();
    private VideoListFragment mHotListFragment;
    private VideoListFragment mNewListFragment;
    private int mCurrentParentClassifyId = 0;
    private int mCurrentClassifyId = 0;
    private int mVideoTab1ClassifyId;
    private int mVideoTab2ClassifyId;
    private String mLibCode;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_btn:
                if (TextUtils.isEmpty(mLibCode)) {
                    SearchActivity.startActivityFromType(this, 3);
                } else {
                    SearchActivity.startActivityFromLib(this, mLibCode, 3);
                }
                break;
        }
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0://一周热门
                    if (mVideoTab1ClassifyId == mCurrentClassifyId) {
                        return;
                    }
                    mVideoTab1ClassifyId = mCurrentClassifyId;
                    if (TextUtils.isEmpty(mLibCode)) {
                        mHotListFragment.dealClassifyClick(mCurrentParentClassifyId, mVideoTab1ClassifyId);
                    } else {
                        mNewListFragment.dealClassifyClick(mCurrentParentClassifyId, mVideoTab1ClassifyId);
                    }
                    break;
                case 1://最新上架
                    if (mVideoTab2ClassifyId == mCurrentClassifyId) {
                        return;
                    }
                    mVideoTab2ClassifyId = mCurrentClassifyId;
                    mNewListFragment.dealClassifyClick(mCurrentParentClassifyId, mVideoTab2ClassifyId);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private ClassifyTwoLevelSelectLayout.OnSelectListener mOnSelectListener = new ClassifyTwoLevelSelectLayout.OnSelectListener() {
        @Override
        public void onSelect(int position, int parentClassifyId, int childClassifyId) {
            mCurrentParentClassifyId = parentClassifyId;
            mCurrentClassifyId = childClassifyId;
            int currentItem = mViewPager.getCurrentItem();
            switch (currentItem) {
                case 0://一周热门
                    mVideoTab1ClassifyId = childClassifyId;
                    if (TextUtils.isEmpty(mLibCode)) {
                        mHotListFragment.dealClassifyClick(parentClassifyId, childClassifyId);
                    } else {
                        mNewListFragment.dealClassifyClick(parentClassifyId, childClassifyId);
                    }
                    break;
                case 1://最新上架
                    mVideoTab2ClassifyId = childClassifyId;
                    mNewListFragment.dealClassifyClick(parentClassifyId, childClassifyId);
                    break;
            }
        }
    };

    @Override
    public int getLayoutId() {
        mLibCode = getIntent().getStringExtra(LIB_CODE);
        if (TextUtils.isEmpty(mLibCode)) {
            return R.layout.activity_video_tab;
        } else {
            return R.layout.activity_video_lib_tab;
        }
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setRightBtnClickAble(true);
        mCommonTitleBar.setRightBtnIcon(R.drawable.bg_btn_search);
        if (TextUtils.isEmpty(mLibCode)) {
            mCommonTitleBar.setTitle("视频");
            mClassifySelectLayout.setRightTextGravityEND();
            mClassifySelectLayout.cutClassifyNameLength(true);
        } else {
            String title = getIntent().getStringExtra(LIB_TITLE);
            mCommonTitleBar.setTitle(title);
            mRecyclerTabLayout.setVisibility(View.GONE);
            mClassifySelectLayout.cutClassifyNameLength(false);
        }
    }

    @Override
    public void initDatas() {
        mPresenter = new VideoTabPresenter();
        mPresenter.attachView(this);
        mPresenter.getVideoGradeList();
    }

    @Override
    public void configViews() {
        mClassifySelectLayout.setGradeName("全部分类");
        mClassifySelectLayout.setDriverVisibility(false);
        mClassifySelectLayout.setOnSelectListener(mOnSelectListener);
    }

    //<editor-fold desc='视频分类回调'>
    @Override
    public void setVideoGradeList(List<ClassifyTwoLevelBean> videoGradeList) {
        if (null != videoGradeList) {
            mClassifySelectLayout.setEnabled(true);
            mClassifySelectLayout.setData(videoGradeList);
        }
        if (mPagerAdapter == null) {
            final List<TabMenuBean> tabList = new ArrayList<>();
            if (TextUtils.isEmpty(mLibCode)) {
                TabMenuBean menuHot = new TabMenuBean("一周热门");
                TabMenuBean menuNew = new TabMenuBean("最新上架");
                tabList.add(menuHot);
                tabList.add(menuNew);
                mVideoFragmentList.clear();
                //一周热门
                mHotListFragment = VideoListFragment.newInstance();
                VideoListPresenter hotPresenter = new VideoListPresenter(mHotListFragment);
                hotPresenter.setFilterType(VideoFilterType.WEEK_HOT_VIDEO_LIST);
                if (!TextUtils.isEmpty(mLibCode)) {
                    hotPresenter.setLibCode(mLibCode);
                }
                mVideoFragmentList.add(mHotListFragment);
                //最新上架
                mNewListFragment = VideoListFragment.newInstance();
                VideoListPresenter newPresenter = new VideoListPresenter(mNewListFragment);
                newPresenter.setFilterType(VideoFilterType.NEW_VIDEO_LIST);
                if (!TextUtils.isEmpty(mLibCode)) {
                    newPresenter.setLibCode(mLibCode);
                }
                mVideoFragmentList.add(mNewListFragment);
                mViewPager.setOffscreenPageLimit(2);
                mRecyclerTabLayout.setTabOnScreenLimit(2);
            } else {
                //馆内视频
                TabMenuBean menuNew = new TabMenuBean("视频");
                tabList.add(menuNew);
                mVideoFragmentList.clear();
                //最新上架
                mNewListFragment = VideoListFragment.newInstance();
                VideoListPresenter newPresenter = new VideoListPresenter(mNewListFragment);
                newPresenter.setFilterType(VideoFilterType.NEW_VIDEO_LIST);
                if (!TextUtils.isEmpty(mLibCode)) {
                    newPresenter.setLibCode(mLibCode);
                }
                mVideoFragmentList.add(mNewListFragment);
                mViewPager.setOffscreenPageLimit(1);
                mRecyclerTabLayout.setTabOnScreenLimit(1);
            }
            mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), mVideoFragmentList, tabList);
            mViewPager.setAdapter(mPagerAdapter);
            mRecyclerTabLayout.setUpWithViewPager(tabList, mViewPager);
            mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        } else {
            mPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showProgress() {
        mMultiStateLayout.showProgress();
    }

    @Override
    public void setNetError() {
        mMultiStateLayout.showError();
        mMultiStateLayout.showRetryError(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getVideoGradeList();
            }
        });
    }

    @Override
    public void setContentView() {
        mMultiStateLayout.showContentView();
    }


    //</editor-fold>
    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
    }

    @Override
    protected void onDestroy() {
        mRecyclerTabLayout.clearList();
        mClassifySelectLayout.releaseClassify();
        mPresenter.detachView();
        super.onDestroy();
    }

}
