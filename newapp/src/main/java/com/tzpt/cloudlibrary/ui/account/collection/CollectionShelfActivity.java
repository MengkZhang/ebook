package com.tzpt.cloudlibrary.ui.account.collection;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseActivity;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.borrow.BorrowPresenter;
import com.tzpt.cloudlibrary.ui.ebook.EBookCollectionActivity;
import com.tzpt.cloudlibrary.ui.video.VideoCollectionListActivity;
import com.tzpt.cloudlibrary.widget.CustomUserGridMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 收藏
 */
public class CollectionShelfActivity extends BaseActivity implements CollectionContract.View {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, CollectionShelfActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.titlebar_left_btn, R.id.collection_ebook_btn, R.id.collection_video_btn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                finish();
                break;
            case R.id.collection_ebook_btn:
                EBookCollectionActivity.startActivity(this);
                break;
            case R.id.collection_video_btn:
                VideoCollectionListActivity.startActivity(this);
                break;
        }
    }

    //收藏的电子书
    @BindView(R.id.collection_ebook_btn)
    CustomUserGridMenu mCollectionEBookBtn;
    //收藏的视频
    @BindView(R.id.collection_video_btn)
    CustomUserGridMenu mCollectionVideoBtn;
    private CollectionPresenter mPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_collection_shelf;
    }

    @Override
    public void initToolBar() {
        mCommonTitleBar.setTitle("收藏");
        mCommonTitleBar.setLeftBtnIcon(R.drawable.bg_btn_back);
    }

    @Override
    public void initDatas() {
        mPresenter = new CollectionPresenter();
        mPresenter.attachView(this);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getLocalUserInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
        //取消事件订阅
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receviceLoginOut(AccountMessage loginMessage) {
        if (loginMessage.mIsLoginOut) {
            finish();
        }
    }

    /**
     * 设置收藏的电子书数量
     *
     * @param count ：收藏的电子书数量
     */
    @Override
    public void setCollectionEBookCount(int count) {
        if (count > 0) {
            mCollectionEBookBtn.setInfo(String.valueOf(count));
        } else {
            mCollectionEBookBtn.setInfo("");
        }
    }

    /**
     * 设置收藏的视频数量
     * @param count ：收藏的视频数量
     */
    @Override
    public void setCollectionVideoCount(int count) {
        if (count > 0) {
            mCollectionVideoBtn.setInfo(String.valueOf(count));
        } else {
            mCollectionVideoBtn.setInfo("");
        }
    }
}
