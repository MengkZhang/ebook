package com.tzpt.cloudlibrary.ui.bookstore;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.LibraryAdvancedSearchBean;
import com.tzpt.cloudlibrary.ui.search.SearchActivity;
import com.tzpt.cloudlibrary.utils.ActivityUtils;

import java.util.Map;

import butterknife.OnClick;

/**
 * 书店
 */
public class BookStoreActivity extends BaseActivity {

    private static final String FROM_TYPE = "from_type";
    private static final String LIBRARY_ADVANCE_BEAN = "LibraryAdvancedSearchBean";

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, BookStoreActivity.class);
        intent.putExtra(FROM_TYPE, 0);
        context.startActivity(intent);
    }

    public static void startActivityAdvanced(Context context, LibraryAdvancedSearchBean item) {
        Intent intent = new Intent(context, BookStoreActivity.class);
        intent.putExtra(LIBRARY_ADVANCE_BEAN, item);
        intent.putExtra(FROM_TYPE, 1);
        context.startActivity(intent);
    }

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_btn:
                SearchActivity.startActivityFromType(this, 6);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_book_store;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setTitle("书店");
    }

    @Override
    public void initDatas() {
        BookStoreFragment bookStoreFragment = (BookStoreFragment) getSupportFragmentManager().findFragmentById(R.id.book_store_list_frame);
        if (bookStoreFragment == null) {
            bookStoreFragment = new BookStoreFragment();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), bookStoreFragment, R.id.book_store_list_frame);
            bookStoreFragment.mIsVisible = true;
        }
        BookStorePresenter presenter = new BookStorePresenter(bookStoreFragment);

        Intent intent = getIntent();
        int fromType = intent.getIntExtra(FROM_TYPE, 0);
        if (fromType == 0) {
            mCommonTitleBar.setRightBtnIcon(R.drawable.bg_btn_search);
            presenter.setBookStoreFilterType(BookStoreFilterType.BOOK_STORE_LIST);
        } else if (fromType == 1) {
            mCommonTitleBar.setRightBtnClickAble(false);
            LibraryAdvancedSearchBean item = (LibraryAdvancedSearchBean) intent.getSerializableExtra(LIBRARY_ADVANCE_BEAN);
            Map<String, Object> parameter = new ArrayMap<>();
            if (null != item) {
                if (!TextUtils.isEmpty(item.areaCode)) {
                    parameter.put("area", item.areaCode);
                }
                if (!TextUtils.isEmpty(item.grade)) {
                    parameter.put("level", item.grade);
                }
                if (!TextUtils.isEmpty(item.libCode)) {
                    parameter.put("libCode", item.libCode);
                }
                if (!TextUtils.isEmpty(item.libName)) {
                    parameter.put("libName", item.libName);
                }
                if (!TextUtils.isEmpty(item.areaName)) {
                    mCommonTitleBar.setTitle(item.areaName);
                } else if (!TextUtils.isEmpty(item.grade)) {
                    mCommonTitleBar.setTitle(item.grade);
                } else {
                    mCommonTitleBar.setTitle("书店");
                }
            }
            presenter.setBookStoreFilterType(BookStoreFilterType.BOOK_STORE_HIGH_SEARCH_LIST);
            presenter.setParameter(parameter);
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
