package com.tzpt.cloudlibrary.ui.account;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tzpt.cloudlibrary.AppIntentGlobalName;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.utils.GalleryDetailLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 相册界面
 * Created by Administrator on 2018/11/9.
 */

public class GalleryActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<List<String>> {
    private static final int REQUEST_CODE_CROP_PHOTO = 1000;

    public static void startActivityForResult(Activity context, int requestCode) {
        Intent intent = new Intent(context, GalleryActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @OnClick({R.id.titlebar_left_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_gallery;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("相册");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        GridLayoutManager linerLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(linerLayoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        return new GalleryDetailLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<String>> loader, final List<String> data) {
        GalleryGridViewAdapter adapter = new GalleryGridViewAdapter(this, data, new GalleryGridViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String imgPath = data.get(position);
                Intent cropPhoto = new Intent(GalleryActivity.this, CropPhotoActivity.class);
                cropPhoto.putExtra(AppIntentGlobalName.PIC_PATH, imgPath);
                startActivityForResult(cropPhoto, REQUEST_CODE_CROP_PHOTO);
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CROP_PHOTO) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
