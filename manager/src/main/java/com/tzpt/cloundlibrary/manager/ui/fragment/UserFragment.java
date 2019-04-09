package com.tzpt.cloundlibrary.manager.ui.fragment;

import android.view.View;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseFragment;
import com.tzpt.cloundlibrary.manager.bean.MsgCountBean;
import com.tzpt.cloundlibrary.manager.ui.activity.CustomerServiceActivity;
import com.tzpt.cloundlibrary.manager.ui.activity.LibraryDepositActivity;
import com.tzpt.cloundlibrary.manager.ui.activity.LibraryHelpActivity;
import com.tzpt.cloundlibrary.manager.ui.activity.LoginActivity;
import com.tzpt.cloundlibrary.manager.ui.activity.MessageActivity;
import com.tzpt.cloundlibrary.manager.ui.activity.OperatorPwdActivity;
import com.tzpt.cloundlibrary.manager.ui.activity.ShowErrorLogActivity;
import com.tzpt.cloundlibrary.manager.ui.contract.UserContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.UserPresenter;
import com.tzpt.cloundlibrary.manager.utils.Utils;
import com.tzpt.cloundlibrary.manager.utils.VersionUtils;
import com.tzpt.cloundlibrary.manager.widget.CustomUserItemView;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import cn.jpush.android.api.JPushInterface;

/**
 * 我的
 * Created by Administrator on 2017/6/21.
 */
public class UserFragment extends BaseFragment implements UserContract.View {

    @BindView(R.id.user_version_number_tv)
    TextView mUserVersionNumberTv;
    @BindView(R.id.user_library_msg_civ)
    CustomUserItemView mCustomUserMsgItemView;
    @BindView(R.id.user_library_deposit_civ)
    CustomUserItemView mCustomUserDepositItemView;
    private UserPresenter mPresenter;

    //长按进入日志
    @OnLongClick(R.id.common_toolbar)
    public boolean onLongClick(View v) {
        ShowErrorLogActivity.startActivity(getContext());
        return false;
    }

    @OnClick({R.id.user_library_deposit_civ, R.id.user_library_msg_civ, R.id.user_library_psw_manage_civ,
            R.id.user_library_login_out_civ, R.id.user_library_help_civ, R.id.user_library_customer_services_civ})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_library_help_civ:
                LibraryHelpActivity.startActivity(getSupportActivity(), null, false, null);
                break;
            case R.id.user_library_deposit_civ:
                LibraryDepositActivity.startActivity(getSupportActivity());
                break;
            case R.id.user_library_msg_civ:
                MessageActivity.startActivity(getSupportActivity());
                break;
            case R.id.user_library_psw_manage_civ:
                if (!mPresenter.checkPswManagePermission()) {
                    showMessageDialog(R.string.no_permission);
                    return;
                }
                OperatorPwdActivity.startActivity(getSupportActivity());
                break;
            case R.id.user_library_login_out_civ:
                final CustomDialog dialog = new CustomDialog(getContext(), R.layout.dialog_layout,
                        R.style.DialogTheme, "退出当前账号？");
                dialog.setCancelable(false);
                dialog.hasNoCancel(true);
                dialog.show();
                dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
                    @Override
                    public void onClickOk() {
                        dialog.dismiss();
                        JPushInterface.deleteAlias(Utils.getContext(), 0);
                        JPushInterface.cleanTags(Utils.getContext(), 0);
                        //清除图书馆对象
                        mPresenter.delLibraryInfo();

                        LoginActivity.startActivity(getActivity());
                    }

                    @Override
                    public void onClickCancel() {
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.user_library_customer_services_civ:
                CustomerServiceActivity.startActivity(getActivity());
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mPresenter) {
            mPresenter.getMsgNoReadCount();
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_user;
    }

    @Override
    public void initDatas() {
        mPresenter = new UserPresenter();
        mPresenter.attachView(this);
        mPresenter.getMsgNoReadCount();
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        //设置本馆押金是否显示
        mCustomUserDepositItemView.setVisibility(mPresenter.checkDepositPermission() ? View.VISIBLE : View.GONE);
        String tips = "(最新版本号：V" + VersionUtils.getVersionInfo() + ")";
        mUserVersionNumberTv.setText(tips);
    }

    @Override
    public void setMenuVisibility(boolean menuVisibile) {
        super.setMenuVisibility(menuVisibile);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisibile ? View.VISIBLE : View.GONE);
            if (menuVisibile && null != mPresenter) {
                mPresenter.getMsgNoReadCount();
            }
        }
    }

    //接收选中区域地址
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveRefreshMsg(MsgCountBean msgCountBean) {
        if (null != mPresenter && null != msgCountBean && msgCountBean.refresh) {
            mPresenter.getMsgNoReadCount();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mPresenter.detachView();
    }

    //设置未读数量
    @Override
    public void setMsgNoReadCount(int count) {
        mCustomUserMsgItemView.setCountNumber(count);
        MsgCountBean msgCountBean = new MsgCountBean();
        msgCountBean.msgCount = count;
        EventBus.getDefault().post(msgCountBean);
    }

    @Override
    public void noPermissionPrompt(int kickedOffline) {
        final CustomDialog dialog = new CustomDialog(getSupportActivity(), R.layout.dialog_layout,
                R.style.DialogTheme, getString(kickedOffline));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                getSupportActivity().finish();
                LoginActivity.startActivity(getSupportActivity());
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    /**
     * 提示信息
     *
     * @param msgId
     */
    private void showMessageDialog(int msgId) {
        final CustomDialog dialog = new CustomDialog(getSupportActivity(), R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }
}