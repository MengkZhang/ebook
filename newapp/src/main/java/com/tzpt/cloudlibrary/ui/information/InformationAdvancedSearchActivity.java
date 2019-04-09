package com.tzpt.cloudlibrary.ui.information;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.ui.common.AdvancedCategoryActivity;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 资讯高级搜索
 */
public class InformationAdvancedSearchActivity extends BaseActivity {
    private static final String LIB_CODE = "lib_code";

    public static void startActivity(Context context, String libCode) {
        Intent intent = new Intent(context, InformationAdvancedSearchActivity.class);
        intent.putExtra(LIB_CODE, libCode);
        context.startActivity(intent);
    }

    @BindView(R.id.advance_search_category)
    TextView mAdvanceSearchCategory;
    @BindView(R.id.advance_search_industry)
    TextView mAdvanceSearchIndustry;
    @BindView(R.id.advance_search_source)
    EditText mAdvanceSearchSource;
    @BindView(R.id.advance_search_title)
    EditText mAdvanceSearchTitle;

    private String mCategoryId;
    private String mIndustryId;
    private String mLibCode;

    @OnClick({R.id.titlebar_left_btn, R.id.advance_search_category_layout, R.id.advance_search_btn,
            R.id.advance_search_industry_layout, R.id.advance_search_source})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.advance_search_source:
                mAdvanceSearchSource.setCursorVisible(true);
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.advance_search_category_layout:
                if (TextUtils.isEmpty(mLibCode)) {
                    AdvancedCategoryActivity.startActivity(this, 0, 1000);
                } else {
                    //馆内资讯类别
                    AdvancedCategoryActivity.startActivity(this, 5, 1000);
                }
                break;
            case R.id.advance_search_industry_layout:
                AdvancedCategoryActivity.startActivity(this, 11, 1001);
                break;
            case R.id.advance_search_btn:
                String title = mAdvanceSearchTitle.getText().toString().trim();
                String source = mAdvanceSearchSource.getText().toString().trim();
                String category = mAdvanceSearchCategory.getText().toString().trim();
                String industry = mAdvanceSearchIndustry.getText().toString().trim();
                if (!TextUtils.isEmpty(source) || !TextUtils.isEmpty(title)
                        || !TextUtils.isEmpty(category) || !TextUtils.isEmpty(industry)) {
                    InformationActivity.startActivity(this, source, title, mCategoryId, mIndustryId, mLibCode);
                    return;
                }
                ToastUtils.showSingleToast("条件不能为空！");
                break;
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_information_advanced_search;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("高级检索");
    }

    @Override
    public void initDatas() {
        mLibCode = getIntent().getStringExtra(LIB_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && null != data) {
            String categoryName = data.getStringExtra("categoryName");
            int categoryId = data.getIntExtra("categoryId", 0);
            mCategoryId = String.valueOf(categoryId);
            if (!TextUtils.isEmpty(categoryName)) {
                mAdvanceSearchCategory.setText(categoryName);
            }
        } else if (requestCode == 1001 && resultCode == RESULT_OK && null != data) {
            String categoryName = data.getStringExtra("categoryName");
            int industryId = data.getIntExtra("categoryId", 0);
            mIndustryId = (industryId == -1 ? null : String.valueOf(industryId));//如果是查询全部（-1），则为null，接口不传值
            if (!TextUtils.isEmpty(categoryName)) {
                mAdvanceSearchIndustry.setText(categoryName);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        KeyboardUtils.hideSoftInput(this);
    }
}
