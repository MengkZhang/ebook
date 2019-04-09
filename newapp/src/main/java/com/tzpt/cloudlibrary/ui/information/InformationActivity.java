package com.tzpt.cloudlibrary.ui.information;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.ui.search.SearchActivity;
import com.tzpt.cloudlibrary.utils.ActivityUtils;

import butterknife.OnClick;

/**
 * 资讯列表
 */
public class InformationActivity extends BaseActivity {
    private static final String FROM_TYPE = "from_type";
    private static final String LIBRARY_CODE = "library_code";
    private static final String KEY_WORD = "keyword";
    private static final String INFO_CATEGORY_ID = "info_type";
    private static final String INFO_INDUSTRY_ID = "info_industry_id";
    private static final String INFO_SOURCE = "info_source";
    private static final String INFO_TITLE = "info_title";
    private static final String LIB_TITLE = "library_title";

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, InformationActivity.class);
        intent.putExtra(FROM_TYPE, 0);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String libCode, String title) {
        Intent intent = new Intent(context, InformationActivity.class);
        intent.putExtra(FROM_TYPE, 2);
        intent.putExtra(LIBRARY_CODE, libCode);
        intent.putExtra(LIB_TITLE, title);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String source, String title, String cId, String industryId, String libCode) {
        Intent intent = new Intent(context, InformationActivity.class);
        intent.putExtra(FROM_TYPE, 1);
        intent.putExtra(INFO_TITLE, title);
        intent.putExtra(INFO_SOURCE, source);
        intent.putExtra(INFO_CATEGORY_ID, cId);
        intent.putExtra(INFO_INDUSTRY_ID, industryId);
        intent.putExtra(LIBRARY_CODE, libCode);
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
                    SearchActivity.startActivityFromType(this, 4);
                } else {
                    SearchActivity.startActivityFromLib(this, mLibCode, 4);
                }
                break;
        }
    }

    private String mLibCode;

    @Override
    public int getLayoutId() {
        return R.layout.activity_information;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }


    @Override
    public void initDatas() {
        InformationFragment informationFragment = (InformationFragment) getSupportFragmentManager().findFragmentById(R.id.info_list_frame);
        if (informationFragment == null) {
            informationFragment = new InformationFragment();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), informationFragment, R.id.info_list_frame);
            informationFragment.mIsVisible = true;
        }
        InformationPresenter presenter = new InformationPresenter(informationFragment);
        Intent intent = getIntent();
        int fromType = intent.getIntExtra(FROM_TYPE, 0);
        mLibCode = intent.getStringExtra(LIBRARY_CODE);
        String keyword = getIntent().getStringExtra(KEY_WORD);
        String cid = getIntent().getStringExtra(INFO_CATEGORY_ID);
        String infoSource = getIntent().getStringExtra(INFO_SOURCE);
        String industry = getIntent().getStringExtra(INFO_INDUSTRY_ID);
        String title = getIntent().getStringExtra(INFO_TITLE);
        switch (fromType) {
            case 0://平台
                mCommonTitleBar.setRightBtnIcon(R.drawable.bg_btn_search);
                mCommonTitleBar.setRightBtnClickAble(true);
                mCommonTitleBar.setTitle("资讯");
                break;
            case 1://高级搜索
                mCommonTitleBar.setRightBtnClickAble(false);
                mCommonTitleBar.setTitle("资讯");
                presenter.setSearchParameter(keyword, mLibCode, infoSource, title, cid, industry, 1);
                break;
            case 2://馆内搜索
                String infoTitle = getIntent().getStringExtra(LIB_TITLE);
                mCommonTitleBar.setRightBtnIcon(R.drawable.bg_btn_search);
                mCommonTitleBar.setRightBtnClickAble(true);
                mCommonTitleBar.setTitle(infoTitle);
                presenter.setSearchParameter(null, mLibCode, null, null, null, null, 0);
                break;
        }
    }

    @Override
    public void configViews() {

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
}
