package com.tzpt.cloudlibrary.ui.account.interaction;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.LoginActivity;
import com.tzpt.cloudlibrary.ui.account.borrow.ReaderNotesActivity;
import com.tzpt.cloudlibrary.ui.account.borrow.UserReservationActivity;
import com.tzpt.cloudlibrary.ui.readers.ActivityListActivity;
import com.tzpt.cloudlibrary.ui.share.ShareAppActivity;
import com.tzpt.cloudlibrary.widget.CustomUserGridMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 互动界面
 * Created by Administrator on 2017/12/14.
 */

public class InteractionActivity extends BaseActivity implements InteractionContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, InteractionActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.user_register_btn)
    CustomUserGridMenu mUserRegisterBtn;
    @BindView(R.id.user_msg_btn)
    CustomUserGridMenu mUserMsgBtn;
    //我的预约
    @BindView(R.id.reservation_book_btn)
    CustomUserGridMenu mUserReservationBookBtn;


    private InteractionPresenter mPresenter;

    @OnClick({R.id.titlebar_left_btn, R.id.recommend_new_book_btn, R.id.user_register_btn,
            R.id.user_msg_btn, R.id.share_app_btn, R.id.borrow_notes_btn, R.id.reservation_book_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.reservation_book_btn://我的预约
                if (mPresenter.isLogin()) {
                    UserReservationActivity.startActivity(this);
                } else {
                    LoginActivity.startActivity(this);
                }
                break;
            case R.id.recommend_new_book_btn:
                if (mPresenter.isLogin()) {
                    RecommendNewBookActivity.startActivity(this);
                } else {
                    LoginActivity.startActivity(this);
                }
                break;
            case R.id.user_register_btn:
                if (mPresenter.isLogin()) {
                    ActivityListActivity.startActivity(this, 1);
                } else {
                    LoginActivity.startActivity(this);
                }
                break;
            case R.id.user_msg_btn:
                if (mPresenter.isLogin()) {
                    MyMessageActivity.startActivity(this);
                } else {
                    LoginActivity.startActivity(this);
                }
                break;
            case R.id.share_app_btn:
                ShareAppActivity.startActivity(this);
                break;
            case R.id.borrow_notes_btn://读书笔记
                if (mPresenter.isLogin()) {
                    ReaderNotesActivity.startActivity(this);
                } else {
                    LoginActivity.startActivity(this);
                }
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_interaction;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("互动");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mPresenter = new InteractionPresenter();
        mPresenter.attachView(this);
        //注册事件
        EventBus.getDefault().register(this);

        setRegisterCount(0);
        setMsgCount(0);
    }

    @Override
    public void configViews() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
        mPresenter.getUserInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
    }

    @Override
    public void setRegisterCount(int count) {
        if (count > 0) {
            mUserRegisterBtn.setInfo(String.valueOf(count));
        } else {
            mUserRegisterBtn.setInfo("");
        }
    }

    @Override
    public void setMsgCount(int count) {
        if (count > 0) {
            mUserMsgBtn.setInfo(String.valueOf(count));
        } else {
            mUserMsgBtn.setInfo("");
        }
    }

    /**
     * 我的预约数量
     *
     * @param count ： 预约数量
     */
    @Override
    public void setReservationCount(int count) {
        if (count > 0) {
            mUserReservationBookBtn.setInfo(String.valueOf(count));
        } else {
            mUserReservationBookBtn.setInfo("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }

        //取消事件订阅
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receviceLoginOut(AccountMessage loginMessage) {
        if (loginMessage.mIsLoginOut) {
            finish();
        }
    }
}
