package com.tzpt.cloudlibrary.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.bean.UserHeadBean;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.LoadingProgressView;
import com.tzpt.cloudlibrary.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloudlibrary.widget.recyclerview.swipe.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 系统推荐头像
 */
public class UserHeadActivity extends BaseListActivity<UserHeadBean> implements
        UserHeadContract.View {

    private static final String IMAGE_PATH = "image";

    public static void startActivityForResult(Activity context, String headImagePath, int requestCode) {
        Intent intent = new Intent(context, UserHeadActivity.class);
        intent.putExtra(IMAGE_PATH, headImagePath);
        context.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;
    @BindView(R.id.progress_layout)
    LoadingProgressView mProgressLayout;
    private UserHeadPresenter mPresenter;
    private UserHeadAdapter mAdapter;
    private String mCurrentImage = "";

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_head;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("系统推荐头像");
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
        mPresenter = new UserHeadPresenter();
        mPresenter.attachView(this);
        mPresenter.getUserHeadList();

        mCurrentImage = getIntent().getStringExtra(IMAGE_PATH);

    }

    @Override
    public void configViews() {
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter = new UserHeadAdapter(this);
        mRecyclerView.setAdapterWithProgress(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }


    @OnClick({R.id.titlebar_left_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public void showProgressDialog() {
        mProgressLayout.showProgressLayout();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressLayout.hideProgressLayout();
    }

    @Override
    public void changeUserHeadSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void showErrorMessage(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }

    @Override
    public void showHeadImageError() {
        mRecyclerView.showError();
        mRecyclerView.setRetryRefreshListener(this);
    }

    @Override
    public void setUserHeadList(List<UserHeadBean> userHeadList) {
        if (userHeadList.size() > 0) {
            mAdapter.clear();
            mAdapter.addAll(userHeadList);
            mAdapter.setCurrentImage(mCurrentImage);
        }
    }

    @Override
    public void setUserHeadListEmpty() {
        mRecyclerView.showEmpty();
    }

    @Override
    public void pleaseLoginTip() {
        //自定义对话框
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        accountMessage.mIsToUserCenter = true;
        EventBus.getDefault().post(accountMessage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengHelper.setUmengResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.detachView();
            mPresenter = null;
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRefresh() {
        mPresenter.getUserHeadList();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onItemClick(int position) {
        if (null != mAdapter && null != mAdapter.getItem(position)) {
            String submitImage = mAdapter.getItem(position).tagImage;
            mAdapter.setChooseImage(position);
            mPresenter.submitUserHead(submitImage);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }
}
