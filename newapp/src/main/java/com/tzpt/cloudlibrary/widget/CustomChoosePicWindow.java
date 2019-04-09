package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;

/**
 * 选择相册弹出框
 * Created by ZhiqiangJia on 2018-01-12.
 */
public class CustomChoosePicWindow extends CustomPopupWindow implements View.OnClickListener {

    private PicOptionsListener mListener;

    public CustomChoosePicWindow(Context context, PicOptionsListener listener) {
        super(context);
        this.mListener = listener;
        View view = LayoutInflater.from(context).inflate(R.layout.view_choose_pic, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        showAtLocation(view, Gravity.CENTER, 0, 0);
        Animation slideInBottom = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
        view.startAnimation(slideInBottom);

        RelativeLayout choosePicRl = (RelativeLayout) view.findViewById(R.id.choose_pic_rl);
        TextView cameraTv = (TextView) view.findViewById(R.id.choose_pic_camera_tv);
        TextView galleyTv = (TextView) view.findViewById(R.id.choose_pic_galley_tv);
        TextView cancelTv = (TextView) view.findViewById(R.id.choose_pic_cancel_tv);

        choosePicRl.setOnClickListener(this);
        cameraTv.setOnClickListener(this);
        galleyTv.setOnClickListener(this);
        cancelTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.choose_pic_camera_tv:
                if (null != mListener) {
                    mListener.onOptionClick(0);
                }
                break;
            case R.id.choose_pic_galley_tv:
                if (null != mListener) {
                    mListener.onOptionClick(1);
                }
                break;
        }
    }


    public interface PicOptionsListener {
        void onOptionClick(int optionType);
    }
}
