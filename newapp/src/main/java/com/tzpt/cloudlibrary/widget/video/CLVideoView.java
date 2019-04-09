package com.tzpt.cloudlibrary.widget.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.utils.CheckInternetUtil;
import com.tzpt.cloudlibrary.utils.DpPxUtils;

/**
 * 视频播放器
 * Created by tonyjia on 2018/6/8.
 */
public class CLVideoView extends FrameLayout implements
        GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private static final int NONE = 0;
    private static final int BRIGHTNESS = 1;
    private static final int VOLUME = 2;
    private static final int FAST_BACKWARD = 3;
    private static final int HIDE_CONTROLLS = 1000;
    private static final int SHOW_PROGRESS = 1001;
    private static final int HIDE_PROGRESS = 1002;
    private static final int VIDEO_PLAY = 1003;
    private static final int VIDEO_PAUSE = 1004;
    private static final int HIDE_VIDEO_BG = 1005;

    private CLVideoViewController mControllerView;
    private VideoView mVideoView;
    private GestureDetector mDetector;
    private boolean mIsTouching = false;
    private boolean mIsLock = false;
    private boolean mIsPrepared = false;
    private boolean mIsComplete = false;
    private boolean mIsAllow4GPlayVideo = false;
    private boolean mShowDelIcon = false;
    private boolean mInited = false;
    private int mCurrentOrientation = Configuration.ORIENTATION_PORTRAIT;
    private int mNetWorkState = CheckInternetUtil.NET_WORK_WIFI;
    private int mVideoHalfWidth;
    private int mVideoState = NONE;
    private int mCurrentPosition = -1;//毫秒
    private String mVideoPath;
    private boolean mIsHandPause = false;
    private boolean mLandNotNeedToolbar; //横屏下是否显示顶部toolbar
    private boolean mIsLocalVideo = false;

    public CLVideoView(Context context) {
        this(context, null);
    }

    public CLVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CLVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HIDE_CONTROLLS:
                    if (!mIsTouching) {
                        hideControls();
                    }
                    break;
                case SHOW_PROGRESS:
                    mControllerView.controllProgress(true);
                    break;
                case HIDE_PROGRESS:
                    mControllerView.controllProgress(false);
                    mControllerView.setVideoBlackBackground(false);
                    break;
                case VIDEO_PLAY:
                    play();
                    break;
                case VIDEO_PAUSE:
                    pause();
                    break;
                case HIDE_VIDEO_BG:
                    mControllerView.hideVideoBackground();
                    break;
            }
        }
    };

    /**
     * 进度更新
     */
    private final Runnable mShowProgressRunnable = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (mVideoView.isPlaying()) {
                postDelayed(mShowProgressRunnable, 1000 - (pos % 1000));
            }
        }
    };

    /**
     * 播放器准备就绪
     */
    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mIsPrepared = true;
            mHandler.sendEmptyMessage(HIDE_PROGRESS);
            mHandler.sendEmptyMessage(HIDE_VIDEO_BG);
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROLLS, 5000);

            //执行获取进度方法
            post(mShowProgressRunnable);

            if (mCurrentPosition > 0 && mVideoView.getDuration() > 0) {
                mVideoView.seekTo(mCurrentPosition);
                mCurrentPosition = -1;
            }
            mIsComplete = false;
            mVideoView.start();
            mControllerView.setEnable(true);
            mControllerView.setLockEnable(false);
            //设置进度是否可用
            mControllerView.setSeekBarEnabled((mVideoView.getDuration() > 0));
        }
    };
    /**
     * 播放器回调信息
     */
    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    mHandler.sendEmptyMessage(SHOW_PROGRESS);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    mHandler.sendEmptyMessage(HIDE_PROGRESS);
                    mHandler.sendEmptyMessage(HIDE_VIDEO_BG);
                    break;
            }
            return false;
        }
    };
    /**
     * 错误回调监听
     */
    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            setNetErrorUI();
            if (mIsPrepared && !mVideoView.isPlaying()) {
                stopPlayback();
                mHandler.sendEmptyMessage(SHOW_PROGRESS);
                if (null != mListener) {
                    mListener.retryGetVideoRealUrlAndPlay();
                }
            }
            return true;
        }
    };
    /**
     * 完成播放监听
     */
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //恢复进度为0
            mIsComplete = true;
            if (null != mListener) {
                mListener.playComplete();
                mCurrentPosition = 0;
            }
        }
    };

    /**
     * 点击重试
     */
    private OnClickListener mRetryClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mControllerView.hideToastTime(0);
            mIsComplete = false;
            mVideoView.resume();
            mVideoView.setOnErrorListener(mOnErrorListener);
            mControllerView.setPlayUI(false);
            mControllerView.showNetErrorUI(false);
            mControllerView.hideReplayBtn();
        }
    };
    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.controller_back_btn://返回
                    if (mListener != null) {
                        mListener.onFinishClick();
                    }
                    break;
                case R.id.controller_more_btn://更多
                    if (mListener != null) {
                        mListener.onMoreClick();
                    }
                    break;
                case R.id.controller_play_btn://播放
                    checkNetWorkStateAndPlayVideo();
                    break;
                case R.id.controller_change_btn://全屏或者半屏
                    if (mListener != null) {
                        mListener.onChangeScreenClick();
                    }
                    break;
                case R.id.controller_center_lock_btn://锁
                    if (mListener != null) {
                        if (mIsLock) {
                            mIsLock = false;
                            mListener.onLockClick(false);
                            mControllerView.setEnable(true);
                            mControllerView.setBackBtnEnable(true);
                            mControllerView.setLockEnable(false);
                            mControllerView.setChangeVideoPreviewEnable(true);
                            mControllerView.setPlayEnable(true);
                            //如果播放时间>0则进度控件可以滑动
                            mControllerView.setSeekBarEnabled(mVideoView.getDuration() > 0);

                            mControllerView.showTopToolBarColor(true);
                            mControllerView.showBottomToolBarColor(true);
                        } else {
                            mIsLock = true;
                            mListener.onLockClick(true);
                            mControllerView.setEnable(false);
                            mControllerView.setBackBtnEnable(false);
                            mControllerView.setLockEnable(true);
                            mControllerView.setChangeVideoPreviewEnable(false);
                            mControllerView.setPlayEnable(false);
                            mControllerView.setSeekBarEnabled(false);

                            mControllerView.showTopToolBarColor(false);
                            mControllerView.showBottomToolBarColor(false);

                        }
                        //如果锁定屏幕，则展示toolbar
                        mControllerView.showOrHideBottomToolBarMenu(!mIsLock);
                        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT || mLandNotNeedToolbar) {
                            mControllerView.showOrHideToolBarMenu(false);
                            mControllerView.showTopToolBarColor(false);
                        } else {
                            mControllerView.showOrHideToolBarMenu(!mIsLock);
                        }
                    }
                    break;
                case R.id.net_error_click_tv: //点击重试
                    mControllerView.showNetErrorUI(false);
                    mControllerView.setPlayUI(false);
                    mVideoView.start();
                    if (mCurrentPosition > 0) {
                        mVideoView.seekTo(mCurrentPosition);
                    }
                    break;
                case R.id.controller_center_play_big_btn:
                    checkNetWorkStateAndPlayVideo();
                    break;
                case R.id.video_del_btn:    //关闭视频
                    stopPlayback();
                    setVisibility(View.GONE);
                    break;

            }
        }
    };

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser && mVideoView.getDuration() > 0) {
                int time = progress * mVideoView.getDuration() / 1000;
                if (mIsComplete) {
                    mIsComplete = false;
                    if (time > 0) {
                        mVideoView.seekTo(time);
                    }
                    mControllerView.showNetErrorUI(false);
                    mControllerView.setPlayUI(false);
                    mVideoView.start();
                    mControllerView.hideReplayBtn();
                } else {
                    mVideoView.seekTo(time);
                }

            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mIsTouching = true;
            removeCallbacks(mShowProgressRunnable);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mIsTouching = false;
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROLLS, 5000);
            post(mShowProgressRunnable);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mIsTouching) {
                mIsTouching = false;
                mControllerView.hideToastTime(500);
            }
            mVideoState = NONE;//手势抬起，恢复为默认状态
        }
        return mDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        toggleControls();
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        //如果屏幕锁定，禁止操作视频的播放与暂停
        if (!mIsLock) {
            checkNetWorkStateAndPlayVideo();
        }
        return true;
    }


    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    /**
     * 滑动监听
     *
     * @param e1
     * @param e2
     * @param distanceX
     * @param distanceY
     * @return
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //如果屏幕锁定，禁止操作
        if (mIsLock) {
            return true;
        }
        if (mVideoState == NONE) {
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                mVideoState = FAST_BACKWARD;
            } else {
                if (e1.getX() < mVideoHalfWidth) {
                    mVideoState = BRIGHTNESS;//当前为亮度
                } else {
                    mVideoState = VOLUME;    //当前为声音
                }
            }
        }
        return adjustInternal(distanceX, distanceY);
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    /**
     * 处理滑动状态
     *
     * @param distanceX x轴距离
     * @param distanceY y轴距离
     * @return
     */
    private boolean adjustInternal(float distanceX, float distanceY) {
        mIsTouching = true;
        if (mVideoState == FAST_BACKWARD) {//快进，快退
            if (mVideoView == null || !mVideoView.isPlaying()) {
                return false;
            }
            int total = mVideoView.getDuration();
            if (total > 0) {
                float percent = distanceX / (float) mVideoView.getMeasuredWidth();

                int currPosition = mVideoView.getCurrentPosition();
                int seekOffset = (int) (total * percent * -1);

                currPosition += seekOffset;
                if (currPosition < 0) {
                    currPosition = 0;
                } else if (currPosition > total) {
                    currPosition = total;
                }
                mVideoView.seekTo(currPosition);
                mControllerView.setToastTime(seekOffset > 0, currPosition, total);
                //设置seek bar进度
                int current = mVideoView.getCurrentPosition();
                mControllerView.getSeekBar().setProgress(current * 1000 / mVideoView.getDuration());
            }
        } else if (mVideoState == BRIGHTNESS) {//亮度
            if (mListener != null) {
                mListener.changeBrightness(distanceY > 0 ? 10 : -10);
            }
        } else if (mVideoState == VOLUME) {//声音
            float percent = distanceY / (float) DpPxUtils.getHeightPixels();
            setVolumePercent(percent, distanceY > 0, false);
        }
        return true;
    }

    /**
     * 设置声音百分比
     *
     * @param percent     添加的比例
     * @param plus        是否增加声音
     * @param sendHideMsg 是否发送隐藏消息
     */
    public void setVolumePercent(float percent, boolean plus, boolean sendHideMsg) {
        AudioManager manager = (AudioManager)
                getContext().getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumeOffsetAccurate = maxVolume * percent * 5;
        int volumeOffset = (int) volumeOffsetAccurate;
        if (volumeOffset == 0 && Math.abs(volumeOffsetAccurate) > 0.2f) {
            if (plus) {
                volumeOffset = 1;
            } else {
                volumeOffset = -1;
            }
        }
        currVolume += volumeOffset;
        if (currVolume < 0) {
            currVolume = 0;
        } else if (currVolume >= maxVolume) {
            currVolume = maxVolume;
        }
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, currVolume, 0);
        float volumePercent = (float) currVolume / (float) maxVolume * 100;
        mControllerView.setVolumeValue((int) volumePercent);
        //发送隐藏消息
        if (sendHideMsg) {
            mControllerView.hideToastTime(500);
        }
    }

    /**
     * 设置屏幕亮度
     *
     * @param percent 百分比
     */
    public void setBrightness(int percent) {
        mControllerView.setBrightness(percent);
    }
    //</editor-fold>

    /**
     * 初始化界面
     *
     * @param context
     */
    private void initViews(Context context) {
        inflate(context, R.layout.view_cl_video, this);
        mVideoView = (VideoView) findViewById(R.id.video_view);
        mControllerView = (CLVideoViewController) findViewById(R.id.video_controller_view);
        mVideoHalfWidth = DpPxUtils.getWidthPixels() / 2;

        mDetector = new GestureDetector(context, this);

        mVideoView.setOnPreparedListener(mOnPreparedListener);
        mVideoView.setOnErrorListener(mOnErrorListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mControllerView.setControllerListener(mClickListener);
        mControllerView.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        //如果sdk >=17 则使用info监听
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mVideoView.setOnInfoListener(mOnInfoListener);
        }
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        mControllerView.setPlayUI(false);
        mControllerView.setEnable(false);
        mControllerView.setLockEnable(false);
        mControllerView.setChangeVideoPreviewEnable(true);
    }

    //<editor-fold desc="配置播放UI">

    public void setVideoUrl(String videoPath) {
        mVideoPath = videoPath;
    }

    public void setLocalVideo(boolean isLocal) {
        this.mIsLocalVideo = isLocal;
    }

    /**
     * 设置播放时间毫秒
     *
     * @param playPosition 播放时间
     */
    public void playByTime(long playPosition) {
        mCurrentPosition = (int) playPosition;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        mControllerView.setTitle(title);
    }

    /**
     * 开始播放
     *
     * @param
     */
    public void startVideo() {
        if (!TextUtils.isEmpty(mVideoPath)) {
            mVideoView.suspend();
            mIsComplete = false;
            mVideoView.setVideoPath(mVideoPath);
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
            mControllerView.setVideoBlackBackground(true);
            mControllerView.hideReplayBtn();
            mControllerView.setPlayUI(false);
            mInited = true;
        }
    }

    /**
     * 播放本地视频
     *
     * @param
     */
    public void startLocalVideo() {
        startVideo();
    }


    /**
     * 播放与暂停
     */
    public void play() {
        if (mInited) {
            mIsComplete = false;
            mControllerView.setPlayUI(false);
            mControllerView.showNetErrorUI(false);
            mControllerView.hideReplayBtn();
            mVideoView.start();
        } else {
            startVideo();
        }
    }

    /**
     * 如果当前没有播放，则开始播放，不释资源
     */
    public void rePlay() {
        if (mInited) {
            mIsComplete = false;
            if (mIsPrepared && !mVideoView.isPlaying()) {
                mVideoView.start();
                //跳转到指定位置
                if (mCurrentPosition > 0) {
                    mVideoView.seekTo(mCurrentPosition);
                }
                mCurrentPosition = -1;
            }
            mControllerView.showNetErrorUI(false);
            mControllerView.hideReplayBtn();
            mControllerView.setPlayUI(false);
        }
    }

    /**
     * 重新设置地址，开始播放
     */
    public void reStartPlay() {
        if (null != mVideoView && !mVideoView.isPlaying()) {
            startVideo();
        }
    }

    /**
     * 重新播放
     */
    public void resume() {
        mVideoView.resume();
    }

    /**
     * 暂停播放状态
     */
    public void pause() {
        if (mVideoView.canPause()) {
            mCurrentPosition = mVideoView.getCurrentPosition();
            mControllerView.setPlayUI(true);
            mVideoView.pause();
        }
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        mCurrentPosition = mVideoView.getCurrentPosition();
        if (mVideoView.canPause()) {
            mVideoView.pause();
        }
        mControllerView.setPlayUI(true);
        mHandler.sendEmptyMessage(HIDE_PROGRESS);
        mHandler.sendEmptyMessage(HIDE_VIDEO_BG);
    }

    /**
     * 停止播放
     */
    public void stopPlayback() {
        if (mVideoView != null) {
            mHandler.sendEmptyMessage(HIDE_PROGRESS);
            mHandler.sendEmptyMessage(HIDE_VIDEO_BG);
            mCurrentPosition = mVideoView.getCurrentPosition();
            mControllerView.setPlayUI(true);
            mVideoView.pause();
            mVideoView.stopPlayback();
        }
    }

    /**
     * 停止播放,释放资源
     */
    public void releaseVideoView() {
        if (null != mControllerView) {
            mControllerView.release();
        }
        if (null != mHandler) {
            mHandler.removeCallbacks(mShowProgressRunnable);
            mHandler.removeMessages(SHOW_PROGRESS);
            mHandler.removeMessages(HIDE_PROGRESS);
            mHandler.removeMessages(HIDE_CONTROLLS);
            mHandler.removeMessages(VIDEO_PLAY);
            mHandler.removeMessages(VIDEO_PAUSE);
            mHandler.removeMessages(HIDE_VIDEO_BG);
            mHandler = null;
        }
    }

    /**
     * 如果没有准备好，或者当前视频还在播放
     * 设置网络错误UI
     */
    public void setNetErrorUI() {
        mHandler.sendEmptyMessage(HIDE_PROGRESS);
        if (!mIsPrepared || mVideoView.isPlaying()) {
            mCurrentPosition = mVideoView.getCurrentPosition();
            mVideoView.pause();
            mControllerView.showNetErrorUI(true);
            mControllerView.setPlayBtnStatus(true);
            mHandler.sendEmptyMessage(HIDE_VIDEO_BG);
        }
    }

    public void hideNetErrorUI() {
        mControllerView.showNetErrorUI(false);
    }

    /**
     * 状态栏是否展示
     *
     * @return
     */
    public boolean isShowToolBar() {
        return mControllerView.getToolBarLayout().getVisibility() == View.VISIBLE;
    }

    /**
     * 切换播放视频导航栏
     */
    void toggleControls() {
        mHandler.removeMessages(HIDE_CONTROLLS);
        if (mControllerView.getBottomLayout().getVisibility() == View.VISIBLE) {
            hideControls();
        } else {
            showControls();
        }
    }

    /**
     * 显示控件
     */
    public void showControls() {
        //开启进度跳动
        post(mShowProgressRunnable);
        mControllerView.getToolBarLayout().setVisibility(View.VISIBLE);
        mControllerView.getToolBarLayout().clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(getContext(),
                R.anim.option_entry_from_top);
        mControllerView.getToolBarLayout().startAnimation(animation);

        mControllerView.getBottomLayout().setVisibility(View.VISIBLE);
        mControllerView.getBottomLayout().clearAnimation();
        Animation animation1 = AnimationUtils.loadAnimation(getContext(),
                R.anim.option_entry_from_bottom);
        mControllerView.getBottomLayout().startAnimation(animation1);

        //横屏显示锁
        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            mControllerView.getLockBtn().setVisibility(View.GONE);
        } else {
            mControllerView.getLockBtn().setVisibility(View.VISIBLE);
        }
        //5s 后隐藏状态栏
        mHandler.sendEmptyMessageDelayed(HIDE_CONTROLLS, 5000);
        if (null != mListener) {
            mListener.setFullScreen(false);
        }
    }

    /**
     * 隐藏控件
     */
    public void hideControls() {
        mControllerView.getToolBarLayout().clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(getContext(),
                R.anim.option_leave_from_top);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //取消进度跳动
                removeCallbacks(mShowProgressRunnable);
                mControllerView.getToolBarLayout().setVisibility(View.GONE);
                if (null != mListener) {
                    mListener.setFullScreen(true);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mControllerView.getToolBarLayout().startAnimation(animation);

        mControllerView.getBottomLayout().clearAnimation();
        Animation animation1 = AnimationUtils.loadAnimation(getContext(),
                R.anim.option_leave_from_bottom);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mControllerView.getBottomLayout().setVisibility(View.GONE);
                mControllerView.getLockBtn().setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mControllerView.getBottomLayout().startAnimation(animation1);

    }
    //</editor-fold>

    /**
     * 设置屏幕横竖屏状态
     *
     * @param currentOrientation 当前屏幕横竖屏状态
     */
    public void setCurrentOrientation(int currentOrientation, boolean landNotNeedToolbar) {
        this.mCurrentOrientation = currentOrientation;
        this.mLandNotNeedToolbar = landNotNeedToolbar;
        switch (currentOrientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                mControllerView.showOrHideToolBarMenu(false);
                mControllerView.setChangeBtnIcon(false);
                mControllerView.getLockBtn().setVisibility(View.GONE);
                mControllerView.showTopToolBarColor(false);
                //控制删除icon
                if (mShowDelIcon) {
                    mControllerView.getVideoDelBtn().setVisibility(View.VISIBLE);
                }
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                //横屏下不需要顶部toolbar
                if (landNotNeedToolbar) {
                    mControllerView.showOrHideToolBarMenu(false);
                    mControllerView.showTopToolBarColor(false);
                } else {
                    mControllerView.showOrHideToolBarMenu(true);
                    mControllerView.showTopToolBarColor(true);
                }
                mControllerView.setChangeBtnIcon(true);
                //如果操作栏是展示状态
                if (mControllerView.getBottomLayout().getVisibility() == View.VISIBLE) {
                    mControllerView.getLockBtn().setVisibility(View.VISIBLE);
                }
                //控制删除icon
                if (mShowDelIcon) {
                    mControllerView.getVideoDelBtn().setVisibility(View.GONE);
                }
                break;
        }
    }

    /**
     * 设置虚拟按键宽度
     *
     * @param spaceWidth 按键宽度
     */
    public void setSpaceLayoutParams(int spaceWidth) {
        mControllerView.setSpaceLayoutParams(spaceWidth);
    }

    //隐藏切换按钮
    public void hideChangeBtn() {
        mControllerView.hideControllerChangeBtn();
    }

    //隐藏分享按钮
    public void hideShareBtn() {
        mControllerView.hideShareBtn();
    }

    /**
     * 设置进度
     *
     * @return
     */
    private int setProgress() {
        if (mVideoView == null) {
            return 0;
        }
        if (mIsComplete) {
            mControllerView.setPlayTime(0, mVideoView.getDuration());
            mControllerView.getSeekBar().setProgress(0);
            return 0;
        } else {
            int position = mVideoView.getCurrentPosition();
            int duration = mVideoView.getDuration();
            if (mControllerView.getSeekBar() != null) {
                if (duration > 0) {
                    mControllerView.setPlayTime(position, duration);
                    //同步播放时间
                    if (null != mListener && position > 0) {
                        mListener.synchronizationPlayTime(position);
                    }
                    // use long to avoid overflow
                    long pos = 1000L * position / duration;
                    mControllerView.getSeekBar().setProgress((int) pos);
                }
                //设置缓冲百分比
                int percent = mVideoView.getBufferPercentage();
                mControllerView.getSeekBar().setSecondaryProgress(percent * 10);
            }
            return position;
        }
    }


    /**
     * 最后一集设置重播UI
     */
    public void setLastVideoReplayUI() {
        mControllerView.setRetryPlayUI(mRetryClickListener);
        mControllerView.setPlayUI(true);
    }

    /**
     * 设置视频背景图片
     *
     * @param imagePath 图片地址
     */
    public void setVideoBackground(String imagePath) {
        mControllerView.setVideoBackground(imagePath);
    }

    /**
     * 检查网络，播放视频
     */
    private void checkNetWorkStateAndPlayVideo() {
        if (mIsLocalVideo) {
            if (mVideoView.isPlaying()) {
                mHandler.sendEmptyMessage(VIDEO_PAUSE);
            } else {
                mHandler.sendEmptyMessage(VIDEO_PLAY);
            }
            return;
        }
        switch (mNetWorkState) {
            case CheckInternetUtil.NET_WORK_MOBILE:
                if (mIsAllow4GPlayVideo) {
                    if (mVideoView.isPlaying()) {
                        mIsHandPause = true;
                        mHandler.sendEmptyMessage(VIDEO_PAUSE);
                    } else {
                        mIsHandPause = false;
                        mHandler.sendEmptyMessage(VIDEO_PLAY);
                    }
                } else {
                    if (mListener != null) {
                        mListener.show4GNoticeDialog();
                    }
                }
                break;
            case CheckInternetUtil.NET_WORK_WIFI:
            case CheckInternetUtil.NET_WORK_NONE:
                if (mVideoView.isPlaying()) {
                    mIsHandPause = true;
                    mHandler.sendEmptyMessage(VIDEO_PAUSE);
                } else {
                    mIsHandPause = false;
                    mHandler.sendEmptyMessage(VIDEO_PLAY);
                }
                break;
        }
    }

    public int getCurrentPosition() {
        return mVideoView.getCurrentPosition();
    }

    public int getDuration() {
        return mVideoView.getDuration();
    }


    public void setAllow4GPlayVideo(boolean isAllow4GPlayVideo) {
        mIsAllow4GPlayVideo = isAllow4GPlayVideo;
    }

    //展示删除icon
    public void showDelIcon() {
        mShowDelIcon = true;
        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            mControllerView.getVideoDelBtn().setVisibility(View.VISIBLE);
        }
    }

    /**
     * 展示下载提示
     */
    public void showDownloadTips() {
        mControllerView.showDownloadTips();
    }

    private VideoControllerListener mListener;

    public void setVideoControllerListener(VideoControllerListener listener) {
        this.mListener = listener;
    }

    public void setNetWorkState(int netWorkState) {
        this.mNetWorkState = netWorkState;
    }

    public abstract static class VideoControllerListener {

        public void onFinishClick() {
        }

        public void onLockClick(boolean isLock) {
        }

        public void onChangeScreenClick() {
        }

        public void onMoreClick() {
        }

        public void changeBrightness(float brightness) {
        }

        public void setFullScreen(boolean screenFull) {
        }

        public void playComplete() {
        }

        public void show4GNoticeDialog() {
        }

        public void retryGetVideoRealUrlAndPlay() {
        }

        public void synchronizationPlayTime(int playTime) {
        }

        public void isHandlePause() {
        }
    }

}
