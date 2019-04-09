package com.tzpt.cloundlibrary.manager.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseListActivity;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.bean.StatisticsClassifyBean;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.ui.adapter.StatisticsListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * 统计分析
 */
public class StatisticsListActivity extends BaseListActivity<StatisticsClassifyBean> {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, StatisticsListActivity.class);
        context.startActivity(intent);
    }

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
        return R.layout.activity_statistics_list;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("统计分析");
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        initAdapter(StatisticsListAdapter.class, false, false);
        int dp3 = getResources().getDimensionPixelSize(R.dimen.space_margin);
        mRecyclerView.setItemDecoration(ContextCompat.getColor(this, R.color.common_divider_narrow), dp3, 0, 0);

        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        List<StatisticsClassifyBean> statisticList = new ArrayList<>();
        if (libraryInfo.mCountBookPermission) {
            //藏书统计权限
            StatisticsClassifyBean item = new StatisticsClassifyBean("藏书统计", 0);
            statisticList.add(item);
        }
        if (libraryInfo.mCebitBookPermission) {
            //在借统计权限
            StatisticsClassifyBean item = new StatisticsClassifyBean("在借统计", 1);
            statisticList.add(item);
        }
        if (libraryInfo.mBorrowBookPermission) {
            //借书统计权限
            StatisticsClassifyBean item = new StatisticsClassifyBean("借书统计", 2);
            statisticList.add(item);
        }
        if (libraryInfo.mReturnBookPermission) {
            //还书统计权限
            StatisticsClassifyBean item = new StatisticsClassifyBean("还书统计", 3);
            statisticList.add(item);
        }
        if (libraryInfo.mDompensateBookPermission) {
            //赔书统计权限
            StatisticsClassifyBean item = new StatisticsClassifyBean("赔书统计", 4);
            statisticList.add(item);
        }
        if (libraryInfo.mSellPermission) {
            //销售统计权限
            StatisticsClassifyBean item = new StatisticsClassifyBean("销售统计", 5);
            statisticList.add(item);
        }
        if (libraryInfo.mDepositPermission) {
            //收款统计权限
            StatisticsClassifyBean item = new StatisticsClassifyBean("收款统计", 6);
            statisticList.add(item);
        }
        if (libraryInfo.mPenaltyFreeStatisticPermission) {
            //免单统计权限
            StatisticsClassifyBean item = new StatisticsClassifyBean("免单统计", 8);
            statisticList.add(item);
        }
        if (libraryInfo.mReaderPermission) {
            //读者统计权限
            StatisticsClassifyBean item = new StatisticsClassifyBean("读者统计", 7);
            statisticList.add(item);
        }
        mAdapter.addAll(statisticList);
        mAdapter.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(int position) {
        StatisticsClassifyBean item = mAdapter.getItem(position);
        StatisticsSelectionActivity.startActivity(StatisticsListActivity.this, item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.clear();
    }
}
