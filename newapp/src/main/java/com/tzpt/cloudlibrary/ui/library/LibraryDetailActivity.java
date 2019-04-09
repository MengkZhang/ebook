package com.tzpt.cloudlibrary.ui.library;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.ui.search.SearchActivity;
import com.tzpt.cloudlibrary.utils.ActivityUtils;

import butterknife.OnClick;

/**
 * 图书馆详情
 */
public class LibraryDetailActivity extends BaseActivity {

    private static final String LIBRARY_CODE = "library_code";
    private static final String LIBRARY_NAME = "library_name";
    private static final String FROM_SEARCH = "from_search";

    public static void startActivity(Context context, String libCode, String libName) {
        Intent intent = new Intent(context, LibraryDetailActivity.class);
        intent.putExtra(LIBRARY_CODE, libCode);
        intent.putExtra(LIBRARY_NAME, libName);
        context.startActivity(intent);
    }

    public static void startActivityForSearchResult(Context context, String libCode, String libName) {
        Intent intent = new Intent(context, LibraryDetailActivity.class);
        intent.putExtra(LIBRARY_CODE, libCode);
        intent.putExtra(LIBRARY_NAME, libName);
        intent.putExtra(FROM_SEARCH, 1);
        context.startActivity(intent);
    }

    private String mLibCode;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_btn:
                SearchActivity.startActivityFromLib(this, mLibCode, 0);
                break;
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_library_detail;
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
        mLibCode = intent.getStringExtra(LIBRARY_CODE);
        String libName = intent.getStringExtra(LIBRARY_NAME);
        int fromSearch = intent.getIntExtra(FROM_SEARCH, 0);
        mCommonTitleBar.setTitle(libName);

        LibraryDetailFragment libraryDetailFragment = (LibraryDetailFragment) getSupportFragmentManager().findFragmentById(R.id.info_list_frame);
        if (null == libraryDetailFragment) {
            libraryDetailFragment = new LibraryDetailFragment();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), libraryDetailFragment, R.id.info_list_frame);
            libraryDetailFragment.mIsVisible = true;
        }
        LibraryDetailPresenter presenter = new LibraryDetailPresenter(libraryDetailFragment);
        presenter.setLibraryCode(mLibCode);
        presenter.setLibraryName(libName);
        presenter.setFromSearch(fromSearch);
    }

}
