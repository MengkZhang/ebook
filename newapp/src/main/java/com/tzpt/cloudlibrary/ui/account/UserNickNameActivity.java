package com.tzpt.cloudlibrary.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.tzpt.cloudlibrary.AppIntentGlobalName;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 昵称
 */
public class UserNickNameActivity extends BaseActivity implements
        UserNickNameContract.View {

    private static final String NICK_NAME = "nick_name";

    public static void startActivityForResult(Activity activity, String nickName, int requestCode) {
        Intent intent = new Intent(activity, UserNickNameActivity.class);
        intent.putExtra(NICK_NAME, nickName);
        activity.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.user_nick_name_et)
    EditText mUserNickNameEt;
    @BindView(R.id.user_nick_name_delete_iv)
    ImageView mUserNickNameDeleteIv;
    @BindView(R.id.loading_progress_view)
    LoadingProgressView mProgressView;
    private UserNickNamePresenter mPresenter;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_txt_btn, R.id.user_nick_name_delete_iv,
            R.id.user_nick_name_root_view_ll})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_txt_btn:
                KeyboardUtils.hideSoftInput(mUserNickNameEt);
                mPresenter.saveUserNickName(mUserNickNameEt.getText().toString().trim());
                break;
            case R.id.user_nick_name_delete_iv:
                mUserNickNameEt.setText("");
                break;
            case R.id.user_nick_name_root_view_ll:
                KeyboardUtils.hideSoftInput(mUserNickNameEt);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_nick_name;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("昵称");
        mCommonTitleBar.setRightBtnText("完成");
        setTitleBarState(false);
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
        mPresenter = new UserNickNamePresenter();
        mPresenter.attachView(this);

        String nickName = getIntent().getStringExtra(NICK_NAME);
        if (!TextUtils.isEmpty(nickName)) {
            mUserNickNameEt.setText(nickName);
            mUserNickNameEt.setSelection(nickName.length());
            mUserNickNameEt.setCursorVisible(true);
            mUserNickNameEt.requestFocus();

            mUserNickNameDeleteIv.setVisibility(View.VISIBLE);
            setTitleBarState(true);

        }
    }

    /**
     * 设置完成按钮状态
     *
     * @param enable 可用
     */
    private void setTitleBarState(boolean enable) {
        if (enable) {
            mCommonTitleBar.setRightBtnTextClickAble(true);
            mCommonTitleBar.setRightBtnTextColor(mContext.getResources().getColor(R.color.color_8a633d));
        } else {
            mCommonTitleBar.setRightBtnTextClickAble(false);
            mCommonTitleBar.setRightBtnTextColor(mContext.getResources().getColor(R.color.color_999999));
        }
    }

    @Override
    public void configViews() {
        mUserNickNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mUserNickNameDeleteIv.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                setTitleBarState(s.length() > 0);
            }
        });
    }

    @Override
    public void showProgressDialog() {
        mProgressView.showProgressLayout();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressView.hideProgressLayout();
    }

    @Override
    public void updateUserNickNameSuccess(String nickName) {
        KeyboardUtils.hideSoftInput(mUserNickNameEt);
        Intent intent = new Intent();
        intent.putExtra(AppIntentGlobalName.NICK_NAME, nickName);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showMsgTipsDialog(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }

    @Override
    public void pleaseLoginTips() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recivceLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPresenter.detachView();
    }

}
