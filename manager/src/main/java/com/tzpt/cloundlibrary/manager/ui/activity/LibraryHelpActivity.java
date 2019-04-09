package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseListActivity;
import com.tzpt.cloundlibrary.manager.bean.HelpInfoBean;
import com.tzpt.cloundlibrary.manager.ui.adapter.LibraryHelpListAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.LibraryHelpContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.LibraryHelpPresenter;
import com.tzpt.cloundlibrary.manager.widget.dialog.CustomDialog;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 图书馆系统帮助
 */
public class LibraryHelpActivity extends BaseListActivity<HelpInfoBean> implements
        LibraryHelpContract.View {

    private static final String HELP_FLAG = "help_flag";
    private static final String HELP_TITLE = "help_title";
    private static final String HELP_BEAN_LIST = "help_bean_list";

    public static void startActivity(Context context, String title, boolean hasChildren, List<HelpInfoBean> helpInfoBeanList) {
        Intent intent = new Intent(context, LibraryHelpActivity.class);
        intent.putExtra(HELP_TITLE, title);
        intent.putExtra(HELP_FLAG, hasChildren);
        intent.putExtra(HELP_BEAN_LIST, (Serializable) helpInfoBeanList);
        context.startActivity(intent);
    }

    @OnClick(R.id.titlebar_left_btn)
    public void onViewClicked() {
        finish();
    }

    @BindView(R.id.library_help_title_tv)
    TextView mLibraryHelpTitleTv;

    LibraryHelpPresenter mPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_library_help;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("帮助");
    }

    @Override
    public void initDatas() {
        mPresenter = new LibraryHelpPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {
        initAdapter(LibraryHelpListAdapter.class, false, false);
        mRecyclerView.setItemDecoration(ContextCompat.getColor(this, R.color.common_divider_narrow),
                1, 0, 0);

        Intent intent = getIntent();
        boolean hasChildren = intent.getBooleanExtra(HELP_FLAG, false);
        if (hasChildren) {
            List<HelpInfoBean> helpInfoBeanList = (List<HelpInfoBean>) intent.getSerializableExtra(HELP_BEAN_LIST);
            String title = intent.getStringExtra(HELP_TITLE);
            mCommonTitleBar.setTitle("帮助 - " + title);
            mAdapter.clear();
            mAdapter.addAll(helpInfoBeanList);
            mLibraryHelpTitleTv.setVisibility(View.VISIBLE);
            mLibraryHelpTitleTv.setText(helpInfoBeanList.get(0).mTitle);
        } else {
            mPresenter.getLibraryHelpList();
        }

    }

    @Override
    public void onItemClick(int position) {
        HelpInfoBean bean = mAdapter.getItem(position);
        if (!bean.mHasChildren) {
            LibraryHelpWebDetailActivity.startActivity(this, bean.mItemName, bean.mUrl);
            return;
        }
        if (null != bean.mChildren && bean.mChildren.size() > 0) {
            LibraryHelpActivity.startActivity(this, bean.mItemName, true,  bean.mChildren);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void setLibraryHelpList(List<HelpInfoBean> helpInfoBeanList, String subTitle) {
        mAdapter.clear();
        mAdapter.addAll(helpInfoBeanList);
        mLibraryHelpTitleTv.setText(subTitle);
        mLibraryHelpTitleTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void setLibraryHelpListEmpty() {
        mLibraryHelpTitleTv.setVisibility(View.GONE);
        mRecyclerView.showEmpty();
    }

    @Override
    public void setNetError() {
        mLibraryHelpTitleTv.setVisibility(View.GONE);
        mRecyclerView.showError();
    }

    @Override
    public void setNoLoginPermission(int kickedOffline) {
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
                LoginActivity.startActivity(LibraryHelpActivity.this);
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }
}
