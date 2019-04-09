package com.tzpt.cloudlibrary.ui.account.borrow;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.widget.CustomUserGridMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 借阅界面
 * Created by Administrator on 2017/12/14.
 */

public class BorrowActivity extends BaseActivity implements BorrowContract.View {

    @BindView(R.id.borrow_appoint_btn)
    CustomUserGridMenu mBorrowAppointBtn;
    @BindView(R.id.borrow_current_btn)
    CustomUserGridMenu mBorrowCurrentBtn;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, BorrowActivity.class);
        context.startActivity(intent);
    }

    private BorrowPresenter mPresenter;

    @OnClick({R.id.borrow_appoint_btn, R.id.borrow_current_btn, R.id.borrow_history_btn,
            R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.borrow_appoint_btn://我的预约
                UserReservationActivity.startActivity(this);
                break;
            case R.id.borrow_current_btn://当前借阅
                BorrowBookActivity.startActivity(this);
                break;
            case R.id.borrow_history_btn://历史借阅
                BorrowBookActivity.startActivity(this);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_borrow;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("借阅");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mPresenter = new BorrowPresenter();
        mPresenter.attachView(this);

        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
//        mPresenter.getUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
        mPresenter.getLocalUserInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
    }

    @Override
    public void setAppointCount(int count) {
        if (count > 0) {
            mBorrowAppointBtn.setInfo(String.valueOf(count));
        } else {
            mBorrowAppointBtn.setInfo("");
        }
    }

    @Override
    public void setCurrentBorrowCountAndOverdueCount(int count, int overdueCount) {
        if (count > 0) {
            if (overdueCount > 0) {
                mBorrowCurrentBtn.setInfo(Html.fromHtml(getString(R.string.user_current_borrow_overdue, count, overdueCount)));
            } else {
                mBorrowCurrentBtn.setInfo(String.valueOf(count));
            }
        } else {
            mBorrowCurrentBtn.setInfo("");
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
