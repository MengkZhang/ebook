package com.tzpt.cloudlibrary.app.ebook.books.bookelectronicshelf;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tzpt.cloudlibrary.app.ebook.CloudLibraryReaderActivity;
import com.tzpt.cloudlibrary.app.ebook.R;
import com.tzpt.cloudlibrary.app.ebook.adapter.BaseRecyclerAdapter;
import com.tzpt.cloudlibrary.app.ebook.books.model.LocalBook;
import com.tzpt.cloudlibrary.app.ebook.constant.EbookConstant;

import java.util.List;

/**
 * 电子书架
 */
public class CloudLibraryElectronicShelfActivity extends AppCompatActivity implements
        BaseRecyclerAdapter.OnItemClickListener<LocalBook>,
        ElectronicShelfAdapter.ElectrionicShelfInterface,
        View.OnClickListener {

    private TextView mTextViewConfirm, mTextViewTitle;
    private Button mTextViewHeadTxt;
    private ImageButton mImageBack;
    private RecyclerView mRecyclerView;
    private Button buttonDelete;
    private ElectronicShelfAdapter adapter;
    private boolean isDeleteMode = false;
    private boolean isReadingBook = false;
    private TextView textShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloudlibrary_electronic_shelf);
        initElectonicUI();
        initCustomActionBar("电子书架");
        initElectonicParams();
        initElectonicListener();

    }

    /**
     * 返回是重新查询数据
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isReadingBook) {
            isReadingBook = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new ElectronicSelfTask().execute();
                    if (null != adapter) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }, 800);
        }
    }

    /**
     * 初始化actionbar
     *
     * @param title
     */
    public void initCustomActionBar(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.getHideOffset();
        actionBar.setElevation(0);
        Drawable myDrawable = getResources().getDrawable(R.drawable.home_color_title);
        actionBar.setBackgroundDrawable(myDrawable);
        actionBar.setCustomView(R.layout.include_header_layout);
        View customView = actionBar.getCustomView();
        mTextViewHeadTxt = (Button) customView.findViewById(R.id.head_txt);
        mImageBack = (ImageButton) customView.findViewById(R.id.head_img_search);
        mTextViewTitle = (TextView) customView.findViewById(R.id.head_txt_title);
        mTextViewConfirm = (TextView) customView.findViewById(R.id.head_txt_confirm);
        mTextViewHeadTxt.setVisibility(View.GONE);
        mTextViewTitle.setText(TextUtils.isEmpty(title) ? "" : title);
        mImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initElectonicListener() {
        adapter.setOnItemClickListener(this);
        adapter.setEletrionicShelfListener(this);
        mTextViewConfirm.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
        mTextViewHeadTxt.setOnClickListener(this);
    }

    private void initElectonicParams() {
        final GridLayoutManager manager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_2dp);
        mRecyclerView.addItemDecoration(new GridItemHeaderDecoration(this, true, 3, spacingInPixels, true, false));
        adapter = new ElectronicShelfAdapter(this);
        mRecyclerView.setAdapter(adapter);

        new ElectronicSelfTask().execute();
    }

    private void initElectonicUI() {
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);
        textShow = (TextView) findViewById(R.id.textShow);
    }

    @Override
    public void onItemClick(int position, LocalBook data) {
        //如果是删除模式,则不可点击
        if (isDeleteMode) {
            return;
        }
        if (null != data) {
            if (null == data.file || TextUtils.isEmpty(data.file)
                    || null == data.author || TextUtils.isEmpty(data.author)) {
                return;
            }
            Intent intent = new Intent(CloudLibraryElectronicShelfActivity.this, CloudLibraryReaderActivity.class);
            intent.putExtra(EbookConstant.EPUB_BOOK_IAMGE_PATH, data.medium_image);
            intent.putExtra(EbookConstant.EPUB_BOOK_DOWNLOAD_PATH, data.file);
            intent.putExtra(EbookConstant.EPUB_BOOK_NAME, data.author);// 作者修改为电子书文件夹名称
            startActivity(intent);
            isReadingBook = true;
        }
    }

    @Override
    public void callbackOnLongClick() {
        setDeleteModeView(true);
    }

    /**
     * 展示是否删除状态
     *
     * @param isDeleteMode
     */
    private void setDeleteModeView(boolean isDeleteMode) {
        this.isDeleteMode = isDeleteMode;
        adapter.showDeleteMode(isDeleteMode);
        buttonDelete.setVisibility(isDeleteMode ? View.VISIBLE : View.GONE);
        mTextViewConfirm.setVisibility(isDeleteMode ? View.VISIBLE : View.GONE);
        mTextViewConfirm.setText("完成");
        mTextViewHeadTxt.setVisibility(isDeleteMode ? View.VISIBLE : View.GONE);
        mTextViewHeadTxt.setText("全选");
        mImageBack.setVisibility(isDeleteMode ? View.GONE : View.VISIBLE);
    }


    /**
     * 异步任务获取书架信息
     */
    private class ElectronicSelfTask extends BaseAsyncTask<List<LocalBook>> {
        @Override
        protected List<LocalBook> doInBackground(String... params) {
            try {
                return LocalBook.getLocalBookList(CloudLibraryElectronicShelfActivity.this,
                        LocalBook.OrderColumn.BY_LAST_ACCESS_TIME,
                        LocalBook.OrderType.DESC);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<LocalBook> ebookSelfs) {
            super.onPostExecute(ebookSelfs);
            if (null != ebookSelfs && ebookSelfs.size() > 0) {
                adapter.addDatas(ebookSelfs, false);
                textShow.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                textShow.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mTextViewConfirm) {
            setDeleteModeView(false);
        } else if (v == buttonDelete) {
            adapter.deleteSelectElectronicShelf();
            setDeleteModeView(false);
            //重新查询书架列表
            new ElectronicSelfTask().execute();
        } else if (v == mTextViewHeadTxt) {
            isAllChecked = !isAllChecked;
            adapter.selectAllShelf(isAllChecked);
            mTextViewHeadTxt.setText(isAllChecked ? "取消" : "全选");
        }
    }

    private boolean isAllChecked = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (isDeleteMode) {
                isDeleteMode = false;
                setDeleteModeView(false);
            } else {
                this.finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
