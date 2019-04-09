package com.tzpt.cloudlibrary.ui.paperbook;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
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
 * 图书界面
 * Created by Administrator on 2017/6/6.
 */

public class BookActivity extends BaseActivity {

//    private static final String FROM_TYPE = "search_type";
    private static final String SEARCH_BEAN = "book_advanced_search_bean";
//    private static final String SEARCH_KEY_WORD = "key_word";
//    private static final String LIB_CODE = "lib_code";

//    //1推荐 2关键字搜索结果 3点赞 4借阅 5图书高级搜索（自定义，与接口无关,所以不必要传送参数）
//    public static void startActivity(Context context, int fromType) {
//        Intent intent = new Intent(context, BookActivity.class);
//        intent.putExtra(FROM_TYPE, fromType);
//        context.startActivity(intent);
//    }

//    public static void startActivitySearchResult(Context context, String keyword, String libCode) {
//        Intent intent = new Intent(context, BookActivity.class);
//        intent.putExtra(FROM_TYPE, 2);
//        intent.putExtra(SEARCH_KEY_WORD, keyword);
//        intent.putExtra(LIB_CODE, libCode);
//        context.startActivity(intent);
//    }

    public static void startActivityAdvancedSearch(Context context, BookAdvancedSearchBean itemBean) {
        Intent intent = new Intent(context, BookActivity.class);
//        intent.putExtra(FROM_TYPE, 5);
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
                SearchActivity.startActivityFromType(this, 0);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_book;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void initDatas() {
        PaperBooksFragment paperBooksFragment = (PaperBooksFragment) getSupportFragmentManager().findFragmentById(R.id.book_list_frame);
        if (paperBooksFragment == null) {
            paperBooksFragment = PaperBooksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), paperBooksFragment, R.id.book_list_frame);
            paperBooksFragment.mIsVisible = true;
        }
        int paperBookFilterType;
        //图书高级搜索
        PaperBookPresenter presenter = new PaperBookPresenter(paperBooksFragment);
        Map<String, String> parameter = new HashMap<>();
        mCommonTitleBar.setTitle("图书");
        mCommonTitleBar.setRightBtnClickAble(false);
        BookAdvancedSearchBean itemBean = (BookAdvancedSearchBean) getIntent().getSerializableExtra(SEARCH_BEAN);
        if (null == itemBean) {
            return;
        }
        mCommonTitleBar.setTitle(TextUtils.isEmpty(itemBean.bookGrade) ? "图书" : itemBean.bookGrade);

        if (!TextUtils.isEmpty(itemBean.libCode)) {
            parameter.put("libCode", itemBean.libCode);
            paperBookFilterType = PaperBookFilterType.Library_Search_Book_List;     //馆内搜索
        } else {
            paperBookFilterType = PaperBookFilterType.Normal_Search_Book_List;      //全局搜索
        }
        if (!TextUtils.isEmpty(itemBean.bookAuthor)) {
            parameter.put("author", itemBean.bookAuthor);
        }
        if (!TextUtils.isEmpty(itemBean.bookCompany)) {
            parameter.put("publisher", itemBean.bookCompany);
        }
        if (!TextUtils.isEmpty(itemBean.bookGradeId)) {
            parameter.put("categoryId", itemBean.bookGradeId);
        }
        if (!TextUtils.isEmpty(itemBean.bookName)) {
            parameter.put("bookName", itemBean.bookName);
        }
        if (!TextUtils.isEmpty(itemBean.bookIsbn)) {
            parameter.put("isbn", itemBean.bookIsbn);
        }
        if (!TextUtils.isEmpty(itemBean.bookYear)) {
            parameter.put("publishDate", itemBean.bookYear);
        }
        if (!TextUtils.isEmpty(itemBean.bookBarCode)){
            parameter.put("barNumber", itemBean.bookBarCode);
        }
        presenter.setParameter(parameter);
        presenter.setFilterType(paperBookFilterType);
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
