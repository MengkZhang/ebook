package com.tzpt.cloudlibrary.ui.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.bean.NetWordMsg;
import com.tzpt.cloudlibrary.bean.ShareBean;
import com.tzpt.cloudlibrary.bean.TabMenuBean;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.bean.VideoTOCTree;
import com.tzpt.cloudlibrary.rxbus.event.EventRotationMsg;
import com.tzpt.cloudlibrary.rxbus.event.HeadsetEvent;
import com.tzpt.cloudlibrary.rxbus.event.VideoEvent;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.LoginActivity;
import com.tzpt.cloudlibrary.ui.library.LibraryDetailActivity;
import com.tzpt.cloudlibrary.ui.library.MyPagerAdapter;
import com.tzpt.cloudlibrary.ui.share.LandScapeShareActivity;
import com.tzpt.cloudlibrary.ui.share.ShareActivity;
import com.tzpt.cloudlibrary.utils.CheckInternetUtil;
import com.tzpt.cloudlibrary.utils.DisplayUtils;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.utils.StatusBarUtil;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;
import com.tzpt.cloudlibrary.widget.tablayout.RecyclerTabLayout;
import com.tzpt.cloudlibrary.widget.titlebar.TitleBarView;
import com.tzpt.cloudlibrary.widget.video.CLVideoRotationObserver;
import com.tzpt.cloudlibrary.widget.video.CLVideoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.functions.Action1;

/**
 * 视频详情
 */
