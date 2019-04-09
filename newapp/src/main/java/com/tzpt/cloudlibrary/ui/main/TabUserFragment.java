package com.tzpt.cloudlibrary.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseFragment;
import com.tzpt.cloudlibrary.base.adapter.glide.RoundedCornersTransformation;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.LoginActivity;
import com.tzpt.cloudlibrary.ui.account.UserInfoActivity;
import com.tzpt.cloudlibrary.ui.account.borrow.BorrowActivity;
import com.tzpt.cloudlibrary.ui.account.card.UserIdentificationActivity;
import com.tzpt.cloudlibrary.ui.account.collection.CollectionShelfActivity;
import com.tzpt.cloudlibrary.ui.account.deposit.UserDepositModuleActivity;
import com.tzpt.cloudlibrary.ui.account.interaction.InteractionActivity;
import com.tzpt.cloudlibrary.ui.account.interaction.MyMessageActivity;
import com.tzpt.cloudlibrary.ui.account.selfhelp.SelfHelpActivity;
import com.tzpt.cloudlibrary.ui.account.setting.SettingActivity;
import com.tzpt.cloudlibrary.ui.account.subscription.SubscriptionActivity;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.CustomAccountItemView;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.titlebar.TitleBarView;
import com.tzpt.cloudlibrary.widget.usertopbar.UserCommonTopBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的账户
 * Created by Administrator on 2017/6/12.
 */
