package com.tzpt.cloudlibrary.ui.ebook;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
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
 * 电子书
 */
public class EBookTabActivity extends BaseActivity implements
        EBookTabContract.View {

    private static final String FROM_TYPE = "from_type";
    private static final String LIBRARY_CODE = "library_code";
    private static final String LIBRARY_TITLE = "library_title";

    public static void startActivity(Context context, int fromType) {
        Intent intent = new Intent(context, EBookTabActivity.class);
        intent.putExtra(FROM_TYPE, fromType);
        context.startActivity(intent);
    }

    /**
     * 馆内电子书
     *
     * @param context
     * @param libCode 馆号
     */
    public static void startActivityFromLib(Context context, String libCode, String title) {
        Intent intent = new Intent(context, EBookTabActivity.class);
        intent.putExtra(FROM_TYPE, 2);
        intent.putExtra(LIBRARY_CODE, libCode);
        intent.putExtra(LIBRARY_TITLE, title);
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

    //首页
    private EBookFragment mEBookFragment1;
    private EBookFragment mEBookFragment2;
    private EBookFragment mEBookFragment3;

    private MyPagerAdapter mPagerAdapter;
    private EBookTabPresenter mPresenter;

    private int mCurrentClassifyId = 0;
    private int mCurrentParentClassifyId = 0;
    private int mTab1ClassifyId = 0;
    private int mTab2ClassifyId = 0;
    private int mTab3ClassifyId = 0;
    private String mLibraryCode;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_btn:
                if (TextUtils.isEmpty(mLibraryCode)) {
                    SearchActivity.startActivityFromType(this, 1);
                } else {
                    SearchActivity.startActivityFromLib(this, mLibraryCode, 1);
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
                case 0:
                    if (mTab1ClassifyId == mCurrentClassifyId) {
                        return;
                    }
                    mTab1ClassifyId = mCurrentClassifyId;
                    mEBookFragment1.dealClassifyClick(mCurrentParentClassifyId, mTab1ClassifyId);
                    break;
                case 1:
                    if (mTab2ClassifyId == mCurrentClassifyId) {
                        return;
                    }
                    mTab2ClassifyId = mCurrentClassifyId;
                    mEBookFragment2.dealClassifyClick(mCurrentParentClassifyId, mTab2ClassifyId);
                    break;
                case 2:
                    if (mTab3ClassifyId == mCurrentClassifyId) {
                        return;
                    }
                    mTab3ClassifyId = mCurrentClassifyId;
                    mEBookFragment3.dealClassifyClick(mCurrentParentClassifyId, mTab3ClassifyId);
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
                case 0:
                    mTab1ClassifyId = childClassifyId;
                    mEBookFragment1.dealClassifyClick(parentClassifyId, childClassifyId);
                    break;
                case 1:
                    mTab2ClassifyId = childClassifyId;
                    mEBookFragment2.dealClassifyClick(parentClassifyId, childClassifyId);
                    break;
                case 2:
                    mTab3ClassifyId = childClassifyId;
                    mEBookFragment3.dealClassifyClick(parentClassifyId, childClassifyId);
                    break;
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_ebook_tab;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        int fromType = getIntent().getIntExtra(FROM_TYPE, 0);//0首页 1阅读排行榜 2 馆内电子书

        mPresenter = new EBookTabPresenter();
        mPresenter.attachView(this);

        switch (fromType) {
            case 0://首页
                initMainEBookList();

                mPresenter.getEBookGradeList();
                break;
            case 1://排行榜
                initRankingEBookList();

                mMultiStateLayout.showContentView();
                break;
            case 2://馆内电子书
                initLibEBookList();

                mPresenter.getEBookGradeList();
                break;
        }
    }

    @Override
    public void configViews() {

    }

    private void initMainEBookList() {
        mCommonTitleBar.setTitle("电子书");
        mCommonTitleBar.setRightBtnClickAble(true);
        mCommonTitleBar.setRightBtnIcon(R.drawable.bg_btn_search);
        mClassifySelectLayout.setGradeName("全部分类");
        mClassifySelectLayout.setRightTextGravityEND();
        mClassifySelectLayout.cutClassifyNameLength(true);
        mClassifySelectLayout.setDriverVisibility(false);
        mClassifySelectLayout.setOnSelectListener(mOnSelectListener);

        final List<TabMenuBean> tabList = new ArrayList<>();
        TabMenuBean menu1 = new TabMenuBean("一周热门");
        TabMenuBean menu2 = new TabMenuBean("好书推荐");
        TabMenuBean menu3 = new TabMenuBean("最新上架");
        tabList.add(menu1);
        tabList.add(menu2);
        tabList.add(menu3);

        mEBookFragment1 = EBookFragment.newInstance();
        EBookPresenter recommendPresenter = new EBookPresenter(mEBookFragment1);
        recommendPresenter.setFilterType(EBookFilterType.One_Week_Hot);

        mEBookFragment2 = EBookFragment.newInstance();
        EBookPresenter eBookPresenter = new EBookPresenter(mEBookFragment2);
        eBookPresenter.setFilterType(EBookFilterType.Recommendations_EBook_List);

        mEBookFragment3 = EBookFragment.newInstance();
        EBookPresenter newEBookPresenter = new EBookPresenter(mEBookFragment3);
        newEBookPresenter.setFilterType(EBookFilterType.New_EBook_List);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(mEBookFragment1);
        fragmentList.add(mEBookFragment2);
        fragmentList.add(mEBookFragment3);
        mViewPager.setOffscreenPageLimit(2);
        mRecyclerTabLayout.setTabOnScreenLimit(2);

        mClassifySelectLayout.setWeightSum(fragmentList.size());

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList, tabList);
        mViewPager.setAdapter(mPagerAdapter);
        mRecyclerTabLayout.setUpWithViewPager(tabList, mViewPager);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
    }

    private void initLibEBookList() {
        mLibraryCode = getIntent().getStringExtra(LIBRARY_CODE);
        String title = getIntent().getStringExtra(LIBRARY_TITLE);
        mCommonTitleBar.setTitle(title);
        mCommonTitleBar.setRightBtnClickAble(true);
        mCommonTitleBar.setRightBtnIcon(R.drawable.bg_btn_search);
        mClassifySelectLayout.setGradeName("全部分类");
        mRecyclerTabLayout.setVisibility(View.GONE);
        mClassifySelectLayout.cutClassifyNameLength(false);
        mClassifySelectLayout.setDriverVisibility(false);
        mClassifySelectLayout.setOnSelectListener(mOnSelectListener);

        final List<TabMenuBean> tabList = new ArrayList<>();

        TabMenuBean libMenu2 = new TabMenuBean("电子书");
        tabList.add(libMenu2);
        mEBookFragment1 = EBookFragment.newInstance();
        EBookPresenter libEBookPresenter = new EBookPresenter(mEBookFragment1);
        libEBookPresenter.setFilterType(EBookFilterType.Library_EBook_List);
        if (!TextUtils.isEmpty(mLibraryCode)) {
            ArrayMap<String, String> arrayMap = new ArrayMap<>();
            arrayMap.put("libCode", mLibraryCode);
            libEBookPresenter.setParameter(arrayMap);
        }

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(mEBookFragment1);
        mViewPager.setOffscreenPageLimit(1);
        mRecyclerTabLayout.setTabOnScreenLimit(1);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList, tabList);
        mViewPager.setAdapter(mPagerAdapter);
        mRecyclerTabLayout.setUpWithViewPager(tabList, mViewPager);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
    }

    private void initRankingEBookList() {
        mCommonTitleBar.setTitle("阅读排行榜");
        mClassifySelectLayout.setVisibility(View.GONE);

        final List<TabMenuBean> tabList = new ArrayList<>();
        TabMenuBean menu4 = new TabMenuBean("月榜");
        TabMenuBean menu5 = new TabMenuBean("季榜");
        TabMenuBean menu6 = new TabMenuBean("年榜");
        tabList.add(menu4);
        tabList.add(menu5);
        tabList.add(menu6);
        //月榜
        mEBookFragment1 = EBookFragment.newInstance();
        EBookPresenter monthPresenter = new EBookPresenter(mEBookFragment1);
        monthPresenter.setFilterType(EBookFilterType.Month_Rank_EBook_List);
        //季榜
        mEBookFragment2 = EBookFragment.newInstance();
        EBookPresenter quartPresenter = new EBookPresenter(mEBookFragment2);
        quartPresenter.setFilterType(EBookFilterType.Quart_Rank_EBook_List);
        //年榜
        mEBookFragment3 = EBookFragment.newInstance();
        EBookPresenter yearPresenter = new EBookPresenter(mEBookFragment3);
        yearPresenter.setFilterType(EBookFilterType.Year_Rank_EBook_List);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(mEBookFragment1);
        fragmentList.add(mEBookFragment2);
        fragmentList.add(mEBookFragment3);
        mViewPager.setOffscreenPageLimit(3);
        mRecyclerTabLayout.setTabOnScreenLimit(3);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList, tabList);
        mViewPager.setAdapter(mPagerAdapter);
        mRecyclerTabLayout.setUpWithViewPager(tabList, mViewPager);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
    }

    @Override
    public void showProgress() {
        mMultiStateLayout.showProgress();
    }

    @Override
    public void setNetError() {
        mMultiStateLayout.showRetryError(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getEBookGradeList();
            }
        });
    }

    @Override
    public void setContentView() {
        mMultiStateLayout.showContentView();
    }

    @Override
    public void setEBookClassificationList(List<ClassifyTwoLevelBean> bookClassifications) {
        if (null != mClassifySelectLayout && null != bookClassifications) {
            mClassifySelectLayout.setEnabled(true);
            mClassifySelectLayout.setData(bookClassifications);
        }
    }

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
