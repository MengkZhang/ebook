package com.tzpt.cloudlibrary.widget.searchview;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.tzpt.cloudlibrary.R;

/**
 * 搜索控件
 */
public class SearchBarLayout extends RelativeLayout {
    //search title bar
    private EditText mSearchContentEt;
    private DefaultTextView mDefaultTv;
    private TextView mCancelTv;
    private Drawable mDelIcon;
    private String mSearchHintName;
    private float mLeftEdge;
    private int mPadding10;
    private boolean mHasAnimation = true;
    private boolean mTranslationAnimator;
    private boolean mCancelAnimator;
    private boolean mIsDefaultModel = true;
    private Drawable mSearchIcon;

    private void setHasAnimation(boolean mHasAnimation) {
        this.mHasAnimation = mHasAnimation;
    }

    public SearchBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public SearchBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SearchBarLayout(Context context) {
        super(context);
        init(null, 0);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SearchBarLayout, defStyleAttr, 0);
        mSearchHintName = a.getString(R.styleable.SearchBarLayout_searchHintName);
        a.recycle();

        initViews();
        initData();
        initListener();
    }

    //初始化界面
    private void initViews() {
        mPadding10 = dip2px(10);
        inflate(getContext(), R.layout.view_search_bar_layout, this);
        mSearchContentEt = (EditText) findViewById(R.id.search_bar_center_content_et);
        mDefaultTv = (DefaultTextView) findViewById(R.id.search_bar_center_default_tv);
        mCancelTv = (TextView) findViewById(R.id.search_bar_right_cancel_tv);
    }

    //初始化数据
    private void initData() {
        setHasAnimation(true);
        //search icon
        mSearchIcon = getContext().getResources().getDrawable(R.mipmap.ic_recommend_book_search);
        mSearchIcon.setBounds(0, 0, mSearchIcon.getIntrinsicWidth(), mSearchIcon.getIntrinsicHeight());
        //editText delete icon
        mDelIcon = getResources().getDrawable(R.mipmap.ic_delete_text);
        mDelIcon.setBounds(0, 0, mDelIcon.getIntrinsicWidth(), mDelIcon.getIntrinsicHeight());
        mSearchContentEt.setPadding(mSearchIcon.getIntrinsicWidth() + mPadding10, 0, mPadding10, 0);
        //EditText
        if (!isInEditMode() && !TextUtils.isEmpty(mSearchHintName)) {
            mDefaultTv.setHint(mSearchHintName);
        }
        manageClearButton();
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocusFromTouch();
    }


    private void initListener() {
        mDefaultTv.setTextLeftEdgeListener(mListener);
        if (!isInEditMode()) {
            mCancelTv.setOnClickListener(mCancelClickListener);
        }
        mSearchContentEt.setOnEditorActionListener(mEditorActionListener);
        mSearchContentEt.addTextChangedListener(mTextWatcher);
        mSearchContentEt.setOnTouchListener(mOnTouchListener);
    }

    //editText 按钮监听
    TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            String keyWord = mSearchContentEt.getText().toString();
            if (mOnSearchListener != null) {
                mSearchContentEt.setSelection(keyWord.length());
                mOnSearchListener.searchContent(keyWord);
                closeKeyboard();
                return true;
            }
            return false;
        }
    };

    //监听TextView leftEdge
    DefaultTextView.LeftEdgeLister mListener = new DefaultTextView.LeftEdgeLister() {
        @Override
        public void callbackLeftEdge(float leftEdge) {
            mLeftEdge = leftEdge;
        }
    };

    //点击取消
    OnClickListener mCancelClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //默认状态
            if (mIsDefaultModel) {
                mOnSearchListener.dismissDialog();
                return;
            }
            //取消状态
            String keyWord = mSearchContentEt.getText().toString().trim();
            if (TextUtils.isEmpty(keyWord)) {
                cancelFocus();
                return;
            }
            //搜索状态
            if (mOnSearchListener != null) {
                mSearchContentEt.setSelection(keyWord.length());
                mOnSearchListener.searchContent(keyWord);
                closeKeyboard();
            }
        }
    };

    //监听
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() <= 0) {
                mDefaultTv.setHint(mSearchHintName);
            } else {
                mDefaultTv.setHint("");
            }
            manageClearButton();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    OnTouchListener mOnTouchListener = new OnTouchListener() {
        boolean result = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //点击删除按钮
                    if (event.getX() > mSearchContentEt.getWidth() - mSearchContentEt.getPaddingRight() - mDelIcon.getIntrinsicWidth()) {
                        mSearchContentEt.setText("");
                        mSearchContentEt.setSelection(0);
                        mSearchContentEt.requestFocus();
                        removeClearButton();
                        //还原
                        if (mIsDefaultModel) {
                            setFocusable(true);
                            setFocusableInTouchMode(true);
                            requestFocusFromTouch();

                            if (mHasAnimation) {
                                ObjectAnimator.ofFloat(mDefaultTv, "translationX", ViewHelper.getTranslationX(mDefaultTv), 0).start();
                            } else {
                                ViewHelper.setTranslationX(mDefaultTv, 0);
                            }
                            return true;
                        }
                        result = true;
                    } else {
                        if (mIsDefaultModel) {
                            focused();
                            manageClearButton();
                        } else {
                            showKeyboard();
                        }
                        result = true;
                    }
                    break;
            }
            return result;
        }
    };

    //展开
    private void focused() {
        setDefaultEditStatus();
        if (mIsDefaultModel) {
            mIsDefaultModel = false;
        }
        if (ViewHelper.getTranslationX(mDefaultTv) == -mLeftEdge || mTranslationAnimator) {
            return;
        }
        mHasAnimation = true;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mDefaultTv, "translationX", ViewHelper.getTranslationX(mDefaultTv), -mLeftEdge);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mTranslationAnimator = false;
                showKeyboard();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mTranslationAnimator = true;
            }
        });
        if (mHasAnimation) {
            objectAnimator.setInterpolator(new ExpoOutInterpolator());
            objectAnimator.setDuration(300);
            objectAnimator.start();
        } else {
            ViewHelper.setTranslationX(mDefaultTv, -mLeftEdge);
        }
        mSearchContentEt.setCursorVisible(true);
        mSearchContentEt.requestFocus();
    }

    //取消焦点-关闭
    public void cancelFocus() {
        //关闭高级搜索，列表及阴影布局
        mIsDefaultModel = true;

        if (mSearchContentEt.isFocused() && !mCancelAnimator) {
            mSearchContentEt.setText("");
            mSearchContentEt.setCursorVisible(false);
            mDefaultTv.setHint(mSearchHintName);

            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mDefaultTv, "translationX", ViewHelper.getTranslationX(mDefaultTv), 0);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCancelAnimator = false;
                    setCloseEditStatus();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    mCancelAnimator = true;
                }
            });
            if (mHasAnimation) {
                objectAnimator.setInterpolator(new ExpoOutInterpolator());
                objectAnimator.setDuration(300);
                objectAnimator.start();
            } else {
                ViewHelper.setTranslationX(mDefaultTv, 0);
            }
        }
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.requestFocus();
        hiddenKeyboard();
    }

    //隐藏或显示键盘
    public void hiddenKeyboard() {
        InputMethodManager manager = ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
        // 判断软键盘是否打开
        if (manager != null) {
            if (manager.isActive()) {
                manager.hideSoftInputFromWindow(mSearchContentEt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } else {
                manager.showSoftInputFromInputMethod(mSearchContentEt.getWindowToken(), 0);
            }
        }
    }

    //显示键盘
    public void showKeyboard() {
        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            mSearchContentEt.requestFocus();
            manager.showSoftInput(mSearchContentEt, 0);
        }
    }

    //关闭键盘
    public void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        imm.hideSoftInputFromWindow(mSearchContentEt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //管理清除按钮
    private void manageClearButton() {
        if (TextUtils.isEmpty(mSearchContentEt.getText().toString())) {
            removeClearButton();
        } else {
            addClearButton();
        }
    }

    //包含内容，显示删除icon
    private void addClearButton() {
        mCancelTv.setText("搜索");
        mSearchContentEt.setCompoundDrawables(mSearchContentEt.getCompoundDrawables()[0], mSearchContentEt.getCompoundDrawables()[1], mDelIcon, mSearchContentEt.getCompoundDrawables()[3]);
    }

    private void removeClearButton() {
        mCancelTv.setText("取消");
        mSearchContentEt.setCompoundDrawables(mSearchContentEt.getCompoundDrawables()[0], mSearchContentEt.getCompoundDrawables()[1], null, mSearchContentEt.getCompoundDrawables()[3]);
    }

    //当前展开状态的编辑框-白色背景
    private void setDefaultEditStatus() {
        setEditStatus(true);
    }

    //当前关闭状态的编辑框-设置为默认背景
    private void setCloseEditStatus() {
        setEditStatus(false);
    }

    private void setEditStatus(boolean open) {
        mSearchContentEt.setPadding(mSearchIcon.getIntrinsicWidth() + mPadding10, 0, mPadding10, 0);
    }

    //listener
    private OnSearchBarListener mOnSearchListener;

    public interface OnSearchBarListener {

        void searchContent(String keyWord);

        void dismissDialog();
    }

    public void setSearchBarListener(OnSearchBarListener listener) {
        this.mOnSearchListener = listener;
    }

}
