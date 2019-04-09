package com.tzpt.cloundlibrary.manager.widget.searchview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.utils.KeyboardUtils;

/**
 * 自定义搜索控件
 *
 * @author ZhiqiangJia
 */
public class CustomAnimationSearchView extends RelativeLayout implements OnClickListener, TextWatcher {

    private Handler hanlder = new Handler();
    private EditText searchEditText, searchBox;
    private ImageView clearSearchBox;
    private RelativeLayout mRelativeLayout;
    private Button cancel;
    private CallbackOnTouchDismissSearchBar callback;
    private AnimatorSet animatorLeft;
    private AnimatorSet animatorRight;
    private int realWidth = -1;
    private int status = 1; // 0 默认状态 1.要恢复的状态
    private float editTextX;
    private boolean isShowing = false;
    private boolean noSearchButton = false;
    private boolean isSearchHasContentStatus = false;
    private boolean closing = false; //是否正在关闭
    private static final int ANIMATION_TIME = 300;

    public CustomAnimationSearchView(Context context) {
        super(context);
        initilizeViews(context);
    }

    public CustomAnimationSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initilizeViews(context);
    }

    public CustomAnimationSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initilizeViews(context);
    }

    private void initilizeViews(final Context context) {
        inflate(context, R.layout.view_search_view, this);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout1);
        cancel = (Button) findViewById(R.id.cancel);
        searchEditText = (EditText) findViewById(R.id.search_box);
        searchBox = (EditText) findViewById(R.id.search_box2);
        clearSearchBox = (ImageView) findViewById(R.id.clear_search_box);

        searchBox.addTextChangedListener(this);
        clearSearchBox.setOnClickListener(this);
        cancel.setOnClickListener(this);
        searchEditText.setOnClickListener(this);
        mRelativeLayout.setOnClickListener(this);
        mRelativeLayout.setFocusable(true);
        mRelativeLayout.requestFocus();

        setsearchEditText(false);
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_SEARCH) {
                    if (null != callback) {
                        String searchContent = searchBox.getText().toString().trim();
                        callback.callbackActionSearch(searchContent);
                    }
                    return true;
                }
                return false;
            }
        });
        cancel.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                cancel.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                cancel.setPivotX(cancel.getMeasuredWidth());
                if (realWidth == -1) {
                    realWidth = cancel.getMeasuredWidth();
                    setWidthToCancelButton(0);
                }
            }
        });
        ViewTreeObserver vto2 = searchEditText.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                searchEditText.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                editTextX = searchEditText.getX();

            }
        });

    }

    /**
     * 没有搜索按钮
     *
     * @param noSearchButton
     */
    public void setNoSearchButton(boolean noSearchButton) {
        this.noSearchButton = noSearchButton;
    }

    public void setTextHint(String hintText) {
        searchEditText.setHint(hintText);
        searchEditText.setTextColor(Color.WHITE);
        searchBox.setHint(hintText);
        searchBox.setTextColor(Color.WHITE);
    }

    public void setText(String hintText) {
        if (TextUtils.isEmpty(hintText)) {
            return;
        }
        searchEditText.setText(hintText);
        searchBox.setText(hintText);
        searchBox.setSelection(hintText.length());
    }

    private void setEditTextVisibility(boolean visibility) {
        searchBox.setVisibility(visibility ? VISIBLE : GONE);
        searchEditText.setVisibility(visibility ? GONE : VISIBLE);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //取消搜索按钮
        if (noSearchButton) {
            String content = getEditContent();
            if (!TextUtils.isEmpty(content)) {
                clearSearchBox.setVisibility(View.VISIBLE);
                if (null != callback) {
                    callback.hasText(true, content);
                }
            } else {
                clearSearchBox.setVisibility(View.GONE);
                if (null != callback) {
                    callback.hasText(false, "");
                }
            }
            return;
        }
        String content = getEditContent();
        if (!TextUtils.isEmpty(content)) {
            cancel.setText("搜索");
            isSearchHasContentStatus = true;
            clearSearchBox.setVisibility(View.VISIBLE);
            if (null != callback) {
                callback.hasText(true, content);
            }
        } else {
            isSearchHasContentStatus = false;
            cancel.setText("取消");
            clearSearchBox.setVisibility(View.GONE);
            if (null != callback) {
                callback.hasText(false, "");
            }
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.clear_search_box:
                clearSearchBox.setVisibility(View.GONE);
                searchBox.setText("");
                searchEditText.setText("");
                break;
            case R.id.cancel:
                if (noSearchButton) {
                    showHideCancelControl(false, true);
                    searchEditText.setText("");
                    searchBox.setText("");
                    return;
                }
                //开始搜索
                if (isSearchHasContentStatus) {
                    if (null != callback) {
                        callback.callbackActionSearch(getEditContent());
                    }
                } else {
                    status = 0;
                    showHideCancelControl(false, true);
                    searchBox.setText("");
                    searchEditText.setText("");
                }
                break;
            case R.id.search_box:
                showSearchView();
                break;
            case R.id.relativeLayout1:
                showSearchView();
                break;
            default:
                break;
        }

    }

    /**
     * 展开搜索bar
     */
    private void showSearchView() {
        if (!isShowing) {
            hanlder.post(new Runnable() {
                @Override
                public void run() {
                    showHideCancelControl(true, true);
                }
            });
        }
    }

    /**
     * 隐藏取消按钮
     *
     * @param show
     */

    private void showHideCancelControl(boolean show, boolean isNeedAnimation) {
        this.closing = true;
        this.isShowing = show;
        if (!show) {
            if (status == 1) {
                return;
            }
            setEditTextVisibility(false);
            // init left animation
            initializeLeftAnimation(isNeedAnimation);
        } else {
            if (cancel.getMeasuredWidth() > 0) {
                return;
            }
            //init right animation
            initializeRightAnimation();
            if (null != callback) {
                callback.callbackShowSearchBar();
            }
            searchEditText.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    /**
     * init left animation -- default status
     *
     * @param isNeedAnimation
     */
    private void initializeLeftAnimation(boolean isNeedAnimation) {
        if (null == animatorLeft) {
            animatorLeft = new AnimatorSet();
            ValueAnimator anim = ValueAnimator
                    .ofInt(cancel.getMeasuredWidth() == 0 ? realWidth : cancel.getMeasuredWidth(), 0);
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(searchEditText, "translationX", -editTextX, 0);
            animatorLeft.playTogether(anim, animator1);
            animator1.setInterpolator(new ExpoOutInterpolator());
            anim.addUpdateListener(new AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    setWidthToCancelButton(val);
                }
            });
            animatorLeft.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    searchEditText.setLayerType(View.LAYER_TYPE_NONE, null);
                    if (null != callback) {
                        callback.callbackDismissSearchBar();
                        closing = false;
                    }
                    setsearchEditText(false);
                    KeyboardUtils.hideSoftInput(searchBox);
                }
            });
        }

        animatorLeft.setDuration(isNeedAnimation ? ANIMATION_TIME : 0);
        animatorLeft.start();

    }

    /**
     * init right animation -search status
     */
    private void initializeRightAnimation() {
        if (null == animatorRight) {
            animatorRight = new AnimatorSet();
            ValueAnimator anim = ValueAnimator.ofInt(0, realWidth);
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(searchEditText, "translationX", 0, -editTextX);
            animatorRight.playTogether(anim, animator1);
            animatorRight.setInterpolator(new ExpoOutInterpolator());
            anim.addUpdateListener(new AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    setWidthToCancelButton(val);
                }
            });
            animatorRight.setDuration(ANIMATION_TIME);
            animatorRight.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    setsearchEditText(true);
                    setEditTextVisibility(true);
                    searchBox.setLayerType(View.LAYER_TYPE_NONE, null);
                    searchBox.findFocus();
                    searchBox.requestFocus();
                    closing = false;
                    if (getEditContentSize() > 0) {
                        KeyboardUtils.hideSoftInput(searchBox);
                    } else {
                        KeyboardUtils.showSoftInput(searchBox);
                    }

                }
            });
        }
        animatorRight.start();
    }

    /**
     * set cancel button width
     *
     * @param width
     */
    private void setWidthToCancelButton(int width) {
        ViewGroup.LayoutParams layoutParams = cancel.getLayoutParams();
        layoutParams.width = width;
        cancel.setLayoutParams(layoutParams);
    }

    public void setsearchEditText(boolean focusable) {
        if (focusable) {
            searchEditText.setClickable(false);
            searchEditText.setFocusableInTouchMode(true);
            searchEditText.findFocus();
            searchEditText.requestFocus();
            status = 0;
        } else {
            searchEditText.setClickable(true);
            searchEditText.clearFocus();
            searchEditText.setFocusableInTouchMode(false);
            status = 1;
        }
        searchEditText.setCursorVisible(focusable);
    }

    public void setSearchBarListener(CallbackOnTouchDismissSearchBar callback) {
        this.callback = callback;
    }

    /**
     * 初始化，恢复searchBar
     */
    public void resetSearchBar() {
        status = 0;
        touchResetSearchView(true);
    }

    public interface CallbackOnTouchDismissSearchBar {

        void callbackShowSearchBar();

        void callbackDismissSearchBar();

        void hasText(boolean hasText, String content);

        void callbackActionSearch(String searchContent);

        void onEditTextClick();

    }

    /**
     * 为onTouch 调用 重置searchView
     */
    public void touchResetSearchView(boolean isNeedAnimation) {
        if (closing) {
            return;
        }
        showHideCancelControl(false, isNeedAnimation);
        searchBox.setText("");
        searchEditText.setText("");
    }


    public EditText getEditText() {
        return searchBox;
    }

    public String getEditContent() {
        return searchBox.getText().toString().trim();
    }

    public int getEditContentSize() {
        return searchBox.getText().toString().trim().length();
    }

    public void resetEditContent() {
        searchBox.setText("");
    }

    /**
     * 关闭键盘
     */
    public void closeKeyBoard() {
        if (null != searchBox) {
            KeyboardUtils.hideSoftInput(searchBox);
        }
    }
}
