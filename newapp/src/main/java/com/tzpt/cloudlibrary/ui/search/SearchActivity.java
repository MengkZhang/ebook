package com.tzpt.cloudlibrary.ui.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.base.BaseFragment;
import com.tzpt.cloudlibrary.bean.SearchTypeBean;
import com.tzpt.cloudlibrary.ui.bookstore.BookStoreDetailActivity;
import com.tzpt.cloudlibrary.ui.bookstore.BookStoreFilterType;
import com.tzpt.cloudlibrary.ui.bookstore.BookStoreFragment;
import com.tzpt.cloudlibrary.ui.bookstore.BookStorePresenter;
import com.tzpt.cloudlibrary.ui.ebook.EBookDetailActivity;
import com.tzpt.cloudlibrary.ui.ebook.EBookFilterType;
import com.tzpt.cloudlibrary.ui.ebook.EBookFragment;
import com.tzpt.cloudlibrary.ui.ebook.EBookPresenter;
import com.tzpt.cloudlibrary.ui.information.InformationAdvancedSearchActivity;
import com.tzpt.cloudlibrary.ui.information.InformationDetailDiscussActivity;
import com.tzpt.cloudlibrary.ui.information.InformationFragment;
import com.tzpt.cloudlibrary.ui.information.InformationPresenter;
import com.tzpt.cloudlibrary.ui.library.LibraryDetailActivity;
import com.tzpt.cloudlibrary.ui.library.LibraryFilterType;
import com.tzpt.cloudlibrary.ui.library.LibraryFragment;
import com.tzpt.cloudlibrary.ui.library.LibraryListAdvancedActivity;
import com.tzpt.cloudlibrary.ui.library.LibraryPresenter;
import com.tzpt.cloudlibrary.ui.paperbook.BookDetailActivity;
import com.tzpt.cloudlibrary.ui.paperbook.BookListAdvancedSearchActivity;
import com.tzpt.cloudlibrary.ui.paperbook.PaperBookFilterType;
import com.tzpt.cloudlibrary.ui.paperbook.PaperBookPresenter;
import com.tzpt.cloudlibrary.ui.paperbook.PaperBooksFragment;
import com.tzpt.cloudlibrary.ui.readers.ActionDetailsActivity;
import com.tzpt.cloudlibrary.ui.readers.ActivityListFragment;
import com.tzpt.cloudlibrary.ui.readers.ActivityListPresenter;
import com.tzpt.cloudlibrary.ui.video.VideoDetailActivity;
import com.tzpt.cloudlibrary.ui.video.VideoFilterType;
import com.tzpt.cloudlibrary.ui.video.VideoListFragment;
import com.tzpt.cloudlibrary.ui.video.VideoListPresenter;
import com.tzpt.cloudlibrary.utils.ActivityUtils;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.CustomPopupWindow;
import com.tzpt.cloudlibrary.widget.LinearLayoutForListView;
import com.tzpt.cloudlibrary.widget.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 搜索界面
 */
public class SearchActivity extends BaseActivity implements SearchContract.View {
    private static final String FROM_TYPE = "from_type";
    private static final String LIB_HALL_CODE = "lib_hall_code";
    private static final String IS_BOOK_STORE = "is_book_store";

