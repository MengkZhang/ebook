package com.tzpt.cloudlibrary.ui.ebook;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.bean.LocalEBook;
import com.tzpt.cloudlibrary.ui.permissions.Permission;
import com.tzpt.cloudlibrary.ui.permissions.RxPermissions;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;
import com.tzpt.cloudlibrary.widget.recyclerview.decoration.GridFirstRowTopDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by Administrator on 2017/11/20.
 */

public class EBookShelfActivity extends BaseActivity implements EBookShelfContract.View,
        RecyclerArrayAdapter.OnItemClickListener {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, EBookShelfActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    @BindView(R.id.titlebar_right_txt_btn)
    Button mRightTxtBtn;
    @BindView(R.id.collection_del_tv)
    TextView mEBookDelTv;
    @BindView(R.id.collection_all_check_tv)
    TextView mEBookAllCheckTv;
    @BindView(R.id.collection_editor_ll)
    LinearLayout mEBookEditorLl;

    private EBookShelfAdapter mAdapter;
    private EBookShelfPresenter mPresenter;
    private boolean mIsReading;

    @OnClick({R.id.collection_del_tv, R.id.titlebar_left_btn, R.id.collection_all_check_tv, R.id.titlebar_right_txt_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.collection_del_tv:
                List<String> bookIDList = new ArrayList<>();
                for (int i = mAdapter.mSparseItemChecked.size() - 1; i >= 0; i--) {
                    int position = mAdapter.mSparseItemChecked.keyAt(i);
                    if (mAdapter.mSparseItemChecked.valueAt(i)) {
                        bookIDList.add(String.valueOf(mAdapter.getItem(position).mId));
                    }
                }
                if (bookIDList.size() > 0) {
                    mPresenter.delLocalEBook(bookIDList);

                    setOperationBtnStatus(false);
                    closeEditModel();
                }
                break;
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.collection_all_check_tv:
                if (mEBookAllCheckTv.getText().toString().trim().equals("全选")) {
                    mEBookAllCheckTv.setText("取消全选");
                    mAdapter.setAllOrNoneChecked(true);
                } else {
                    mEBookAllCheckTv.setText("全选");
                    mAdapter.setAllOrNoneChecked(false);
                }
                break;
            case R.id.titlebar_right_txt_btn:
                if (mRightTxtBtn.getText().toString().trim().equals("编辑")) {
                    setOperationBtnStatus(true);
                    showEditModel();
                } else {
                    setOperationBtnStatus(false);
                    closeEditModel();
                }
                break;
        }
    }

    private EBookShelfAdapter.EBookCheckListener mCheckListener = new EBookShelfAdapter.EBookCheckListener() {
        @Override
        public void setCheckCount(int count) {
            if (count > 0) {
                StringBuffer delText = new StringBuffer();
                delText.append("删除")
                        .append("(")
                        .append(count)
                        .append(")");
                mEBookDelTv.setText(delText);
                mEBookDelTv.setClickable(true);
                mEBookAllCheckTv.setText(count == mAdapter.getCount() ? "取消全选" : "全选");
            } else {
                mEBookDelTv.setText("删除");
                mEBookDelTv.setClickable(false);
                mEBookAllCheckTv.setText("全选");
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_ebook_shelf;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("电子书架");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mPresenter = new EBookShelfPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {
        mAdapter = new EBookShelfAdapter(this, mCheckListener);
        mAdapter.setOnItemClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mPresenter.getLocalEBookList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsReading) {
            mIsReading = false;
            mPresenter.getLocalEBookList();
        }
        UmengHelper.setUmengResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengHelper.setUmengPause(this);
    }

    @Override
    public void showLocalEBook(List<LocalEBook> list) {
        mAdapter.clear();
        mAdapter.addAll(list);
        mRecyclerView.setRefreshing(false);
    }

    @Override
    public void showLocalEBookEmpty() {
        mRecyclerView.showEmpty();
        mRecyclerView.setRefreshing(false);
    }

    @Override
    public void onItemClick(int position) {
        if (mAdapter.isEditModel()) {
            mAdapter.setItemChecked(position);
            return;
        }
        LocalEBook eBook = mAdapter.getItem(position);
        initStoragePermission(eBook.mId, eBook.mDownloadUrl, eBook.mTitle, eBook.mAuthor, eBook.mCoverImg, eBook.mShareUrl, eBook.mShareContent, eBook.mBelongLibCode);
    }

    /**
     * 设置编辑模式
     */
    private void showEditModel() {
        mEBookEditorLl.setVisibility(View.VISIBLE);
        mAdapter.setEditModel(true);
    }

    /**
     * 关闭编辑模式
     */
    public void closeEditModel() {
        mEBookEditorLl.setVisibility(View.GONE);
        mAdapter.setEditModel(false);
        mEBookDelTv.setClickable(false);
        mEBookDelTv.setText("删除");
        mEBookAllCheckTv.setText("全选");
    }

    /**
     * 设置编辑模式下的按钮状态
     *
     * @param editorModel 编辑模式
     */
    private void setOperationBtnStatus(boolean editorModel) {
        if (editorModel) {
            mCommonTitleBar.setRightBtnText("取消");
        } else {
            mCommonTitleBar.setRightBtnText("编辑");
        }
    }

    /**
     * 设置是否有编辑功能
     *
     * @param editAble 编辑功能
     */
    public void checkEditFunction(boolean editAble) {
        if (editAble) {
            mCommonTitleBar.setRightBtnText("编辑");
            mCommonTitleBar.setRightBtnClickAble(true);
        } else {
            mCommonTitleBar.setRightBtnClickAble(false);
            mCommonTitleBar.clearRightBtnTxt();
        }
    }

    //初始化定位权限
    private void initStoragePermission(final String name, final String downloadPath, final String title,
                                       final String author, final String coverImg, final String shareUrl,
                                       final String shareContent, final String belongLibCode) {
        if (Build.VERSION.SDK_INT < 23) {
            mIsReading = true;
            EBookReaderActivity.startActivity(this, name, downloadPath, title, author, coverImg, shareUrl, shareContent, belongLibCode);
            return;
        }
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Permission>() {
                    @Override
                    public void call(Permission permission) {
                        if (permission.granted) {//权限已授权
                            mIsReading = true;
                            EBookReaderActivity.startActivity(EBookShelfActivity.this, name, downloadPath, title, author, coverImg, shareUrl, shareContent, belongLibCode);
                        } else if (permission.shouldShowRequestPermissionRationale) {//下次继续提示(应该显示请求许可理由)

                        } else {//没有权限,不能使用权限模块-去设置权限

                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }

}
