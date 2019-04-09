package com.tzpt.cloudlibrary.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.AppVersionBean;
import com.tzpt.cloudlibrary.receiver.NetStatusReceiver;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.interaction.MyMessageActivity;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.CustomDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/6/9.
 */

public class MainActivity extends BaseActivity implements MainContract.View {

    private static final int NUM_ITEMS = 3;

    @BindView(R.id.fragment_content)
    FrameLayout mFragmentContent;
    @BindView(R.id.main_tab_group)
    RadioGroup mMainTabGroup;
    @BindView(R.id.msg_count_tv)
    TextView mMsgCountTv;

    private long exitTime;                          //退出时间
    private static final int TOAST_TIME = 2000;     //弹出提示显示的时间
    private MainPresenter mPresenter;

    private NetStatusReceiver mNetStatusReceiver;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        mPresenter = new MainPresenter();
        mPresenter.attachView(this);
        mPresenter.checkVersionInfo();
        mPresenter.getUserInfo();
        mPresenter.checkAttentionLib();
        //注册事件
        EventBus.getDefault().register(this);

        if (mNetStatusReceiver == null) {
            mNetStatusReceiver = new NetStatusReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetStatusReceiver, filter);
    }

    @Override
    public void configViews() {

    }

    private void init() {
        mMainTabGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                int index = 0;
                switch (checkedId) {
                    case R.id.home_page_rb:
                        index = 0;
                        break;
                    case R.id.attention_lib_rb:
                        index = 1;
                        break;
                    case R.id.user_rb:
                        index = 2;
                        break;
                }
                Fragment fragment = (Fragment) fragments.instantiateItem(mFragmentContent, index);
                fragments.setPrimaryItem(mFragmentContent, 0, fragment);
                fragments.finishUpdate(mFragmentContent);
            }
        });
    }


    private FragmentStatePagerAdapter fragments = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = new TabHomeFragment();
                    break;
                case 1:
                    fragment = new TabAttentionLibFragment();
                    break;
                case 2:
                    fragment = new TabUserFragment();
                    break;
                default:
                    fragment = new TabHomeFragment();
                    break;
            }
            return fragment;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getLocalInfo();
        UmengHelper.setUmengResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 如果按了返回键
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            int checkedId = mMainTabGroup.getCheckedRadioButtonId();
            if (checkedId != R.id.home_page_rb) {
                mMainTabGroup.check(R.id.home_page_rb);
            } else if ((System.currentTimeMillis() - exitTime) > TOAST_TIME) {
                exitTime = System.currentTimeMillis();
                ToastUtils.showSingleToast(R.string.click_again_leave_cloud_library);
            } else {
                finish();
            }
        }
        return false;
    }


    /**
     * 接收浏览器信息,进入资讯或者读友会
     */
    private void handlerInformationWebViewDetail() {
        Intent intent = getIntent();
        if (null == intent) {
            return;
        }
        String scheme = intent.getScheme();
        if (null == scheme) {
            return;
        }
        Uri uri = intent.getData();
        String url = uri.toString();
        if (null == url || !url.contains("ytsg://")) {
            return;
        }
        //type 1资讯 2读友会
        try {
            if (url.contains(",")) {
                String newUrl = url.replace("ytsg://", "");
                String[] mUrl = newUrl.split(",");
                String type = mUrl[0];
                String myLink = mUrl[1];
            }
        } catch (Exception e) {
        }
    }


    private AppUpdateDialogFragment mAppUpdateDialogFragment;

    @Override
    public void setUserRbStatus(int msgCount) {
        mPresenter.handleShortCutBadger(msgCount);
        if (msgCount > 0) {
            mMsgCountTv.setVisibility(View.VISIBLE);
            if (msgCount > 99) {
                mMsgCountTv.setText("99+");
            } else {
                mMsgCountTv.setText(String.valueOf(msgCount));
            }
        } else {
            mMsgCountTv.setVisibility(View.GONE);
        }
    }

    //设置APP版本信息
    @Override
    public void setAppVersionInfo(AppVersionBean bean) {
        if (null == mAppUpdateDialogFragment) {
            mAppUpdateDialogFragment = new AppUpdateDialogFragment();
        }
        if (mAppUpdateDialogFragment.isAdded()) {
            return;
        }
        mAppUpdateDialogFragment.show(getFragmentManager(), "AppUpdateDialogFragment");
        mAppUpdateDialogFragment.setAppUpdateBean(bean);
    }

    @Override
    public void showAttentionLib(boolean isShow) {
        init();
        if (isShow) {
            mMainTabGroup.check(R.id.attention_lib_rb);
        } else {
            mMainTabGroup.check(R.id.home_page_rb);
        }
        handlerInformationWebViewDetail();
    }

    @Override
    public void setUserBorrowOverdueMsg(int borrowOverdueSum) {
        final CustomDialog delDialog = new CustomDialog(this, R.style.DialogTheme, "");
        delDialog.setCancelable(false);
        delDialog.hasNoCancel(true);
        delDialog.setText(getString(R.string.has_overdue_borrow_book_sum, borrowOverdueSum));
        delDialog.setTitle("逾期提示");
        delDialog.setOkText("查看详情");
        delDialog.setCancelText("忽略");
        delDialog.show();
        delDialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                delDialog.dismiss();
                MyMessageActivity.startActivity(MainActivity.this);
            }

            @Override
            public void onClickCancel() {
                delDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        //取消事件订阅
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mNetStatusReceiver);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginOut(AccountMessage loginMessage) {
        if (loginMessage.mIsLoginOut) {
            setUserRbStatus(0);
        }
//        else if (loginMessage.mIsRefreshUserInfo) {
//
//        }
    }
}
