package com.tzpt.cloudlibrary.ui.readers;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.ui.search.SearchActivity;
import com.tzpt.cloudlibrary.utils.ActivityUtils;

import butterknife.OnClick;

/**
 * 活动界面
 * create by JiaZhiqiang 2018/3/21.
 */
public class ActivityListActivity extends BaseActivity {

    private static final String FROM_TYPE = "from_type";
    private static final String LIB_CODE = "lib_code";
    private static final String LIB_TITLE = "lib_title";

    public static void startActivity(Context context, int fromType) {
        Intent intent = new Intent(context, ActivityListActivity.class);
        intent.putExtra(FROM_TYPE, fromType);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String libCode, String title) {
        Intent intent = new Intent(context, ActivityListActivity.class);
        intent.putExtra(FROM_TYPE, 2);
        intent.putExtra(LIB_CODE, libCode);
        intent.putExtra(LIB_TITLE, title);
        context.startActivity(intent);
    }

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_btn:
                if (TextUtils.isEmpty(mLibCode)) {
                    SearchActivity.startActivityFromType(this, 5);
                } else {
                    SearchActivity.startActivityFromLib(this, mLibCode, 5);
                }
                break;
        }
    }

    private String mLibCode;

    @Override
    public int getLayoutId() {
        return R.layout.activity_activity_list;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        ActivityListFragment activityListFragment = (ActivityListFragment) getSupportFragmentManager().findFragmentById(R.id.activity_list_frame);
        if (activityListFragment == null) {
            activityListFragment = new ActivityListFragment();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), activityListFragment, R.id.activity_list_frame);
            activityListFragment.mIsVisible = true;
        }
        ActivityListPresenter presenter = new ActivityListPresenter(activityListFragment);

        int fromType = getIntent().getIntExtra(FROM_TYPE, 0);

        switch (fromType) {
            case 0://活动列表
                mCommonTitleBar.setTitle("活动");
                mCommonTitleBar.setRightBtnIcon(R.drawable.bg_btn_search);
                presenter.setParameters(0, false, null, null);
                break;
            case 1://我的报名
                mCommonTitleBar.setTitle("我的报名");
                mCommonTitleBar.setRightBtnClickAble(false);
                presenter.setParameters(1, false, null, null);
                break;
            case 2://本馆活动列表
                mLibCode = getIntent().getStringExtra(LIB_CODE);
                String title = getIntent().getStringExtra(LIB_TITLE);
                mCommonTitleBar.setTitle(title);
                mCommonTitleBar.setRightBtnIcon(R.drawable.bg_btn_search);
                presenter.setParameters(2, false, null, mLibCode);
                break;
        }
    }

    @Override
    public void configViews() {

    }
}
