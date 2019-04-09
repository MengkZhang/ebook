package com.tzpt.cloudlibrary.ui.paperbook;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.BookAdvancedSearchBean;
import com.tzpt.cloudlibrary.ui.common.AdvancedCategoryActivity;
import com.tzpt.cloudlibrary.ui.common.AdvancedTwoLevelCategoryActivity;
import com.tzpt.cloudlibrary.ui.ebook.EBookActivity;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.PermissionsDialogFragment;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.ui.search.ScanningIsbnActivity;
import com.tzpt.cloudlibrary.utils.KeyboardUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 图书电子书高级搜索
 */
public class BookListAdvancedSearchActivity extends BaseActivity {
    private static final String FROM_TYPE = "from_type";
    private static final String LIBRARY_CODE = "library_code";

    //1.图书 2.电子书
    public static void startActivity(Context context, int fromType, String libCode) {
        Intent intent = new Intent(context, BookListAdvancedSearchActivity.class);
        intent.putExtra(FROM_TYPE, fromType);
        intent.putExtra(LIBRARY_CODE, libCode);
        context.startActivity(intent);
    }

    @BindView(R.id.search_book_grade_tv)
    TextView mSearchBookGradeTv;
    @BindView(R.id.search_book_isbn_et)
    EditText mSearchBookIsbnEt;
    @BindView(R.id.search_book_name_et)
    EditText mSearchBookNameEt;
    @BindView(R.id.search_book_company_et)
    EditText mSearchBookCompanyEt;
    @BindView(R.id.search_book_year_et)
    EditText mSearchBookYearEt;
    @BindView(R.id.search_book_author_et)
    EditText mSearchBookAuthorEt;
    @BindView(R.id.search_book_bar_code_ll)
    LinearLayout mSearchBookBarCodeLl;
    @BindView(R.id.search_book_bar_code_et)
    EditText mSearchBookBarCodeEt;

    private String mBookGradeId;        //图书分类
    private int mOneLevelCategoryId;    //电子书一级分类
    private int mTwoLevelCategoryId;    //电子书二级分类
    private String mBookClassifyName;
    private String mLibCode;
    private int mFromType;


