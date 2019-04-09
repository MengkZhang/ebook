package com.tzpt.cloudlibrary.ui.ebook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.bean.BookMarkBean;
import com.tzpt.cloudlibrary.bean.ReadingColorBean;
import com.tzpt.cloudlibrary.bean.ShareBean;
import com.tzpt.cloudlibrary.cbreader.bookmodel.TOCTree;
import com.tzpt.cloudlibrary.ui.account.LoginActivity;
import com.tzpt.cloudlibrary.ui.share.ShareActivity;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.CBProgressBar;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.DrawableCenterTextView;
import com.tzpt.cloudlibrary.widget.pageflipview.NavigationOperateListener;
import com.tzpt.cloudlibrary.widget.pageflipview.OnReadPageCountListener;
import com.tzpt.cloudlibrary.widget.pageflipview.PageFlipView;
import com.tzpt.cloudlibrary.widget.pageflipview.RestrictReadListener;
import com.tzpt.cloudlibrary.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;
import com.tzpt.cloudlibrary.zlibrary.core.tree.ZLTree;
import com.tzpt.cloudlibrary.zlibrary.core.util.ZLColor;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 电子书阅读界面
 * Created by Administrator on 2017/10/10.
 */

public class EBookReaderActivity extends AppCompatActivity implements EBookReaderContract.View,
        NavigationOperateListener {
    private final static String EPUB_BOOK_NAME = "book_name";
    private final static String EPUB_BOOK_DOWNLOAD_PATH = "book_download_path";
    private final static String EPUB_BOOK_IMAGE_PATH = "book_image_path";
    private final static String EPUB_BOOK_TITLE = "book_title";
    private final static String EPUB_BOOK_AUTHOR = "book_author";
    private final static String EPUB_SHARE_URL = "share_url";
    private final static String EPUB_SHARE_CONTENT = "share_content";
    private final static String EPUB_BELONG_LIB = "belong_lib";


    public static void startActivity(Context context, String name, String downloadPath, String title,
                                     String author, String coverImg, String shareUrl, String summary,
                                     String libCode) {
        Intent intent = new Intent(context, EBookReaderActivity.class);
        intent.putExtra(EPUB_BOOK_NAME, name);
        intent.putExtra(EPUB_BOOK_DOWNLOAD_PATH, downloadPath);
        intent.putExtra(EPUB_BOOK_TITLE, title);
        intent.putExtra(EPUB_BOOK_AUTHOR, author);
        intent.putExtra(EPUB_BOOK_IMAGE_PATH, coverImg);
        intent.putExtra(EPUB_SHARE_URL, shareUrl);
        intent.putExtra(EPUB_SHARE_CONTENT, summary);
        intent.putExtra(EPUB_BELONG_LIB, libCode);
        context.startActivity(intent);
    }

    public static void startActivityForResult(Activity activity, String name, String downloadPath, String title,
                                              String author, String coverImg, String shareUrl, String summary,
                                              String libCode, int requestCode) {
        Intent intent = new Intent(activity, EBookReaderActivity.class);
        intent.putExtra(EPUB_BOOK_NAME, name);
        intent.putExtra(EPUB_BOOK_DOWNLOAD_PATH, downloadPath);
        intent.putExtra(EPUB_BOOK_TITLE, title);
        intent.putExtra(EPUB_BOOK_AUTHOR, author);
        intent.putExtra(EPUB_BOOK_IMAGE_PATH, coverImg);
        intent.putExtra(EPUB_SHARE_URL, shareUrl);
        intent.putExtra(EPUB_SHARE_CONTENT, summary);//这里的分享简介 就传的是服务端的summary简介 在表中再加个简介字段还是存summary内容
        intent.putExtra(EPUB_BELONG_LIB, libCode);
        activity.startActivityForResult(intent, requestCode);
    }

    //Activity的根布局RootView
    @BindView(R.id.root_view_rl)
    RelativeLayout mRootViewRl;
    @BindView(R.id.reader_widget)
    PageFlipView mReaderWidget;
    @BindView(R.id.ebook_toc_rBtn)
    RadioButton mEbookTocRBtn;
    @BindView(R.id.ebook_mark_rBtn)
    RadioButton mEbookMarkRBtn;
    @BindView(R.id.slide_menu_rg)
    RadioGroup mSlideMenuRg;
    @BindView(R.id.toc_mark_vp)
    ViewPager mTocMarkVp;
    @BindView(R.id.slide_menu_dl)
    DrawerLayout mSlideMenuDl;
    @BindView(R.id.download_cbProBar)
    CBProgressBar mDownloadCbProBar;
    @BindView(R.id.parse_proBar)
    ProgressBar mParseProBar;
    @BindView(R.id.menu_cover_view)
    View mMenuCoverView;
    @BindView(R.id.loading_cover_view)
    View mLoadingCoverView;
    @BindView(R.id.toolbar_back_btn)
    ImageButton mToolbarBackBtn;
    @BindView(R.id.toolbar_title_tv)
    TextView mToolbarTitleTv;
    @BindView(R.id.top_toolbar_rl)
    RelativeLayout mTopToolbarRl;
    @BindView(R.id.toolbar_right_btn)
    ImageButton mToolbarRightBtn;

    private TextView mToolbarProgressTv;
    private SeekBar mToolbarReadProgressSeekBar;
    private DrawableCenterTextView mAddMarkImgBtn;
    private DrawableCenterTextView mLightImgBtn;
    private PopupWindow mOptionPanelBottomPpw;

    private ReadingColorAdapter mReadColorAdapter;
    private PopupWindow mDelMarkPopWindow;
    private int mBrightness = -1;

    private EBookReaderPresenter mPresenter;

    private EBookTocListAdapter mTocListAdapter;
    private ListView mTocLv;
    private EBookMarkAdapter mMarkAdapter;
    private ListView mMarkLv;
    private DrawableCenterTextView mBookMarkEmptyTv;

    private boolean mIsFullState;
    private boolean mIsAutoHide = true;
    private String mBookName;
    private String mDownloadUrl;
    private String mBookAuthor;
    private String mBookCoverImg;
    private String mBookTitle;
    private String mBelongLib;
    private Receiver mReceiver;

    private String mShareContent;
    private String mDescContent;//简介
    private String mShareUrl;

    private int mCurrentPageMarkIndex = -1;
    private int mMarkListPosition;

    private boolean mIsOpenSuccess = false;
    private boolean mIsCollectionLock = true;
    private boolean mIsCollection;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    setScreenFull(true);
                    break;
                case 1001:
                    mSlideMenuDl.openDrawer(GravityCompat.START);
                    break;
                case 1002:
                    initFontChooserPopUpWindow();
                    break;
            }
        }
    };

    @OnClick({
            R.id.menu_cover_view, R.id.toolbar_back_btn, R.id.toolbar_right_btn,
            R.id.close_read_menu_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu_cover_view:
                hideOperatePanel(true);
                break;
            case R.id.toolbar_back_btn:
                mPresenter.saveReadPosition();
                finish();
                break;
            case R.id.toolbar_right_btn:
                hideOperatePanel(true);
                String content;
                if (TextUtils.isEmpty(mShareContent)) {
                    content = getString(R.string.no_summary);
                } else {
                    content = mShareContent;
                }
                String shareImg;
                if (TextUtils.isEmpty(mBookCoverImg)) {
                    shareImg = "http://img.ytsg.cn/images/htmlPage/ic_logo.png";
                } else {
                    shareImg = mBookCoverImg;
                }

                ShareBean shareBean = new ShareBean();
                shareBean.shareTitle = mBookTitle;
                shareBean.shareContent = content;
                shareBean.shareUrl = mShareUrl;
                shareBean.shareUrlForWX = mShareUrl;
                shareBean.shareImagePath = shareImg;

                shareBean.mNeedCopy = true;
                shareBean.mNeedCollection = true;
                shareBean.mIsCollection = mIsCollection;
                ShareActivity.startActivityForResult(this, shareBean, 1002);
                break;
            case R.id.close_read_menu_btn:
                mSlideMenuDl.closeDrawer(GravityCompat.START);
                break;
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_ebook_read);
        ButterKnife.bind(this);

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0 && mIsAutoHide) {
                    mIsFullState = true;
                    mHandler.sendEmptyMessageDelayed(1000, 2000);
                }
            }
        });

        setScreenFull(true);

        initViewListener();
        initFragments();
        initOptionPanelBottom();
        mPresenter = new EBookReaderPresenter();
        mPresenter.attachView(this);

        mBookName = getIntent().getStringExtra(EPUB_BOOK_NAME);
        mDownloadUrl = getIntent().getStringExtra(EPUB_BOOK_DOWNLOAD_PATH);
        mBookTitle = getIntent().getStringExtra(EPUB_BOOK_TITLE);
        mBookAuthor = getIntent().getStringExtra(EPUB_BOOK_AUTHOR);
        mBookCoverImg = getIntent().getStringExtra(EPUB_BOOK_IMAGE_PATH);
        mBelongLib = getIntent().getStringExtra(EPUB_BELONG_LIB);

        mShareUrl = getIntent().getStringExtra(EPUB_SHARE_URL);
        mShareContent = getIntent().getStringExtra(EPUB_SHARE_CONTENT);
        mDescContent = getIntent().getStringExtra(EPUB_SHARE_CONTENT);

        mToolbarTitleTv.setText(mBookTitle);

        mPresenter.openBook(mBookName, mBookCoverImg, mDownloadUrl, mBookTitle, mBookAuthor, mShareUrl, mShareContent, mBelongLib, mDescContent);

        mReceiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mReceiver, intentFilter);
    }

    /**
     * 初始化面板底部
     */
    private void initOptionPanelBottom() {
        View mOptionPanelBottomPpv = getLayoutInflater().inflate(R.layout.ppw_option_panel_bottom, null);
        mToolbarProgressTv = (TextView) mOptionPanelBottomPpv.findViewById(R.id.toolbar_progress_tv);
        ImageButton preIBtn = (ImageButton) mOptionPanelBottomPpv.findViewById(R.id.toolbar_read_progress_minus_imgBtn);
        preIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.turnPreToc();
            }
        });
        mToolbarReadProgressSeekBar = (SeekBar) mOptionPanelBottomPpv.findViewById(R.id.toolbar_read_progress_seekBar);
        mToolbarReadProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private boolean mIsTrackingTouch = false;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mIsTrackingTouch
                        && mTopToolbarRl.getVisibility() == View.VISIBLE) {
                    mPresenter.gotoPageByPec(seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsTrackingTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsTrackingTouch = false;
                if (mTopToolbarRl.getVisibility() == View.VISIBLE) {
                    mPresenter.getMarkBtnStatus();
                    repaint();
                }

            }
        });
        ImageButton nextIBtn = (ImageButton) mOptionPanelBottomPpv.findViewById(R.id.toolbar_read_progress_increase_imgBtn);
        nextIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.turnNextToc();
            }
        });
        DrawableCenterTextView mSlideMenuImgBtn = (DrawableCenterTextView) mOptionPanelBottomPpv.findViewById(R.id.slide_menu_imgBtn);
        mSlideMenuImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getBookToc();
                mPresenter.getBookMarkList();
                hideOperatePanel(true);
                mSlideMenuDl.openDrawer(GravityCompat.START);
                mIsAutoHide = true;
            }
        });
        mAddMarkImgBtn = (DrawableCenterTextView) mOptionPanelBottomPpv.findViewById(R.id.add_mark_imgBtn);
        mAddMarkImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPageMarkIndex == -1) {
                    mPresenter.addBookMark();
                } else {
                    mPresenter.delBookMark(mCurrentPageMarkIndex);
                }
            }
        });
        DrawableCenterTextView mSizeImgBtn = (DrawableCenterTextView) mOptionPanelBottomPpv.findViewById(R.id.size_imgBtn);
        mSizeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReaderFontSizePopWindow();
            }
        });
        mLightImgBtn = (DrawableCenterTextView) mOptionPanelBottomPpv.findViewById(R.id.light_imgBtn);
        mLightImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.setDayOrNight();
            }
        });
        this.mOptionPanelBottomPpw = new PopupWindow(mOptionPanelBottomPpv, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.mOptionPanelBottomPpw.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        this.mOptionPanelBottomPpw.setFocusable(false);
        this.mOptionPanelBottomPpw.setOutsideTouchable(false);
        this.mOptionPanelBottomPpw.setAnimationStyle(R.style.pop_anim);
    }

    /**
     * 显示底部控制面板
     */
    private void showOptionPanelBottomPpw() {
        mOptionPanelBottomPpw.showAtLocation(mRootViewRl, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
    }

    /**
     * 隐藏底部控制面板
     *
     * @param changeScreenFull : 是否变成了全屏
     */
    private void displayOptionPanelBottomPpw(final boolean changeScreenFull) {
        if (mOptionPanelBottomPpw != null) {
            mOptionPanelBottomPpw.dismiss();
            //当面板底部的动画执行完毕 再关闭导航栏的操作
            Handler mNavigationBarHandler = new Handler();
            mNavigationBarHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //执行操作 关闭导航栏的操作
                    if (changeScreenFull) {
                        setScreenFull(true);
                    }
                }
            }, 500);

        }
    }

    private void setScreenFull(boolean isFull) {
        if (isFull) {
            if (Build.VERSION.SDK_INT < 16) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_IMMERSIVE |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                );

            }
            mIsFullState = true;
        } else {
            if (Build.VERSION.SDK_INT < 16) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            } else {
                // TODO: 2019/4/1  SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION的方式在Android4.4和Android刘海屏出现bug
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            mIsFullState = false;
        }
    }

    /**
     * 初始化控件事件
     */
    private void initViewListener() {
        mReaderWidget.setOnNavigationOperateListener(this);
        mReaderWidget.setOnReadPageCountListener(new OnReadPageCountListener() {
            @Override
            public void onReadPageCount(int count) {
                mPresenter.saveReadPageCount(count);
            }
        });
        mReaderWidget.setOnRestrictListener(new RestrictReadListener() {
            @Override
            public void onRestrictRead() {
                EBookReadLimitActivity.startActivityForResult(EBookReaderActivity.this, 1000);
            }
        });
        mTocMarkVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mSlideMenuRg.check(R.id.ebook_toc_rBtn);
                } else {
                    mSlideMenuRg.check(R.id.ebook_mark_rBtn);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mSlideMenuDl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mSlideMenuDl.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mPresenter.getCurrentToc();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        });

        mSlideMenuRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.ebook_toc_rBtn:
                        mTocMarkVp.setCurrentItem(0);
                        break;
                    case R.id.ebook_mark_rBtn:
                        mTocMarkVp.setCurrentItem(1);
                        break;
                }
            }
        });
    }

    private void initFragments() {
        String[] titles = {"目录", "书签"};
        List<View> fragmentList = new ArrayList<>();
        fragmentList.clear();

        LayoutInflater mInflater = LayoutInflater.from(this);
        View view1 = mInflater.inflate(R.layout.fragment_ebook_toc, null);
        mTocLv = (ListView) view1.findViewById(R.id.toc_lv);

        View view2 = mInflater.inflate(R.layout.fragment_ebook_mark, null);
        mMarkLv = (ListView) view2.findViewById(R.id.mark_lv);
        mBookMarkEmptyTv = (DrawableCenterTextView) view2.findViewById(R.id.book_mark_empty_tv);

        fragmentList.add(view1);
        fragmentList.add(view2);

        SlideMenuPagerAdapter adapterView = new SlideMenuPagerAdapter(fragmentList, titles);
        mTocMarkVp.setAdapter(adapterView);
    }

    /**
     * 修改字体大小及主题
     */
    private void showReaderFontSizePopWindow() {
        hideOperatePanel(false);
        mHandler.sendEmptyMessageDelayed(1002, 500);
    }

    /**
     * 字体主题亮度操作面板
     */
    private void initFontChooserPopUpWindow() {
        View fontSizeView = LayoutInflater.from(this).inflate(R.layout.ppw_textfont_layout, null);
        PopupWindow fontSizePopWindow = new PopupWindow(fontSizeView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        fontSizePopWindow.setBackgroundDrawable(dw);
        fontSizePopWindow.setOutsideTouchable(true);
        fontSizePopWindow.setFocusable(true);
        fontSizePopWindow.setAnimationStyle(R.style.pop_anim);

        fontSizePopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //执行操作 当面板底部的动画执行完毕 再关闭导航栏的操作 避免导航栏跟fontSizePopWindow一起消失 屏幕出现闪动
                Handler mFontSizePopWindowHandler = new Handler();
                mFontSizePopWindowHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //执行操作 关闭导航栏的操作
                        setScreenFull(true);
                    }
                }, 500);
            }
        });

        SeekBar bgLightSeekBar = (SeekBar) fontSizeView.findViewById(R.id.bg_light_seekBar);
        EasyRecyclerView bgSetRv = (EasyRecyclerView) fontSizeView.findViewById(R.id.bg_set_rv);
        Button minusFont = (Button) fontSizeView.findViewById(R.id.font_size_minus_btn);
        Button increaseFont = (Button) fontSizeView.findViewById(R.id.font_size_increase_btn);
        minusFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.setFontMinus(DpPxUtils.dipToPx(EBookReaderActivity.this, 15));
            }
        });
        increaseFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.setFontIncrease(DpPxUtils.dipToPx(EBookReaderActivity.this, 22));
            }
        });

        //设置画廊视图
        if (mReadColorAdapter == null) {
            mReadColorAdapter = new ReadingColorAdapter(this);
        } else {
            mReadColorAdapter.notifyDataSetChanged();
        }

        bgSetRv.setAdapter(mReadColorAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5);
        bgSetRv.setLayoutManager(gridLayoutManager);
        mPresenter.getReadingColor();

        mReadColorAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ReadingColorBean data = mReadColorAdapter.getItem(position);
                if (data != null) {
                    mPresenter.setBgTheme(data);
                    mPresenter.getScreenBrightness();
                }
            }
        });
        mPresenter.getBgTheme();

        //设置亮度
        bgLightSeekBar.setMax(100);
        bgLightSeekBar.setProgress(mBrightness);

        bgLightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPresenter.saveScreenBrightness(progress);
                setScreenBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        fontSizePopWindow.showAtLocation(mReaderWidget, Gravity.BOTTOM, 0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setScreenFull(true);//设置全屏应该在onCreate方法中调用 避免手动点击了关闭全屏 从onPause方法再到onResume方法 无法恢复不是全屏的状态 效果参照掌阅
        //在onPause已经取消了mOptionPanelBottomPpw的动画 所以切换回app的时候应再次恢复动画 否则mOptionPanelBottomPpw退出时就没有动画执行
        Handler mOptionPanelAnimHandler = new Handler();
        mOptionPanelAnimHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mOptionPanelBottomPpw != null && mOptionPanelBottomPpw.isShowing()) {
                    mOptionPanelBottomPpw.setAnimationStyle(R.style.pop_anim);
                    mOptionPanelBottomPpw.update();
                }
            }
        }, 500);
        UmengHelper.setUmengResume(this);


    }

    @Override
    protected void onPause() {
        super.onPause();
        //手动取消mOptionPanelBottomPpw的动画 避免打开控制面板时从onPause到onResume时会再次执行动画 （效果参照掌阅）
        if (mOptionPanelBottomPpw != null && mOptionPanelBottomPpw.isShowing()) {
            mOptionPanelBottomPpw.setAnimationStyle(0);
            mOptionPanelBottomPpw.update();
        }
        UmengHelper.setUmengPause(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000
                && resultCode == RESULT_OK) {
            mReaderWidget.setRestrict(false);
        } else if (requestCode == 1001) {
            //登录成功后获取
            mPresenter.getEBookCollectionStatus();
        } else if (requestCode == 1002 && data != null
                && data.getBooleanExtra("mIsCollection", false)) {
            //分享-收藏或者取消收藏电子书
            if (!mPresenter.isLogin()) {
                LoginActivity.startActivityForResult(this, 1001);
                return;
            }
            if (mIsCollectionLock) {
                return;
            }
            mPresenter.collectionOrCancelEBook();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mPresenter.cancelDownload();
        mPresenter.saveScreenBrightness(mBrightness);
        mPresenter.releaseBookData();
        mPresenter.detachView();
        mPresenter = null;
        mReaderWidget.release();

        System.gc();
        System.gc();
    }

    private void showOperatePanel() {
        mPresenter.getReadProgressInfo();
        mPresenter.getMarkBtnStatus();
        setScreenFull(false);

        mMenuCoverView.setVisibility(View.VISIBLE);

        TranslateAnimation translateTitleBar = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);

        AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);

        showOptionPanelBottomPpw();//底部控制面板显示

        AnimationSet animationTitleBar = new AnimationSet(true);
        animationTitleBar.addAnimation(translateTitleBar);
        animationTitleBar.addAnimation(alpha);
        animationTitleBar.setDuration(500);//动画时间500毫秒
        animationTitleBar.setFillAfter(false);//动画出来控件可以点击
        mTopToolbarRl.setAnimation(animationTitleBar);
        mTopToolbarRl.setVisibility(View.VISIBLE);
    }

    private boolean mIsOperatePanelAnimating;

    private void hideOperatePanel(final boolean changeScreenFull) {
        mMenuCoverView.setVisibility(View.GONE);

        TranslateAnimation translateTitleBar = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f);

        AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);

        displayOptionPanelBottomPpw(changeScreenFull);//底部控制面板隐藏

        AnimationSet animationTitleBar = new AnimationSet(true);
        animationTitleBar.addAnimation(translateTitleBar);
        animationTitleBar.addAnimation(alpha);
        animationTitleBar.setDuration(500);//动画时间500毫秒
        animationTitleBar.setFillAfter(false);//动画出来控件可以点击
        mTopToolbarRl.setAnimation(animationTitleBar);
        mTopToolbarRl.setVisibility(View.GONE);
    }

    private int getNavigationBarHeight() {
        if (!isNavigationBarShow()) {
            return 0;
        }
        Resources resources = this.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    private boolean isNavigationBarShow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(this).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !(menu || back);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            //如果抽屉打开，则关闭抽屉，否则finish Activity
            if (mSlideMenuDl.isDrawerOpen(GravityCompat.START)) {
                mSlideMenuDl.closeDrawer(GravityCompat.START);
            } else {
                mPresenter.saveReadPosition();
                this.finish();
            }
        }
        return true;
    }

    @Override
    public void startParseBooks() {
        mParseProBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void openEpubBooksSuccess() {
        mIsOpenSuccess = true;
        mPresenter.getScreenBrightness();
        mPresenter.getDayOrNightState();
        mPresenter.getBookMarkList();

        mParseProBar.setVisibility(View.GONE);
        mMenuCoverView.setVisibility(View.GONE);
        mLoadingCoverView.setVisibility(View.GONE);

        mReaderWidget.changeDrawCommand();
        mReaderWidget.requestRender();
    }

    @Override
    public void openEpubBooksFailure() {
        mParseProBar.setVisibility(View.GONE);
    }

    @Override
    public void startDownload() {
        mDownloadCbProBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void downLoadProgress(int progress) {
        mDownloadCbProBar.setProgress(progress);
    }

    @Override
    public void downLoadSuccess() {
        mDownloadCbProBar.setVisibility(View.GONE);
    }

    @Override
    public void downLoadFailure() {
        mDownloadCbProBar.setVisibility(View.GONE);

        final CustomDialog callPhoneDialog = new CustomDialog(this, R.style.DialogTheme, getString(R.string.epb_download_failed));
        callPhoneDialog.setCancelable(false);
        callPhoneDialog.hasNoCancel(true);
        callPhoneDialog.show();
        callPhoneDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                callPhoneDialog.dismiss();
                mPresenter.openBook(mBookName, mBookCoverImg, mDownloadUrl, mBookTitle, mBookAuthor, mShareUrl, mShareContent, mBelongLib, mDescContent);
            }

            @Override
            public void onClickCancel() {
                callPhoneDialog.dismiss();
                finish();
            }
        });
    }

    @Override
    public void showBookToc(TOCTree tocList, Typeface typeface) {
        if (mTocListAdapter == null) {
            mTocListAdapter = new EBookTocListAdapter(tocList, typeface);
            mTocLv.setAdapter(mTocListAdapter);
        } else {
            mTocListAdapter.notifyDataSetChanged();
        }
        mTocLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                runTreeItem(mTocListAdapter.getItem(position));
            }
        });
    }

    private boolean runTreeItem(ZLTree<?> tree) {
        if (!tree.hasChildren()) {
            //点击打开指定目录章节
            final TOCTree tocTree = (TOCTree) tree;
            mSlideMenuDl.closeDrawer(GravityCompat.START);
            mPresenter.gotoPosition(tocTree.getParagraphIndex());
            repaint();
        }
        //有子目录打开或关闭目录
        mTocListAdapter.expandOrCollapseTree(tree);
        return true;
    }


    @Override
    public void setReadingColorSet(List<ReadingColorBean> data) {
        mReadColorAdapter.clear();
        mReadColorAdapter.addAll(data);
    }

    @Override
    public void repaint() {
        mReaderWidget.reset();
        mReaderWidget.changeDrawCommand();
        mReaderWidget.requestRender();
    }

    @Override
    public void setSelectedBgTheme(ZLColor color) {
        mReadColorAdapter.setSelected(color);
    }

    @Override
    public void setNoSelectedBgTheme() {
        mReadColorAdapter.setNoSelected();
    }

    @Override
    public void setReadProgressInfo(int max, int progress, String title) {
        mToolbarReadProgressSeekBar.setMax(max);
        mToolbarReadProgressSeekBar.setProgress(progress);
        mToolbarProgressTv.setText(title);
    }

    @Override
    public void showBookMark(List<BookMarkBean> list, Typeface typeface) {
        if (list == null || list.size() == 0) {
            mBookMarkEmptyTv.setVisibility(View.VISIBLE);
            mMarkLv.setVisibility(View.GONE);
        } else {
            mBookMarkEmptyTv.setVisibility(View.GONE);
            mMarkLv.setVisibility(View.VISIBLE);

            if (mMarkAdapter == null) {
                mMarkAdapter = new EBookMarkAdapter(this, list);
                mMarkLv.setAdapter(mMarkAdapter);
            } else {
                mMarkAdapter.notifyDataSetChanged();
            }
            mMarkLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position >= 0) {
                        mPresenter.gotoMarkPosition(position);
                        mSlideMenuDl.closeDrawer(GravityCompat.START);
                        repaint();
                    }
                }
            });
            mMarkLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    mMarkListPosition = position;
                    if (null == mDelMarkPopWindow) {
                        initDeletePopUpWindow();
                    }
                    mDelMarkPopWindow.showAtLocation(mSlideMenuDl, Gravity.BOTTOM, 0, 0);
                    return true;
                }
            });
        }
    }

    @Override
    public void showBookMarkEmpty() {
        mBookMarkEmptyTv.setVisibility(View.VISIBLE);
        mMarkLv.setVisibility(View.GONE);
    }

    @Override
    public void setMarkBtnStatus(boolean includeMark, int markIndex) {
        if (includeMark) {
            mCurrentPageMarkIndex = markIndex;
            Drawable flup = getResources().getDrawable(R.mipmap.ic_mlsq);
            flup.setBounds(0, 0, flup.getMinimumWidth(), flup.getMinimumHeight());
            mAddMarkImgBtn.setCompoundDrawables(null, flup, null, null);
        } else {
            mCurrentPageMarkIndex = markIndex;
            Drawable flup = getResources().getDrawable(R.mipmap.ic_sq);
            flup.setBounds(0, 0, flup.getMinimumWidth(), flup.getMinimumHeight());
            mAddMarkImgBtn.setCompoundDrawables(null, flup, null, null);
        }
    }

    @Override
    public void setCurrentToc(TOCTree tree) {
        mTocListAdapter.notifyDataSetChanged();
        int position = mTocListAdapter.selectItem(tree);
        if (position < mTocLv.getFirstVisiblePosition() || position > mTocLv.getLastVisiblePosition()) {
            mTocLv.setSelection(position);// 设置当前位置
        }
    }

    @Override
    public void showDayOrNight(boolean isDay) {
        if (isDay) {
            Drawable flup = getResources().getDrawable(R.mipmap.ic_yj);
            flup.setBounds(0, 0, flup.getMinimumWidth(), flup.getMinimumHeight());
            mLightImgBtn.setCompoundDrawables(null, flup, null, null);
            mPresenter.getScreenBrightness();
            mLightImgBtn.setText("夜间");
        } else {
            Drawable flup = getResources().getDrawable(R.mipmap.ic_rzsmall);
            flup.setBounds(0, 0, flup.getMinimumWidth(), flup.getMinimumHeight());
            mLightImgBtn.setCompoundDrawables(null, flup, null, null);
            setScreenBrightness(30);
            mLightImgBtn.setText("白天");
        }
    }

    @Override
    public void setScreenBrightness(int level) {
        if (level == 0) {
            final float levelSys = getWindow().getAttributes().screenBrightness;
            mBrightness = 25 + (int) ((levelSys >= 0 ? level : .5f - .01f) * 75 / .99f);
        } else {
            mBrightness = level;
        }

        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.screenBrightness = mBrightness / 100.0f;
        getWindow().setAttributes(attrs);
    }

    @Override
    public void needLoginTip(boolean showActivity) {
        mReaderWidget.setRestrict(true);
        if (showActivity) {
            EBookReadLimitActivity.startActivityForResult(EBookReaderActivity.this, 1000);
        }

    }

    @Override
    public void pleaseLogin() {
        LoginActivity.startActivityForResult(this, 1001);
    }

    @Override
    public void collectEBookSuccess(boolean collection) {
        mIsCollection = collection;
        if (collection) {
            ToastUtils.showSingleToast(R.string.collect_video_success);
        } else {
            ToastUtils.showSingleToast(R.string.cancel_collect_video);
        }
    }

    /**
     * 获取电子书收藏状态
     *
     * @param isCollectedEBook 是否收藏状态
     */
    @Override
    public void setEBookCollectionStatus(boolean isCollectedEBook) {
        mIsCollectionLock = false;
        mIsCollection = isCollectedEBook;

    }

    @Override
    public void operateNavigation() {
        if (!mIsOpenSuccess) {
            return;
        }
        mHandler.removeMessages(1000);
        mIsAutoHide = !mIsFullState;
        if (!mIsOperatePanelAnimating) {
            showOperatePanel();
        }
    }


    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                if (mPresenter != null) {
                    mPresenter.setBattery(intent.getIntExtra("level", 0));
                }
            }
        }
    }


    /**
     * 初始化删除界面
     */
    private void initDeletePopUpWindow() {
        View bookMarkView = LayoutInflater.from(this).inflate(R.layout.ppw_del_mark, null);
        mDelMarkPopWindow = new PopupWindow(bookMarkView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mDelMarkPopWindow.setBackgroundDrawable(new BitmapDrawable());
        mDelMarkPopWindow.setOutsideTouchable(true);
        TextView jumpMark = (TextView) bookMarkView.findViewById(R.id.jump_mark_btn);
        jumpMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMarkListPosition >= 0) {
                    mPresenter.gotoMarkPosition(mMarkListPosition);
                    mSlideMenuDl.closeDrawer(GravityCompat.START);
                    repaint();
                }
                mMarkListPosition = 0;
                if (mDelMarkPopWindow != null && mDelMarkPopWindow.isShowing()) {
                    mDelMarkPopWindow.dismiss();
                }
            }
        });
        TextView delMark = (TextView) bookMarkView.findViewById(R.id.del_mark_btn);
        delMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMarkAdapter.getItem(mMarkListPosition);
                mPresenter.delBookMark(((BookMarkBean) mMarkAdapter.getItem(mMarkListPosition)).getParagraphIndex());
                mMarkListPosition = 0;
                if (mDelMarkPopWindow != null && mDelMarkPopWindow.isShowing()) {
                    mDelMarkPopWindow.dismiss();
                }
            }
        });
        View shadow = bookMarkView.findViewById(R.id.shadow_view);
        shadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDelMarkPopWindow != null && mDelMarkPopWindow.isShowing()) {
                    mDelMarkPopWindow.dismiss();
                }
            }
        });
    }

    @Override
    public void finish() {
        if (!mIsCollectionLock) {
            Intent intent = new Intent();
            intent.putExtra("readCollectStatus", mIsCollection ? 1 : 2);
            setResult(RESULT_OK, intent);
        }
        super.finish();
    }
}
