package com.tzpt.cloudlibrary.ui.account;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.widget.bottomdialog.CLBottomDialog;

/**
 * 选择头像图片
 * Created by tonyjia on 2018/11/7.
 */
public class UserChooseHeadPicBottomDialog extends CLBottomDialog implements
        View.OnClickListener {

    public UserChooseHeadPicBottomDialog(@NonNull Context context) {
        super(context);
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.view_bottom_choose_pic);
        TextView bottomTakePicTv = (TextView) findViewById(R.id.bottom_take_pic_tv);
        TextView bottomChoosePicTv = (TextView) findViewById(R.id.bottom_choose_pic_tv);
        TextView bottomSystemPicTv = (TextView) findViewById(R.id.bottom_system_pic_tv);
        TextView bottomCancelTv = (TextView) findViewById(R.id.bottom_cancel_tv);
        bottomTakePicTv.setOnClickListener(this);
        bottomChoosePicTv.setOnClickListener(this);
        bottomSystemPicTv.setOnClickListener(this);
        bottomCancelTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.bottom_take_pic_tv:
                if (mListener != null) {
                    mListener.onPicClick(0);
                }
                break;
            case R.id.bottom_choose_pic_tv:
                if (mListener != null) {
                    mListener.onPicClick(1);
                }
                break;
            case R.id.bottom_system_pic_tv:
                if (mListener != null) {
                    mListener.onPicClick(2);
                }
                break;
        }
    }

    private ChoosePicListener mListener;

    public void setChoosePicListener(ChoosePicListener listener) {
        this.mListener = listener;
    }

    public interface ChoosePicListener {
        void onPicClick(int clickType);
    }
}
