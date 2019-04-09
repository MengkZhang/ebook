package com.tzpt.cloudlibrary.widget.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;

import java.util.Formatter;
import java.util.Locale;

/**
 * 播放控制界面
 * Created by tonyjia on 2018/6/11.
 */
public class CLVideoViewController extends FrameLayout {

    private static final int HIDE_CONTROLS = 1000;
    private static final int HIDE_DOWNLOAD_TIPS = 1001;

    public CLVideoViewController(@NonNull Context context) {
        this(context, null);
    }

    public CLVideoViewController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CLVideoViewController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private LinearLayout mControllerToolbarSubLl;
    private LinearLayout mControllerToolbarLl;
    private LinearLayout mControllerBottomLl;
    private LinearLayout mControllerNetErrorLl;
    private LinearLayout mControllerProgressLl;
    private CLVideoFucController mCLVideoFucVoiceController;
    private CLVideoFucController mCLVideoFucLightController;
    private TextView mTitleTv;
    private TextView mTimeTv;
    private TextView mControllTimeTv;
    private TextView mRetryNetClickTv;
    private SeekBar mSeekBar;
    private ImageButton mControllerPlayBigBtn;
    private ImageButton mControllerPlayBtn;
    private ImageButton mControllerLockBtn;
    private ImageButton mControllerBackBtn;
    private ImageButton mControllerMoreBtn;
    private ImageButton mControllerChangeBtn;
    private ImageButton mVideoDelBtn;
    private ImageView mControllerVideoBgIv;
    private ImageView mProgressView;
    private TextView mVideoDownloadTipsTv;
    private View mControllerTopSpaceV;
    private View mControllerTopToolbarStatusV;
    private View mControllerBottomSpaceV;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private ViewPropertyAnimator mViewPropertyAnimator;
    private boolean mHasVoice = true;
    private boolean mSpaceLayoutInit = false;
    private boolean mAlwaysHideChangeBtn = false;   //隐藏切换按钮
    private boolean mHideMoreBtn = false;           //隐藏更多按钮


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HIDE_CONTROLS://隐藏快进快退UI
                    if (mControllTimeTv.getVisibility() == View.VISIBLE) {
                        mControllTimeTv.setVisibility(View.GONE);
                    }//隐藏声音控制UI
                    if (mCLVideoFucVoiceController.getVisibility() == View.VISIBLE) {
                        mCLVideoFucVoiceController.showOrHideFucController(false);
                    }//隐藏亮度控制UI
                    if (mCLVideoFucLightController.getVisibility() == View.VISIBLE) {
                        mCLVideoFucLightController.showOrHideFucController(false);
                    }
                    break;
                //隐藏本地已下线视频提醒
                case HIDE_DOWNLOAD_TIPS:
                    hideDownloadTips();
                    break;
            }
        }
    };
    /**
     * 手动控制静音的显示
     */
    private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress > 0 && !mHasVoice) {
                mHasVoice = true;
                setHasVoiceView();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private void initView(Context context) {
        inflate(context, R.layout.view_video_controller, this);
        setVideoBlackBackground(true);

        mControllerToolbarSubLl = (LinearLayout) findViewById(R.id.controller_toolbar_sub_ll);
        mControllerToolbarLl = (LinearLayout) findViewById(R.id.controller_toolbar_ll);
        mControllerBottomLl = (LinearLayout) findViewById(R.id.controller_bottom_ll);
        mControllerNetErrorLl = (LinearLayout) findViewById(R.id.ic_video_net_error_ll);
        mControllerProgressLl = (LinearLayout) findViewById(R.id.controller_center_progress_ll);
        mCLVideoFucVoiceController = (CLVideoFucController) findViewById(R.id.ic_video_voice_cl_controller);
        mCLVideoFucLightController = (CLVideoFucController) findViewById(R.id.ic_video_light_cl_controller);

        mRetryNetClickTv = (TextView) findViewById(R.id.net_error_click_tv);
        mTitleTv = (TextView) findViewById(R.id.controller_title_tv);
        mTimeTv = (TextView) findViewById(R.id.controller_time_tv);
        mControllTimeTv = (TextView) findViewById(R.id.controller_center_time_tv);
        mSeekBar = (SeekBar) findViewById(R.id.controller_progress_sb);

        mControllerPlayBigBtn = (ImageButton) findViewById(R.id.controller_center_play_big_btn);
        mControllerPlayBtn = (ImageButton) findViewById(R.id.controller_play_btn);
        mControllerLockBtn = (ImageButton) findViewById(R.id.controller_center_lock_btn);
        mControllerBackBtn = (ImageButton) findViewById(R.id.controller_back_btn);
        mControllerMoreBtn = (ImageButton) findViewById(R.id.controller_more_btn);
        mControllerChangeBtn = (ImageButton) findViewById(R.id.controller_change_btn);
        mControllerVideoBgIv = (ImageView) findViewById(R.id.controller_video_bg_iv);
        mVideoDelBtn = (ImageButton) findViewById(R.id.video_del_btn);

        mControllerTopSpaceV = findViewById(R.id.controller_top_space_v);
        mControllerTopToolbarStatusV = findViewById(R.id.controller_toolbar_status);
        mControllerBottomSpaceV = findViewById(R.id.controller_bottom_space_v);

        mVideoDownloadTipsTv = (TextView) findViewById(R.id.video_download_tips_tv);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        setKeepScreenOn(true);

        mProgressView = (ImageView) findViewById(R.id.controller_center_progress_iv);
        mProgressView.animate().setInterpolator(new LinearInterpolator());

        mCLVideoFucLightController.setVideoControllerDesc(R.string.video_lightness);
        mCLVideoFucLightController.setVideoControllerLogo(R.mipmap.ic_video_light_logo);
        setHasVoiceView();
        mCLVideoFucVoiceController.getSeekBar().setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }

    /**
     * 设置虚拟按键宽度
     *
     * @param viewWidth 按键宽度
     */
    public void setSpaceLayoutParams(int viewWidth) {
        if (viewWidth > 0) {
            mControllerTopSpaceV.setVisibility(View.VISIBLE);
            mControllerBottomSpaceV.setVisibility(View.VISIBLE);
            //设置初始化
            if (!mSpaceLayoutInit) {
                mSpaceLayoutInit = true;
                LinearLayout.LayoutParams topLayoutParams = (LinearLayout.LayoutParams) mControllerTopSpaceV.getLayoutParams();
                topLayoutParams.width = viewWidth;
                mControllerTopSpaceV.setLayoutParams(topLayoutParams);

                LinearLayout.LayoutParams bottomLayoutParams = (LinearLayout.LayoutParams) mControllerBottomSpaceV.getLayoutParams();
                bottomLayoutParams.width = viewWidth;
                mControllerBottomSpaceV.setLayoutParams(bottomLayoutParams);
            }
        } else {
            mControllerTopSpaceV.setVisibility(View.GONE);
            mControllerBottomSpaceV.setVisibility(View.GONE);
        }

    }

    /**
     * 设置是否黑色背景
     *
     * @param isBlack 是否黑色
     */
    public void setVideoBlackBackground(boolean isBlack) {
        this.setBackgroundResource(isBlack ? R.drawable.bg_000000 : R.drawable.bg_00000000);
    }

    /**
     * 设置操作按钮是否可用
     *
     * @param enable 是否可用
     */
    public void setEnable(boolean enable) {
        mSeekBar.setEnabled(enable);
        mControllTimeTv.setEnabled(enable);
        mControllerMoreBtn.setEnabled(enable);
    }

    public void setBackBtnEnable(boolean enable){
        mControllerBackBtn.setEnabled(enable);
    }

    /**
     * 设置是否锁
     *
     * @param enable
     */
    public void setLockEnable(boolean enable) {
        mControllerLockBtn.setImageResource(enable ? R.mipmap.ic_video_locked : R.mipmap.ic_video_unlock);
    }

    /**
     * 设置切换是否可以点击
     *
     * @param enable
     */
    public void setChangeVideoPreviewEnable(boolean enable) {
        mControllerChangeBtn.setEnabled(enable);
    }

    public void setPlayEnable(boolean enable) {
        mControllerPlayBtn.setEnabled(enable);
    }

    /**
     * 是否显示上部toolbar
     *
     * @param show 是否展示
     */
    public void showOrHideToolBarMenu(boolean show) {
        mTitleTv.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mControllerBackBtn.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        if (mHideMoreBtn) {
            mControllerMoreBtn.setVisibility(View.INVISIBLE);
        } else {
            mControllerMoreBtn.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * 是否展示底部toolbar
     *
     * @param show
     */
    public void showOrHideBottomToolBarMenu(boolean show) {
        mControllerPlayBtn.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mSeekBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mTimeTv.setVisibility(show ? View.VISIBLE : View.INVISIBLE);

        //是否一直隐藏切换控件
        if (mAlwaysHideChangeBtn) {
            mControllerChangeBtn.setVisibility(View.GONE);
        } else {
            mControllerChangeBtn.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * 隐藏分享按钮
     */
    public void hideShareBtn() {
        mHideMoreBtn = true;
    }

    public View getToolBarLayout() {
        return mControllerToolbarLl;
    }

    public View getBottomLayout() {
        return mControllerBottomLl;
    }

    public SeekBar getSeekBar() {
        return mSeekBar;
    }

    public ImageButton getLockBtn() {
        return mControllerLockBtn;
    }

    public void setTitle(String title) {
        mTitleTv.setText(title);
    }

    public ImageButton getVideoDelBtn() {
        return mVideoDelBtn;
    }

    private StringBuilder mTimeStr = new StringBuilder();

    /**
     * 设置静音UI
     */
    private void setNoVoiceView() {
        mCLVideoFucVoiceController.showOrHideProgressDesc(true);
        mCLVideoFucVoiceController.setVideoControllerLogo(R.mipmap.ic_video_no_vioce_logo);
        mCLVideoFucVoiceController.setVideoControllerProgressDesc(R.string.video_no_voice);
        mCLVideoFucVoiceController.showOrHideSeekBar(false);
    }

    /**
     * 设置声音UI
     */
    private void setHasVoiceView() {
        mCLVideoFucVoiceController.setVideoControllerDesc(R.string.video_voice);
        mCLVideoFucVoiceController.setVideoControllerLogo(R.mipmap.ic_video_vioce_logo);
        mCLVideoFucVoiceController.showOrHideProgressDesc(false);
        mCLVideoFucVoiceController.showOrHideSeekBar(true);
    }

    /**
     * 配置播放时间
     *
     * @param currentTime 当前播放时间
     * @param totalTime   总共时间
     */
    public void setPlayTime(int currentTime, int totalTime) {
        mTimeStr.setLength(0);
        mTimeStr.append(stringForTime(currentTime));
        mTimeStr.append("/");
        mTimeStr.append(stringForTime(totalTime));
        mTimeTv.setText(mTimeStr);
    }

    /**
     * 显示快进时间
     *
     * @param isToRight   是否向右滑动
     * @param currentTime 当前时间
     * @param totalTime   总共时间
     */
    public void setToastTime(boolean isToRight, int currentTime, int totalTime) {
        hideAllCenterUI();
        mControllTimeTv.setVisibility(View.VISIBLE);
        StringBuilder builder = new StringBuilder();
        builder.append(stringForTime(currentTime));
        builder.append("/");
        builder.append(stringForTime(totalTime));
        mControllTimeTv.setText(builder.toString());
        setControllerTimeDrawableTop(isToRight ? R.mipmap.ic_video_fast : R.mipmap.ic_video_forward);
    }

    /**
     * 设置声音显示
     *
     * @param volumePercent 百分比
     */
    public void setVolumeValue(int volumePercent) {
        mCLVideoFucVoiceController.setVideoControllerProgress(volumePercent);
        mCLVideoFucVoiceController.showOrHideFucController(true);
        mCLVideoFucLightController.showOrHideFucController(false);
        if (volumePercent == 0) {
            mHasVoice = false;
            setNoVoiceView();
        }
    }

    /**
     * 设置亮度
     *
     * @param brightness 亮度进度
     */
    public void setBrightness(int brightness) {
        mCLVideoFucLightController.setVideoControllerProgress(brightness);
        mCLVideoFucLightController.showOrHideFucController(true);
        mCLVideoFucVoiceController.showOrHideFucController(false);
    }

    /**
     * 设置显示时间标签
     *
     * @param resId 图标资源
     */
    private void setControllerTimeDrawableTop(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getMinimumHeight());
        mControllTimeTv.setCompoundDrawables(null, drawable, null, null);
        mControllTimeTv.setCompoundDrawablePadding(DpPxUtils.dp2px(8));
    }

    /**
     * 隐藏快进时间，亮度和声音控制UI
     *
     * @param delayMillis 延长时间
     */
    public void hideToastTime(long delayMillis) {
        if (mControllTimeTv.getVisibility() == View.VISIBLE
                || mCLVideoFucVoiceController.getVisibility() == View.VISIBLE
                || mCLVideoFucLightController.getVisibility() == View.VISIBLE) {
            mHandler.removeMessages(HIDE_CONTROLS);
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROLS, delayMillis);
            mControllTimeTv.setOnClickListener(null);
        }
    }

    /**
     * 配置重播UI
     *
     * @param listener 点击监听
     */
    public void setRetryPlayUI(OnClickListener listener) {
        hideAllCenterUI();
        mControllTimeTv.setVisibility(View.VISIBLE);
        mControllTimeTv.setText("重播");
        setControllerTimeDrawableTop(R.drawable.bg_video_btn_replay);
        mControllTimeTv.setOnClickListener(listener);
    }

    /**
     * 设置播放按钮icon
     *
     * @param isPlay 是否在播放
     */
    public void setPlayUI(boolean isPlay) {
        mControllerPlayBtn.setImageResource(isPlay ? R.mipmap.ic_video_play : R.mipmap.ic_video_pause);
        if (mControllTimeTv.getVisibility() != View.VISIBLE) {//如果重播未显示
            hideAllCenterUI();
            if (isPlay) {
                mControllerPlayBigBtn.setVisibility(View.VISIBLE);
            } else {
                mControllerPlayBigBtn.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置播放按钮是否为播放
     *
     * @param isPlay
     */
    public void setPlayBtnStatus(boolean isPlay) {
        mControllerPlayBtn.setImageResource(isPlay ? R.mipmap.ic_video_play : R.mipmap.ic_video_pause);
    }

    /**
     * 隐藏重播按钮
     */
    public void hideReplayBtn() {
        mControllTimeTv.setVisibility(View.GONE);
    }

    /**
     * 控制加载进度条
     *
     * @param showProgress
     */
    public void controllProgress(boolean showProgress) {
        if (showProgress) {
            hideAllCenterUI();
            mControllerProgressLl.setVisibility(View.VISIBLE);
            mViewPropertyAnimator = mProgressView.animate().rotation(360000).setDuration(1000000);
            mViewPropertyAnimator.start();
        } else {
            mControllerProgressLl.setVisibility(View.GONE);
            if (null != mViewPropertyAnimator) {
                mViewPropertyAnimator.cancel();
                mViewPropertyAnimator = null;
            }
        }
    }

    /**
     * 格式化播放时间
     *
     * @param timeMs 时间
     * @return
     */
    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 显示网络请求失败界面
     *
     * @param show
     */
    public void showNetErrorUI(boolean show) {
        if (show) {
            hideAllCenterUI();
            mControllerNetErrorLl.setVisibility(View.VISIBLE);
        } else {
            mControllerNetErrorLl.setVisibility(View.GONE);
        }
    }

    /**
     * 设置按钮监听
     *
     * @param listener
     */
    public void setControllerListener(OnClickListener listener) {
        mControllerBackBtn.setOnClickListener(listener);
        mControllerMoreBtn.setOnClickListener(listener);
        mControllerChangeBtn.setOnClickListener(listener);
        mControllerPlayBtn.setOnClickListener(listener);
        mControllerLockBtn.setOnClickListener(listener);
        mRetryNetClickTv.setOnClickListener(listener);
        mControllerPlayBigBtn.setOnClickListener(listener);
        mVideoDelBtn.setOnClickListener(listener);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        mSeekBar.setOnSeekBarChangeListener(listener);
    }

    /**
     * 设置进度条是否可以用
     *
     * @param seekBarEnabled
     */
    public void setSeekBarEnabled(boolean seekBarEnabled) {
        mSeekBar.setEnabled(seekBarEnabled);
    }

    /**
     * 清除handler发送消息
     */
    public void release() {
        if (null != mHandler) {
            mHandler.removeMessages(HIDE_CONTROLS);
            mHandler.removeMessages(HIDE_DOWNLOAD_TIPS);
            mHandler = null;
        }
    }

    /**
     * 隐藏切换按钮
     */
    public void hideControllerChangeBtn() {
        mAlwaysHideChangeBtn = true;
        mControllerChangeBtn.setVisibility(View.GONE);
    }

    /**
     * 隐藏所有控件
     */
    public void hideAllCenterUI() {
        mControllerProgressLl.setVisibility(View.GONE);
        mControllerNetErrorLl.setVisibility(View.GONE);
        mControllerPlayBigBtn.setVisibility(View.GONE);
        mControllTimeTv.setVisibility(View.GONE);
    }

    /**
     * 设置切换按钮
     *
     * @param isLandscape 是否横屏
     */
    public void setChangeBtnIcon(boolean isLandscape) {
        mControllerChangeBtn.setImageResource(isLandscape ? R.drawable.bg_video_btn_change : R.mipmap.bg_video_btn_change_2_big);
    }


    /**
     * 设置背景图片
     *
     * @param imagePath 图片地址
     */
    public void setVideoBackground(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            mControllerVideoBgIv.setVisibility(View.VISIBLE);
            GlideApp.with(getContext()).load(imagePath).centerCrop().into(mControllerVideoBgIv);
        }
    }

    /**
     * 隐藏背景图片
     */
    public void hideVideoBackground() {
        if (mControllerVideoBgIv.getVisibility() == View.VISIBLE) {
            mControllerVideoBgIv.setVisibility(View.GONE);
        }
    }

    /**
     * 展示下载提示
     */
    public void showDownloadTips() {
        mVideoDownloadTipsTv.setVisibility(View.VISIBLE);
        mVideoDownloadTipsTv.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(getContext(),
                R.anim.option_entry_from_top);
        mVideoDownloadTipsTv.startAnimation(animation);
        mHandler.sendEmptyMessageDelayed(HIDE_DOWNLOAD_TIPS, 3000);
    }

    /**
     * 隐藏下载提示
     */
    private void hideDownloadTips() {
        mVideoDownloadTipsTv.clearAnimation();
        Animation animation1 = AnimationUtils.loadAnimation(getContext(),
                R.anim.option_leave_from_top);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mVideoDownloadTipsTv.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mVideoDownloadTipsTv.startAnimation(animation1);
    }

    /**
     * 展示顶部toolbar颜色
     *
     * @param show
     */
    public void showTopToolBarColor(boolean show) {
        if (show) {
            mControllerTopToolbarStatusV.setBackgroundColor(getResources().getColor(R.color.color_half000000));
            mControllerToolbarSubLl.setBackgroundResource(R.drawable.bg_gradient_64_2_0f);
        } else {
            mControllerTopToolbarStatusV.setBackgroundColor(getResources().getColor(R.color.color_translate));
            mControllerToolbarSubLl.setBackgroundResource(R.drawable.bg_00000000);
        }
    }

    /**
     * 展示底部toolbar颜色
     *
     * @param show
     */
    public void showBottomToolBarColor(boolean show) {
        if (show) {
            mControllerBottomLl.setBackgroundResource(R.drawable.bg_gradient_0_2_64f);
        } else {
            mControllerBottomLl.setBackgroundResource(R.drawable.bg_00000000);
        }
    }

}



