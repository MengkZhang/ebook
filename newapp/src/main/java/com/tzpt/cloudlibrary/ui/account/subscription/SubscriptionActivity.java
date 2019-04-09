package com.tzpt.cloudlibrary.ui.account.subscription;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.borrow.BorrowBookActivity;
import com.tzpt.cloudlibrary.ui.account.selfhelp.SelfBuyBookShelfActivity;
import com.tzpt.cloudlibrary.ui.ebook.EBookShelfActivity;
import com.tzpt.cloudlibrary.ui.video.VideoShelfActivity;
import com.tzpt.cloudlibrary.widget.CustomUserGridMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 订阅界面 我的书架activity
 * 需求： 显示借阅架、购书架、电子书架、视频架总数 其中电子书架显示：已阅读的电子书数量 是本地数据库存储，其他是网络存储，数据在mUserInfo的JavaBean中
 * Created by Administrator on 2017/12/14.
 */

public class SubscriptionActivity extends BaseActivity implements SubscriptionContract.View {


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SubscriptionActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.ebook_shelf_btn)
    CustomUserGridMenu mEBookShelfBtn;
    //借阅架：当前借阅+历史借阅书籍数量/逾期书籍
    @BindView(R.id.borrow_book_btn)
    CustomUserGridMenu mBorrowShelfBtn;
    //购书架
    @BindView(R.id.buy_book_btn)
    CustomUserGridMenu mBuyShelfBtn;
    //视频架
    @BindView(R.id.video_btn)
    CustomUserGridMenu mVideoShelfBtn;

    private SubscriptionPresenter mPresenter;

    @OnClick({R.id.titlebar_left_btn, R.id.ebook_shelf_btn,
            R.id.video_btn, R.id.buy_book_btn, R.id.borrow_book_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.borrow_book_btn://借阅架
                BorrowBookActivity.startActivity(this);
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.ebook_shelf_btn:
                EBookShelfActivity.startActivity(this);
                break;
            case R.id.video_btn:
                VideoShelfActivity.startActivity(this);
                break;
            case R.id.buy_book_btn:
                SelfBuyBookShelfActivity.startActivity(this);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_subscription;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("书架");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mPresenter = new SubscriptionPresenter();
        mPresenter.attachView(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
        mPresenter.getLocalEBookCount();
        mPresenter.getLocalVideoCount();
        mPresenter.getLocalUserInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
    }

    @Override
    public void setLocalEBookCount(int count) {
        if (count > 0) {
            mEBookShelfBtn.setInfo(String.valueOf(count));
        } else {
            mEBookShelfBtn.setInfo("");
        }
    }

    /**
     * 设置借阅架数量
     *
     * @param count        ：当前借阅
     * @param overdueCount ：逾期借阅
     */
    @Override
    public void setCurrentBorrowCountAndOverdueCount(int count, int overdueCount) {
        if (count > 0) {//当前有借阅书籍
            if (overdueCount > 0) {//当前有逾期借阅
                String overDueBookSum = String.valueOf(count) + "<font color= '#ee7853'>" + getResources().getString(R.string.over_due) + String.valueOf(overdueCount) + "</font>";
                mBorrowShelfBtn.setInfo(true, overDueBookSum);
            } else {               //当前没有逾期借阅
                mBorrowShelfBtn.setInfo(String.valueOf(count));
            }
        } else {//当前没有借阅书籍
            if (overdueCount > 0) {//当前有逾期借阅
                String onlyOverDueBookSum = "<font color= '#ee7853'>" + getResources().getString(R.string.over_due) + String.valueOf(overdueCount) + "</font>";
                mBorrowShelfBtn.setInfo(true,onlyOverDueBookSum);
            } else {               //当前没有逾期借阅
                mBorrowShelfBtn.setInfo("");
            }
        }
    }

    /**
     * 设置购书架数量
     *
     * @param count ：购书架的数量
     */
    @Override
    public void setBuyBookShelfCount(int count) {
        if (count > 0) {
            mBuyShelfBtn.setInfo(String.valueOf(count));
        } else {
            mBuyShelfBtn.setInfo("");
        }
    }

    /**
     * 设置视频架数量
     *
     * @param count ： 视频架数量
     */
    @Override
    public void setVideoShelfCount(int count) {
        if (count > 0) {
            mVideoShelfBtn.setInfo(String.valueOf(count));
        } else {
            mVideoShelfBtn.setInfo("");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }
}
