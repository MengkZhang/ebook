package com.tzpt.cloudlibrary.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.ClassifyInfo;
import com.tzpt.cloudlibrary.bean.ClassifyTwoLevelBean;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;
import com.tzpt.cloudlibrary.widget.recyclerview.decoration.DividerDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 高级搜索选择类别
 */
public class AdvancedTwoLevelCategoryActivity extends BaseActivity implements
        AdvancedCategoryContract.View {

    public static void startActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, AdvancedTwoLevelCategoryActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.classify_rv)
    RecyclerView mClassifyRv;
    @BindView(R.id.classify_sub_rv)
    RecyclerView mClassifySubRv;
    private AdvancedTwoLevelCategoryAdapter mAdapter;
    private AdvancedTwoLevelCategoryAdapter mSubAdapter;
    private AdvancedCategoryPresenter mPresenter;
    private int mSelectParentPosition = 0;
    private int mParentPosition;
    private int mSubPosition;

    private RecyclerArrayAdapter.OnItemClickListener mOnItemClickListener = new RecyclerArrayAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            if (null != mAdapter && null != mAdapter.getItem(position)) {
                mAdapter.setSelectPosition(position);
                mParentPosition = position;
                //设置子类列表
                List<ClassifyTwoLevelBean> subList = mAdapter.getItem(position).mSubList;
                if (null != subList) {
                    mSubAdapter.clear();
                    mSubAdapter.addAll(subList);
                    mSubAdapter.notifyDataSetChanged();
                    if (position == mSelectParentPosition) {
                        //设置选中下标
                        if (mSubPosition < mSubAdapter.getCount()) {
                            mSubAdapter.setSelectPosition(mSubPosition);
                        }
                    } else {
                        mSubAdapter.setSelectPosition(-1);
                    }
                }
            }
        }
    };
    private RecyclerArrayAdapter.OnItemClickListener mOnSubItemClickListener = new RecyclerArrayAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            if (null != mSubAdapter && mSubAdapter.getCount() > 0) {
                mSelectParentPosition = mParentPosition;
                mSubPosition = position;
                mSubAdapter.setSelectPosition(position);
                int twoLevelClassifyId = mSubAdapter.getItem(position).mId;

                String classifyName;
                if (mParentPosition == 0 && mSubPosition == 0) {
                    classifyName = "全部分类";
                } else if (mSubPosition == 0) {
                    //如果没有二级分类ID则设置名称为一级分类名称（即全部）
                    classifyName = mAdapter.getItem(mParentPosition).mName;
                } else {
                    classifyName = mSubAdapter.getItem(position).mName;
                }
                int oneLevelClassifyId = mAdapter.getItem(mSelectParentPosition).mId;
                Intent intent = new Intent();
                intent.putExtra("categoryName", classifyName);
                intent.putExtra("categoryId", -1);//设置为-1，区别图书分类和电子书二级分类
                intent.putExtra("oneLevelCategoryId", oneLevelClassifyId);
                intent.putExtra("twoLevelCategoryId", twoLevelClassifyId);


                setResult(RESULT_OK, intent);
                finish();
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_common_advanced_two_level_category;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("选择类别");
    }

    @Override
    public void initDatas() {
        mPresenter = new AdvancedCategoryPresenter();
        mPresenter.attachView(this);
        mPresenter.setFromType(2);
    }

    @Override
    public void configViews() {
        mAdapter = new AdvancedTwoLevelCategoryAdapter(this, false);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mClassifyRv.setLayoutManager(new LinearLayoutManager(this));
        mClassifyRv.setAdapter(mAdapter);
        //sub list
        mSubAdapter = new AdvancedTwoLevelCategoryAdapter(this, true);
        mSubAdapter.setOnItemClickListener(mOnSubItemClickListener);
        mClassifySubRv.setLayoutManager(new LinearLayoutManager(this));
        mClassifySubRv.setAdapter(mSubAdapter);

        mMultiStateLayout.showProgress();
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

    @OnClick(R.id.titlebar_left_btn)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void setAdvancedGradeBeanList(List<ClassifyInfo> informationGradeList) {

    }

    @Override
    public void setEBookClassificationList(List<ClassifyTwoLevelBean> beanList) {
        mMultiStateLayout.showContentView();
        if (beanList != null && !beanList.isEmpty()) {
            mAdapter.clear();
            mAdapter.addAll(beanList);
            if (null != beanList.get(0)
                    && null != beanList.get(0).mSubList
                    && beanList.get(0).mSubList.size() > 0) {
                mSubAdapter.clear();
                mSubAdapter.addAll(beanList.get(0).mSubList);
            }
            setSelectPosition();
        }
    }

    //设置指定数据
    private void setSelectPosition() {
        if (mSelectParentPosition < mAdapter.getCount()) {
            mAdapter.setSelectPosition(mSelectParentPosition);
        }
        //设置为指定下标数据
        mSubAdapter.clear();
        mSubAdapter.addAll(mAdapter.getItem(mSelectParentPosition).mSubList);
        mSubAdapter.notifyDataSetChanged();
        if (mSubPosition < mSubAdapter.getCount()) {
            mSubAdapter.setSelectPosition(mSubPosition);
        }
    }

    @Override
    public void showError() {
        mMultiStateLayout.showError();
        mMultiStateLayout.showRetryError(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getAdvancedCategoryList();
            }
        });
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
