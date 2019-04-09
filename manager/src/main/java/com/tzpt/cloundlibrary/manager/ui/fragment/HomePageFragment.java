package com.tzpt.cloundlibrary.manager.ui.fragment;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseFragment;
import com.tzpt.cloundlibrary.manager.bean.StatisticsClassifyBean;
import com.tzpt.cloundlibrary.manager.ui.activity.DepositManagerActivity;
import com.tzpt.cloundlibrary.manager.ui.activity.EntranceGuardActivity;
import com.tzpt.cloundlibrary.manager.ui.activity.LibrarySetOpenTimeActivity;
import com.tzpt.cloundlibrary.manager.ui.activity.LoginActivity;
import com.tzpt.cloundlibrary.manager.ui.activity.ReaderLoginActivity;
import com.tzpt.cloundlibrary.manager.ui.activity.ReturnBookManagementActivity;
import com.tzpt.cloundlibrary.manager.ui.activity.StatisticsListActivity;
import com.tzpt.cloundlibrary.manager.ui.activity.StatisticsSelectionActivity;
import com.tzpt.cloundlibrary.manager.ui.contract.HomePageContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.HomePagePresenter;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页
 * Created by Administrator on 2017/6/20.
 */
public class HomePageFragment extends BaseFragment implements HomePageContract.View {
    @BindView(R.id.library_name_tv)
    TextView mLibraryNameTv;
    @BindView(R.id.library_code_btn)
    TextView mLibraryCodeBtn;
    private HomePagePresenter mPresenter;

    @OnClick({R.id.open_time_btn, R.id.borrow_btn, R.id.back_btn, R.id.outflow_btn,
            R.id.inflow_btn, R.id.statistics_btn, R.id.entrance_btn, R.id.reader_management_btn,
            R.id.lost_book_btn, R.id.deposit_manager_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.open_time_btn:
                if (!mPresenter.checkPermission(0)) {
                    showMessageDialog(R.string.no_permission);
                    return;
                }
                LibrarySetOpenTimeActivity.startActivity(getActivity());
                break;
            case R.id.borrow_btn:
                if (!mPresenter.checkPermission(1)) {
                    showMessageDialog(R.string.no_permission);
                    return;
                }
                ReaderLoginActivity.startActivity(getActivity(), 0);
                break;
            case R.id.back_btn:
                if (!mPresenter.checkPermission(2)) {
                    showMessageDialog(R.string.no_permission);
                    return;
                }
                ReturnBookManagementActivity.startActivity(getActivity());
                break;
            case R.id.lost_book_btn://陪书管理 TODO 权限判断和还书一样？
                if (!mPresenter.checkPermission(2)) {
                    showMessageDialog(R.string.no_permission);
                    return;
                }
                ReaderLoginActivity.startActivity(getActivity(), 1);
                break;
            case R.id.deposit_manager_btn://押金管理
                DepositManagerActivity.startActivity(getActivity());
                break;
            case R.id.outflow_btn://流出管理
                if (!mPresenter.checkPermission(3)) {
                    showMessageDialog(R.string.no_permission);
                    return;
                }
                StatisticsClassifyBean item0 = new StatisticsClassifyBean("流出管理", 9);
                StatisticsSelectionActivity.startActivity(getContext(), item0);
                break;
            case R.id.inflow_btn: //流入管理
                if (!mPresenter.checkPermission(4)) {
                    showMessageDialog(R.string.no_permission);
                    return;
                }
                StatisticsClassifyBean item1 = new StatisticsClassifyBean("流入管理", 10);
                StatisticsSelectionActivity.startActivity(getContext(), item1);
                break;
            case R.id.statistics_btn:
                //统计管理
                if (!mPresenter.checkPermission(6)) {
                    showMessageDialog(R.string.no_permission);
                    return;
                }
                StatisticsListActivity.startActivity(getActivity());
                break;
            case R.id.entrance_btn:
                EntranceGuardActivity.startActivity(getActivity());
                break;
            case R.id.reader_management_btn:
                if (!mPresenter.checkPermission(5)) {
                    showMessageDialog(R.string.no_permission);
                    return;
                }
                ReaderLoginActivity.startActivity(getActivity(), 3);
                break;

        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_page;
    }

    @Override
    public void initDatas() {
        mPresenter = new HomePagePresenter();
        mPresenter.attachView(this);
        mPresenter.getLoginInfo();
    }

    @Override
    public void configViews() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mPresenter) {
            mPresenter.detachView();
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisibile) {
        super.setMenuVisibility(menuVisibile);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisibile ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void setLibraryName(String name, String operatorInfo, boolean isNormal) {
        mLibraryCodeBtn.setText(operatorInfo);
        mLibraryCodeBtn.setTextColor(isNormal ? Color.parseColor("#ffffff") : Color.parseColor("#ffe3ca"));
        mLibraryNameTv.setText(name);
    }

    @Override
    public void setNoLoginPermission(int msgId) {
        final CustomDialog dialog = new CustomDialog(getActivity(), R.layout.dialog_layout,
                R.style.DialogTheme, getString(msgId));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                getActivity().finish();
                LoginActivity.startActivity(getActivity());
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
