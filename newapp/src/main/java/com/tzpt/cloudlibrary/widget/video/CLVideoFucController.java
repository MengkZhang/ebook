package com.tzpt.cloudlibrary.widget.video;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;

/**
 * 视频声音，静音，亮度控制界面
 * Created by tonyjia on 2018/6/19.
 */
public class CLVideoFucController extends FrameLayout {

    public CLVideoFucController(@NonNull Context context) {
        this(context, null);
    }

    public CLVideoFucController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CLVideoFucController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private ImageView mVideoControllerLogoIv;
    private TextView mVideoControllerDescTv;
    private TextView mVideoControllerProgressTv;
    private SeekBar mVideoControllerSb;

    private void initView(Context context) {
        inflate(context, R.layout.view_cl_video_fuc_controller, this);
        mVideoControllerLogoIv = (ImageView) findViewById(R.id.ic_video_controller_logo_iv);
        mVideoControllerDescTv = (TextView) findViewById(R.id.ic_video_controller_desc_tv);
        mVideoControllerProgressTv = (TextView) findViewById(R.id.ic_video_progress_desc_tv);
        mVideoControllerSb = (SeekBar) findViewById(R.id.ic_video_controller_sb);
        mVideoControllerSb.setMax(100);
    }

    public SeekBar getSeekBar() {
        return mVideoControllerSb;
    }

    //设置logo
    public void setVideoControllerLogo(int resId) {
        mVideoControllerLogoIv.setImageResource(resId);
    }

    //设置描述
    public void setVideoControllerDesc(int resId) {
        mVideoControllerDescTv.setText(resId);
    }

    //设置进度
    public void setVideoControllerProgress(int progress) {
        mVideoControllerSb.setProgress(progress);
    }

    //设置进度描述
    public void setVideoControllerProgressDesc(int resId) {
        mVideoControllerProgressTv.setText(resId);
    }

    //设置进度条是否显示
    public void showOrHideSeekBar(boolean show) {
        mVideoControllerSb.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    //设置进度描述是否显示
    public void showOrHideProgressDesc(boolean show) {
        mVideoControllerProgressTv.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showOrHideFucController(boolean show) {
        this.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