public class VideoDetailActivity extends AppCompatActivity implements
        VideoDetailContract.View {

    private static final int LOGIN_REQUEST_CODE = 1000;
    private static final int COLLECTION_REQUEST_CODE = 1001;
    private static final int SHARE_REQUEST_CODE = 1002;
    public static final String VIDEO_ID = "video_id";
    public static final String VIDEO_TITLE = "video_title";
    public static final String VIDEO_LIBCODE = "video_lib_code";
    private static final int VIDEO_RE_PLAY = 1000;
    private static final int VIDEO_SHOW_NOTICE_DIALOG = 1001;
    private static final int HAND_STOP_VIDEO = 1002;        //手动暂停视频

    public static void startActivity(Context context, long videoId, String videoTitle, String libCode) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        intent.putExtra(VIDEO_ID, videoId);
        intent.putExtra(VIDEO_TITLE, videoTitle);
        intent.putExtra(VIDEO_LIBCODE, libCode);
        context.startActivity(intent);
    }

    Unbinder unbinder;
    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.status_bar_v)
    View mStatusBarV;
    @BindView(R.id.common_toolbar)
    TitleBarView mCommonToolbar;
    @BindView(R.id.cl_video_view)
    CLVideoView mCLVideoView;
    @BindView(R.id.recycler_tab_layout)
    RecyclerTabLayout mRecyclerTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.video_detail_library_name_tv)
    TextView mVideoDetailLibraryNameTv;

    private MyPagerAdapter mPagerAdapter;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private VideoDetailPresenter mPresenter;
    private VideoCatalogPresenter mVideoCatalogPresenter;
    private CustomDialog mCustomDialog;
    private boolean mIsLock = false;
    private boolean mIsToShare = false;
    private boolean mIsCollectionVideoLock = true;
    private boolean mIsCollectionVideo = false;
    private String mVideoTitle;
    private String mShareUrl;
    private String mVideoImage;
    private long mSetId;
    private int mOrientation;                       //屏幕方向
    private boolean mIsInited = false;
    private boolean mIsAllow4GPlayVideo = false;    //是否提示过流量播放
    private boolean mIsPlayLocalVideo = false;      //是否播放本地视频
    private String mLibCode;
    private String mLibName;

    private CLVideoRotationObserver mObserver;
    private OrientationEventListener mOrientationEventListener = null;
    private boolean mIsRotationEnable = false;      //当前是否可旋转

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VIDEO_RE_PLAY:
                    mCLVideoView.rePlay();
                    break;
                case VIDEO_SHOW_NOTICE_DIALOG:
                    if (null != mCustomDialog && mCustomDialog.isShowing()) {
                        return;
                    }
                    setMobileNetTips(null, 0);
                    break;
                case HAND_STOP_VIDEO://暂停视频播放
                    mCLVideoView.stopPlay();
                    break;
            }
        }
    };
    //video controller
    private CLVideoView.VideoControllerListener mVideoControllerListener = new CLVideoView.VideoControllerListener() {
        @Override
        public void onFinishClick() {
            dealBackClick();
        }

        @Override
        public void onLockClick(boolean isLock) {
            mIsLock = isLock;
            if (!isLock) {
                setFullScreen(false);
            }
        }

        @Override
        public void onChangeScreenClick() {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        @Override
        public void onMoreClick() {
            mIsToShare = true;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ShareBean shareBean = new ShareBean();
                shareBean.shareTitle = mVideoTitle;
                shareBean.shareContent = mVideoTitle;
                shareBean.shareUrl = mShareUrl;
                shareBean.shareUrlForWX = mShareUrl;
                shareBean.shareImagePath = mVideoImage;

                shareBean.mNeedDownload = true;
                shareBean.mNeedCopy = true;
                shareBean.mNeedCollection = true;
                shareBean.mIsCollection = mIsCollectionVideo;
                LandScapeShareActivity.startActivityForResult(VideoDetailActivity.this, shareBean, SHARE_REQUEST_CODE);
            }
        }

        @Override
        public void changeBrightness(float brightness) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
            if (lp.screenBrightness > 1) {
                lp.screenBrightness = 1;
            } else if (lp.screenBrightness < 0.01) {
                lp.screenBrightness = (float) 0.01;
            }
            getWindow().setAttributes(lp);
            mCLVideoView.setBrightness((int) (lp.screenBrightness * 100));
        }

        @Override
        public void setFullScreen(boolean screenFull) {
            setScreenFull(mIsLock || screenFull);
        }

        //TODO 处理播放下一集
        @Override
        public void playComplete() {
            mPresenter.playNextVideo();
        }

        @Override
        public void show4GNoticeDialog() {
            if (!mIsPlayLocalVideo) {
                mHandler.sendEmptyMessage(VIDEO_SHOW_NOTICE_DIALOG);
            }
        }

        @Override
        public void retryGetVideoRealUrlAndPlay() {
            mPresenter.retryToGetVideoRealUrl();
        }

        @Override
        public void synchronizationPlayTime(int playTime) {
            mPresenter.synchronizationPlayTime(playTime);
        }

        @Override
        public void isHandlePause() {
            mHandler.sendEmptyMessageDelayed(HAND_STOP_VIDEO, 500);
        }
    };

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_btn, R.id.video_detail_library_name_tv})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_btn:
                mIsToShare = true;
                ShareBean shareBean = new ShareBean();
                shareBean.shareTitle = mVideoTitle;
                shareBean.shareContent = mVideoTitle;
                shareBean.shareUrl = mShareUrl;
                shareBean.shareUrlForWX = mShareUrl;
                shareBean.shareImagePath = mVideoImage;

                shareBean.mNeedDownload = true;
                shareBean.mNeedCopy = true;
                shareBean.mNeedCollection = true;
                shareBean.mIsCollection = mIsCollectionVideo;
                ShareActivity.startActivityForResult(this, shareBean, SHARE_REQUEST_CODE);
                break;
            case R.id.video_detail_library_name_tv:
                if (!TextUtils.isEmpty(mLibCode) && !TextUtils.isEmpty(mLibName)) {
                    LibraryDetailActivity.startActivity(this, mLibCode, mLibName);
                }
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video);
        unbinder = ButterKnife.bind(this);

        initDatas();
        configViews();
    }


    private void initDatas() {
        registerHeadsetReceiver();
        EventBus.getDefault().register(this);
        mPresenter = new VideoDetailPresenter(CloudLibraryApplication.mRxBus);
        mPresenter.attachView(this);

        Intent intent = getIntent();
        mLibCode = intent.getStringExtra(VIDEO_LIBCODE);
        long videosId = intent.getLongExtra(VIDEO_ID, 0);

        mSetId = videosId;
        mPresenter.setVideosBelongLibCode(mLibCode);
        mPresenter.setVideosId(videosId);
        mPresenter.getVideoDetail();
        mPresenter.getVideoCollectionStatus();
    }

    /**
     * 初始化菜单
     */
    private void configViews() {
        configPortrait();
        mCommonToolbar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonToolbar.setRightBtnIcon(R.drawable.bg_btn_share);
        mCommonToolbar.setTitle("视频详情");
        mCommonToolbar.setRightBtnClickAble(false);
        //获取标题栏状态栏高度
        int statusBarHeight = DisplayUtils.getStatusBarHeight(this);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mStatusBarV.getLayoutParams();
        layoutParams.height = statusBarHeight;
        mStatusBarV.setLayoutParams(layoutParams);
        mStatusBarV.setBackgroundColor(getResources().getColor(R.color.color_ffffff));

        setScreenFull(false);
        //监听视频旋转
        mOrientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int rotation) {
                if (!mIsRotationEnable) {//旋转开关未打开
                    return;
                }
                //如果锁屏，则不支持旋转
                if (!mIsInited || mIsLock || rotation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    return;
                }
                int orientation = convert2Orientation(rotation);
                // 方向没有变化,跳过
                if (orientation == mOrientation) {
                    return;
                }
                mOrientation = orientation;
                setRequestedOrientation(mOrientation);
            }
        };
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        } else {
            mOrientationEventListener.disable();
        }
        mObserver = new CLVideoRotationObserver(mHandler, this);
    }

    /**
     * 计算设置视频旋转方向
     *
     * @param rotation 旋转值
     * @return
     */
    private int convert2Orientation(int rotation) {
        //如果当前是竖屏
        if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            if ((rotation <= 300 && rotation >= 240)) {
                //横屏
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            } else if (rotation >= 60 && rotation <= 120) {
                //反横屏
                return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            }
        }//如果当前是横屏
        else if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            if ((rotation >= 330 && rotation <= 360) || (rotation >= 180 && rotation <= 210)) {
                //竖屏
                return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            } else if (rotation >= 75 && rotation <= 105) {
                //反横屏
                return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            }
        }//如果当前是反横屏
        else if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            if ((rotation <= 180 && rotation >= 150) || (rotation <= 30 && rotation >= 0)) {
                //竖屏
                return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            } else if (rotation <= 285 && rotation >= 255) {
                //横屏
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            }
        }
        return mOrientation;
    }

    /**
     * 配置横竖屏
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mIsLock) {
            return;
        }
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            configLandscape();
            if (mCLVideoView.isShowToolBar()) {
                setScreenFull(false);
            } else {
                setScreenFull(true);
            }
        } else {
            //竖屏
            configPortrait();
            setScreenFull(false);
        }
    }

    /**
     * 设置横屏
     */
    private void configLandscape() {
        StatusBarUtil.setStatusBarColor(this, R.color.color_translate);
        mStatusBarV.setVisibility(View.GONE);
        mCommonToolbar.setVisibility(View.GONE);
        mCLVideoView.setCurrentOrientation(Configuration.ORIENTATION_LANDSCAPE, false);
        float widthPixels = DpPxUtils.getRealWidthPixels(this);
        float heightPixels = DpPxUtils.getHeightPixels();
        mCLVideoView.getLayoutParams().width = (int) widthPixels;
        mCLVideoView.getLayoutParams().height = (int) heightPixels;

        mCLVideoView.setSpaceLayoutParams((int) widthPixels - DpPxUtils.getWidthPixels());

    }

    /**
     * 设置竖屏
     */
    private void configPortrait() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            //针对5.0-5.1状态设置为半透明主题颜色，在白色背景下可以看清楚状态栏文字
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                StatusBarUtil.setStatusBarColor(this, R.color.color_half000000);
            } else {
                StatusBarUtil.setStatusBarColor(this, R.color.color_ffffff);
            }
        } else {
            StatusBarUtil.setStatusBarColor(this, R.color.color_translate);
        }
        mStatusBarV.setVisibility(View.VISIBLE);
        mCommonToolbar.setVisibility(View.VISIBLE);
        mCLVideoView.setCurrentOrientation(Configuration.ORIENTATION_PORTRAIT, false);
        float widthPixels = DpPxUtils.getWidthPixels();
        float heightPixels = DpPxUtils.dp2px(165.f);
        mCLVideoView.getLayoutParams().width = (int) widthPixels;
        mCLVideoView.getLayoutParams().height = (int) heightPixels;

        mCLVideoView.setSpaceLayoutParams(0);

    }

    /**
     * 设置全屏
     *
     * @param isFull 是否全屏
     */
    private void setScreenFull(boolean isFull) {
        if (isFull) {
            if (Build.VERSION.SDK_INT < 16) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                    getWindow().getDecorView().setSystemUiVisibility(uiOptions);
                }
            }
        } else {
            if (Build.VERSION.SDK_INT < 16) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            } else {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
                } else {
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
                }
            }
        }
    }

    /**
     * 返回点击处理，切换为竖屏，然后退出
     */
    private void dealBackClick() {
        if (mIsLock) {
            return;
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            dealBackClick();
        }//音量减小
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
            mCLVideoView.setVolumePercent(-0.005f, false, true);
        }//音量增大
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
            mCLVideoView.setVolumePercent(0.005f, true, true);
        }
        return true;
    }

    /**
     * 接收网络状态改变信息，对不同网络状态下的处理
     *
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void reciveNetInfo(NetWordMsg msg) {
        if (!mIsInited || mIsPlayLocalVideo) { //如果初始化未完成，则不执行播放任务,本地视频不影响当前播放
            return;
        }

        mCLVideoView.setNetWorkState(msg.mNetWorkState);
        switch (msg.mNetWorkState) {
            case CheckInternetUtil.NET_WORK_MOBILE:
                //如果允许4G播放，则直接开始播放
                if (mIsAllow4GPlayVideo) {
                    mHandler.sendEmptyMessage(VIDEO_RE_PLAY);
                    return;
                }
                mHandler.sendEmptyMessage(VIDEO_SHOW_NOTICE_DIALOG);
                break;
            case CheckInternetUtil.NET_WORK_WIFI:
                //如果不是流量播放，则取消显示
                if (null != mCustomDialog && mCustomDialog.isShowing()) {
                    mCustomDialog.dismiss();
                }
                mHandler.sendEmptyMessage(VIDEO_RE_PLAY);
                break;
            case CheckInternetUtil.NET_WORK_NONE:
                mCLVideoView.setNetErrorUI();
                break;
        }
    }

    /**
     * 提示流量是否播放
     */
    private void setMobileNetTips(final String videoUrl, final long videoPlayedTime) {
        mCLVideoView.stopPlay();
        mCustomDialog = new CustomDialog(this, R.style.DialogTheme, "");
        mCustomDialog.setCancelable(false);
        mCustomDialog.hasNoCancel(true);
        mCustomDialog.setOkText("继续播放");
        mCustomDialog.setCancelText("暂不播放");
        mCustomDialog.setText("当前非WIFI环境，播放将产生\n" +
                "流量费用，是否继续？");
        mCustomDialog.show();
        mCustomDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                mCustomDialog.dismiss();
                mCLVideoView.setAllow4GPlayVideo(true);
                mIsAllow4GPlayVideo = true;//已经提示流量播放，下一次直接播放
                if (null != videoUrl) {
                    playVideo(videoUrl, videoPlayedTime);
                } else {
                    mHandler.sendEmptyMessage(VIDEO_RE_PLAY);
                }
            }

            @Override
            public void onClickCancel() {
                mCustomDialog.dismiss();
            }
        });
    }

    @Override
    public void showLoadingDialog() {
        mMultiStateLayout.showProgress();
    }

    @Override
    public void setContentView() {
        mMultiStateLayout.showContentView();
    }

    /**
     * 设置视频详情信息
     *
     * @param videoSetBean  详情信息
     * @param videoTOCTrees 视频目录
     */
    @Override
    public void setVideoDetail(VideoSetBean videoSetBean, List<VideoTOCTree> videoTOCTrees) {
        final List<TabMenuBean> tabList = new ArrayList<>();
        TabMenuBean menuHot = new TabMenuBean("详情");
        TabMenuBean menuNew = new TabMenuBean("目录");
        tabList.add(menuHot);
        tabList.add(menuNew);
        if (mPagerAdapter == null) {
            mFragmentList.clear();
            //设置视频详情信息
            Intent intent = getIntent();
            mVideoTitle = intent.getStringExtra(VIDEO_TITLE);
            mCLVideoView.setTitle(mVideoTitle);
            //详情
            VideoDetailFragment detailFragment = new VideoDetailFragment();
            CLVideoDetailPresenter presenter = new CLVideoDetailPresenter(detailFragment);
            presenter.setVideoDetail(videoSetBean);
            //目录
            VideoCatalogFragment catalogFragment = new VideoCatalogFragment();
            mVideoCatalogPresenter = new VideoCatalogPresenter(catalogFragment);
            mVideoCatalogPresenter.setVideosCatalogList(videoTOCTrees);

            mFragmentList.add(detailFragment);
            mFragmentList.add(catalogFragment);

            mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), mFragmentList, tabList);
            mViewPager.setAdapter(mPagerAdapter);
            mViewPager.setOffscreenPageLimit(2);
            mRecyclerTabLayout.setUpWithViewPager(tabList, mViewPager);
        } else {
            mPagerAdapter.notifyDataSetChanged();
        }
        //设置分享链接
        if (!TextUtils.isEmpty(videoSetBean.getShareUrl())) {
            this.mShareUrl = videoSetBean.getShareUrl();
        }
        //分享分享图片
        if (!TextUtils.isEmpty(videoSetBean.getCoverImg())) {
            mVideoImage = videoSetBean.getCoverImg();
            mCLVideoView.setVideoBackground(mVideoImage);
        }
        mIsInited = true;
        //启动分享按钮
        mCommonToolbar.setRightBtnClickAble(true);
    }

    /**
     * 设置视频所属馆
     *
     * @param libCode 馆号
     * @param libName 馆名
     */
    @Override
    public void showBelongLibrary(String libCode, String libName) {
        this.mLibCode = libCode;
        this.mLibName = libName;
        mVideoDetailLibraryNameTv.setVisibility(View.VISIBLE);
        String libInfo = mLibCode + "  " + mLibName;
        mVideoDetailLibraryNameTv.setText(libInfo);
    }

    /**
     * 隐藏所属馆
     */
    @Override
    public void hideBelongLibrary() {
        mVideoDetailLibraryNameTv.setVisibility(View.GONE);
    }

    /**
     * 出现网络故障情况
     */
    @Override
    public void setNetError() {
        mMultiStateLayout.showError();
        mMultiStateLayout.showRetryError(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getVideoDetail();
            }
        });
    }

    /**
     * 没有视频目录无法播放
     */
    @Override
    public void setVideoDetailEmptyList() {
        mCLVideoView.stopPlay();
        mMultiStateLayout.showEmpty();
    }

    /**
     * 收藏或者取消收藏成功 的处理
     *
     * @param collection
     */
    @Override
    public void collectionVideoSuccess(boolean collection) {
        mIsCollectionVideo = collection;
        if (collection) {
            ToastUtils.showSingleToast(R.string.collect_video_success);
        } else {
            ToastUtils.showSingleToast(R.string.cancel_collect_video);
        }
    }

    @Override
    public void finish() {
        if (!mIsCollectionVideo) {
            Intent data = new Intent();
            data.putExtra(VIDEO_ID, mSetId);
            setResult(RESULT_OK, data);
        }
        super.finish();
    }

    /**
     * 设置收藏状态
     *
     * @param saved
     */
    @Override
    public void setVideoCollectionStatus(boolean saved) {
        mIsCollectionVideoLock = false;
        mIsCollectionVideo = saved;
    }

    @Override
    public void showErrorMsg(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }

    /**
     * 登录被踢下线的提示
     */
    @Override
    public void showNoLoginDialog() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = false;
        EventBus.getDefault().post(accountMessage);

        final CustomDialog dialog = new CustomDialog(this, R.style.DialogTheme, getString(R.string.account_login_other_device));
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.setLoginAnewBtn();
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                LoginActivity.startActivity(VideoDetailActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    /**
     * 播放视频
     *
     * @param videoUrl 视频地址
     */
    @Override
    public void playVideoByPlayId(final String videoUrl, final String localVideoPath, final long videoPlayedTime) {
        //播放本地视频
        if (!TextUtils.isEmpty(localVideoPath) && new File(localVideoPath).exists()) {
            mIsPlayLocalVideo = true;
            mCLVideoView.setLocalVideo(true);
            mCLVideoView.setVideoUrl(localVideoPath);
            playVideo(localVideoPath, videoPlayedTime);
        } else {
            //播放网络视频
            mIsPlayLocalVideo = false;
            mCLVideoView.setLocalVideo(false);
            mCLVideoView.setVideoUrl(videoUrl);
            int netStatus = CheckInternetUtil.checkNetWork(this, false);
            mCLVideoView.setNetWorkState(netStatus);
            switch (netStatus) {
                case CheckInternetUtil.NET_WORK_MOBILE:
                    //如果已经提示，则不再提示，自动播放下一集
                    if (mIsAllow4GPlayVideo) {
                        playVideo(videoUrl, videoPlayedTime);
                        return;
                    }
                    if (null != mCustomDialog && mCustomDialog.isShowing()) {
                        return;
                    }
                    setMobileNetTips(videoUrl, videoPlayedTime);
                    break;
                case CheckInternetUtil.NET_WORK_WIFI:
                    //如果不是流量播放，则取消显示
                    if (null != mCustomDialog && mCustomDialog.isShowing()) {
                        mCustomDialog.dismiss();
                    }
                    playVideo(videoUrl, videoPlayedTime);
                    break;
                case CheckInternetUtil.NET_WORK_NONE:
                    mCLVideoView.setNetErrorUI();
                    break;
            }
        }
    }

    /**
     * 设置播放第一个视频
     *
     * @param videoUrl
     */
    @Override
    public void playFirstVideo(String videoUrl, final String localVideoPath, final long videoPlayedTime) {
        mPresenter.checkUrlAndPlayVideo(videoUrl, localVideoPath, videoPlayedTime);
        mCLVideoView.setVideoControllerListener(mVideoControllerListener);
    }

    /**
     * 通知视频目录更新选中列表
     *
     * @param sectionId 视频小节ID
     */
    @Override
    public void updateCatalogPlayInfo(long sectionId) {
        mVideoCatalogPresenter.updateCatalogChooseSectionId(sectionId);
    }

    /**
     * 播放了最后一集，显示重播
     */
    @Override
    public void playLastVideoComplete() {
        mCLVideoView.setLastVideoReplayUI();
    }

    @Override
    public void playVideoError() {
        mCLVideoView.setNetErrorUI();
    }

    /**
     * 播放视频
     *
     * @param videoUrl 视频地址
     */
    private void playVideo(String videoUrl, long videoPlayedTime) {
        if (mIsPlayLocalVideo) {
            mCLVideoView.showDownloadTips();
        }
        mCLVideoView.playByTime(videoPlayedTime);
        mCLVideoView.setVideoUrl(videoUrl);
        mCLVideoView.startVideo();
        mPresenter.recordWatchVideo(mLibCode);
    }

    /**
     * 接收手机旋转状态
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveRotationStatus(EventRotationMsg msg) {
        mIsRotationEnable = msg.isRotationEnable();
    }

    /**
     * 检查网络，判断是否分享的处理
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerRxBus();
        mObserver.startObserver();
        mObserver.onChange(false);
        UmengHelper.setUmengResume(this);
        if (mIsToShare) {
            mIsToShare = false;
        } else {
            int netStatus = CheckInternetUtil.checkNetWork(this, false);
            mCLVideoView.setNetWorkState(netStatus);
            if (netStatus != CheckInternetUtil.NET_WORK_MOBILE || mIsAllow4GPlayVideo) {
                //mCLVideoView.reStartPlay();   //重新设置链接播放
                mCLVideoView.rePlay();          //暂停后的播放
            }
        }

    }

    /**
     * 订阅RxBus
     */
    private void registerRxBus() {
        //接收播放视频信息
        mPresenter.registerRxBus(VideoEvent.class, new Action1<VideoEvent>() {
            @Override
            public void call(VideoEvent videoEvent) {
                if (videoEvent.getMsgType() == 1) {
                    //如果是同一个视频则返回
                    if (mPresenter.isSameVideoByVideoId(videoEvent.getVideoPlayId())) {
                        return;
                    }
                    mPresenter.setPlayVideoIndex(videoEvent.getVideoPlayId());
                    String localPath = mPresenter.getLocalVideoPathByTempVideoList();
                    mPresenter.checkUrlAndPlayVideo(videoEvent.getVideoPlayUrl(), localPath, 0);
                }
            }
        });
        //监听耳机拔插广播
        mPresenter.registerRxBus(HeadsetEvent.class, new Action1<HeadsetEvent>() {
            @Override
            public void call(HeadsetEvent headsetEvent) {
                //接入耳机观看，断开后，视频暂停，再次接入耳机时，视频自动播放
                if (headsetEvent.mIsInsert) {
                    mCLVideoView.rePlay();
                } else {
                    mCLVideoView.pause();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK
                && requestCode == LOGIN_REQUEST_CODE) {
            VideoDownloadChooseActivity.startActivity(this, mSetId);
        } else if (resultCode == RESULT_OK
                && requestCode == COLLECTION_REQUEST_CODE) {
            mPresenter.collectionVideo();
        } else if (requestCode == SHARE_REQUEST_CODE && data != null) {
            //下载视频
            boolean isDownLoad = data.getBooleanExtra("mIsDownLoad", false);
            if (isDownLoad) {
                mCLVideoView.hideControls();
                if (!mPresenter.isLogin()) {
                    LoginActivity.startActivityForResult(VideoDetailActivity.this, LOGIN_REQUEST_CODE);
                    return;
                }
                mIsToShare = false;
                VideoDownloadChooseActivity.startActivity(this, mSetId);
            }

            //收藏，取消视频
            boolean isCollection = data.getBooleanExtra("mIsCollection", false);
            if (isCollection) {
                mCLVideoView.hideControls();
                if (!mPresenter.isLogin()) {
                    mIsToShare = false;
                    mCLVideoView.pause();
                    LoginActivity.startActivityForResult(VideoDetailActivity.this, COLLECTION_REQUEST_CODE);
                    return;
                }
                if (!mIsCollectionVideoLock) {
                    mPresenter.collectionOrCancelVideo();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mObserver.stopObserver();
        mPresenter.saveVideoPlayedTime(mCLVideoView.getCurrentPosition(), mCLVideoView.getDuration());
        UmengHelper.setUmengPause(this);
        if (!mIsToShare) {
            mCLVideoView.pause();
        }
        mPresenter.unregisterRxBus();
    }

    /**
     * 释放资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mReceiver) {
            unregisterReceiver(mReceiver);
        }
        mHandler.removeMessages(VIDEO_RE_PLAY);
        mHandler.removeMessages(VIDEO_SHOW_NOTICE_DIALOG);
        mHandler.removeMessages(HAND_STOP_VIDEO);
        if (null != mOrientationEventListener) {
            mOrientationEventListener.disable();
        }
        mFragmentList.clear();
        EventBus.getDefault().unregister(this);
        mRecyclerTabLayout.clearList();
        mCLVideoView.releaseVideoView();
        mPresenter.clearVideoTempData();
        mPresenter.detachView();
        unbinder.unbind();
    }

    /**
     * 处理内存泄露问题，使用application
     * VideoView内部的AudioManager会对Activity持有一个强引用，
     * 而AudioManager的生命周期比较长，导致这个Activity始终无法被回收
     *
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ContextWrapper(newBase) {
            @Override
            public Object getSystemService(String name) {
                if (Context.AUDIO_SERVICE.equals(name))
                    return getApplicationContext().getSystemService(name);
                return super.getSystemService(name);
            }
        });
    }

    //拔插耳机操作视频播放或暂停
    private HeadsetReceiver mReceiver;

    //注册耳机操作广播
    private void registerHeadsetReceiver() {
        mReceiver = new HeadsetReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mReceiver, intentFilter);
    }
}
