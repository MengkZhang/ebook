package com.tzpt.cloudlibrary.ui.ebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseListActivity;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.rxbus.event.EBookEvent;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.OnLoadMoreListener;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;
import com.tzpt.cloudlibrary.widget.recyclerview.swipe.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 电子书收藏列表
 */
public class EBookCollectionActivity extends BaseListActivity<EBookBean> implements
        EBookCollectionContract.View,
        OnLoadMoreListener,
        OnRefreshListener,
        RecyclerArrayAdapter.OnItemClickListener {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, EBookCollectionActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.titlebar_right_txt_btn)
    Button mRightTxtBtn;
    @BindView(R.id.collection_del_tv)
    TextView mCollectionDelTv;
    @BindView(R.id.collection_all_check_tv)
    TextView mCollectionAllCheckTv;
    @BindView(R.id.collection_editor_ll)
    LinearLayout mCollectionEditorLl;

    private EBookCollectionPresenter mPresenter;
    private int mCurrentPage = 1;

    @OnClick({R.id.titlebar_left_btn, R.id.titlebar_right_txt_btn, R.id.collection_all_check_tv, R.id.collection_del_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.titlebar_right_txt_btn:
                if (mRightTxtBtn.getText().toString().equals("编辑")) {
                    setOperationBtnStatus(true);
                    showEditorModel();
                } else {
                    closeEditorModel();
                    setOperationBtnStatus(false);
                }
                break;
            case R.id.collection_all_check_tv:  //全选，取消全选
                if (mCollectionAllCheckTv.getText().toString().equals("全选")) {
                    mCollectionAllCheckTv.setText("取消全选");
                    ((EBookCollectionAdapter) mAdapter).checkAllOrNone(true);
                } else {
                    ((EBookCollectionAdapter) mAdapter).checkAllOrNone(false);
                    mCollectionAllCheckTv.setText("全选");
                }
                break;
            case R.id.collection_del_tv:        //删除
                delEBook();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_ebook_collection;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("收藏电子书");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
        mPresenter = new EBookCollectionPresenter(CloudLibraryApplication.mRxBus);
        mPresenter.attachView(this);

        mPresenter.registerRxBus(EBookEvent.class, new Action1<EBookEvent>() {
            @Override
            public void call(EBookEvent eBookEvent) {
                switch (eBookEvent.getMsgType()) {
                    case 0://接收电子书数量信息
                        if (eBookEvent.getCount() > 0) {
                            mCollectionDelTv.setTextColor(Color.parseColor("#ee7853"));
                            StringBuilder delCountStr = new StringBuilder();
                            delCountStr.append("删除");
                            delCountStr.append("(");
                            delCountStr.append(eBookEvent.getCount());
                            delCountStr.append(")");
                            mCollectionDelTv.setText(delCountStr);
                            mCollectionDelTv.setClickable(true);
                            //如果选中数量=列表总数，则为可以取消全部状态
                            mCollectionAllCheckTv.setText(eBookEvent.getCount() == mAdapter.getCount() ? "取消全选" : "全选");
                        } else {
                            mCollectionDelTv.setTextColor(Color.parseColor("#80999999"));
                            mCollectionDelTv.setClickable(false);
                            mCollectionDelTv.setText("删除");
                            mCollectionAllCheckTv.setText("全选");
                        }
                        break;
                    case 1://编辑状态，当前接收到为编辑状态，则取消全选
                        if (eBookEvent.isEditorAble()) {
                            mCollectionDelTv.setTextColor(Color.parseColor("#80999999"));
                            ((EBookCollectionAdapter) mAdapter).checkAllOrNone(false);
                            mCollectionDelTv.setClickable(false);
                            mCollectionDelTv.setText("删除");
                            mCollectionAllCheckTv.setText("全选");
                        }
                        if (mRightTxtBtn.getText().toString().equals("取消")) {
                            return;
                        }
                        checkEditFunction(eBookEvent.isEditorAble());
                        break;
                    case 2://恢复为默认编辑样式，关闭选择状态
                        closeEditorModel();
                        setOperationBtnStatus(false);
                        checkEditFunction(eBookEvent.isEditorAble());
                        break;
                }
            }
        });
    }

    @Override
    public void configViews() {
        mAdapter = new EBookCollectionAdapter(this);
        initAdapter(true, true);

        mPresenter.getCollectionEBookList(1, false);
    }

    @Override
    public void setEBookListEmpty(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showEmpty();
        } else {
            mAdapter.stopMore();
        }
    }

    @Override
    public void showErrorMsg(int resId) {
        ToastUtils.showSingleToast(resId);
    }

    @Override
    public void showDelProgress() {
        showDialog("删除中...");
    }

    @Override
    public void dismissDelProgress() {
        dismissDialog();
    }

    @Override
    public void setEBookListError(boolean refresh) {
        mRecyclerView.setRefreshing(false);
        if (refresh) {
            mAdapter.clear();
            mRecyclerView.showError();
        } else {
            mAdapter.pauseMore();
        }
    }

    @Override
    public void setEBookList(List<EBookBean> eBookList, boolean refresh) {
        if (refresh) {
            mAdapter.clear();
            mCurrentPage = 1;
        } else {
            mCurrentPage = mCurrentPage + 1;
        }
        mAdapter.addAll(eBookList);
        mRecyclerView.setRefreshing(false);
        mRecyclerView.showToastTv(String.valueOf(mAdapter.getCount()));
    }

    @Override
    public void onRefresh() {
        mPresenter.getCollectionEBookList(1, false);
    }

    @Override
    public void onLoadMore() {
        mPresenter.getCollectionEBookList(mCurrentPage + 1, false);
    }

    @Override
    public void onItemClick(int position) {
        //编辑模式不可点击
        if (((EBookCollectionAdapter) mAdapter).isEditMode()) {
            ((EBookCollectionAdapter) mAdapter).chooseCollectionEBook(position);
            return;
        }
        EBookBean bean = mAdapter.getItem(position);
        if (null != bean) {
            Intent intent = new Intent(this, EBookDetailActivity.class);
            intent.putExtra(EBookDetailActivity.BOOK_ID, String.valueOf(bean.mEBook.mId));
            startActivityForResult(intent, 1000);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            String bookId = data.getStringExtra(EBookDetailActivity.BOOK_ID);
            if (!TextUtils.isEmpty(bookId)) {
                long newBookId = Long.parseLong(bookId);
                for (EBookBean item : mAdapter.getAllData()) {
                    if (item.mEBook.mId == newBookId) {
                        mAdapter.remove(item);
                        break;
                    }
                }
            }
            if (mAdapter.getCount() == 0) {
                mPresenter.setEditorAble(false);
                mRecyclerView.showEmpty();
            }
        }
    }

    /**
     * 编辑模式
     */
    public void showEditorModel() {
        ((EBookCollectionAdapter) mAdapter).setEditMode(true);
        mCollectionEditorLl.setVisibility(View.VISIBLE);
    }

    /**
     * 关闭编辑模式
     */
    public void closeEditorModel() {
        ((EBookCollectionAdapter) mAdapter).setEditMode(false);
        mCollectionEditorLl.setVisibility(View.GONE);
        mCollectionDelTv.setClickable(false);
        mCollectionDelTv.setText("删除");
        mCollectionAllCheckTv.setText("全选");
    }

    /**
     * 删除电子书收藏
     */
    public void delEBook() {
        List<String> eBookIdList = new ArrayList<>();
        for (int i = ((EBookCollectionAdapter) mAdapter).mSparseItemChecked.size() - 1; i >= 0; i--) {
            int position = ((EBookCollectionAdapter) mAdapter).mSparseItemChecked.keyAt(i);
            if (((EBookCollectionAdapter) mAdapter).mSparseItemChecked.valueAt(i)) {
                eBookIdList.add(String.valueOf(mAdapter.getItem(position).mEBook.mId));
            }
        }
        mPresenter.cancelCollectionEBookList(eBookIdList);
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
     * @param hasFunction
     */

    private void checkEditFunction(boolean hasFunction) {
        if (hasFunction) {
            mCommonTitleBar.setRightBtnText("编辑");
            mCommonTitleBar.setRightBtnClickAble(true);
        } else {
            mCommonTitleBar.clearRightBtnTxt();
            mCommonTitleBar.setRightBtnClickAble(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveLoginOut(AccountMessage loginMessage) {
        if (null != mPresenter && loginMessage.mIsLoginOut) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (null != mAdapter) {
            mAdapter.clear();
        }
        if (mPresenter != null) {
            mPresenter.unregisterRxBus();
            mPresenter.detachView();
            mPresenter = null;
        }
    }
}
