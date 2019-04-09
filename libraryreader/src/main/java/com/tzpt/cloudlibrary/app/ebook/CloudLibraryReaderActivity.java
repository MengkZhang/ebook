package com.tzpt.cloudlibrary.app.ebook;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.tzpt.cloudlibrary.app.ebook.adapter.CloudLibraryTOCAdapter;
import com.tzpt.cloudlibrary.app.ebook.adapter.ReadingChooseColorAdapter;
import com.tzpt.cloudlibrary.app.ebook.books.bookmenu.EbookMarksFragment;
import com.tzpt.cloudlibrary.app.ebook.books.bookmenu.EbookMenuFragment;
import com.tzpt.cloudlibrary.app.ebook.books.bookmenu.MyPagerAdapter;
import com.tzpt.cloudlibrary.app.ebook.books.controller.EpubReaderController;
import com.tzpt.cloudlibrary.app.ebook.books.listener.EpubUIListener;
import com.tzpt.cloudlibrary.app.ebook.books.model.Book;
import com.tzpt.cloudlibrary.app.ebook.books.model.BookLastMark;
import com.tzpt.cloudlibrary.app.ebook.books.model.ChoosedColor;
import com.tzpt.cloudlibrary.app.ebook.books.model.EpubBookMarks;
import com.tzpt.cloudlibrary.app.ebook.books.model.LocalBook;
import com.tzpt.cloudlibrary.app.ebook.books.model.ReaderSettings;
import com.tzpt.cloudlibrary.app.ebook.books.parser.EpubParser;
import com.tzpt.cloudlibrary.app.ebook.books.view.ReaderListener;
import com.tzpt.cloudlibrary.app.ebook.books.view.WebReader;
import com.tzpt.cloudlibrary.app.ebook.books.widget.CBProgressBar;
import com.tzpt.cloudlibrary.app.ebook.constant.DataService;
import com.tzpt.cloudlibrary.app.ebook.constant.EbookConstant;
import com.tzpt.cloudlibrary.app.ebook.helper.ScreenHelper;
import com.tzpt.cloudlibrary.app.ebook.utils.FileUtil;
import com.tzpt.cloudlibrary.app.ebook.utils.Md5Encrypt;
import com.tzpt.cloudlibrary.app.ebook.utils.SharePreferencesUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * epub 阅读类
 */
