package com.tzpt.cloudlibrary.ui.library;

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
 * 图书馆列表界面
 */
public class LibraryActivity extends BaseActivity {
    private static final String LIBRARY_ADVANCE_BEAN = "LibraryAdvancedSearchBean";
    private static final String FROM_TYPE = "from_type";

    //fromType 0 附近 1图书馆
    public static void startActivity(Context context, int fromType) {
        Intent intent = new Intent(context, LibraryActivity.class);
        intent.putExtra(FROM_TYPE, fromType);
        context.startActivity(intent);
    }

    //4.来自高级搜索
    public static void startActivityAdvanced(Context context, LibraryAdvancedSearchBean item) {
        Intent intent = new Intent(context, LibraryActivity.class);
        intent.putExtra(LIBRARY_ADVANCE_BEAN, item);
        context.startActivity(intent);
    }

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_btn:
                SearchActivity.startActivityFromType(this, mFromType == 0 ? 7 : 2);
                break;
        }
    }

    private int mFromType = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_library;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);

    }

    @Override
    public void initDatas() {
        LibraryFragment libraryFragment = (LibraryFragment) getSupportFragmentManager().findFragmentById(R.id.library_list_frame);
        if (libraryFragment == null) {
            libraryFragment = new LibraryFragment();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), libraryFragment, R.id.library_list_frame);
            libraryFragment.mIsVisible = true;
        }
        LibraryPresenter presenter = new LibraryPresenter(libraryFragment);

        Intent intent = getIntent();
        mFromType = intent.getIntExtra(FROM_TYPE, -1);
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
                setLibraryTitle(mFromType, presenter);
            }
            mCommonTitleBar.setRightBtnClickAble(false);
            presenter.setLibListType(true);
        } else {
            setLibraryTitle(mFromType, presenter);
            mCommonTitleBar.setRightBtnIcon(R.drawable.bg_btn_search);
            presenter.setLibListType(false);
        }
        presenter.setParameter(parameter);
    }

    /**
     * 设置标题
     *
     * @param fromType
     */
    private void setLibraryTitle(int fromType, LibraryPresenter presenter) {
        if (fromType == 0) {
            mCommonTitleBar.setTitle("附近");
            presenter.setLibFilterType(LibraryFilterType.Near_Library_Book_Store_List);
        } else if (fromType == 1) {
            mCommonTitleBar.setTitle("图书馆");
            presenter.setLibFilterType(LibraryFilterType.Library_List);
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
