package com.tzpt.cloudlibrary.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.bean.ClassifyInfo;
import com.tzpt.cloudlibrary.bean.ClassifyTwoLevelBean;
import com.tzpt.cloudlibrary.utils.DpPxUtils;

import java.util.List;

import butterknife.OnClick;

/**
 * 高级搜索选择类别
 */
public class AdvancedCategoryActivity extends BaseListActivity<ClassifyInfo> implements
        AdvancedCategoryContract.View {

    private static final String FROM_TYPE = "from_type";

    //0资讯分类 5馆内资讯分类 1图书高级搜索 2.电子书高级搜索 10.图书馆高级搜索馆别 11.资讯行业分类
    public static void startActivity(Activity activity, int fromType, int requestCode) {
        Intent intent = new Intent(activity, AdvancedCategoryActivity.class);
        intent.putExtra(FROM_TYPE, fromType);
        activity.startActivityForResult(intent, requestCode);
    }

    private AdvancedCategoryPresenter mPresenter;

    @Override
    public void onRefresh() {
        mPresenter.getAdvancedCategoryList();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_common_advanced_category;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mPresenter = new AdvancedCategoryPresenter();
        mPresenter.attachView(this);

        Intent intent = getIntent();
        int fromType = intent.getIntExtra(FROM_TYPE, 0);
        mPresenter.setFromType(fromType);
        switch (fromType) {
            case 0:
            case 1:
            case 2:
            case 5:
            case 12:
                mCommonTitleBar.setTitle("选择类别");
                break;
            case 10:
                mCommonTitleBar.setTitle("选择馆别");
                break;
            case 11:
                mCommonTitleBar.setTitle("行业");
                break;
        }

    }

    @Override
    public void configViews() {
        mAdapter = new AdvancedCategoryAdapter(this);
        initAdapter(false, false);

        mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_one);

        mPresenter.getAdvancedCategoryList();
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
    public void onItemClick(int position) {
        Log.e("position--->", String.valueOf(position));
        if (null != mAdapter && null != mAdapter.getItem(position)) {
            ClassifyInfo bean = mAdapter.getItem(position);
            Intent intent = new Intent();
            intent.putExtra("categoryName", bean.name);
            intent.putExtra("categoryId", bean.id);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @OnClick(R.id.titlebar_left_btn)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void setAdvancedGradeBeanList(List<ClassifyInfo> informationGradeList) {
        mAdapter.clear();
        mAdapter.addAll(informationGradeList);
        mRecyclerView.setRefreshing(false);
    }

    @Override
    public void setEBookClassificationList(List<ClassifyTwoLevelBean> beanList) {

    }

    @Override
    public void showError() {
        mRecyclerView.setRefreshing(false);
        mRecyclerView.showError();
        mRecyclerView.setRetryRefreshListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mAdapter) {
            mAdapter.clear();
        }
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }
}