    //app的全局搜索
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    //单一搜索 0图书 1电子书 2图书馆 3视频 4咨询 5活动 6书店 7附近搜索
    public static void startActivityFromType(Context context, int type) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(FROM_TYPE, type);
        context.startActivity(intent);
    }

    //图书馆的全局搜索
    public static void startActivityFromLib(Context context, String hallCode, int type) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(LIB_HALL_CODE, hallCode);
        intent.putExtra(FROM_TYPE, type);
        context.startActivity(intent);
    }

    //书店的搜索
    public static void startActivityFromBookStore(Context context, String hallCode) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(LIB_HALL_CODE, hallCode);
        intent.putExtra(FROM_TYPE, 0);
        intent.putExtra(IS_BOOK_STORE, true);
        context.startActivity(intent);
    }

    //search UI
    @BindView(R.id.search_type_tv)
    TextView mSearchTypeTv;
    @BindView(R.id.search_type_more_iv)
    ImageView mSearchTypeMoreIv;
    @BindView(R.id.search_btn_tv)
    TextView mSearchBtnTv;
    @BindView(R.id.search_list_frame)
    FrameLayout mSearchListFrameRl;
    @BindView(R.id.search_bar_layout)
    LinearLayout mSearchBarLL;
    @BindView(R.id.search_bar_center_del_iv)
    ImageButton mSearchBarCenterDelIv;
    @BindView(R.id.search_bar_center_content_et)
    EditText mSearchBarCenterContentEt;
    //history UI
    @BindView(R.id.flow_layout)
    FlowLayout mFlowLayout;
    //high search
    @BindView(R.id.advanced_search_ll)
    LinearLayout mAdvancedSearchLL;
    //hot search
    @BindView(R.id.search_hot_list)
    LinearLayoutForListView mSearchHotList;
    @BindView(R.id.hot_search_title_tv)
    TextView mHotSearchTitleTv;
    @BindView(R.id.search_title_bar_divider)
    View mSearchTitleBarDivider;

    //fragment
    private PaperBooksFragment mPaperBooksFragment;
    private EBookFragment mEBookFragment;
    private LibraryFragment mLibraryFragment;
    private BookStoreFragment mBookStoreFragment;
    private InformationFragment mInformationFragment;
    private ActivityListFragment mActivityListFragment;
    private BaseFragment mCurrentFragment;
    private VideoListFragment mVideoListFragment;
    private LibraryFragment mLibraryAndBookStoreFragment;
    //presenter
    private SearchPresenter mPresenter;
    //adapter
    private SearchHotAdapter mSearchHotAdapter;
    private SearchClassifyAdapter mClassifyAdapter;

    private CustomPopupWindow mTypeListPopupWindow;
    private List<SearchTypeBean> mClassifyData = new ArrayList<>();
    private boolean mIsSearchResult;
    private int mSearchType;
    private String mLibHallCode;
    private boolean mIsFromNearLib = false;//是否来做附近

    @OnClick({R.id.search_type_tv, R.id.search_bar_left_btn,
            R.id.search_bar_del_btn, R.id.search_bar_center_del_iv, R.id.advanced_search_ll,
            R.id.search_bar_center_content_et, R.id.search_btn_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_bar_left_btn:
                closeKeyBoard();
                finish();
                break;
            case R.id.search_bar_center_content_et:
                //点击恢复焦点
                mSearchBarCenterContentEt.setCursorVisible(true);
                int contentLength = mSearchBarCenterContentEt.getText().toString().trim().length();
                if (mIsSearchResult) {      //搜索结果的操作
                    mIsSearchResult = false;
                    if (contentLength > 0) {
                        setSearchBtnUI();
                    } else {
                        setSearchTypeUI();
                    }
                    //隐藏当前fragment
                    ActivityUtils.hideFragmentInActivity(getSupportFragmentManager(), mCurrentFragment);
                    mSearchListFrameRl.setVisibility(View.GONE);
                } else {                //默认界面操作
                    mSearchBarCenterContentEt.requestFocus();
                    mSearchBarCenterContentEt.findFocus();
                    mSearchBarCenterContentEt.setSelection(contentLength);
                    if (contentLength > 0) {
                        setSearchBtnUI();
                    } else {
                        setSearchTypeUI();
                    }
                }
                break;
            case R.id.search_type_tv:
                if (mTypeListPopupWindow == null || !mTypeListPopupWindow.isShowing()) {
                    mHandler.sendEmptyMessageDelayed(100, 100);
                }
                break;
            case R.id.search_bar_del_btn:
                if (mFlowLayout != null && mFlowLayout.getChildCount() > 0) {
                    showDelDialog(0, "确认删除全部历史记录？", -1);
                }
                break;
            case R.id.search_bar_center_del_iv:
                mSearchBarCenterContentEt.setText("");
                break;
            case R.id.advanced_search_ll:
                switch (mSearchType) {
                    case 0:
                        BookListAdvancedSearchActivity.startActivity(this, 1, mLibHallCode);
                        break;
                    case 1:
                        BookListAdvancedSearchActivity.startActivity(this, 2, mLibHallCode);
                        break;
                    case 2:
                        LibraryListAdvancedActivity.startActivity(this, 0);
                        break;
                    case 4:
                        InformationAdvancedSearchActivity.startActivity(this, mLibHallCode);
                        break;
                    case 6://书店高级搜索
                        LibraryListAdvancedActivity.startActivity(this, 1);
                        break;
                    case 7://附近高级搜素
                        LibraryListAdvancedActivity.startActivity(this, 2);
                        break;
                }
                break;
            case R.id.search_btn_tv:
                closeKeyBoard();
                searchContent();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (null != mPresenter) {
            mPresenter.getHistoryTag(mSearchType);
        }
    }

    @Override
    public void initDatas() {
        mLibHallCode = getIntent().getStringExtra(LIB_HALL_CODE);
        mSearchType = getIntent().getIntExtra(FROM_TYPE, 0);
        //是否来自附近模块
        mIsFromNearLib = mSearchType == 7;

        mPresenter = new SearchPresenter();
        mPresenter.attachView(this);

        if (TextUtils.isEmpty(mLibHallCode)) {
            mPresenter.getSearchTypeList(false, mSearchType);
            mPresenter.getHotSearchList(mSearchType);
        } else {
            mPresenter.getSearchTypeList(true, mSearchType);
            mPresenter.getLibHotSearchList(mLibHallCode, mSearchType);
        }
        mPresenter.getHistoryTag(mSearchType);

    }

    @Override
    public void configViews() {
        //附近，不显示切换按钮
        if (!mIsFromNearLib) {
            mSearchTypeTv.setVisibility(View.VISIBLE);
            mSearchTypeMoreIv.setVisibility(View.VISIBLE);
        } else {
            mSearchTypeTv.setVisibility(View.GONE);
            mSearchTypeMoreIv.setVisibility(View.GONE);
        }

        mSearchBarCenterContentEt.setOnEditorActionListener(mOnEditorActionListener);
        mSearchBarCenterContentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //如果当前不是搜索结果状态
                if (!mIsSearchResult) {
                    mSearchBarCenterDelIv.setVisibility((s.length() == 0) ? View.GONE : View.VISIBLE);
                    if (s.length() > 0) {
                        setSearchBtnUI();
                    } else {
                        setSearchTypeUI();
                    }
                }
            }
        });
        mFlowLayout.setOnFlowClickListener(mFlowClickListener);
    }

    private void setSearchTypeUI() {
        if (!mIsFromNearLib) {
            mSearchTypeTv.setVisibility(View.VISIBLE);
            mSearchTypeMoreIv.setVisibility(View.VISIBLE);
        }
        mSearchBtnTv.setVisibility(View.GONE);
    }

    private void setSearchBtnUI() {
        mSearchTypeTv.setVisibility(View.GONE);
        mSearchTypeMoreIv.setVisibility(View.GONE);
        mSearchBtnTv.setVisibility(View.VISIBLE);
        mSearchBarCenterDelIv.setVisibility(mSearchBarCenterContentEt.getText().toString().trim().length() > 0 ? View.VISIBLE : View.GONE);
    }

    private FlowLayout.FlowClickListener mFlowClickListener = new FlowLayout.FlowClickListener() {
        @Override
        public void onFlowClickListener(int position) {
            closeKeyBoard();
            mPresenter.clickToSearch(mSearchType, position);
        }

        @Override
        public void onFlowLongClickListener(int position) {
            showDelDialog(1, "确认删除本条历史记录？", position);
        }
    };
    private TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                closeKeyBoard();
                searchContent();
                return true;
            }
            return false;
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                if (mTypeListPopupWindow == null) {
                    createPopupWindow();
                }

                if (Build.VERSION.SDK_INT >= 24) {
                    mTypeListPopupWindow.showAtLocation(mSearchBarLL, Gravity.TOP, 0, 0);
                } else {
                    mTypeListPopupWindow.showAsDropDown(mSearchBarLL, 0, -(int) DpPxUtils.dipToPx(SearchActivity.this, 48f));
                }

            }
        }
    };

    //开始搜索
    private void searchContent() {
        String searchContent = mSearchBarCenterContentEt.getText().toString().trim();
        if (!TextUtils.isEmpty(searchContent)) {
            if (searchContent.length() > 35) {//如果大于35字，则只取35字
                searchContent = searchContent.substring(0, 35);
            }
            mSearchBarCenterContentEt.setSelection(searchContent.length());
            mSearchListFrameRl.setVisibility(View.VISIBLE);
            mSearchBarCenterDelIv.setVisibility(View.GONE);
            mSearchBarCenterContentEt.setCursorVisible(false);

            setSearchTypeUI();
            mIsSearchResult = true; //当前设置为搜索状态
            boolean hasInstance = true;
            switch (mSearchType) {
                case 0://图书
                    if (mPaperBooksFragment == null) {
                        hasInstance = false;
                        mPaperBooksFragment = PaperBooksFragment.newInstance();
                    }
                    ActivityUtils.showFragmentInActivity(getSupportFragmentManager(), mCurrentFragment, mPaperBooksFragment, R.id.search_list_frame);
                    mCurrentFragment = mPaperBooksFragment;
                    mPaperBooksFragment.mIsVisible = true;
                    setPaperBookParameter(searchContent, hasInstance);

                    mPresenter.searchOption(mSearchType, searchContent);

                    break;
                case 1://电子书
                    if (mEBookFragment == null) {
                        hasInstance = false;
                        mEBookFragment = EBookFragment.newInstance();
                    }
                    ActivityUtils.showFragmentInActivity(getSupportFragmentManager(), mCurrentFragment, mEBookFragment, R.id.search_list_frame);
                    mCurrentFragment = mEBookFragment;
                    mEBookFragment.mIsVisible = true;
                    setEBookParameter(searchContent, hasInstance);

                    mPresenter.searchOption(mSearchType, searchContent);

                    break;
                case 2://图书馆
                    if (mLibraryFragment == null) {
                        hasInstance = false;
                        mLibraryFragment = new LibraryFragment();
                    }
                    ActivityUtils.showFragmentInActivity(getSupportFragmentManager(), mCurrentFragment, mLibraryFragment, R.id.search_list_frame);
                    mCurrentFragment = mLibraryFragment;
                    mLibraryFragment.mIsVisible = true;
                    setLibraryParameter(searchContent, hasInstance, mLibraryFragment, LibraryFilterType.Library_List, 2);

                    mPresenter.searchOption(mSearchType, searchContent);

                    break;
                case 3://视频
                    if (mVideoListFragment == null) {
                        hasInstance = false;
                        mVideoListFragment = new VideoListFragment();
                    }
                    ActivityUtils.showFragmentInActivity(getSupportFragmentManager(), mCurrentFragment, mVideoListFragment, R.id.search_list_frame);
                    mCurrentFragment = mVideoListFragment;
                    mVideoListFragment.mIsVisible = true;
                    setVideoParameter(searchContent, hasInstance);

                    mPresenter.searchOption(mSearchType, searchContent);

                    break;
                case 4://资讯
                    if (mInformationFragment == null) {
                        hasInstance = false;
                        mInformationFragment = new InformationFragment();
                    }
                    ActivityUtils.showFragmentInActivity(getSupportFragmentManager(), mCurrentFragment, mInformationFragment, R.id.search_list_frame);
                    mCurrentFragment = mInformationFragment;
                    mInformationFragment.mIsVisible = true;
                    setInformationParameter(searchContent, hasInstance);

                    mPresenter.searchOption(mSearchType, searchContent);

                    break;
                case 5://活动
                    if (mActivityListFragment == null) {
                        hasInstance = false;
                        mActivityListFragment = new ActivityListFragment();
                    }
                    ActivityUtils.showFragmentInActivity(getSupportFragmentManager(), mCurrentFragment, mActivityListFragment, R.id.search_list_frame);
                    mCurrentFragment = mActivityListFragment;
                    mActivityListFragment.mIsVisible = true;
                    setActivityParameter(searchContent, hasInstance);

                    mPresenter.searchOption(mSearchType, searchContent);

                    break;
                case 6://书店
                    if (mBookStoreFragment == null) {
                        hasInstance = false;
                        mBookStoreFragment = new BookStoreFragment();
                    }
                    ActivityUtils.showFragmentInActivity(getSupportFragmentManager(), mCurrentFragment, mBookStoreFragment, R.id.search_list_frame);
                    mCurrentFragment = mBookStoreFragment;
                    mBookStoreFragment.mIsVisible = true;
                    setBookStoreParameter(searchContent, hasInstance);

                    mPresenter.searchOption(mSearchType, searchContent);

                    break;
                case 7://图书馆和书店混合列表
                    if (mLibraryAndBookStoreFragment == null) {
                        hasInstance = false;
                        mLibraryAndBookStoreFragment = new LibraryFragment();
                    }
                    ActivityUtils.showFragmentInActivity(getSupportFragmentManager(), mCurrentFragment, mLibraryAndBookStoreFragment, R.id.search_list_frame);
                    mCurrentFragment = mLibraryAndBookStoreFragment;
                    mLibraryAndBookStoreFragment.mIsVisible = true;
                    setLibraryParameter(searchContent, hasInstance, mLibraryAndBookStoreFragment, LibraryFilterType.Near_Library_Book_Store_List, 7);

                    mPresenter.searchOption(mSearchType, searchContent);

                    break;
            }
        }
    }

    private void setBookStoreParameter(String searchContent, boolean hasInstance) {
        BookStorePresenter bookStorePresenter = new BookStorePresenter(mBookStoreFragment);
        Map<String, Object> bookStoreParameter = new ArrayMap<>();
        bookStoreParameter.put("keyword", searchContent);
        bookStorePresenter.setBookStoreFilterType(BookStoreFilterType.BOOK_STORE_SEARCH_LIST);
        bookStorePresenter.setParameter(bookStoreParameter);
        if (hasInstance && !searchContent.equals(mPresenter.getSearchValueBySearchType(6))) {
            bookStorePresenter.mustShowProgressLoading();
            bookStorePresenter.getBookStoreList(1);
        }
    }

    private void setActivityParameter(String searchContent, boolean hasInstance) {
        ActivityListPresenter activityListPresenter = new ActivityListPresenter(mActivityListFragment);
        activityListPresenter.setParameters(null == mLibHallCode ? 0 : 2, true, searchContent, mLibHallCode);
        if (hasInstance && !searchContent.equals(mPresenter.getSearchValueBySearchType(5))) {
            activityListPresenter.mustShowProgressLoading();
            activityListPresenter.getActivityList(1);
        }
    }

    private void setInformationParameter(String searchContent, boolean hasInstance) {
        InformationPresenter informationPresenter = new InformationPresenter(mInformationFragment);
        informationPresenter.setSearchParameter(searchContent, mLibHallCode, null, null, null, null, 1);
        if (hasInstance && !searchContent.equals(mPresenter.getSearchValueBySearchType(4))) {
            informationPresenter.mustShowProgressLoading();
            informationPresenter.getInformationList(1);
        }
    }

    private void setVideoParameter(String searchContent, boolean hasInstance) {
        VideoListPresenter presenter = new VideoListPresenter(mVideoListFragment);
        presenter.setFilterType(VideoFilterType.SEARCH_VIDEO_LIST);
        presenter.setKeyWord(searchContent);
        if (!TextUtils.isEmpty(mLibHallCode)) {
            presenter.setLibCode(mLibHallCode);
        }
        if (hasInstance && !searchContent.equals(mPresenter.getSearchValueBySearchType(3))) {
            presenter.mustShowProgressLoading();
            presenter.getVideoList(1);
        }
    }

    private void setLibraryParameter(String searchContent, boolean hasInstance, LibraryFragment mLibraryFragment, int library_list, int i) {
        LibraryPresenter libraryPresenter = new LibraryPresenter(mLibraryFragment);
        Map<String, Object> libraryParameter = new ArrayMap<>();
        libraryParameter.put("keyword", searchContent);
        libraryPresenter.setLibListType(true);
        libraryPresenter.setLibFilterType(library_list);
        libraryPresenter.setParameter(libraryParameter);
        if (hasInstance && !searchContent.equals(mPresenter.getSearchValueBySearchType(i))) {
            libraryPresenter.mustShowProgressLoading();
            libraryPresenter.getLibraryList(1);
        }
    }

    private void setEBookParameter(String searchContent, boolean hasInstance) {
        EBookPresenter eBookPresenter = new EBookPresenter(mEBookFragment);
        Map<String, String> eBookParameter = new ArrayMap<>();
        eBookParameter.put("keyword", searchContent);
        if (!TextUtils.isEmpty(mLibHallCode)) {
            eBookParameter.put("libCode", mLibHallCode);
            eBookPresenter.setFilterType(EBookFilterType.Library_Search_EBook_List);
        } else {
            eBookPresenter.setFilterType(EBookFilterType.Search_EBook_List);
        }
        eBookPresenter.setParameter(eBookParameter);
        if (hasInstance && !searchContent.equals(mPresenter.getSearchValueBySearchType(1))) {
            eBookPresenter.mustShowProgressLoading();
            eBookPresenter.getEBookList(1);
        }
    }

    private void setPaperBookParameter(String searchContent, boolean hasInstance) {
        //0图书 1推荐 3点赞 4借阅 5图书高级搜索
        PaperBookPresenter paperBookPresenter = new PaperBookPresenter(mPaperBooksFragment);
        Map<String, String> bookParameter = new ArrayMap<>();
        bookParameter.put("keyword", searchContent);
        int paperBookFilterType;
        if (!TextUtils.isEmpty(mLibHallCode)) {
            bookParameter.put("libCode", mLibHallCode);
            paperBookFilterType = PaperBookFilterType.Library_Search_Book_List;
        } else {
            paperBookFilterType = PaperBookFilterType.Normal_Search_Book_List;
        }
        paperBookPresenter.setParameter(bookParameter);
        paperBookPresenter.setFilterType(paperBookFilterType);
        if (hasInstance && !searchContent.equals(mPresenter.getSearchValueBySearchType(0))) {
            paperBookPresenter.mustShowProgressLoading();
            paperBookPresenter.getPaperBook(1);
        }
    }

    private void closeKeyBoard() {
        KeyboardUtils.hideSoftInput(mSearchBarCenterContentEt);
    }


    //设置历史搜索记录
    @Override
    public void setHistoryTag(String[] result) {
        //移除所有的view
        mFlowLayout.removeAllViews();
        if (null != result) {
            for (String item : result) {
                View view = LayoutInflater.from(this).inflate(R.layout.view_search_history_item, null);
                TextView tv = (TextView) view.findViewById(R.id.search_history_item_tv);
                tv.setText(item);
                mFlowLayout.addView(view);
            }
        }
    }

    @Override
    public void toSearchResult(String searchContent) {
        mSearchBarCenterContentEt.setText(searchContent);
        searchContent();
    }

    @Override
    public void setSearchTypeList(List<SearchTypeBean> searchTypeList, SearchTypeBean item) {
        mClassifyData.clear();
        mClassifyData.addAll(searchTypeList);
        if (null != item) {
            mSearchType = item.mSearchType;
            mSearchTypeTv.setText(item.mTypeName);
        }
        if (TextUtils.isEmpty(mLibHallCode)) {
            mSearchBarCenterContentEt.setHint("搜索");
        } else {
            boolean isBookStore = getIntent().getBooleanExtra(IS_BOOK_STORE, false);
            mSearchBarCenterContentEt.setHint(isBookStore ? "搜本店" : "搜本馆");
        }
        if (mSearchType == 3 || mSearchType == 5) {//视频和活动无高级搜索
            mAdvancedSearchLL.setVisibility(View.GONE);
        } else {
            mAdvancedSearchLL.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setHotSearchList(List<String> hotSearchList) {
        mHotSearchTitleTv.setVisibility(View.VISIBLE);
        mSearchHotList.setVisibility(View.VISIBLE);
        if (mSearchHotAdapter == null) {
            mSearchHotAdapter = new SearchHotAdapter(this, hotSearchList);
        } else {
            mSearchHotAdapter.clear();
            mSearchHotAdapter.addAll(hotSearchList);
        }
        mSearchHotList.setAdapter(mSearchHotAdapter);
        mSearchHotList.setOnItemClickListener(new LinearLayoutForListView.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                mPresenter.getHotSearchValue(position);
            }
        });
    }

    @Override
    public void dismissHotSearchList() {
        mHotSearchTitleTv.setVisibility(View.GONE);
        if (mSearchHotAdapter != null) {
            mSearchHotAdapter.clear();
        }
        mSearchHotList.removeAllViews();
        mSearchHotList.setVisibility(View.GONE);
    }

    @Override
    public void doHotSearch(String name, String value, boolean isBookStore) {
        switch (mSearchType) {
            case 0:
                if (TextUtils.isEmpty(mLibHallCode)) {
                    BookDetailActivity.startActivityForSearchResult(this, value, null, 0);
                } else {
                    BookDetailActivity.startActivityForSearchResult(this, value, mLibHallCode, 2);
                }
                break;
            case 1:
                EBookDetailActivity.startActivityForSearchResult(this, value, mLibHallCode);
                break;
            case 2:
                LibraryDetailActivity.startActivityForSearchResult(this, value, name);
                break;
            case 3:
                VideoDetailActivity.startActivity(this, Long.valueOf(value), name, mLibHallCode);
                break;
            case 4:
                InformationDetailDiscussActivity.startActivityFromHotSearch(this, Long.valueOf(value));
                break;
            case 5:
                ActionDetailsActivity.startActivityFromHotSearch(this, Integer.valueOf(value));
                break;
            case 6:
                BookStoreDetailActivity.startActivityForSearchResult(this, value, name);
                break;
            case 7://附近，需要区分图书馆和书店
                if (isBookStore) {
                    BookStoreDetailActivity.startActivityForSearchResult(this, value, name);
                } else {
                    LibraryDetailActivity.startActivityForSearchResult(this, value, name);
                }
                break;
        }
    }

    //展示删除对话框
    private void showDelDialog(final int type, String message, final int position) {
        final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, "");
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.setText(message);
        dialog.setOkText("删除");
        dialog.setCancelText("取消");
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                switch (type) {
                    case 0:
                        mPresenter.delAllHistoryTag(mSearchType);
                        break;
                    case 1:
                        mPresenter.delHistoryTag(mSearchType, position);
                        break;
                }
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
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
        super.onDestroy();
        if (null != mFlowLayout) {
            mFlowLayout.removeAllViews();
        }
        if (null != mHandler) {
            mHandler.removeMessages(100);
        }
        if (null != mPresenter) {
            mPresenter.clearSearchValue();
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    private void createPopupWindow() {
        //设置选中状态
        for (SearchTypeBean bean : mClassifyData) {
            bean.mIsSelected = false;
        }
        int selectIndex = 0;
        int classifySize = mClassifyData.size();
        for (int i = 0; i < classifySize; i++) {
            if (mClassifyData.get(i).mSearchType == mSearchType) {
                selectIndex = i;
                break;
            }
        }
        mClassifyData.get(selectIndex).mIsSelected = true;

        mTypeListPopupWindow = new CustomPopupWindow(mContext);
        mClassifyAdapter = new SearchClassifyAdapter(mContext, mClassifyData);
        View view = View.inflate(mContext, R.layout.ppw_search_type_list, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTypeListPopupWindow.dismiss();
            }
        });
        GridView lv = (GridView) view.findViewById(R.id.search_type_rv);
        lv.setAdapter(mClassifyAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SearchTypeBean item = mClassifyData.get(position);

                if (mSearchType == item.mSearchType) {
                    mTypeListPopupWindow.dismiss();
                    return;
                }

                for (SearchTypeBean bean : mClassifyData) {
                    bean.mIsSelected = false;
                }
                mClassifyData.get(position).mIsSelected = true;
                mClassifyAdapter.notifyDataSetChanged();

                mSearchType = item.mSearchType;
                mSearchTypeTv.setText(item.mTypeName);
                if (mSearchType == 3 || mSearchType == 5) {//视频和活动无高级搜索
                    mAdvancedSearchLL.setVisibility(View.GONE);
                } else {
                    mAdvancedSearchLL.setVisibility(View.VISIBLE);
                }
                mPresenter.getHistoryTag(mSearchType);
                dismissHotSearchList();
                if (TextUtils.isEmpty(mLibHallCode)) {
                    mPresenter.getHotSearchList(mSearchType);
                } else {
                    mPresenter.getLibHotSearchList(mLibHallCode, mSearchType);
                }
                mTypeListPopupWindow.dismiss();
                //如果是搜索界面，则选择tab 后开始搜索
                if (mIsSearchResult) {
                    searchContent();
                }
            }
        });
        mTypeListPopupWindow.setContentView(view);
        mTypeListPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mTypeListPopupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mTypeListPopupWindow.setTouchable(true);
        mTypeListPopupWindow.setOutsideTouchable(true);
        mTypeListPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }
}
