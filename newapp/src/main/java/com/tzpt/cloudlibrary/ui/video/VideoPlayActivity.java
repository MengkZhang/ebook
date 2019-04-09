package com.tzpt.cloudlibrary.ui.video;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.rxbus.event.HeadsetEvent;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.utils.StatusBarUtil;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.video.CLVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.functions.Action1;

/**
 * 本地视频播放
 */
public class VideoPlayActivity extends AppCompatActivity implements
        VideoPlayContract.View {

    private static final String VIDEO_SET_ID = "video_set_id";
    private static final String VIDEO_IMAGE = "video_image_path";
    private static final String VIDEO_POSITION = "video_position";

    public static void startActivity(Context context, long setId, String imagePath, int position) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra(VIDEO_SET_ID, setId);
        intent.putExtra(VIDEO_IMAGE, imagePath);
        intent.putExtra(VIDEO_POSITION, position);
        context.startActivity(intent);
    }

    Unbinder unbinder;
    @BindView(R.id.cl_video_view)
    CLVideoView mCLVideoView;
    private VideoPlayPresenter mPresenter;

    private boolean mIsLock = false;
    private String mVideoImage;

    private CLVideoView.VideoControllerListener mVideoControllerListener = new CLVideoView.VideoControllerListener() {
        @Override
        public void onFinishClick() {
            dealBackClick();
        }

        @Override
        public void onLockClick(boolean isLock) {
            mIsLock = isLock;
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
            if (mIsLock) {
                return;
            }
            setScreenFull(screenFull);
        }

        @Override
        public void playComplete() {
            //TODO 播放下一集
            mPresenter.playNextVideo();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_play);
        unbinder = ButterKnife.bind(this);

        initDatas();
        initViews();
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        mPresenter = new VideoPlayPresenter(CloudLibraryApplication.mRxBus);
        mPresenter.attachView(this);

        Intent intent = getIntent();
        mVideoImage = intent.getStringExtra(VIDEO_IMAGE);
        long setId = intent.getLongExtra(VIDEO_SET_ID, -1);
        int videoPosition = intent.getIntExtra(VIDEO_POSITION, 0);
        mPresenter.setPlayVideoIndex(videoPosition);
        mPresenter.setVideoSetId(setId);
        mPresenter.getVideoCompleteList();
    }

    /**
     * 初始化UI
     */
    private void initViews() {
        StatusBarUtil.setStatusBarColor(this, R.color.color_translate);
        mCLVideoView.setSpaceLayoutParams(DpPxUtils.getRealWidthPixels(this) - DpPxUtils.getWidthPixels());
        setScreenFull(false);
        mCLVideoView.hideShareBtn();
        mCLVideoView.hideChangeBtn();
        mCLVideoView.setVideoControllerListener(mVideoControllerListener);
        mCLVideoView.setCurrentOrientation(Configuration.ORIENTATION_LANDSCAPE, false);
        if (!TextUtils.isEmpty(mVideoImage)) {
            mCLVideoView.setVideoBackground(mVideoImage);
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
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        } else {
            if (Build.VERSION.SDK_INT < 16) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
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
        finish();
    }

    @Override
    public void showErrorMsg(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }

    /**
     * 显示重播
     */
    @Override
    public void playLastVideoComplete() {
        mCLVideoView.setLastVideoReplayUI();
    }

    /**
     * 播放视频
     *
     * @param videoTitle     视频标题
     * @param videoUrl       视频地址
     * @param localVideoPath 视频本地地址
     */
    @Override
    public void playVideoByPlayId(String videoTitle, String videoUrl, String localVideoPath, long playedTime) {
        mCLVideoView.setTitle(videoTitle);
        mCLVideoView.playByTime(playedTime);
        //上报观看视频记录
        mPresenter.recordWatchVideo();

        mCLVideoView.setVideoUrl(localVideoPath);
        mCLVideoView.startLocalVideo();

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

    /**
     * 检查网络，判断是否分享的处理
     */
    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
        mCLVideoView.rePlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.saveVideoPlayedTime(mCLVideoView.getCurrentPosition(), mCLVideoView.getDuration());
        UmengHelper.setUmengPause(this);
        mCLVideoView.pause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCLVideoView.releaseVideoView();
        mPresenter.clearCompleteVideoList();
        mPresenter.unregisterRxBus();
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
}
