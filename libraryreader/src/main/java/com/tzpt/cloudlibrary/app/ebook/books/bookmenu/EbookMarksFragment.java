package com.tzpt.cloudlibrary.app.ebook.books.bookmenu;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tzpt.cloudlibrary.app.ebook.R;
import com.tzpt.cloudlibrary.app.ebook.adapter.BookMarkAdapter;
import com.tzpt.cloudlibrary.app.ebook.books.bookelectronicshelf.BaseAsyncTask;
import com.tzpt.cloudlibrary.app.ebook.books.model.EpubBookMarks;

import java.util.List;

/**
 * 书签列表
 */
public class EbookMarksFragment extends Fragment implements
        BookMarkAdapter.BookMarkDeleteInterface,
        View.OnClickListener {

    private RecyclerView mRecyclerView;
    private BookMarkAdapter mBookMarkAdapter;
    private View bookMarkView;
    private PopupWindow bookMarkPopWindow;
    private TextView textShow, mTextJumpToBookMark, mTextDeleteBookMark;
    private View mTextshadow;
    private long bookPageIndex = 1;
    private boolean isPrepard;
    private long lastAccessTime;
    private String bookId;
    private EbookMarksFragmentToPageIndexListener callback;
    private Typeface typeFace;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ebook_marks, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (EbookMarksFragmentToPageIndexListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement EbookMarksFragmentToPageIndexListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * 设置用户可见的 默认提示
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint() && isPrepard) {
            queryBookMarkList();
        }
    }

    //可见:延迟加载
    //查询书签列表
    private void queryBookMarkList() {
        new QueryBookMarksTask().execute();
        if (null != mBookMarkAdapter) {
            mBookMarkAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBookMarksUI(view);
        isPrepard = true;
        initBookMarkParams();
        initBookMarkListener();

    }

    /**
     * 初始化UI
     */
    private void initBookMarksUI(View view) {
        this.mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        this.textShow = (TextView) view.findViewById(R.id.textShow);
    }

    /**
     * 初始化书签参数
     */
    private void initBookMarkParams() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DeviderItemDerocation(getActivity(), DeviderItemDerocation.VERTICAL_LIST));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mBookMarkAdapter = new BookMarkAdapter(typeFace);
        mRecyclerView.setAdapter(mBookMarkAdapter);
        mBookMarkAdapter.setOnItemClickListener(new BookMarkAdapter.OnItemClickListener<EpubBookMarks>() {
            @Override
            public void onItemClick(int position, EpubBookMarks data) {

                //跳转到指定界面
                if (null != callback && null != data) {
                    callback.callbackClickItemToPageIndex((int) data.current_page);
                }
            }
        });

    }

    private void initBookMarkListener() {
        mBookMarkAdapter.setOnItemDeleteLongClickListener(this);
    }

    @Override
    public void callbackItemLongClick(long lastAccessTime, long pageIndex) {
        this.lastAccessTime = lastAccessTime;
        this.bookPageIndex = pageIndex;
        //弹出菜单，删除书签
        showDeletePopUpWindow();
    }

    public BookMarkAdapter getAdapter() {
        return mBookMarkAdapter;
    }

    /**
     * 设置字体类型
     *
     * @param typeFace
     */
    public void setTypeFace(Typeface typeFace) {
        this.typeFace = typeFace;
    }


    /**
     * 查询书签列表
     */
    private class QueryBookMarksTask extends BaseAsyncTask<List<EpubBookMarks>> {

        @Override
        protected List<EpubBookMarks> doInBackground(String... params) {
            return EpubBookMarks.getLocalBookList(getActivity(), bookId, EpubBookMarks.OrderColumn.BY_LAST_ACCESS_TIME, EpubBookMarks.OrderType.DESC);
        }

        @Override
        protected void onPostExecute(List<EpubBookMarks> epubBookMarkses) {
            super.onPostExecute(epubBookMarkses);
            if (null != epubBookMarkses && epubBookMarkses.size() > 0) {
                mBookMarkAdapter.addDatas(epubBookMarkses, false);
                mRecyclerView.setVisibility(View.VISIBLE);
                textShow.setVisibility(View.GONE);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                textShow.setVisibility(View.VISIBLE);

                if (null != typeFace) {
                    textShow.setTypeface(typeFace);
                }
            }
        }
    }

    /**
     * 设置图书ID
     *
     * @param bookId
     */
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    /**
     * 初始化删除界面
     */
    private void initDeletePopUpWindow() {
        bookMarkView = LayoutInflater.from(getActivity()).inflate(R.layout.popwindow_delete_item, null);
        bookMarkPopWindow = new PopupWindow(bookMarkView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        bookMarkPopWindow.setBackgroundDrawable(new BitmapDrawable());
        bookMarkPopWindow.setOutsideTouchable(true);
        mTextJumpToBookMark = (TextView) bookMarkView.findViewById(R.id.mTextJumpToBookMark);
        mTextDeleteBookMark = (TextView) bookMarkView.findViewById(R.id.mTextDeleteBookMark);
        mTextshadow = bookMarkView.findViewById(R.id.mTextshadow);
        mTextJumpToBookMark.setOnClickListener(this);
        mTextDeleteBookMark.setOnClickListener(this);
        mTextshadow.setOnClickListener(this);
        bookMarkPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                bookPageIndex = 1;
            }
        });
    }

    /**
     * 弹出删除界面
     */
    private void showDeletePopUpWindow() {
        if (null == bookMarkPopWindow) {
            initDeletePopUpWindow();
        }
        bookMarkPopWindow.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onClick(View v) {
        if (v == mTextJumpToBookMark) {
            if (null != callback) {
                callback.callbackClickItemToPageIndex((int) bookPageIndex);
            }
            popUpWindowDismiss();
        } else if (v == mTextDeleteBookMark) {
            try {
                new EpubBookMarks(getActivity()).deleteByAddTime(String.valueOf(lastAccessTime));
            } catch (Exception e) {
                toastInfo("删除书签失败！");
            }
            //查询书签列表
            queryBookMarkList();
            popUpWindowDismiss();
        } else if (v == mTextshadow) {
            popUpWindowDismiss();
        }

    }

    /**
     * 消失界面
     */
    private void popUpWindowDismiss() {
        if (null != bookMarkPopWindow) {
            bookMarkPopWindow.dismiss();
        }
    }

    public interface EbookMarksFragmentToPageIndexListener {

        void callbackClickItemToPageIndex(int pageIndex);
    }

    public void toastInfo(String message) {
        if (null != bookMarkView) {
            Snackbar.make(bookMarkView, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
