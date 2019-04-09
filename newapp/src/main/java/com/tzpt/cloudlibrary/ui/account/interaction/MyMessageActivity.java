package com.tzpt.cloudlibrary.ui.account.interaction;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.TabMenuBean;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.library.MyPagerAdapter;
import com.tzpt.cloudlibrary.widget.tablayout.RecyclerTabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的消息
 */
public class MyMessageActivity extends BaseActivity implements MyMessageContract.View {
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MyMessageActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.recycler_tab_layout)
    RecyclerTabLayout mRecyclerTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<TabMenuBean> mTabList = new ArrayList<>();
    private MyMessagePresenter mPresenter;

    @OnClick({R.id.titlebar_left_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_my_message;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("我的消息");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);

        mPresenter = new MyMessagePresenter();
        mPresenter.attachView(this);
        mPresenter.getUserUnreadMsgCount();
    }

    @Override
    public void configViews() {
        //setSupportSlideBack(false);
    }

    @Override
    public void setNormalAndOverdueMsgCount(int normalMsgCount, int overdueMsgCount) {
        if (mTabList == null || mTabList.size() == 0) {
            TabMenuBean menu1 = new TabMenuBean("逾期消息");
            menu1.setCount(overdueMsgCount);
            TabMenuBean menu2 = new TabMenuBean("其它消息");
            menu2.setCount(normalMsgCount);
            mTabList.add(menu1);
            mTabList.add(menu2);

            NormalMsgFragment normalMsgFragment = new NormalMsgFragment();
            OverdueMsgFragment overdueMsgFragment = new OverdueMsgFragment();

            mFragmentList.add(overdueMsgFragment);
            mFragmentList.add(normalMsgFragment);

            final MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), mFragmentList, mTabList);
            mViewPager.setAdapter(pagerAdapter);
            mViewPager.setOffscreenPageLimit(2);
            mRecyclerTabLayout.setUpWithViewPager(mTabList, mViewPager);
        } else {
            mTabList.get(1).setCount(normalMsgCount);
            mTabList.get(0).setCount(overdueMsgCount);
            mRecyclerTabLayout.notifyDataSetChanged();
        }

    }


    @Override
    protected void onDestroy() {
        mRecyclerTabLayout.clearList();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receviceLoginOut(AccountMessage loginMessage) {
        if (loginMessage.mIsLoginOut) {
            finish();
        } else if (loginMessage.mIsRefreshUserInfo) {
            mPresenter.getUserUnreadMsgCount();
        }
    }
}
