package com.tzpt.cloudlibrary.ui.account.interaction;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.ui.library.LibraryAdapter;
import com.tzpt.cloudlibrary.ui.map.LocationManager;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;
import com.tzpt.cloudlibrary.widget.searchview.SearchBarLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 推荐新书
 * Created by ZhiqiangJia on 2017-09-04.
 */
public class RecommendNewBookDialogFragment extends DialogFragment implements
        RecyclerArrayAdapter.OnItemClickListener,
        RecommendNewBookDialogContract.View {

    @BindView(R.id.search_bar_layout)
    SearchBarLayout mSearchBarLayout;
    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;
    @BindView(R.id.parent_layout)
    LinearLayout mParentLayout;
    private Unbinder mUnbinder;
    private LibraryAdapter mAdapter;
    private String mBookIsbn;
    private RecommendNewBookDialogPresenter mPresenter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_recommend_newbook_dialog, null);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消
        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM; // 紧贴底部
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        window.setAttributes(lp);
        mUnbinder = ButterKnife.bind(this, view);
        initRecyclerViewUI();
        return dialog;
    }

    private void initRecyclerViewUI() {
        mAdapter = new LibraryAdapter(getActivity(), false, true);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setError(R.layout.common_rv_error_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.resumeMore();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_four);
        mRecyclerView.setAdapterWithProgress(mAdapter);

        mSearchBarLayout.setSearchBarListener(mOnSearchBarListener);
    }

    private SearchBarLayout.OnSearchBarListener mOnSearchBarListener = new SearchBarLayout.OnSearchBarListener() {

        @Override
        public void searchContent(String keyWord) {
            mPresenter.searchLibByKeyWord(mBookIsbn, keyWord);
        }

        @Override
        public void dismissDialog() {
            dismiss();
        }
    };

    /**
     * 销毁界面
     *
     * @param dialog
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (null != mPresenter) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    public void setBookIsbn(String isbn) {
        this.mBookIsbn = isbn;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mPresenter = new RecommendNewBookDialogPresenter();
        mPresenter.attachView(this);

        mPresenter.getRecommendBookLibList(mBookIsbn);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onItemClick(int position) {
        LibraryBean bean = mAdapter.getItem(position);
        if (null != bean && null != bean.mLibrary.mCode) {
            if (bean.bookExist == 1 || bean.recommendExist == 1) {
                return;
            }
            mPresenter.recommendNewBookToLibrary(mBookIsbn, bean.mLibrary.mCode);
        }
    }

    @Override
    public void showLoadingProgress() {
        mRecyclerView.showProgress();
    }

    @Override
    public void showNetError() {
        mRecyclerView.showError();
    }

    @Override
    public void setLibraryList(List<LibraryBean> libraryBeanList) {
        mAdapter.clear();
        mAdapter.addAll(libraryBeanList);
        refreshLibraryDistanceColor();
    }

    //设置颜色
    private void refreshLibraryDistanceColor() {
        if (null != mAdapter) {
            mAdapter.refreshLibraryDistanceColor(LocationManager.getInstance().getLocationStatus() == 0);
        }
    }

    @Override
    public void setLibraryListEmpty() {
        mRecyclerView.showEmpty();
    }

    @Override
    public void showErrorMsg(int resId) {
        ToastUtils.showSingleToast(resId);
    }

    @Override
    public void recommendSuccess(String libCode) {
        ToastUtils.showSingleToast(R.string.recommend_success);
        refreshCurrentRecommendLibraryInfo(libCode);
        mPresenter.refreshTempRecommendLibraryInfo(libCode);
    }

    @Override
    public void pleaseLogin() {
        if (null != mLoginOutListener) {
            mLoginOutListener.callbackLoginOut();
        }
        dismiss();
    }

    /**
     * 刷新当前列表
     */
    private void refreshCurrentRecommendLibraryInfo(String libCode) {
        if (null == mAdapter) {
            return;
        }
        List<LibraryBean> libraryList = mAdapter.getAllData();
        if (null == libraryList) {
            return;
        }
        int size = libraryList.size();
        int index = -1;
        for (int i = 0; i < size; i++) {
            if (libraryList.get(i).mLibrary.mCode.equals(libCode)) {
                index = i;
                break;
            }
        }
        if (index != -1 && index < size) {
            mAdapter.getAllData().get(index).recommendExist = 1;
            mAdapter.notifyItemChanged(index);
        }
    }

    public interface NewBookDialogListener {
        void callbackLoginOut();
    }

    public NewBookDialogListener mLoginOutListener;

    public void setNewBookDialogListener(NewBookDialogListener loginOutListener) {
        this.mLoginOutListener = loginOutListener;
    }
}
