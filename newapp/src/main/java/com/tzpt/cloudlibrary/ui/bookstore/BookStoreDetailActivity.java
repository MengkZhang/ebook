package com.tzpt.cloudlibrary.ui.bookstore;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.ui.search.SearchActivity;
import com.tzpt.cloudlibrary.utils.ActivityUtils;

import butterknife.OnClick;

/**
 * 书店详情
 */
public class BookStoreDetailActivity extends BaseActivity {

    private static final String BOOK_STORE_CODE = "book_store_code";
    private static final String BOOK_STORE_NAME = "book_store_name";
    private static final String FROM_SEARCH = "from_search";

    public static void startActivity(Context context, String libCode, String libName) {
        Intent intent = new Intent(context, BookStoreDetailActivity.class);
        intent.putExtra(BOOK_STORE_CODE, libCode);
        intent.putExtra(BOOK_STORE_NAME, libName);
        context.startActivity(intent);
    }

    public static void startActivityForSearchResult(Context context, String libCode, String libName) {
        Intent intent = new Intent(context, BookStoreDetailActivity.class);
        intent.putExtra(BOOK_STORE_CODE, libCode);
        intent.putExtra(BOOK_STORE_NAME, libName);
        intent.putExtra(FROM_SEARCH, 1);
        context.startActivity(intent);
    }

    private String mStoreCode;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_btn:
                SearchActivity.startActivityFromBookStore(this, mStoreCode);
                break;
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_book_store_detail;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
        mCommonTitleBar.setRightBtnIcon(R.drawable.bg_btn_search);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        Intent intent = getIntent();
        mStoreCode = intent.getStringExtra(BOOK_STORE_CODE);
        String storeName = intent.getStringExtra(BOOK_STORE_NAME);
        int fromSearch = intent.getIntExtra(FROM_SEARCH, 0);
        mCommonTitleBar.setTitle(storeName);

        BookStoreDetailFragment bookStoreDetailFragment = (BookStoreDetailFragment) getSupportFragmentManager().findFragmentById(R.id.info_list_frame);
        if (null == bookStoreDetailFragment) {
            bookStoreDetailFragment = new BookStoreDetailFragment();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), bookStoreDetailFragment, R.id.info_list_frame);
            bookStoreDetailFragment.mIsVisible = true;
        }
        BookStoreDetailPresenter presenter = new BookStoreDetailPresenter(bookStoreDetailFragment);
        presenter.setStoreCode(mStoreCode);
        presenter.setStoreName(storeName);
        presenter.setFromSearch(fromSearch);
    }

}
