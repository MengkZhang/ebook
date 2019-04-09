package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseListActivity;
import com.tzpt.cloundlibrary.manager.bean.FlowManageListBean;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.bean.SameRangeLibraryBean;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.ui.adapter.FlowManageInLibraryListAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.FlowManageInLibraryListContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.FlowManageInLibraryListPresenter;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.adapter.OnLoadMoreListener;
import com.tzpt.cloundlibrary.manager.widget.searchview.CustomAnimationSearchView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 流出管理新增流向馆列表
 */
public class FlowManageInLibraryListActivity extends BaseListActivity<SameRangeLibraryBean> implements
        FlowManageInLibraryListContract.View,
        OnLoadMoreListener {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, FlowManageInLibraryListActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.custom_search_view)
    public CustomAnimationSearchView mSearchView;
    private FlowManageInLibraryListContract.Presenter mPresenter;
    private int mCurrentPage = 1;

    @OnClick({R.id.titlebar_left_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                this.finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_flow_manage_in_library_list;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("流出新增");
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
    }

    @Override
    public void initDatas() {
        mPresenter = new FlowManageInLibraryListPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {
        initAdapter(FlowManageInLibraryListAdapter.class, false, true);
        mSearchView.setSearchBarListener(mListener);
        mAdapter.setOnItemClickListener(this);

        mPresenter.searchFlowManageInLibraryList(1, "");
    }

    /**
     * 搜索监听
     */
    private CustomAnimationSearchView.CallbackOnTouchDismissSearchBar mListener
            = new CustomAnimationSearchView.CallbackOnTouchDismissSearchBar() {
        @Override
        public void callbackShowSearchBar() {
        }

        @Override
        public void callbackDismissSearchBar() {
            mPresenter.searchFlowManageInLibraryList(1, "");
        }

        @Override
        public void hasText(boolean hasText, String content) {
        }

        @Override
        public void callbackActionSearch(String searchContent) {
            if (null != mSearchView) {
                mSearchView.closeKeyBoard();
            }
            mPresenter.searchFlowManageInLibraryList(1, searchContent);
        }

        @Override
        public void onEditTextClick() {
        }
    };

    @Override
    public void onItemClick(int position) {
        //进入流出管理新增
        if (null != mAdapter) {
            SameRangeLibraryBean bean = mAdapter.getItem(position);
            LibraryInfo userInfo = DataRepository.getInstance().getLibraryInfo();
            if (null != bean && null != userInfo) {
                FlowManageListBean listBean = new FlowManageListBean();
                listBean.inHallCode = bean.hallCode;
                listBean.name = bean.name;
                listBean.conperson = bean.conperson;
                listBean.phone = bean.phone;
                listBean.outOperUserId = userInfo.mOperaterId;
                listBean.outHallCode = userInfo.mHallCode;//新增流出清单，设置为当前操作员id,馆号
                FlowManagementOperationActivity.startActivity(this, listBean, 0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.clear();
        mPresenter.clearTempInLibraryList();
        mPresenter.detachView();
    }

    @Override
    public void showProgressLoading() {
        showDialog(getString(R.string.loading));
    }

    @Override
    public void dismissProgressLoading() {
        dismissDialog();
    }

    @Override
    public void showErrorView(boolean fresh) {
        mRecyclerView.setRefreshing(false);
        if (fresh) {
            mAdapter.clear();
            mRecyclerView.showError();
        } else {
            mAdapter.pauseMore();
        }
    }


    @Override
    public void complete() {
        mRecyclerView.setRefreshing(false);
    }

    @Override
    public void onLoadMore() {
        mPresenter.searchFlowManageInLibraryList(mCurrentPage + 1, mSearchView.getEditContent());
    }

    /**
     * 设置流入馆列表
     *
     * @param sameRangeLibraryBeanList 流入馆列表
     * @param totalSum                 总数量
     * @param refresh                  是否刷新
     */
    @Override
    public void setFlowManageInLibraryList(List<SameRangeLibraryBean> sameRangeLibraryBeanList, int totalSum, boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(sameRangeLibraryBeanList);
    }

    @Override
    public void setFlowManageInLibraryListEmpty(boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void noPermissionPrompt(int kickedOffline) {
        final CustomDialog dialog = new CustomDialog(this, R.layout.dialog_layout,
                R.style.DialogTheme, getString(kickedOffline));
        dialog.setCancelable(false);
        dialog.hasNoCancel(false);
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                LoginActivity.startActivity(FlowManageInLibraryListActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }
}
