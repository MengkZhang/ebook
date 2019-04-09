package com.tzpt.cloudlibrary.ui.paperbook;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.ui.library.LibraryAdapter;
import com.tzpt.cloudlibrary.ui.map.LocationManager;
import com.tzpt.cloudlibrary.widget.bottomdialog.CLBottomDialog;
import com.tzpt.cloudlibrary.widget.recyclerview.EasyRecyclerView;
import com.tzpt.cloudlibrary.widget.recyclerview.adapter.RecyclerArrayAdapter;

import java.util.List;

/**
 * 所在馆弹出列表
 * Created by tonyjia on 2018/11/5.
 */
public final class BookBottomLibraryDialog extends CLBottomDialog {

    private EasyRecyclerView mRecyclerView;
    private TextView mLibTitleTv;
    private LibraryAdapter mAdapter;

    public BookBottomLibraryDialog(@NonNull Context context) {
        super(context);
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.view_library_list);
        mRecyclerView = (EasyRecyclerView) findViewById(R.id.belong_library_list_rv);
        mLibTitleTv = (TextView) findViewById(R.id.belong_library_title_tv);
    }

    /**
     * 设置列表适配器
     *
     * @param showStatus 是否展示图书在馆状态
     */
    public void initAdapter(boolean showStatus) {
        mAdapter = new LibraryAdapter(getContext(), showStatus, false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapterWithProgress(mAdapter);
        mRecyclerView.setDividerDrawable(R.drawable.divider_rv_vertical_four);

        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                LibraryBean bean = mAdapter.getItem(position);
                if (null != bean && null != mListener) {
                    mListener.libraryClick(bean.mLibrary.mCode, bean.mLibrary.mName);
                }
            }
        });
        mAdapter.setError(R.layout.common_rv_error_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.resumeMore();
            }
        });
        findViewById(R.id.belong_library_root_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.belong_library_title_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 添加图书馆数据
     *
     * @param libraryBeanList 图书馆列表
     */
    public void addData(List<LibraryBean> libraryBeanList) {
        mAdapter.clear();
        mAdapter.addAll(libraryBeanList);
        mAdapter.refreshLibraryDistanceColor(LocationManager.getInstance().getLocationStatus() == 0);
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        mLibTitleTv.setText(title);
    }

    private LibraryListener mListener;

    public void setLibraryListener(LibraryListener listener) {
        this.mListener = listener;
    }

    public interface LibraryListener {

        void libraryClick(String libCode, String libName);
    }

    /**
     * 清除数据
     */
    public void clear() {
        if (mAdapter != null) {
            mAdapter.clear();
        }
    }
}