public class TabUserFragment extends BaseFragment implements
        UserContract.View {

    @BindView(R.id.common_toolbar)
    TitleBarView mCommonTitleBar;
    @BindView(R.id.user_top_bar)
    UserCommonTopBar mUserCommonTopBar;
    @BindView(R.id.account_nest_scrollview)
    ScrollView mAccountNestScrollView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.item_interaction_civ)
    CustomAccountItemView mInteractionCiv;

    private UserPresenter mPresenter;
    private boolean mIsShowLoginTips = false;

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mPresenter.getUserInfo();
        }
    };

    @OnClick({R.id.user_top_bar, R.id.item_card_civ,
            R.id.item_self_help_civ, R.id.item_subscription_civ, R.id.item_interaction_civ,
            R.id.item_deposit_civ,
            R.id.item_setting_civ, R.id.item_collection_civ})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.user_top_bar:
                if (!mPresenter.isLogin()) {
                    LoginActivity.startActivity(getSupportActivity());
                } else {
                    Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                    startActivityForResult(intent, 1001);
                }
                break;
            case R.id.item_card_civ:       //名片
                if (!mPresenter.isLogin()) {
                    LoginActivity.startActivity(getActivity());
                } else {
                    UserIdentificationActivity.startActivity(getActivity());
                }
                break;
            case R.id.item_self_help_civ://自助
                if (!mPresenter.isLogin()) {
                    LoginActivity.startActivity(getActivity());
                } else {
                    SelfHelpActivity.startActivity(getActivity());
                }
                break;
            case R.id.item_subscription_civ://书架
                if (!mPresenter.isLogin()) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, 1000);
                } else {
                    SubscriptionActivity.startActivity(getActivity());
                }
                break;
            case R.id.item_interaction_civ://互动
                InteractionActivity.startActivity(getActivity());
                break;
            case R.id.item_deposit_civ://我的押金
                if (!mPresenter.isLogin()) {
                    LoginActivity.startActivity(getActivity());
                } else {
                    UserDepositModuleActivity.startActivity(getActivity());
                }
                break;
            case R.id.item_setting_civ://设置
                SettingActivity.startActivity(getActivity());
                break;

            case R.id.item_collection_civ://收藏
                if (!mPresenter.isLogin()) {
                    LoginActivity.startActivity(getActivity());
                } else {
                    CollectionShelfActivity.startActivity(getSupportActivity());
                }
                break;
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_user;
    }

    @Override
    public void initDatas() {
        mCommonTitleBar.setTitle("我的");
        mPresenter = new UserPresenter();
        mPresenter.attachView(this);
        mPresenter.getUserInfo();
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        mSwipeRefreshLayout.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getLocalUserInfo();
        UmengHelper.setPageStart("TabUserFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        UmengHelper.setPageEnd("TabUserFragment");
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }

    //退出登陆
    private void setUserLoginOut() {
        mAccountNestScrollView.scrollTo(0, 0);
        mPresenter.quit();
        mSwipeRefreshLayout.setEnabled(false);
        //显示未登录状态
        mUserCommonTopBar.setLoginOutState();
        mInteractionCiv.setItemNumber("");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 &&
                resultCode == Activity.RESULT_OK) {
            SubscriptionActivity.startActivity(getActivity());
        } else if (requestCode == 1001 &&
                resultCode == Activity.RESULT_OK) {
            mPresenter.getUserInfo();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mPresenter) {
            mPresenter.detachView();
        }
        //取消事件订阅
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void lazyLoad() {
//        if (mPresenter != null) {
//            mPresenter.getUserInfo();
//        }
    }

    //设置刷新用户信息
    @Override
    public void setRefreshUserInfo() {
        mSwipeRefreshLayout.setRefreshing(false);
        mAccountNestScrollView.scrollTo(0, 0);
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);

//        AccountMessage accountMessage = new AccountMessage();
//        accountMessage.mIsRefreshUserInfo = true;
//        EventBus.getDefault().post(accountMessage);
    }

    /**
     * 设置昵称
     *
     * @param readerNickName 昵称
     */
    @Override
    public void setUserNickName(String readerNickName) {
        mUserCommonTopBar.setUserNickName(readerNickName);
    }

    @Override
    public void setUserHeadImage(String headImage, boolean isMan) {
        if (TextUtils.isEmpty(headImage)) {
            GlideApp.with(this)
                    .load(isMan ? R.mipmap.ic_head_mr_edit : R.mipmap.ic_head_miss_edit)
                    .into(mUserCommonTopBar.getUserHeadImageView());
        } else {
            GlideApp.with(this)
                    .load(headImage)
                    .dontAnimate()
                    .placeholder(R.color.color_ffffff)
                    .error(isMan ? R.mipmap.ic_head_mr_edit : R.mipmap.ic_head_miss_edit)
                    .centerCrop()
                    .transform(new RoundedCornersTransformation(5, RoundedCornersTransformation.CornerType.ALL))
                    .into(mUserCommonTopBar.getUserHeadImageView());
        }
    }

    /**
     * 设置电话号码
     *
     * @param phone 电话号码
     */
    @Override
    public void setUserPhone(String phone) {
        mUserCommonTopBar.setUserPhone(phone);
    }

    @Override
    public void setUserUnreadMsgCount(int unreadMsgCount) {
        if (unreadMsgCount > 0) {
            mInteractionCiv.setItemNumber(String.valueOf(unreadMsgCount));
        } else {
            mInteractionCiv.setItemNumber("");
        }
    }

    //设置其他统计及逾期信息
    @Override
    public void setUserBorrowOverdueSum(int borrowOverdueSum) {

    }

    @Override
    public void setNetError() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
    }

    /**
     * 逾期提示即将逾期书籍
     *
     * @param borrowOverdueSum 即将逾期书籍数量
     */
    @Override
    public void setUserBorrowOverdueMsg(int borrowOverdueSum) {
        final CustomDialog delDialog = new CustomDialog(getSupportActivity(), R.style.DialogTheme, "");
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
                MyMessageActivity.startActivity(getSupportActivity());
            }

            @Override
            public void onClickCancel() {
                delDialog.dismiss();
            }
        });
    }

    //EventBus接收收藏成功后刷新用户信息的回调（及时获取用户信息中的收藏数量）
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveCollectionOrNot(AccountMessage collectionOrNotAccountMessage) {
        if (mPresenter != null && collectionOrNotAccountMessage.mIsRefreshUserInfo) {
            mPresenter.getUserInfo();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginSuccess(AccountMessage loginMessage) {
        //如果登录成功，则访问用户数据
        if (null != mPresenter && loginMessage.success) {
            mSwipeRefreshLayout.setEnabled(true);
            mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
            mPresenter.getUserInfo();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            mSwipeRefreshLayout.setEnabled(false);
            setUserLoginOut();

            if (loginMessage.mIsToUserCenter) {
                pleaseLogin();
            }
        }
    }


    private void pleaseLogin() {
        //自定义对话框
        mSwipeRefreshLayout.setRefreshing(false);
        if (mIsShowLoginTips) {//如果弹出被提下线的对话框，则不再提示
            return;
        }
        mIsShowLoginTips = true;
        final CustomDialog dialog = new CustomDialog(getActivity(), R.style.DialogTheme, getString(R.string.account_login_other_device));
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.setLoginAnewBtn();
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                mIsShowLoginTips = false;
                dialog.dismiss();
                LoginActivity.startActivity(getActivity());
                mSwipeRefreshLayout.setEnabled(false);
                setUserLoginOut();
            }

            @Override
            public void onClickCancel() {
                mIsShowLoginTips = false;
                dialog.dismiss();
                mSwipeRefreshLayout.setEnabled(false);
                setUserLoginOut();
            }
        });
    }

}