public class CloudLibraryReaderActivity extends AppCompatActivity implements
        ReaderListener,
        EpubUIListener,
        View.OnClickListener,
        EbookMenuFragment.EbookMenuFragmentToChapterListener,
        EbookMarksFragment.EbookMarksFragmentToPageIndexListener {

    //toobar info
    private LinearLayout middleToolbar;
    private RelativeLayout bookReaderToolbar;
    private RelativeLayout topToolbar;
    private LinearLayout bottomToolbar;
    private SeekBar bottomToolbarReadProgressSeekBar;
    private ProgressBar mPaginatingProgress;
    private ImageButton bottomToolbarTeadProgressDecrease;
    private ImageButton bottomToolbarTeadProgressPlus;
    private ImageButton toolbarTopBack;
    private ImageButton bookBtnMenu;
    private ImageButton bookBtnMark;
    private ImageButton bookBtnSize;
    private ImageButton imageBtnLight;
    private TextView toolbarTopTitle;
    //drawerLayout
    private DrawerLayout drawerLayout;
    private WebReader mWebReader;
    //book base info
    private TextView mPageIndexView;
    private TextView mChapterNameView;
    private TextView bookName;
    private TextView authorName;
    private CBProgressBar mCBProgressBar;
    private ProgressBar mProgressBar;
    //set font size , webreader background, lightnes
    private View fontSizeView;
    private PopupWindow fontSizePopWindow;
    private TextView textViewFont1;
    private TextView textViewFont2;
    private TextView mTextPageNumber;
    private SeekBar mLightSeakBar;
    private RecyclerView mRecyclerView;
    private RelativeLayout mWebReaderParent;
    private ReaderSettings mReaderSettings;
    private EpubReaderController controller;
    private int mBookCurPageIndex = 1;//当前页数
    private boolean fontWindowDismiss = false;//弹出框是否刚刚消失
    private boolean isPageingChapter = false; //正在分页
    /**
     * current ui handler message
     */
    private static final int HANDLER_BOOK_READY = 1;
    private static final int HANDLER_BOOK_ERROR = 2;
    private static final int HANDLER_SHOW_TOOLBAR = 3;
    private static final int HANDLER_HIDE_TOOLBAR = 4;
    private static final int HANDLER_CHAPTER_LOADING = 5;
    private static final int HANDLER_THEME_CHANGED = 6;
    private static final int HANDLER_PAGE_NAVIGATION_FINISHED = 7;
    private static final int HANDLER_PAGINATION_BEGIN = 8;
    private static final int HANDLER_PAGINATION_PROGRESS_UPDATE = 9;
    private static final int HANDLER_PAGINATION_FINISH = 10;
    private static final int HANDLER_CURRENT_PAGE_CHANGED = 13;
    //Android 系统设置
    private Configuration mCurConfig = new Configuration();

    private RadioGroup mRadioGroup;
    private ViewPager mViewPager;
    private List<Fragment> fragmentList;
    private final String BOOK_MENU_TITLES[] = {"目录", "书签"};
    private EbookMenuFragment menuFragment;
    private EbookMarksFragment marksFragment;
    private Typeface typeFace;

    //*=============================================================================================
    //* 初始化UI操作
    //*
    //*=============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置全屏
        ScreenHelper.setActivityFullScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.controller = new EpubReaderController(this);
        //初始化fresco
        Fresco.initialize(this);
        initReaderOkhttp();
        hideActionBar();
        initReaderUI();
        initReaderParams();
        initReaderListener();
    }

    /**
     * 初始化okHttp
     */
    private void initReaderOkhttp() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    /**
     * 隐藏标题栏
     */
    private void hideActionBar() {
        ActionBar getActionBar = getSupportActionBar();
        if (null != getActionBar) {
            getActionBar.hide();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //保持配置
        saveConfig();
    }

    /**
     * 销毁界面操作
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != controller) {
            controller = null;
        }
        //关闭阅读器
        if (null != mWebReader) {
            mWebReader.closeBook();
        }
        try {
            //保存书签
            saveBookMark();
        } catch (Exception e) {
        }
        System.exit(0);
    }

    /**
     * 保存书签
     */
    private void saveBookMark() throws Exception {
        if (null == mWebReader) {
            return;
        }
        Book book = mWebReader.mBook;
        if (null == book) {
            return;
        }
        if (!TextUtils.isEmpty(book.path) && mWebReader.isChapterReady(mWebReader.getCurrentChapterIndex())) {
            //加入本地书架
            LocalBook localBook = new LocalBook(CloudLibraryReaderActivity.this);
            localBook.id = Md5Encrypt.md5(book.path);
            localBook.load();
            localBook.author = getEpubBookName();
            localBook.title = book.name;
            localBook.medium_image = getEpubBookImagePath();//获取电子书图片
            localBook.current_page = mWebReader.getCurrentPageIndex();
            localBook.total_page = book.pageCount;
            localBook.last_access_date = System.currentTimeMillis();
            localBook.file = getEpubBookDownLoadPath(); //下载地址
            localBook.save();
            // 保存书签
            BookLastMark bookmark = new BookLastMark(this);
            bookmark.bookid = Md5Encrypt.md5(book.path);
            bookmark.author = book.author;
            bookmark.file = "";
            bookmark.title = mWebReader.getCurrentChapterTitle();
            bookmark.big_image = "";
            bookmark.medium_image = "";
            bookmark.small_image = "";
            bookmark.detail_image = "";
            bookmark.list_image = "";
            bookmark.chapter = mWebReader.getCurrentChapterIndex();
            bookmark.chapter_title = mWebReader.getCurrentChapterTitle();
            bookmark.current_offset = 0;
            bookmark.total_offset = 0;
            bookmark.current_page = mWebReader.getCurrentPageIndex();
            bookmark.total_page = book.pageCount;
            bookmark.modified_date = 0;
            bookmark.save();
        }
    }

    /**
     * 如果屏幕切换
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mCurConfig.orientation != newConfig.orientation) {
            mCurConfig.orientation = newConfig.orientation;
            mWebReader.viewSizeChanged();
        }
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 初始化UI
     */
    private void initReaderUI() {
        this.mWebReaderParent = (RelativeLayout) findViewById(R.id.mWebReaderParent);
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        this.mWebReader = (WebReader) findViewById(R.id.mWebReader);
        //book base info
        this.mPageIndexView = (TextView) findViewById(R.id.pageIndex);
        this.mChapterNameView = (TextView) findViewById(R.id.chapterName);
        this.mCBProgressBar = (CBProgressBar) findViewById(R.id.mCBProgressBar);
        this.mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);
        this.bookName = (TextView) findViewById(R.id.bookName);
        this.authorName = (TextView) findViewById(R.id.authorName);
        //toobar
        topToolbar = (RelativeLayout) findViewById(R.id.top_toolbar);
        bottomToolbar = (LinearLayout) findViewById(R.id.bottom_toolbar);
        toolbarTopBack = (ImageButton) findViewById(R.id.toolbar_top_back);
        toolbarTopTitle = (TextView) findViewById(R.id.toolbar_top_title);
        bookReaderToolbar = (RelativeLayout) findViewById(R.id.bookreader_toolbar);
        middleToolbar = (LinearLayout) findViewById(R.id.middle_toolbar);
        bottomToolbarTeadProgressDecrease = (ImageButton) findViewById(R.id.bottom_toolbar_read_progress_decrease);
        bottomToolbarTeadProgressPlus = (ImageButton) findViewById(R.id.bottom_toolbar_read_progress_plus);
        bottomToolbarReadProgressSeekBar = (SeekBar) findViewById(R.id.bottom_toolbar_read_progress_seekBar);
        bookBtnMenu = (ImageButton) findViewById(R.id.bookBtn_menu);
        bookBtnMark = (ImageButton) findViewById(R.id.bookBtn_mark);
        bookBtnSize = (ImageButton) findViewById(R.id.bookBtn_size);
        imageBtnLight = (ImageButton) findViewById(R.id.imageBtn_light);
        mPaginatingProgress = (ProgressBar) findViewById(R.id.bottom_toolbar_read_progress_paginating);
        mTextPageNumber = (TextView) findViewById(R.id.mTextPageNumber);

        //配置toolbar 默认不显示
        bookReaderToolbar.setVisibility(View.GONE);
        bookReaderToolbar.setClickable(true);
        //初始化菜单
        initFragment();
        //设置字体包数据
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/fangzhenglanting.ttf");
        if (null != typeFace) {
            menuFragment.setTypeFace(typeFace);
            marksFragment.setTypeFace(typeFace);
            mPageIndexView.setTypeface(typeFace);
            mChapterNameView.setTypeface(typeFace);
            bookName.setTypeface(typeFace);
            authorName.setTypeface(typeFace);
            toolbarTopTitle.setTypeface(typeFace);
            mTextPageNumber.setTypeface(typeFace);
        }
    }

    /**
     * 初始化菜单
     */
    private void initFragment() {
        mRadioGroup = (RadioGroup) findViewById(R.id.mRadioGroup);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        fragmentList = new ArrayList<>();
        fragmentList.clear();
        menuFragment = new EbookMenuFragment();
        marksFragment = new EbookMarksFragment();
        fragmentList.add(menuFragment);
        fragmentList.add(marksFragment);

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList, BOOK_MENU_TITLES);
        mViewPager.setAdapter(adapter);
    }

    /**
     * 初始化参数配置,下载epub 文件
     */
    private void initReaderParams() {
        //侧边栏 禁止手指滑动弹出
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mReaderSettings = new ReaderSettings(this);
        //初始化背景图片
        initReaderBackGroud();
        changeTheme();
        Intent intent = getIntent();
        String fileDownLoadPath = intent.getStringExtra(EbookConstant.EPUB_BOOK_DOWNLOAD_PATH);
        String fileName = intent.getStringExtra(EbookConstant.EPUB_BOOK_NAME);
        if (null != fileDownLoadPath && null != fileName &&
                !TextUtils.isEmpty(fileDownLoadPath) && !TextUtils.isEmpty(fileName)) {
            //开始下载和解析epub文件
            controller.downLoadOrParserEpubFile(this, fileName, fileDownLoadPath);
        }
        // TODO:TAG 4 配置屏幕亮度
        if (!mReaderSettings.mAutoAdjustBrightness) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = (float) (mReaderSettings.mBrightness / (255 * 1.0f));
            getWindow().setAttributes(lp);
        }
        //修改主题
        changeTheme();
    }

    /**
     * 保存当前阅读配置
     */
    private void saveConfig() {
        mReaderSettings.save(this);
    }

    /**
     * 初始化背景图片
     */
    private void initReaderBackGroud() {
        if (mReaderSettings.isThemeNight) {
            mWebReaderParent.setBackgroundColor(Color.BLACK);
        } else {
            List<ChoosedColor> backgroudColorList = DataService.getChoosedColorList();
            if (mReaderSettings.mTheme == 1) {
                this.mWebReaderParent.setBackgroundResource(R.drawable.paper);
            } else {
                this.mWebReaderParent.setBackgroundResource(0);
                String color = backgroudColorList.get(mReaderSettings.mTheme).bgColorResourse;
                this.mWebReaderParent.setBackgroundColor(Color.parseColor(color));
            }
        }
    }

    /**
     * 初始化监听
     */
    private void initReaderListener() {
        mWebReader.initializeReader(this);
        mWebReader.setReaderListener(this);
        toolbarTopBack.setOnClickListener(this);
        middleToolbar.setOnClickListener(this);
        bookBtnMenu.setOnClickListener(this);
        bookBtnMark.setOnClickListener(this);
        bookBtnSize.setOnClickListener(this);
        imageBtnLight.setOnClickListener(this);

        bottomToolbarTeadProgressPlus.setOnClickListener(this);
        bottomToolbarTeadProgressDecrease.setOnClickListener(this);
        //进度更新
        bottomToolbarReadProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == seekBar.getMax()) {
                    progress = seekBar.getMax() - 1;
                }
                String pageNum = String.format("%d/%d", progress + 1, seekBar.getMax());
                mTextPageNumber.setText(pageNum);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //更新阅读进度
                mWebReader.gotoPercent(seekBar.getProgress() * 1.0F / seekBar.getMax());
            }
        });
        /**
         * 滑动切换目录,书签界面
         */
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton) mRadioGroup.getChildAt(position)).setChecked(true);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        /**
         * 选择目录还是书签
         */
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.ebook_menu) {
                    mViewPager.setCurrentItem(0);
                } else if (checkedId == R.id.ebook_mark) {
                    mViewPager.setCurrentItem(1);
                }
            }
        });
    }


    /**
     * 设置当前文章所在的currentId
     */
    private void setAdapterCurrentId() {
        if (null != menuFragment && null != menuFragment.getAdapter() && null != mWebReader) {
            menuFragment.getAdapter().setCurrentId(mWebReader.getCurrentChapterIndex());
        }
    }

    /**
     * toast
     *
     * @param message
     */
    public void toastInfo(String message) {
        if (null != mWebReaderParent) {
            Snackbar.make(mWebReaderParent, message, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * 打开epub电子书
     *
     * @param fileRelativePath
     */
    @Override
    public void openEpubBooks(String fileRelativePath, String fileDownLoadUrl) {
        mWebReader.openBook(fileRelativePath, "", "");
        //解压成功
        SharePreferencesUtil.setBoolean(this, fileDownLoadUrl, true);
    }

    /**
     * 显示下载进度
     *
     * @param progress
     */
    @Override
    public void downLoadProgress(int progress) {
        if (null != mCBProgressBar) {
            mCBProgressBar.setProgress(progress);
        }
    }

    /**
     * 出错以后销毁界面
     */
    @Override
    public void finishActivity() {
        toastInfo("打开电子书失败！");
        this.finish();
    }

    /**
     * 删除书架内容中的源文件
     *
     * @param bookId
     */
    private void deleteSelectElectronicShelfByBookId(String bookId, String fileName) {
//        new LocalBook(this).delete(bookId);
//        new EpubBookMarks(this).delete(bookId);
//        new BookLastMark(this).delete(bookId);
        //删除文件
        FileUtil.deleteFile(EbookConstant.FILE_SAVE_URL + fileName);
    }

    /**
     * 下载成功
     */
    @Override
    public void downLoadSuccess() {
        mCBProgressBar.setVisibility(View.GONE);
        toastInfo(getString(R.string.join_to_electronic_shelf));
    }

    /**
     * 下载失败
     */
    @Override
    public void downLoadFailure(String savePath, String downLoadUrl) {
        openEpubBooksFailure(savePath, downLoadUrl);
    }

    /**
     * 开始下载
     */
    @Override
    public void downLoadStart() {
        mCBProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 打开电子书失败
     *
     * @param savePath
     * @param downLoadUrl
     */
    @Override
    public void openEpubBooksFailure(String savePath, String downLoadUrl) {
        SharePreferencesUtil.setBoolean(this, downLoadUrl, false);
        //删除关于图书的资料
        try {
            deleteSelectElectronicShelfByBookId(Md5Encrypt.md5(savePath),
                    getIntent().getStringExtra(EbookConstant.EPUB_BOOK_NAME));
        } catch (Exception e) {
        }
    }

    //*=============================================================================================
    //* 接收handler处理事件更新UI
    //*
    //*=============================================================================================
    private final Handler mCurrentUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //显示toolbar
                case HANDLER_SHOW_TOOLBAR:
                    //如果刚刚fontWindow 刚刚消失，则不执行显示状态栏
                    if (fontWindowDismiss) {
                        fontWindowDismiss = false;
                        return;
                    }
                    if (bookReaderToolbar.getVisibility() == View.GONE) {
                        bookReaderToolbar.setVisibility(View.VISIBLE);
                        Animation topAnim = AnimationUtils.loadAnimation(CloudLibraryReaderActivity.this, R.anim.dialog_top_enter);
                        topToolbar.setAnimation(topAnim);
                        bottomToolbar.setAnimation(topAnim);
                        // 显示状态栏
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    }
                    break;
                //隐藏toolbar
                case HANDLER_HIDE_TOOLBAR:
                    if (bookReaderToolbar.getVisibility() == View.VISIBLE) {
                        bookReaderToolbar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                        Animation topAnim = AnimationUtils.loadAnimation(CloudLibraryReaderActivity.this, R.anim.dialog_top_exit);
                        topToolbar.setAnimation(topAnim);
                        bottomToolbar.setAnimation(topAnim);
                    }
                    break;
                //加载loading
                case HANDLER_CHAPTER_LOADING:
                    mProgressBar.setVisibility(View.VISIBLE);
                    mChapterNameView.setText("");
                    mPageIndexView.setText("");
                    //如果在加载loading 显示背景图
                    break;
                //切换主题
                case HANDLER_THEME_CHANGED:
                    changeTheme();
                    break;
                //隐藏loading，更新界面
                case HANDLER_PAGE_NAVIGATION_FINISHED:
                    mProgressBar.setVisibility(View.GONE);
                    //如果在加载loading完毕 隐藏背景图
                    updateViews();
                    break;
                //更新分页进度
                case HANDLER_CURRENT_PAGE_CHANGED:
                    mBookCurPageIndex = msg.arg1;
                    updateReadingProgress(msg.arg1, msg.arg2);
                    break;
                //开始分页
                case HANDLER_PAGINATION_BEGIN:
                    mChapterNameView.setText("");
                    mPageIndexView.setText("");
                    bottomToolbarReadProgressSeekBar.setVisibility(View.GONE);
                    mPaginatingProgress.setVisibility(View.VISIBLE);
                    mPaginatingProgress.setProgress(0);
                    mTextPageNumber.setText(R.string.paginating);
                    isPageingChapter = true;
                    break;
                //分页中
                case HANDLER_PAGINATION_PROGRESS_UPDATE:
                    mPaginatingProgress.setProgress(msg.arg1);
                    isPageingChapter = true;
                    break;
                //分页结束
                case HANDLER_PAGINATION_FINISH:
                    bottomToolbarReadProgressSeekBar.setVisibility(View.VISIBLE);
                    mPaginatingProgress.setVisibility(View.GONE);
                    bottomToolbarReadProgressSeekBar.setMax(msg.arg1);
                    bottomToolbarReadProgressSeekBar.setProgress(msg.arg2);
                    String pageNum = String.format("%d/%d", msg.arg2, msg.arg1);
                    mPageIndexView.setText(pageNum);
                    mTextPageNumber.setText(pageNum);
                    //设置当前文章所在的currentId
                    setAdapterCurrentId();
                    isPageingChapter = false;
                    break;
                //分页出错
                case HANDLER_BOOK_ERROR:
                    toastInfo("分页出错!");
                    finish();
                    return;
                //目录展示
                case HANDLER_BOOK_READY:
                    if (null != menuFragment) {
                        menuFragment.setListViewDatas(mReaderSettings,
                                mWebReader.getSequenceReadingChapterList(), mWebReader.getTOC(),
                                mWebReader.getCurrentChapterIndex());
                    }
                    break;
            }
            mCurrentUIHandler.removeMessages(msg.what);
        }
    };

    /**
     * 更新阅读进度
     *
     * @param pageIndex
     * @param pageCount
     */
    private void updateReadingProgress(int pageIndex, int pageCount) {
        if (!mWebReader.isPaginatingFinish()) {
            mWebReader.mBook.pageCount = pageCount;
            String pageNum = String.format("%d/%d", pageIndex + 1, pageCount);
            mPageIndexView.setText(pageNum);
            mTextPageNumber.setText(pageNum);
            bottomToolbarReadProgressSeekBar.setProgress(pageIndex);

        }
    }


    @Override
    public void d(String warning) {

    }

    @Override
    public void e(String error) {

    }

    /**
     * 电子书准备就绪
     */
    @Override
    public void onBookReady() {
        mCurrentUIHandler.obtainMessage(HANDLER_BOOK_READY).sendToTarget();
    }

    /**
     * 电子书打开出错
     */
    @Override
    public void onBookError() {
        mCurrentUIHandler.obtainMessage(HANDLER_BOOK_ERROR).sendToTarget();
    }

    /**
     * 电子书开始分页
     */
    @Override
    public void onPaginationStarting() {
        mCurrentUIHandler.obtainMessage(HANDLER_PAGINATION_BEGIN).sendToTarget();
    }

    /**
     * 电子书进度修改
     */
    @Override
    public void onPaginationProgressChanged(int progress) {
        mCurrentUIHandler.obtainMessage(HANDLER_PAGINATION_PROGRESS_UPDATE, progress, 0).sendToTarget();
    }

    /**
     * 分页就绪
     *
     * @param pageCount
     * @param pageIndex
     */
    @Override
    public void onPaginationReady(int pageCount, int pageIndex) {
        mCurrentUIHandler.obtainMessage(HANDLER_PAGINATION_FINISH, pageCount, pageIndex).sendToTarget();
    }

    /**
     * 开始文章分页
     *
     * @param chapterIndex
     */
    @Override
    public void onChapterLoading(int chapterIndex) {
        mCurrentUIHandler.obtainMessage(HANDLER_CHAPTER_LOADING).sendToTarget();
    }

    /**
     * 文章就绪
     *
     * @param chapterIndex
     */
    @Override
    public void onChapterReady(int chapterIndex) {
    }

    /**
     * 当前页码发生改变
     *
     * @param chapterIndex 当前章序号
     * @param pageIndex    当前页码
     * @param pageCount    文档总页码
     */
    @Override
    public void onCurrentPageChanged(int chapterIndex, int pageIndex, int pageCount) {
        mCurrentUIHandler.obtainMessage(HANDLER_CURRENT_PAGE_CHANGED, pageIndex, pageCount).sendToTarget();
    }

    /**
     * 页面跳转完成通知
     *
     * @param chapterIndex 当前章序号
     * @param pageIndex    当前页码
     */
    @Override
    public void onPageNavigationFinish(int chapterIndex, int pageIndex) {
        mCurrentUIHandler.obtainMessage(HANDLER_PAGE_NAVIGATION_FINISHED).sendToTarget();
    }

    /**
     * Theme更改完成通知
     */
    @Override
    public void onThemeApplied() {
        mCurrentUIHandler.obtainMessage(HANDLER_THEME_CHANGED).sendToTarget();
    }

    /**
     * 通知外部需要显示或者关闭工具栏
     */
    @Override
    public void onShowToolbar() {
        if (bookReaderToolbar.getVisibility() == View.GONE) {
            mCurrentUIHandler.obtainMessage(HANDLER_SHOW_TOOLBAR).sendToTarget();
        } else {
            mCurrentUIHandler.obtainMessage(HANDLER_HIDE_TOOLBAR).sendToTarget();
        }
    }

    /**
     * 更新界面
     */
    private void updateViews() {
        if (mWebReader.isChapterReady(mWebReader.getCurrentChapterIndex())) {
            String title = mWebReader.getCurrentChapterTitle();
            if (TextUtils.isEmpty(title)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("章节: ").append(mWebReader.getCurrentChapterIndex() + 1);
                mChapterNameView.setText(stringBuilder.toString());
            } else {
                mChapterNameView.setText(title);
            }
        } else {
            mChapterNameView.setText("");
            mPageIndexView.setText("");
        }
        //设置电子书信息 名称 作者
        setEpubBookInformation();
        //设置阅读字体颜色
        setReaderBookFontColor();
    }

    /**
     * 设置电子书信息 名称 作者
     */
    private void setEpubBookInformation() {
        if (null != mWebReader) {
            String name = mWebReader.mBook.name;
            String author = mWebReader.mBook.author;
            bookName.setText(TextUtils.isEmpty(name) ? "" : name);
            authorName.setText(TextUtils.isEmpty(author) ? "" : author);
            toolbarTopTitle.setText(TextUtils.isEmpty(name) ? "" : name);
        }
    }

    /**
     * 设置阅读字体颜色
     */
    private void setReaderBookFontColor() {
        if (null != mReaderSettings) {
            boolean isThemeNight = mReaderSettings.isThemeNight;
            if (isThemeNight) {
                setChapterTextColor("#ffffff");
            } else {
                List<ChoosedColor> chooseColorList = DataService.getChoosedColorList();
                String color1 = chooseColorList.get(mReaderSettings.mTheme).textColorStringResourse;
                setChapterTextColor(color1);
            }
        }
    }

    /**
     * 设置文章字体颜色
     *
     * @param color
     */
    private void setChapterTextColor(String color) {
        bookName.setTextColor(Color.parseColor(color));
        authorName.setTextColor(Color.parseColor(color));
        mPageIndexView.setTextColor(Color.parseColor(color));
        mChapterNameView.setTextColor(Color.parseColor(color));
    }

    //初始化字体弹出框
    private void initReaderFontChooserPopUpWindow() {
        if (null == fontSizeView || null == fontSizePopWindow) {
            fontSizeView = LayoutInflater.from(this).inflate(R.layout.popwindow_textfont_layout, null);
            fontSizePopWindow = new PopupWindow(fontSizeView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            fontSizePopWindow.setBackgroundDrawable(new BitmapDrawable());
            fontSizePopWindow.setOutsideTouchable(true);
            textViewFont1 = (TextView) fontSizeView.findViewById(R.id.textViewFont1);
            textViewFont2 = (TextView) fontSizeView.findViewById(R.id.textViewFont2);
            mLightSeakBar = (SeekBar) fontSizeView.findViewById(R.id.mLightSeakBar);
            mRecyclerView = (RecyclerView) fontSizeView.findViewById(R.id.mRecyclerView);
            textViewFont1.setOnClickListener(this);
            textViewFont2.setOnClickListener(this);
            //设置字体包数据
            if (null != typeFace) {
                textViewFont1.setTypeface(typeFace);
                textViewFont2.setTypeface(typeFace);
            }
            //设置画廊视图
            setGallery();
            //设置亮度
            mLightSeakBar.setMax(255);
            int brightness = mReaderSettings.mBrightness;
            mLightSeakBar.setProgress(brightness);
            //亮度调节
            mLightSeakBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.screenBrightness = (float) (progress / (255 * 1.0f));
                    getWindow().setAttributes(lp);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (null != mReaderSettings && null != mWebReader) {
                        mReaderSettings.mBrightness = seekBar.getProgress();
                    }
                }
            });
            fontSizePopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    fontWindowDismiss = true;
                }
            });
        }
    }

    /**
     * 修改字体大小及主题
     */
    private void showReaderFontSizePopWindow() {
        initReaderFontChooserPopUpWindow();
        if (null != fontSizePopWindow) {
            fontSizePopWindow.showAtLocation(mWebReader, Gravity.BOTTOM, 0, 0);
        }
        mCurrentUIHandler.obtainMessage(HANDLER_HIDE_TOOLBAR).sendToTarget();
    }

    /**
     * 设置选择背景
     */
    private void setGallery() {
        mRecyclerView.setHasFixedSize(true);
        List<ChoosedColor> list = DataService.getChoosedColorList();
        final ReadingChooseColorAdapter chooseColorAdapter = new ReadingChooseColorAdapter(this);
        mRecyclerView.setAdapter(chooseColorAdapter);
        chooseColorAdapter.addDatas(list, false);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(manager);
        chooseColorAdapter.setSelected(mReaderSettings.mTheme);
        //设置默认选中
        //选择背景
        chooseColorAdapter.setOnItemClickListener(new ReadingChooseColorAdapter.OnItemClickListener<ChoosedColor>() {
            @Override
            public void onItemClick(int position, ChoosedColor data) {
                if (null != data) {
                    chooseColorAdapter.setSelected(position);
                    changeThemeByPosition(position);
                }
            }
        });
    }

    /**
     * 选择背景图和切换字体
     *
     * @param position 背景下标
     */
    private void changeThemeByPosition(int position) {
        if (mWebReader.isChapterLoading()) {
            mReaderSettings.mTheme = position;
            mReaderSettings.isThemeNight = false;
            // 阅读器应用主题和其他配置
            mWebReader.applyRuntimeSettings(mReaderSettings);
            mCurrentUIHandler.obtainMessage(HANDLER_THEME_CHANGED).sendToTarget();
            initReaderBackGroud();
        }
    }

    /**
     * 按键监听
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            //如果抽屉打开，则关闭抽屉，否则finish Activity
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
                drawerLayout.setFocusable(true);
            } else if (null != bookReaderToolbar && bookReaderToolbar.getVisibility() == View.VISIBLE) {
                //标题栏
                mCurrentUIHandler.obtainMessage(HANDLER_HIDE_TOOLBAR).sendToTarget();
            } else if (null != fontSizePopWindow && fontSizePopWindow.isShowing()) {
                //字体显示栏
                fontSizePopWindow.dismiss();
            } else {
                this.finish();
            }
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        boolean rePaginating = false;
        if (v == toolbarTopBack) {
            this.finish();
        } else if (v == middleToolbar) {//隐藏toolbar
            mCurrentUIHandler.obtainMessage(HANDLER_HIDE_TOOLBAR).sendToTarget();
        } else if (v == bottomToolbarTeadProgressDecrease) {//上一章
            if (!mWebReader.isChapterLoading()) {
                return;
            }
            //进入上一章
            goToLastChapter();
        } else if (v == bottomToolbarTeadProgressPlus) {//下一张
            if (!mWebReader.isChapterLoading()) {
                return;
            }
            //进入下一章
            goToNextChapter();
        } else if (v == bookBtnMenu) {//目录
            //刷新目录列表
            if (null != menuFragment && null != menuFragment.getAdapter()) {
                menuFragment.getAdapter().notifyDataSetChanged();
            }
            if (null != marksFragment && null != marksFragment.getAdapter()) {
                //刷新书签界面
                marksFragment.setBookId(Md5Encrypt.md5(mWebReader.mBook.path));
                marksFragment.setUserVisibleHint(true);
                marksFragment.getAdapter().notifyDataSetChanged();
            }
            drawerLayout.openDrawer(GravityCompat.START);
            drawerLayout.setFocusable(true);
            mCurrentUIHandler.obtainMessage(HANDLER_HIDE_TOOLBAR).sendToTarget();
        } else if (v == bookBtnMark) {//添加书签
            if (isPageingChapter) {
                toastInfo("正在分页中...");
                return;
            }
            if (!mWebReader.isChapterLoading() || null == mWebReader.mBook) {
                return;
            }
            try {
                if (EpubBookMarks.hasBookMark(this, mBookCurPageIndex)) {
                    toastInfo("书签已存在！");
                    return;
                }
                EpubBookMarks marks = new EpubBookMarks(this);
                marks.id = Md5Encrypt.md5(mWebReader.mBook.path);
                marks.load();
                marks.current_page = mBookCurPageIndex;
                marks.title = mWebReader.getCurrentChapterTitle();
                marks.total_page = mWebReader.getTotalPageCount();
                marks.last_access_date = System.currentTimeMillis();
                marks.save();
                toastInfo("书签保存成功！");
            } catch (Exception e) {
                toastInfo("书签保存失败！");
            }
        } else if (v == bookBtnSize) {//修改字体大小及主题
            if (isPageingChapter) {
                toastInfo("正在分页中...");
                return;
            }
            showReaderFontSizePopWindow();
        } else if (v == imageBtnLight) {//黑夜模式
            if (isPageingChapter) {
                toastInfo("正在分页中...");
                return;
            }
            if (mWebReader.isChapterLoading()) {
                mReaderSettings.isThemeNight = !mReaderSettings.isThemeNight;
                mWebReader.applyRuntimeSettings(mReaderSettings);
            }
        } else if (v == textViewFont1) {//字体--
            if (isPageingChapter) {
                return;
            }
            if (!mWebReader.isChapterLoading()) {
                return;
            }
            if (mReaderSettings.mTextSize > ReaderSettings.MIN_FONT_SIZE) {
                mReaderSettings.mTextSize -= ReaderSettings.FONT_SIZE_DELTA;
                rePaginating = true;
            }
        } else if (v == textViewFont2) {//字体++
            if (isPageingChapter) {
                return;
            }
            if (!mWebReader.isChapterLoading()) {
                return;
            }
            if (mReaderSettings.mTextSize < ReaderSettings.MAX_FONT_SIZE) {
                mReaderSettings.mTextSize += ReaderSettings.FONT_SIZE_DELTA;
                rePaginating = true;
            }
        }
        //修改阅读器配置
        if (rePaginating) {
            mWebReader.applyRuntimeSettings(mReaderSettings);
        }
    }

    /**
     * 进入上一章节
     */
    private void goToLastChapter() {
        goTospecifiedChapter(mWebReader.getCurrentChapterIndex() - 1);
    }

    /**
     * 进入下一章节
     */
    private void goToNextChapter() {
        goTospecifiedChapter(mWebReader.getCurrentChapterIndex() + 1);
    }

    /**
     * 进入指定的章节
     */
    private void goTospecifiedChapter(int chapterIndex) {
        if (null == menuFragment) {
            return;
        }
        CloudLibraryTOCAdapter adapter = menuFragment.getAdapter();
        if (null == adapter) {
            return;
        }
        EpubParser.NavPoint nav = adapter.getItem(chapterIndex);
        if (null == nav) {
            return;
        }
        int prePageCount = mWebReader.getChapterPreviousPageCount(nav.chapterIndex);
        int targetpageindex = nav.pageIndex - prePageCount - 1;
        if (mWebReader.getCurrentChapterIndex() != nav.chapterIndex
                || (mWebReader.getCurrentChapterIndex() == nav.chapterIndex
                && mWebReader.getCurrentPageIndex() != targetpageindex)) {
            mWebReader.loadChapter(nav.chapterIndex, targetpageindex);
        } else if (mWebReader.isChapterReady(nav.chapterIndex)) {
            mWebReader.loadChapter(nav.chapterIndex, targetpageindex);
        }
    }


    /**
     * 修改主题
     */
    private void changeTheme() {
        //设置标题等字体颜色
        setReaderBookFontColor();
    }

    /**
     * 点击目录跳转到指定的界面
     *
     * @param nav
     */
    @Override
    public void callbackClickItemToChapter(EpubParser.NavPoint nav, int position) {
        if (null == mWebReader) {
            return;
        }
        if (null == nav) {
            return;
        }
        goTospecifiedChapter(position);
        //设置当前文章所在的currentId
        setAdapterCurrentId();
        //关闭抽屉
        this.drawerLayout.closeDrawer(GravityCompat.START);
        drawerLayout.setFocusable(false);
    }

    /**
     * 获取电子书图片地址
     *
     * @return
     */
    private String getEpubBookImagePath() {
        return getIntent().getStringExtra(EbookConstant.EPUB_BOOK_IAMGE_PATH);
    }

    /**
     * 获取epub文件下载地址
     *
     * @return
     */
    private String getEpubBookDownLoadPath() {
        return getIntent().getStringExtra(EbookConstant.EPUB_BOOK_DOWNLOAD_PATH);
    }

    /**
     * 获取epub文件下载地址
     *
     * @return
     */
    private String getEpubBookName() {
        return getIntent().getStringExtra(EbookConstant.EPUB_BOOK_NAME);
    }

    /**
     * 书签回调跳转到指定页码
     *
     * @param pageIndex
     */
    @Override
    public void callbackClickItemToPageIndex(int pageIndex) {
        jumpToPage(pageIndex);
        //关闭抽屉
        this.drawerLayout.closeDrawer(GravityCompat.START);
        this.drawerLayout.setFocusable(false);
    }

    /**
     * 跳转到指定页码
     *
     * @param bookPageIndex
     */
    public void jumpToPage(int bookPageIndex) {
        if (null != mWebReader) {
            mWebReader.gotoPage(bookPageIndex);
        }
    }
}