    @OnClick({R.id.titlebar_left_btn, R.id.search_book_grade_ll, R.id.scanner_ibtn, R.id.advance_search_btn,
            R.id.search_book_isbn_et})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_book_isbn_et:
                mSearchBookIsbnEt.setCursorVisible(true);
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.search_book_grade_ll:
                switch (mFromType) {
                    case 1:
                        AdvancedCategoryActivity.startActivity(this, 1, 1000);
                        break;
                    case 2:
                        AdvancedTwoLevelCategoryActivity.startActivity(this, 1000);
                        break;
                }
                break;
            case R.id.scanner_ibtn:
                initCameraPermission();
                break;
            case R.id.advance_search_btn:
                //执行去搜索高级搜索
                getAdvancedSearchResult();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_book_list_advanced_search;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("高级检索");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        mFromType = intent.getIntExtra(FROM_TYPE, 1);
        mLibCode = intent.getStringExtra(LIBRARY_CODE);
        if (mFromType == 1 && !TextUtils.isEmpty(mLibCode)) {
            mSearchBookBarCodeLl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void configViews() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && null != data) {
            mBookClassifyName = data.getStringExtra("categoryName");
            mSearchBookGradeTv.setText(mBookClassifyName);
            //图书分类
            int bookGradeId = data.getIntExtra("categoryId", 0);
            if (bookGradeId != -1) {//如果是电子书，默认分类将为设置为-1
                this.mBookGradeId = String.valueOf(bookGradeId);
            } else {
                this.mBookGradeId = null;
                //电子书二级分类处理
                mOneLevelCategoryId = data.getIntExtra("oneLevelCategoryId", 0);
                mTwoLevelCategoryId = data.getIntExtra("twoLevelCategoryId", 0);
            }

        } else if (requestCode == 1001 && resultCode == RESULT_OK && null != data) {
            String bookIsbn = data.getStringExtra("isbn");
            if (mFromType == 1) {
                if (!TextUtils.isEmpty(bookIsbn)) {
                    BookDetailActivity.startActivity(this, bookIsbn, null, null, 0);
                }
            } else {
                if (!TextUtils.isEmpty(bookIsbn)) {
                    mSearchBookIsbnEt.setText(bookIsbn);
                }
            }
        }
    }


    //执行去搜索高级搜索
    private void getAdvancedSearchResult() {
        String bookIsbn = mSearchBookIsbnEt.getText().toString().trim();
        String bookGradeId = mSearchBookGradeTv.getText().toString().trim();
        String bookName = mSearchBookNameEt.getText().toString().trim();
        String bookCompany = mSearchBookCompanyEt.getText().toString().trim();
        String bookYear = mSearchBookYearEt.getText().toString().trim();
        String bookAuthor = mSearchBookAuthorEt.getText().toString().trim();
        String bookBarCode = mSearchBookBarCodeEt.getText().toString().trim();
        if (!TextUtils.isEmpty(bookIsbn)
                || !TextUtils.isEmpty(bookGradeId)
                || !TextUtils.isEmpty(bookName)
                || !TextUtils.isEmpty(bookCompany)
                || !TextUtils.isEmpty(bookYear)
                || !TextUtils.isEmpty(bookAuthor)
                || !TextUtils.isEmpty(bookBarCode)) {
            BookAdvancedSearchBean itemBean = new BookAdvancedSearchBean();
            itemBean.bookGrade = mBookClassifyName;
            itemBean.bookIsbn = bookIsbn;
            itemBean.bookName = bookName;
            itemBean.bookCompany = bookCompany;
            itemBean.bookYear = bookYear;
            itemBean.bookAuthor = bookAuthor;
            itemBean.libCode = mLibCode;
            itemBean.bookBarCode = bookBarCode;
            if (mBookGradeId != null) {//图书分类
                itemBean.bookGradeId = mBookGradeId;
            } else {
                //电子书二级分类
                itemBean.oneLevelCategoryId= mOneLevelCategoryId;
                itemBean.twoLevelCategoryId= mTwoLevelCategoryId;
            }
            toAdvancedSearchBookList(itemBean);
        } else {
            showErrorMsg("条件不能为空！");
        }

    }

    //开始高级搜索
    private void toAdvancedSearchBookList(BookAdvancedSearchBean itemBean) {
        switch (mFromType) {
            case 1:
                BookActivity.startActivityAdvancedSearch(this, itemBean);
                break;
            case 2:
                EBookActivity.startActivityAdvanced(this, itemBean);
                break;
        }
    }

    public void showErrorMsg(String msg) {
        ToastUtils.showSingleToast(msg);
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
    public void onBackPressed() {
        super.onBackPressed();
        KeyboardUtils.hideSoftInput(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //初始化摄像头权限
    private void initCameraPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            ScanningIsbnActivity.startActivityForResult(this, 1001);
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(Manifest.permission.CAMERA)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {//权限已授权
                            ScanningIsbnActivity.startActivityForResult(BookListAdvancedSearchActivity.this, 1001);
                        } else if (permission.shouldShowRequestPermissionRationale) {//下次继续提示(应该显示请求许可理由)

                        } else {//没有权限,不能使用权限模块-去设置权限
                            showCameraPermissionPopUpWindow();
                        }
                    }
                });
    }

    PermissionsDialogFragment mSetCameraDialogFragment;

    //展示设置权限弹窗
    private void showCameraPermissionPopUpWindow() {
        if (null == mSetCameraDialogFragment) {
            mSetCameraDialogFragment = new PermissionsDialogFragment();
        }
        if (mSetCameraDialogFragment.isAdded()) {
            return;
        }
        mSetCameraDialogFragment.initPermissionUI(PermissionsDialogFragment.PERMISSION_CAMERA);
        mSetCameraDialogFragment.show(this.getFragmentManager(), "PermissionsDialogFragment");
    }
}
