package com.tzpt.cloudlibrary.ui.library;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.LibraryAdvancedSearchBean;
import com.tzpt.cloudlibrary.ui.bookstore.BookStoreActivity;
import com.tzpt.cloudlibrary.ui.common.AdvancedCategoryActivity;
import com.tzpt.cloudlibrary.ui.map.SwitchCityActivity;
import com.tzpt.cloudlibrary.utils.AllCapTransformationMethod;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.CustomInputTextView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 图书馆高级搜索
 */
public class LibraryListAdvancedActivity extends BaseActivity {

    private static final int CITY_REQUEST_CODE = 1000;
    private static final int GRADE_REQUEST_CODE = CITY_REQUEST_CODE + 1;
    private static final int TYPE_REQUEST_CODE = GRADE_REQUEST_CODE + 1;

    private static final String FROM_TYPE = "from_type";

    public static void startActivity(Context context, int fromType) {
        Intent intent = new Intent(context, LibraryListAdvancedActivity.class);
        intent.putExtra(FROM_TYPE, fromType);
        context.startActivity(intent);
    }

    @BindView(R.id.lib_advanced_type_view)
    CustomInputTextView mLibAdvancedTypeView;
    @BindView(R.id.lib_advanced_city_view)
    CustomInputTextView mLibAdvancedCityView;
    @BindView(R.id.lib_advanced_grade_view)
    CustomInputTextView mLibAdvancedGradeView;
    @BindView(R.id.lib_advanced_code_view)
    CustomInputTextView mLibAdvancedCodeView;
    @BindView(R.id.lib_advanced_name_view)
    CustomInputTextView mLibAdvancedNameView;

    private String mAreaCode;
    private String mAreaTitle;
    private int mFromType;

