package com.tzpt.cloundlibrary.manager.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseActivity;
import com.tzpt.cloundlibrary.manager.ui.adapter.ReaderNationListAdapter;
import com.tzpt.cloundlibrary.manager.ui.contract.ReaderNationSelectContract;
import com.tzpt.cloundlibrary.manager.ui.presenter.ReaderNationSelectPresenter;
import com.tzpt.cloundlibrary.manager.utils.Utils;
import com.tzpt.cloundlibrary.manager.widget.recyclerview.EasyRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 读者民族选择界面
 */
public class ReaderNationSelectActivity extends BaseActivity implements
        ReaderNationSelectContract.View {

    public static void startActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ReaderNationSelectActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;
    private ReaderNationListAdapter mAdapter;
    private ReaderNationSelectPresenter mPresenter;

    @OnClick(R.id.titlebar_left_btn)
    public void onViewClicked(View v) {
        this.finish();
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String nation = (String) v.getTag();
            Intent intent = new Intent();
            intent.putExtra("nation", nation);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_reader_nation_select;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_arrow_left);
        mCommonTitleBar.setTitle("选择民族");
    }

    @Override
    public void initDatas() {
        mPresenter = new ReaderNationSelectPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void configViews() {
        mAdapter = new ReaderNationListAdapter(this, mClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemDecoration(ContextCompat.getColor(this, R.color.color_e5e5e5),
                2, (int) Utils.dpToPx(this, 16), 0);
        mRecyclerView.setAdapterWithProgress(mAdapter);

        mPresenter.getReaderNationList();
    }

    @Override
    public void setReaderNationList(List<String> nationList) {
        mAdapter.clear();
        mAdapter.addAll(nationList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.clear();
        mPresenter.detachView();
        mPresenter = null;
    }

}
