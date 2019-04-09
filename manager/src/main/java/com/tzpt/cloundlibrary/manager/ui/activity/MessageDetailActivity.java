package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.bean.MsgInfo;

import butterknife.BindView;
import butterknife.OnClick;

public class MessageDetailActivity extends BaseActivity {
    private static final String MESSAGE_BEAN = "msg_info";
    public static void startActivity(Context context, MsgInfo msgInfo) {
        Intent intent = new Intent(context, MessageDetailActivity.class);
        intent.putExtra(MESSAGE_BEAN, msgInfo);
        context.startActivity(intent);
    }
    @BindView(R.id.msg_time_tv)
    TextView mMsgTimeTv;
    @BindView(R.id.msg_content_tv)
    TextView mMsgContentTv;

    @OnClick(R.id.titlebar_left_btn)
    public void onViewClicked() {
        this.finish();
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_message_detail;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("通知消息");
    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        MsgInfo msgInfo = (MsgInfo) intent.getSerializableExtra(MESSAGE_BEAN);
        if (null != msgInfo) {
            mMsgTimeTv.setTextColor(msgInfo.mDateValid ? Color.parseColor("#999999") : Color.parseColor("#fe0d00"));
            mMsgTimeTv.setText(msgInfo.mDateInfo);
            mMsgContentTv.setText(Html.fromHtml("<html><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + msgInfo.mMsg + "</html>"));
        }
    }

    @Override
    public void configViews() {

    }

}
