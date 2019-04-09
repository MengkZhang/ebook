package com.tzpt.cloudlibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;

import java.lang.reflect.Field;

/**
 * 评论控件
 * Created by Administrator on 2017/6/15.
 */
public class CustomDiscussView extends LinearLayout {

    private TextView mDiscussSendTv;
    private TextView mDiscussListNumTv;
    private ImageButton mDiscussListBtn;
    private View mNoLoginCoverView;
    private View mDiscussView;

    private String mHintName;
    private boolean mHasEmptyView;
    private boolean mHasMsgIcon;
    private LinearLayout mLayoutMain;
    private EditText mDiscussContentEt;
    private RelativeLayout mDiscussCommentContentLl;
    // 软键盘的显示状态
    private boolean mShowKeyboard;

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.discuss_comment_content_ll:
                    if (null != mListener) {
                        mListener.discussToListClick();
                    }
                    break;
                case R.id.discuss_send_tv:
                    if (null != mListener) {
                        String content = mDiscussContentEt.getText().toString().trim();
                        if (!TextUtils.isEmpty(content)) {
                            mListener.discussToSendClick(content);
                        }
                    }
                    break;
                case R.id.discuss_comment_content_et:
                    if (null != mListener) {
                        mListener.discussEtClick();
                    }
                    break;
                case R.id.no_login_cover_view:
                    if (mListener != null) {
                        mListener.discussCoverClick();
                    }
                    break;
                case R.id.discuss_comment_layout_v:
                    if (null != mListener) {
                        mListener.hideDiscussLayout();
                    }
                    break;
            }
        }
    };


    public CustomDiscussView(Context context) {
        this(context, null);
    }

    public CustomDiscussView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomDiscussView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomDiscussView, defStyleAttr, 0);
            try {
                mHintName = a.getString(R.styleable.CustomDiscussView_hintName);
                mHasEmptyView = a.getBoolean(R.styleable.CustomDiscussView_emptyView, false);
                mHasMsgIcon = a.getBoolean(R.styleable.CustomDiscussView_hasMsgIcon, true);
            } finally {
                a.recycle();
            }
        }
        initView();
    }


    private void initView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.common_discuss_view, this);
        mDiscussSendTv = (TextView) v.findViewById(R.id.discuss_send_tv);
        mDiscussListNumTv = (TextView) v.findViewById(R.id.discuss_discuss_list_num_tv);
        mDiscussListBtn = (ImageButton) v.findViewById(R.id.discuss_discuss_list_btn);
        mDiscussContentEt = (EditText) v.findViewById(R.id.discuss_comment_content_et);
        mLayoutMain = (LinearLayout) v.findViewById(R.id.discuss_comment_layout_main);
        mDiscussCommentContentLl = (RelativeLayout) v.findViewById(R.id.discuss_comment_content_ll);
        mNoLoginCoverView = findViewById(R.id.no_login_cover_view);

        mLayoutMain.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
        mDiscussContentEt.setHint(!TextUtils.isEmpty(mHintName) ? mHintName : "");
        mDiscussCommentContentLl.setOnClickListener(mOnClickListener);
        mDiscussSendTv.setOnClickListener(mOnClickListener);
        mDiscussContentEt.setOnClickListener(mOnClickListener);
        mNoLoginCoverView.setOnClickListener(mOnClickListener);
        mDiscussCommentContentLl.setVisibility(mHasMsgIcon ? View.VISIBLE : View.GONE);
        mDiscussView = v.findViewById(R.id.discuss_comment_layout_v);
    }

    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            mLayoutMain.getWindowVisibleDisplayFrame(r);
            int minKeyboardHeight = 150;
            int statusBarHeight = getStatusBarHeight(getContext());
            int screenHeight = mLayoutMain.getRootView().getHeight();
            int height = screenHeight - (r.bottom - r.top);
            if (mShowKeyboard) {
                //隐藏键盘
                if (height - statusBarHeight < minKeyboardHeight) {
                    mShowKeyboard = false;
                    //如果内容为空，则恢复默认图标
                    if (TextUtils.isEmpty(mDiscussContentEt.getText().toString().trim())) {
                        mDiscussCommentContentLl.setVisibility(mHasMsgIcon ? View.VISIBLE : View.GONE);
                        mDiscussListBtn.setVisibility(View.VISIBLE);
                        mDiscussSendTv.setVisibility(View.GONE);
                    }
                    if (mHasEmptyView) {
                        mDiscussView.setVisibility(View.GONE);
                    }
                    if (null != mListener) {
                        mListener.hideDiscussLayout();
                    }
                }
            } else {
                //显示键盘
                if (height - statusBarHeight > minKeyboardHeight) {
                    mShowKeyboard = true;
                    mDiscussCommentContentLl.setVisibility(View.GONE);
                    mDiscussListBtn.setVisibility(View.GONE);
                    mDiscussSendTv.setVisibility(View.VISIBLE);
                    mDiscussContentEt.setSelection(mDiscussContentEt.getText().toString().trim().length());
                    mDiscussContentEt.requestFocus();
                    if (mHasEmptyView) {
                        mDiscussView.setVisibility(View.VISIBLE);
                        mDiscussView.setOnClickListener(mOnClickListener);
                    }
                }
            }
        }
    };

    /**
     * 获取状态栏高度
     */
    private int getStatusBarHeight(Context context) {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setDiscussNumber(int discussNumber) {
        if (discussNumber > 0) {
            mDiscussListNumTv.setVisibility(View.VISIBLE);
            mDiscussListNumTv.setText(String.valueOf(discussNumber));
        } else {
            mDiscussListNumTv.setVisibility(View.GONE);
        }

    }

    public void setLoginStatus(boolean isLogin) {
        if (isLogin) {
            mNoLoginCoverView.setVisibility(View.GONE);
        } else {
            mNoLoginCoverView.setVisibility(View.VISIBLE);
        }
    }

    public void setSendStatus(boolean canSend) {
        if (canSend) {
            mDiscussSendTv.setTextColor(getResources().getColor(R.color.color_aa886c));
            mDiscussSendTv.setClickable(true);
        } else {
            mDiscussSendTv.setTextColor(getResources().getColor(R.color.color_cccccc));
            mDiscussSendTv.setClickable(false);
        }
    }

    public void clearDiscussContent() {
        if (mDiscussContentEt != null) {
            mDiscussContentEt.setText("");
            mDiscussContentEt.setHint("写评论");
            mDiscussCommentContentLl.setVisibility(mHasMsgIcon ? View.VISIBLE : View.GONE);
            mDiscussListBtn.setVisibility(View.VISIBLE);
            mDiscussSendTv.setVisibility(View.GONE);
        }
    }

    public void setHintText(String hintText) {
        if (mDiscussContentEt != null) {
            mDiscussContentEt.setHint(hintText);
        }
    }

    private DiscussListener mListener;

    public void setDiscussListener(DiscussListener listener) {
        this.mListener = listener;
    }

    public interface DiscussListener {

        void discussToListClick();

        void discussToSendClick(String content);

        void discussEtClick();

        void discussCoverClick();

        void hideDiscussLayout();
    }
}