    @OnClick({R.id.titlebar_left_btn, R.id.lib_advanced_city_view, R.id.lib_advanced_grade_view,
            R.id.advance_search_btn, R.id.lib_advanced_type_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.lib_advanced_type_view://选中类型
                AdvancedCategoryActivity.startActivity(this, 12, TYPE_REQUEST_CODE);
                break;
            case R.id.lib_advanced_city_view:
                SwitchCityActivity.startActivityForResult(this, mAreaTitle, mAreaCode, CITY_REQUEST_CODE);
                break;
            case R.id.lib_advanced_grade_view:
                AdvancedCategoryActivity.startActivity(this, 10, GRADE_REQUEST_CODE);
                break;
            case R.id.advance_search_btn:
                switch (mFromType) {
                    case 0://图书馆
                        checkLibraryAdvancedSearch();
                        break;
                    case 1://书店
                        checkBookStoreAdvancedSearch();
                        break;
                    case 2://附近
                        //判断是图书馆还是书店
                        if ("书店".equals(mLibAdvancedCityView.getInputTextContent())) {
                            checkBookStoreAdvancedSearch();
                        } else {
                            checkLibraryAdvancedSearch();
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_library_list_advanced;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("高级检索");
    }

    @Override
    public void initDatas() {
    }

    @Override
    public void configViews() {
        //配置为大写
        mLibAdvancedCodeView.setTransformationMethod(new AllCapTransformationMethod());
        //配置输入指定字符
        mLibAdvancedNameView.setInputType(InputType.TYPE_CLASS_TEXT);

        mFromType = getIntent().getIntExtra(FROM_TYPE, 0);
        switch (mFromType) {
            case 0://图书馆
                mLibAdvancedTypeView.setVisibility(View.GONE);
                setLibraryUI();
                break;
            case 1://书店
                mLibAdvancedTypeView.setVisibility(View.GONE);
                setBookStoreUI();
                break;
            case 2://附近
                mLibAdvancedTypeView.setVisibility(View.VISIBLE);
                mLibAdvancedTypeView.setInputTextContent("图书馆");
                setLibraryUI();
                break;
        }

    }

    /**
     * 配置图书馆高级搜索UI
     */
    private void setLibraryUI() {
        mLibAdvancedGradeView.setVisibility(View.VISIBLE);
        mLibAdvancedGradeView.setInputName("馆别");
        mLibAdvancedCodeView.setInputName("馆号");
        mLibAdvancedNameView.setInputName("馆名");
        clearContent();

    }

    /**
     * 配置书店高级搜索UI
     */
    private void setBookStoreUI() {
        mLibAdvancedGradeView.setVisibility(View.GONE);
        mLibAdvancedGradeView.setInputName("店别");
        mLibAdvancedCodeView.setInputName("店号");
        mLibAdvancedNameView.setInputName("店名");
        clearContent();
    }

    /**
     * 清空之前输入内容
     */
    private void clearContent() {
        mAreaCode = null;
        mLibAdvancedGradeView.clearInputContent();
        mLibAdvancedCityView.clearInputContent();
        mLibAdvancedCodeView.clearEditContent();
        mLibAdvancedNameView.clearEditContent();
    }

    /**
     * 图书馆高级搜索
     */
    private void checkLibraryAdvancedSearch() {
        String areaName = mLibAdvancedCityView.getInputTextContent();
        String grade = mLibAdvancedGradeView.getInputTextContent();
        String libName = mLibAdvancedNameView.getInputEditContent();
        String typeName = mLibAdvancedTypeView.getInputTextContent();
        String libCode = mLibAdvancedCodeView.getInputEditContent();
        //xiu
        if (!TextUtils.isEmpty(libCode)) {
            libCode = libCode.toUpperCase();
        }
        //如果图书馆等级为空，则显示为图书馆
        if (!TextUtils.isEmpty(typeName) && TextUtils.isEmpty(grade)) {
            grade = typeName;
        }
        if (!TextUtils.isEmpty(mAreaCode)
                || !TextUtils.isEmpty(grade)
                || !TextUtils.isEmpty(libCode)
                || !TextUtils.isEmpty(libName)) {
            LibraryAdvancedSearchBean itemBean = new LibraryAdvancedSearchBean();
            itemBean.areaCode = mAreaCode;
            itemBean.areaName = areaName;
            itemBean.grade = grade;
            itemBean.libCode = libCode;
            itemBean.libName = libName;

            LibraryActivity.startActivityAdvanced(this, itemBean);
        } else {
            ToastUtils.showSingleToast("条件不能为空！");
        }
    }

    /**
     * 书店高级搜索
     */
    private void checkBookStoreAdvancedSearch() {
        String areaName = mLibAdvancedCityView.getInputTextContent();
        String libName = mLibAdvancedNameView.getInputEditContent();
        String libCode = mLibAdvancedCodeView.getInputEditContent();
        if (!TextUtils.isEmpty(libCode)) {
            libCode = libCode.toUpperCase();
        }
        if (!TextUtils.isEmpty(mAreaCode)
                || !TextUtils.isEmpty(libCode)
                || !TextUtils.isEmpty(libName)) {
            LibraryAdvancedSearchBean itemBean = new LibraryAdvancedSearchBean();
            itemBean.areaCode = mAreaCode;
            itemBean.areaName = areaName;
            itemBean.grade = "书店";
            itemBean.libCode = libCode;
            itemBean.libName = libName;
            BookStoreActivity.startActivityAdvanced(this, itemBean);
        } else {
            ToastUtils.showSingleToast("条件不能为空！");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && null != data) {
            switch (requestCode) {
                case CITY_REQUEST_CODE:
                    String areaName = data.getStringExtra("areaName");
                    this.mAreaCode = data.getStringExtra("areaCode");
                    this.mAreaTitle = data.getStringExtra("areaNameTitle");
                    mLibAdvancedCityView.setInputTextContent(areaName);
                    break;
                case GRADE_REQUEST_CODE:
                    String categoryName = data.getStringExtra("categoryName");
                    mLibAdvancedGradeView.setInputTextContent(categoryName);
                    break;
                case TYPE_REQUEST_CODE:
                    String typeName = data.getStringExtra("categoryName");
                    mLibAdvancedTypeView.setInputTextContent(typeName);
                    if ("书店".equals(typeName)) {
                        setBookStoreUI();
                    } else {
                        setLibraryUI();
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        KeyboardUtils.hideSoftInput(this);
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

    }
}
