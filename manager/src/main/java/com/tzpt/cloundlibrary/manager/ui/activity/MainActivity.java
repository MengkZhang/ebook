package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.Constant;
import com.tzpt.cloundlibrary.manager.bean.MsgCountBean;
import com.tzpt.cloundlibrary.manager.bean.UpdateAppBean;
import com.tzpt.cloundlibrary.manager.event.LoginOutEvent;
import com.tzpt.cloundlibrary.manager.ui.contract.MainContract;
import com.tzpt.cloundlibrary.manager.ui.fragment.HomePageFragment;
import com.tzpt.cloundlibrary.manager.ui.fragment.UserFragment;
import com.tzpt.cloundlibrary.manager.ui.presenter.MainPresenter;
import com.tzpt.cloundlibrary.manager.utils.KeyboardUtils;
import com.tzpt.cloundlibrary.manager.utils.ToastUtils;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomLoadingDialog;
import com.tzpt.cloundlibrary.manager.widget.popupwindow.UpdateAppPPW;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    public static void startActivity(Context context, int messageFlag, boolean fromLogin) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constant.TO_MESSAGE_VIEW, messageFlag);
        intent.putExtra(Constant.FROM_LOGIN, fromLogin);
        context.startActivity(intent);
    }

    @BindView(R.id.fragment_content)
    FrameLayout mFragmentContent;
    @BindView(R.id.home_page_rb)
    RadioButton mHomePageRb;
    @BindView(R.id.main_tab_group)
    RadioGroup mMainTabGroup;
    @BindView(R.id.msg_count_tv)
    TextView mUnreadMsgCountTv;

    Unbinder unbinder;
    long exitTime = 0;

    private CustomLoadingDialog mLoadingDialog;//进度条
    private MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        init();
        mMainTabGroup.check(R.id.home_page_rb);
        responsePushMessage();
        EventBus.getDefault().register(this);

        boolean fromLogin = getIntent().getBooleanExtra(Constant.FROM_LOGIN, false);
        if (!fromLogin) {
            mPresenter = new MainPresenter();
            mPresenter.attachView(this);
            mPresenter.getLoginUserInfo(this);
        }
    }

    //接收推送消息
    private void responsePushMessage() {
        int messageFlag = getIntent().getIntExtra(Constant.TO_MESSAGE_VIEW, -1);
        if (messageFlag == 1000) {
            mMainTabGroup.check(R.id.mine_rb);
            MessageActivity.startActivity(this);
        }
    }

    private FragmentStatePagerAdapter mFragments = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new HomePageFragment();
                    break;
                case 1:
                    fragment = new UserFragment();
                    break;
            }
            return fragment;
        }
    };

    private void init() {
        mMainTabGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                int index = 0;
                switch (checkedId) {
                    case R.id.home_page_rb:
                        index = 0;
                        break;
                    case R.id.mine_rb:
                        index = 1;
                        break;
                }
                Fragment fragment = (Fragment) mFragments.instantiateItem(mFragmentContent, index);
                mFragments.setPrimaryItem(mFragmentContent, 0, fragment);
                mFragments.finishUpdate(mFragmentContent);
            }
        });
    }

    //接收未读数量
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMsgCount(MsgCountBean msgCountBean) {
        if (null != msgCountBean) {
            mUnreadMsgCountTv.setVisibility(msgCountBean.msgCount == 0 ? View.GONE : View.VISIBLE);
            mUnreadMsgCountTv.setText(String.valueOf(msgCountBean.msgCount));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveFinish(LoginOutEvent loginOutEvent) {
        if (null != loginOutEvent && loginOutEvent.mIsLoginOut) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            int checkedId = mMainTabGroup.getCheckedRadioButtonId();
            //恢复到首页
            if (checkedId != R.id.home_page_rb) {
                mMainTabGroup.check(R.id.home_page_rb);
            } else {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    ToastUtils.showSingleToast("再按一次退出云图管理！");
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
//                    ActivityManager.getInstance().finishAllActivity();
                }
            }
        }
        return false;
    }

    private CustomLoadingDialog getDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = CustomLoadingDialog.instance(this, "");
            mLoadingDialog.setCancelable(false);
        }
        return mLoadingDialog;
    }

    @Override
    public void showLoadingDialog() {
        if (!getDialog().isShowing()) {
            getDialog().show();
        }
    }

    @Override
    public void hideLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    @Override
    public void showDialogTip(String msg) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, msg);
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                LoginActivity.startActivity(MainActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void showDialogTip(int msgId) {
        showDialogTip(getString(msgId));
    }

    @Override
    public void showUpdateAppInfo(UpdateAppBean updateAppBean) {
        if (null != mFragmentContent && null != updateAppBean) {
            if (updateAppBean.forceUpdate == 1) {
//                setLoginEditAble();
            }
            UpdateAppPPW updateAppPPW = new UpdateAppPPW(MainActivity.this, mFragmentContent, updateAppBean);
            updateAppPPW.showAtLocation(mFragmentContent, Gravity.CENTER, 0, 0);
            KeyboardUtils.hideSoftInput(this);
        }
    }
}
