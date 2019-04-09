package com.tzpt.cloudlibrary.ui.ebook;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.BookAdvancedSearchBean;
import com.tzpt.cloudlibrary.ui.search.SearchActivity;
import com.tzpt.cloudlibrary.utils.ActivityUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.OnClick;

/**
 * 电子书列表界面-高级搜索
 */
public class EBookActivity extends BaseActivity {

    private static final String SEARCH_BEAN = "book_advanced_search_bean";

    //高级搜索
    public static void startActivityAdvanced(Context context, BookAdvancedSearchBean itemBean) {
        Intent intent = new Intent(context, EBookActivity.class);
        intent.putExtra(SEARCH_BEAN, itemBean);
        context.startActivity(intent);
    }

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_btn:
                SearchActivity.startActivityFromType(this, 1);
                break;
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_ebook;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("电子书");

    }

    @Override
    public void initDatas() {
        EBookFragment eBookFragment = (EBookFragment) getSupportFragmentManager().findFragmentById(R.id.book_list_frame);
        if (eBookFragment == null) {
            eBookFragment = EBookFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), eBookFragment, R.id.book_list_frame);
            eBookFragment.mIsVisible = true;
        }
        Intent intent = getIntent();
        EBookPresenter presenter = new EBookPresenter(eBookFragment);
        Map<String, String> parameter = new HashMap<>();

        BookAdvancedSearchBean itemBean = (BookAdvancedSearchBean) intent.getSerializableExtra(SEARCH_BEAN);
        if (itemBean != null) {
            mCommonTitleBar.setTitle(TextUtils.isEmpty(itemBean.bookGrade) ? "电子书" : itemBean.bookGrade);
            mCommonTitleBar.setRightBtnClickAble(false);

            if (!TextUtils.isEmpty(itemBean.libCode)) {
                parameter.put("libCode", itemBean.libCode);
                presenter.setFilterType(EBookFilterType.Library_Advanced_EBook_List);
            } else {
                presenter.setFilterType(EBookFilterType.Advanced_EBook_List);
            }

            if (!TextUtils.isEmpty(itemBean.bookIsbn)) {
                parameter.put("isbn", itemBean.bookIsbn);
            }
            if (!TextUtils.isEmpty(itemBean.bookName)) {
                parameter.put("bookName", itemBean.bookName);
            }
            if (!TextUtils.isEmpty(itemBean.bookCompany)) {
                parameter.put("publisher", itemBean.bookCompany);
            }
            if (!TextUtils.isEmpty(itemBean.bookYear)) {
                parameter.put("publishDate", itemBean.bookYear);
            }
            if (!TextUtils.isEmpty(itemBean.bookAuthor)) {
                parameter.put("author", itemBean.bookAuthor);
            }
            //二级分类
            presenter.setEBookClassificationId(itemBean.oneLevelCategoryId, itemBean.twoLevelCategoryId);
        }
        presenter.setParameter(parameter);
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
