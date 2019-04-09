package com.tzpt.cloudlibrary.ui.paperbook;

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
import com.tzpt.cloudlibrary.bean.ClassifyInfo;
import com.tzpt.cloudlibrary.bean.TabMenuBean;
import com.tzpt.cloudlibrary.ui.library.MyPagerAdapter;
import com.tzpt.cloudlibrary.ui.search.SearchActivity;
import com.tzpt.cloudlibrary.widget.ClassifySelectLayout;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;
import com.tzpt.cloudlibrary.widget.tablayout.RecyclerTabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 图书
 */
public class BookTabActivity extends BaseActivity implements
        BookTabContract.View {

    private static final String FROM_TYPE = "from_type";
    private static final String LIB_CODE = "lib_code";
    private static final String LIB_TITLE = "lib_title";

    /**
     * @param context
     * @param fromType 1推荐 3点赞 4借阅
     */
    public static void startActivity(Context context, int fromType) {
        Intent intent = new Intent(context, BookTabActivity.class);
        intent.putExtra(FROM_TYPE, fromType);
        context.startActivity(intent);
    }

    public static void startActivityFromLib(Context context, String libCode, String title) {
        Intent intent = new Intent(context, BookTabActivity.class);
        intent.putExtra(FROM_TYPE, 2);
        intent.putExtra(LIB_CODE, libCode);
        intent.putExtra(LIB_TITLE, title);
        context.startActivity(intent);
    }

    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.recycler_tab_layout)
    RecyclerTabLayout mRecyclerTabLayout;
    @BindView(R.id.classify_tab_layout)
    ClassifySelectLayout mClassifySelectLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private MyPagerAdapter mPagerAdapter;
    private BookTabPresenter mPresenter;
    //图书首页
    private PaperBooksFragment mBookFragment1;
    private PaperBooksFragment mBookFragment2;
    private PaperBooksFragment mBookFragment3;

    private int mCurrentClassifyId;
    private int mTab1ClassifyId;
    private int mTab2ClassifyId;
    private int mTab3ClassifyId;
    private String mLibraryCode;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_btn:
                if (TextUtils.isEmpty(mLibraryCode)) {
                    SearchActivity.startActivityFromType(this, 0);
                } else {
                    SearchActivity.startActivityFromLib(this, mLibraryCode, 0);
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
                case 0://
                    if (mTab1ClassifyId == mCurrentClassifyId) {
                        return;
                    }
                    mTab1ClassifyId = mCurrentClassifyId;
                    mBookFragment1.dealClassifyClick(mTab1ClassifyId);
                    break;
                case 1://
                    if (mTab2ClassifyId == mCurrentClassifyId) {
                        return;
                    }
                    mTab2ClassifyId = mCurrentClassifyId;
                    mBookFragment2.dealClassifyClick(mTab2ClassifyId);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
    private ClassifySelectLayout.OnSelectListener mOnSelectListener = new ClassifySelectLayout.OnSelectListener() {
        @Override
        public void onSelect(int position, ClassifyInfo classify) {
            if (null != classify) {
                mCurrentClassifyId = classify.id;
                int currentItem = mViewPager.getCurrentItem();
                switch (currentItem) {
                    case 0:
                        mTab1ClassifyId = classify.id;
                        mBookFragment1.dealClassifyClick(classify.id);
                        break;
                    case 1:
                        mTab2ClassifyId = classify.id;
                        mBookFragment2.dealClassifyClick(classify.id);
                        break;
                }
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_book_tab;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        int fromType = getIntent().getIntExtra(FROM_TYPE, 0);
        mPresenter = new BookTabPresenter();
        mPresenter.attachView(this);

        switch (fromType) {
            case 0://首页
                initMainBookList();

                mPresenter.getBookClassificationList();
                break;
            case 2://本馆图书
                initLibBookList();

                mPresenter.getBookClassificationList();
                break;
            case 1://1推荐
                mCommonTitleBar.setTitle("荐购排行榜");
                initRankingBookList(fromType);

                mMultiStateLayout.showContentView();
                break;
            case 3://3点赞
                mCommonTitleBar.setTitle("点赞排行榜");
                initRankingBookList(fromType);

                mMultiStateLayout.showContentView();
                break;
            case 4://4借阅
                mCommonTitleBar.setTitle("借阅排行榜");
                initRankingBookList(fromType);

                mMultiStateLayout.showContentView();
                break;
        }
    }

    private void initMainBookList(){
        mCommonTitleBar.setTitle("图书");
        mCommonTitleBar.setRightBtnClickAble(true);
        mCommonTitleBar.setRightBtnIcon(R.drawable.bg_btn_search);

        mClassifySelectLayout.setLimitWordsCount(4);
        mClassifySelectLayout.setGradeName("全部分类");
        mClassifySelectLayout.setRightTextGravityEND();
        mClassifySelectLayout.setOnSelectListener(mOnSelectListener);

        final List<TabMenuBean> tabList = new ArrayList<>();
        TabMenuBean menu1 = new TabMenuBean("一周热门");
        TabMenuBean menu2 = new TabMenuBean("最新上架");
        tabList.add(menu1);
        tabList.add(menu2);

        mBookFragment1 = PaperBooksFragment.newInstance();
        PaperBookPresenter newBookPresenter = new PaperBookPresenter(mBookFragment1);
        newBookPresenter.setFilterType(PaperBookFilterType.Hot_Book_List);

        mBookFragment2 = PaperBooksFragment.newInstance();
        PaperBookPresenter hotBookPresenter = new PaperBookPresenter(mBookFragment2);
        hotBookPresenter.setFilterType(PaperBookFilterType.New_Book_List);
        Map<String, String> hotParameter = new ArrayMap<>();
        if (!TextUtils.isEmpty(mLibraryCode)) {
            hotParameter.put("libCode", mLibraryCode);
        }
        hotBookPresenter.setParameter(hotParameter);

        Map<String, String> parameter = new ArrayMap<>();
        if (!TextUtils.isEmpty(mLibraryCode)) {
            parameter.put("libCode", mLibraryCode);
        }
        newBookPresenter.setParameter(parameter);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(mBookFragment1);
        fragmentList.add(mBookFragment2);
        mViewPager.setOffscreenPageLimit(2);
        mRecyclerTabLayout.setTabOnScreenLimit(2);

        mClassifySelectLayout.setWeightSum(fragmentList.size());

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList, tabList);
        mViewPager.setAdapter(mPagerAdapter);
        mRecyclerTabLayout.setUpWithViewPager(tabList, mViewPager);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
    }

    private void initLibBookList(){
        mLibraryCode = getIntent().getStringExtra(LIB_CODE);
        String title = getIntent().getStringExtra(LIB_TITLE);
        mCommonTitleBar.setTitle(title);
        mCommonTitleBar.setRightBtnClickAble(true);
        mCommonTitleBar.setRightBtnIcon(R.drawable.bg_btn_search);

        mClassifySelectLayout.setLimitWordsCount(Integer.MAX_VALUE);
        mClassifySelectLayout.setGradeName("全部分类");
        mClassifySelectLayout.setOnSelectListener(mOnSelectListener);

        mRecyclerTabLayout.setVisibility(View.GONE);

        final List<TabMenuBean> tabList = new ArrayList<>();
        TabMenuBean menu3 = new TabMenuBean("图书");
        mBookFragment1 = PaperBooksFragment.newInstance();
        PaperBookPresenter libBookPresenter = new PaperBookPresenter(mBookFragment1);
        libBookPresenter.setFilterType(PaperBookFilterType.Library_Book_list);
        tabList.add(menu3);

        Map<String, String> libParameter = new ArrayMap<>();
        if (!TextUtils.isEmpty(mLibraryCode)) {
            libParameter.put("libCode", mLibraryCode);
        }
        libBookPresenter.setParameter(libParameter);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(mBookFragment1);
        mViewPager.setOffscreenPageLimit(1);
        mRecyclerTabLayout.setTabOnScreenLimit(1);

        mClassifySelectLayout.setWeightSum(fragmentList.size());

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList, tabList);
        mViewPager.setAdapter(mPagerAdapter);
        mRecyclerTabLayout.setUpWithViewPager(tabList, mViewPager);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
    }

    private void initRankingBookList(int fromType) {
        mCommonTitleBar.setRightBtnClickAble(false);
        mClassifySelectLayout.setVisibility(View.GONE);

        final List<TabMenuBean> tabList = new ArrayList<>();

        TabMenuBean menu4 = new TabMenuBean("月榜");
        TabMenuBean menu5 = new TabMenuBean("季榜");
        TabMenuBean menu6 = new TabMenuBean("年榜");
        tabList.add(menu4);
        tabList.add(menu5);
        tabList.add(menu6);

        mBookFragment1 = PaperBooksFragment.newInstance();
        mBookFragment2 = PaperBooksFragment.newInstance();
        mBookFragment3 = PaperBooksFragment.newInstance();

        PaperBookPresenter monthPresenter = new PaperBookPresenter(mBookFragment1);
        int filterType;
        if (fromType == 1) {
            filterType = PaperBookFilterType.Ranking_Recommend_Month;
        } else if (fromType == 3) {
            filterType = PaperBookFilterType.Ranking_Like_Month;
        } else {
            filterType = PaperBookFilterType.Ranking_Read_Month;
        }
        monthPresenter.setFilterType(filterType);

        PaperBookPresenter quartPresenter = new PaperBookPresenter(mBookFragment2);
        if (fromType == 1) {
            filterType = PaperBookFilterType.Ranking_Recommend_Quarter;
        } else if (fromType == 3) {
            filterType = PaperBookFilterType.Ranking_Like_Quarter;
        } else {
            filterType = PaperBookFilterType.Ranking_Read_Quarter;
        }
        quartPresenter.setFilterType(filterType);

        PaperBookPresenter yearPresenter = new PaperBookPresenter(mBookFragment3);
        if (fromType == 1) {
            filterType = PaperBookFilterType.Ranking_Recommend_Year;
        } else if (fromType == 3) {
            filterType = PaperBookFilterType.Ranking_Like_Year;
        } else {
            filterType = PaperBookFilterType.Ranking_Read_Year;
        }
        yearPresenter.setFilterType(filterType);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(mBookFragment1);
        fragmentList.add(mBookFragment2);
        fragmentList.add(mBookFragment3);
        mViewPager.setOffscreenPageLimit(3);
        mRecyclerTabLayout.setTabOnScreenLimit(3);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList, tabList);
        mViewPager.setAdapter(mPagerAdapter);
        mRecyclerTabLayout.setUpWithViewPager(tabList, mViewPager);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
    }

    @Override
    public void configViews() {

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
                mPresenter.getBookClassificationList();
            }
        });
    }

    @Override
    public void setContentView() {
        mMultiStateLayout.showContentView();
    }

    @Override
    public void setBookClassificationList(List<ClassifyInfo> bookClassifications) {
        if (null != mClassifySelectLayout) {
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
