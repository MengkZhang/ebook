package com.tzpt.cloudlibrary.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.UmengHelper;
import com.tzpt.cloudlibrary.base.BaseFragment;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.OnRvItemClickListener;
import com.tzpt.cloudlibrary.bean.ActionInfoBean;
import com.tzpt.cloudlibrary.bean.BannerInfo;
import com.tzpt.cloudlibrary.bean.InformationBean;
import com.tzpt.cloudlibrary.bean.ModelMenu;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.ui.bookstore.BookStoreActivity;
import com.tzpt.cloudlibrary.ui.bookstore.BookStoreDetailActivity;
import com.tzpt.cloudlibrary.ui.ebook.EBookDetailActivity;
import com.tzpt.cloudlibrary.ui.ebook.EBookGridListAdapter;
import com.tzpt.cloudlibrary.ui.ebook.EBookTabActivity;
import com.tzpt.cloudlibrary.ui.information.InformationActivity;
import com.tzpt.cloudlibrary.ui.information.InformationDetailDiscussActivity;
import com.tzpt.cloudlibrary.ui.information.InformationHomeAdapter;
import com.tzpt.cloudlibrary.ui.library.LibraryActivity;
import com.tzpt.cloudlibrary.ui.library.LibraryDetailActivity;
import com.tzpt.cloudlibrary.ui.library.LibraryHomeAdapter;
import com.tzpt.cloudlibrary.ui.map.SwitchCityActivity;
import com.tzpt.cloudlibrary.ui.paperbook.BookDetailActivity;
import com.tzpt.cloudlibrary.ui.paperbook.BookHomeListAdapter;
import com.tzpt.cloudlibrary.ui.paperbook.BookTabActivity;
import com.tzpt.cloudlibrary.ui.ranklist.RankListActivity;
import com.tzpt.cloudlibrary.ui.readers.ActionDetailsActivity;
import com.tzpt.cloudlibrary.ui.readers.ActionListHomeAdapter;
import com.tzpt.cloudlibrary.ui.readers.ActivityListActivity;
import com.tzpt.cloudlibrary.ui.search.SearchActivity;
import com.tzpt.cloudlibrary.ui.video.VideoDetailActivity;
import com.tzpt.cloudlibrary.ui.video.VideoListHomeAdapter;
import com.tzpt.cloudlibrary.ui.video.VideoTabActivity;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.widget.HomeGridManager;
import com.tzpt.cloudlibrary.widget.HomeListItemView;
import com.tzpt.cloudlibrary.widget.bannerview.BannerView;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页
 * Created by tonyjia on 2018/10/17.
 */
public class TabHomeFragment extends BaseFragment implements
        TabHomeContract.View {

    @BindView(R.id.home_content_sv)
    ScrollView mHomeContentSv;
    @BindView(R.id.home_location_tv)
    TextView mHomeLocationTv;
    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.home_banner_view)
    BannerView mBannerView;
    @BindView(R.id.banner_title_tv)
    TextView mBannerTitleTv;
    @BindView(R.id.home_model_rv)
    RecyclerView mHomeModelRv;
    @BindView(R.id.banner_default_img)
    ImageView mBannerDefaultImg;
    @BindView(R.id.home_near_library_vs)
    ViewStub mHomeNearLibraryVs;
    @BindView(R.id.home_paper_book_vs)
    ViewStub mHomePaperBookVs;
    @BindView(R.id.home_ebook_vs)
    ViewStub mHomeEBookVs;
    @BindView(R.id.home_video_vs)
    ViewStub mHomeVideoVs;
    @BindView(R.id.home_news_vs)
    ViewStub mHomeNewsVs;
    @BindView(R.id.home_activity_vs)
    ViewStub mHomeActivityVs;

    private HomeListItemView mNearLibIv;
    private HomeListItemView mPaperBookIv;
    private HomeListItemView mEBookIv;
    private HomeListItemView mVideoIv;
    private HomeListItemView mNewsIv;
    private HomeListItemView mActivityIv;

    private LibraryHomeAdapter mLibraryAdapter;
    private BookHomeListAdapter mBookListAdapter;
    private EBookGridListAdapter mEBookAdapter;
    private VideoListHomeAdapter mVideoListAdapter;
    private InformationHomeAdapter mInformationAdapter;
    private ActionListHomeAdapter mActionListAdapter;
    private BannerAdapter mBannerAdapter;
    private MenuModelListAdapter mModelListAdapter;

    private TabHomePresenter mPresenter;
    private boolean mIsPrepared;
    private boolean mIsFirstLoad = true;

    @OnClick({R.id.home_location_tv, R.id.home_search_btn, R.id.banner_default_img})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.banner_default_img:
                mPresenter.getBannerList();
                break;
            case R.id.home_location_tv: //切换位置
                SwitchCityActivity.startActivityForResultFragment(this, 1000);
                break;
            case R.id.home_search_btn:  //搜索
                SearchActivity.startActivity(getSupportActivity());
                break;
        }
    }

    private OnRvItemClickListener<ModelMenu> mModelItemClickListener = new OnRvItemClickListener<ModelMenu>() {
        @Override
        public void onItemClick(int position, ModelMenu data) {
            int id = data.mId;
            switch (id) {
                case 0:
                    LibraryActivity.startActivity(getActivity(), 1);
                    break;
                case 1:
                    BookTabActivity.startActivity(getActivity(), 0);
                    break;
                case 2:
                    EBookTabActivity.startActivity(getActivity(), 0);
                    break;
                case 3:
                    InformationActivity.startActivity(getActivity());
                    break;
                case 4:
                    ActivityListActivity.startActivity(getActivity(), 0);
                    break;
                case 5:
                    VideoTabActivity.startActivity(getActivity(), null, null);
                    break;
                case 6:
                    RankListActivity.startActivity(getActivity());
                    break;
                case 7:
                    BookStoreActivity.startActivity(getSupportActivity());
                    break;
            }
        }
    };


    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    public void initDatas() {
        mPresenter = new TabHomePresenter();
        mPresenter.attachView(this);

    }

    @Override
    public void configViews() {
        mIsPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!mIsVisible || !mIsPrepared) {
            return;
        }
        if (null != mPresenter && mIsFirstLoad) {
            this.mIsFirstLoad = false;
            mPresenter.getHomeInfoList();
        }
    }

    @Override
    public void showHomeDataProgress() {
        mMultiStateLayout.showProgress();
    }

    @Override
    public void setHomeDataErr() {
        mMultiStateLayout.showError();
        mMultiStateLayout.showRetryError(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getHomeInfoList();
            }
        });
    }

    @Override
    public void showHomeData() {
        mMultiStateLayout.showContentView();
        mHomeContentSv.scrollTo(0, 0);
    }

    /**
     * 设置banner列表
     *
     * @param list banner列表
     */
    @Override
    public void setBannerList(List<BannerInfo> list) {
        mBannerView.showBannerView();
        if (null == mBannerAdapter) {
            mBannerAdapter = new BannerAdapter(getActivity());
        }
        mBannerAdapter.addAllData(list);
        mBannerView.setAdapter(mBannerAdapter);
        mBannerAdapter.setOnItemClickListener(new BannerAdapter.OnItemClickListener() {

            @Override
            public void onClick(BannerInfo item) {
                if (null != item && !TextUtils.isEmpty(item.mNewsId)) {
                    InformationDetailDiscussActivity.startActivity(getActivity(), Long.valueOf(item.mNewsId));
                }
            }
        });

        mBannerView.setBannerTitle(mBannerAdapter.getItemData(0).mTitle);
    }

    @Override
    public void setBannerErr() {
        mBannerView.hideBannerView();
    }

    /**
     * 设置首页模块列表
     *
     * @param homeModelBeanList 模块列表
     */
    @Override
    public void setHomeModelList(List<ModelMenu> homeModelBeanList) {
        if (mModelListAdapter == null) {
            mHomeModelRv.setLayoutManager(new HomeGridManager(getSupportActivity(), 5));
            mHomeModelRv.setHasFixedSize(true);
            ViewCompat.stopNestedScroll(mHomeModelRv);
            ((SimpleItemAnimator) mHomeModelRv.getItemAnimator()).setSupportsChangeAnimations(false);

            mModelListAdapter = new MenuModelListAdapter(getSupportActivity());
            mHomeModelRv.setAdapter(mModelListAdapter);
            mModelListAdapter.setOnItemClickListener(mModelItemClickListener);
        }
        mModelListAdapter.clear();
        mModelListAdapter.addAll(homeModelBeanList);
    }

    /**
     * 附近图书馆和书店
     *
     * @param libraryBeanList 图书馆列表
     */
    @Override
    public void setNearLibraryList(List<LibraryBean> libraryBeanList) {
        if (mNearLibIv == null) {
            View view = mHomeNearLibraryVs.inflate();
            mNearLibIv = (HomeListItemView) view.findViewById(R.id.home_lib_list_iv);
            mNearLibIv.configLinearLayoutManager(getSupportActivity());
            mLibraryAdapter = new LibraryHomeAdapter(getActivity());
            mNearLibIv.setAdapter(mLibraryAdapter);
            mNearLibIv.setTitle("附近");
            mLibraryAdapter.setOnItemClickListener(new OnRvItemClickListener<LibraryBean>() {
                @Override
                public void onItemClick(int position, LibraryBean libraryBean) {
                    if (libraryBean != null) {
                        if (libraryBean.mIsBookStore) {
                            BookStoreDetailActivity.startActivity(getSupportActivity(), libraryBean.mLibrary.mCode, libraryBean.mLibrary.mName);
                        } else {
                            LibraryDetailActivity.startActivity(getActivity(), libraryBean.mLibrary.mCode, libraryBean.mLibrary.mName);
                        }
                    }
                }
            });
            mNearLibIv.setTitleListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LibraryActivity.startActivity(getActivity(), 0);
                }
            });
        } else {
            mNearLibIv.showHomeListItem();
        }
        mLibraryAdapter.clear();
        mLibraryAdapter.addAll(libraryBeanList);
    }

    @Override
    public void hideNearLibraryList() {
        if (mNearLibIv != null) {
            mNearLibIv.hideHomeListItem();
        }
    }

    /**
     * 图书
     *
     * @param bookList 图书列表
     */
    @Override
    public void setPaperBookList(List<BookBean> bookList) {
        if (mPaperBookIv == null) {
            View view = mHomePaperBookVs.inflate();
            mPaperBookIv = (HomeListItemView) view.findViewById(R.id.home_book_list_iv);
            mPaperBookIv.setRecyclerViewMargin(DpPxUtils.dp2px(13.5f), 0, DpPxUtils.dp2px(13.5f), 0);

            mBookListAdapter = new BookHomeListAdapter(getSupportActivity());
            mPaperBookIv.configGridLayoutManager(getSupportActivity(), 4);
            mPaperBookIv.setAdapter(mBookListAdapter);
            mPaperBookIv.setTitle("图书");
            mBookListAdapter.setOnItemClickListener(new OnRvItemClickListener<BookBean>() {
                @Override
                public void onItemClick(int position, BookBean bean) {
                    if (bean != null) {
                        BookDetailActivity.startActivity(getSupportActivity(), bean.mBook.mIsbn, null, null, 0);
                    }
                }
            });
            mPaperBookIv.setTitleListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookTabActivity.startActivity(getActivity(), 0);
                }
            });
        } else {
            mPaperBookIv.showHomeListItem();
        }
        mBookListAdapter.clear();
        mBookListAdapter.addAll(bookList);
    }

    @Override
    public void hidePaperBookList() {
        if (mPaperBookIv != null) {
            mPaperBookIv.hideHomeListItem();
        }
    }

    /**
     * 电子书
     *
     * @param eBookBeanList 电子书列表
     */
    @Override
    public void setEBookList(List<EBookBean> eBookBeanList) {
        if (mEBookIv == null) {
            View view = mHomeEBookVs.inflate();
            mEBookIv = (HomeListItemView) view.findViewById(R.id.home_ebook_list_iv);
            mEBookIv.setRecyclerViewMargin(DpPxUtils.dp2px(13.5f), 0, DpPxUtils.dp2px(13.5f), 0);
            mEBookAdapter = new EBookGridListAdapter(getSupportActivity(), false);
            mEBookIv.configGridLayoutManager(getSupportActivity(), 4);
            mEBookIv.setAdapter(mEBookAdapter);

            mEBookIv.setTitle("电子书");
            mEBookAdapter.setOnItemClickListener(new OnRvItemClickListener<EBookBean>() {
                @Override
                public void onItemClick(int position, EBookBean data) {
                    if (data != null) {
                        EBookDetailActivity.startActivity(getSupportActivity(), String.valueOf(data.mEBook.mId), null);
                    }
                }
            });
            mEBookIv.setTitleListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EBookTabActivity.startActivity(getActivity(), 0);
                }
            });
        } else {
            mEBookIv.showHomeListItem();
        }
        mEBookAdapter.clear();
        mEBookAdapter.addAll(eBookBeanList);
    }

    @Override
    public void hideEBookList() {
        if (mEBookIv != null) {
            mEBookIv.hideHomeListItem();
        }
    }

    /**
     * 视频
     *
     * @param videoSetBeanList 视频列表
     */
    @Override
    public void setVideoList(List<VideoSetBean> videoSetBeanList) {
        if (mVideoIv == null) {
            View view = mHomeVideoVs.inflate();
            mVideoIv = (HomeListItemView) view.findViewById(R.id.home_video_list_iv);
            mVideoListAdapter = new VideoListHomeAdapter(getSupportActivity());
            mVideoIv.configLinearLayoutManager(getSupportActivity());
            mVideoIv.setAdapter(mVideoListAdapter);
            mVideoIv.setTitle("视频");
            mVideoListAdapter.setOnItemClickListener(new OnRvItemClickListener<VideoSetBean>() {
                @Override
                public void onItemClick(int position, VideoSetBean data) {
                    if (data != null) {
                        VideoDetailActivity.startActivity(getSupportActivity(), data.getId(), data.getTitle(), null);
                    }
                }
            });
            mVideoIv.setTitleListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoTabActivity.startActivity(getActivity(), null, null);
                }
            });
        } else {
            mVideoIv.showHomeListItem();
        }
        mVideoListAdapter.clear();
        mVideoListAdapter.addAll(videoSetBeanList);
    }

    @Override
    public void hideVideoList() {
        if (mVideoIv != null) {
            mVideoIv.hideHomeListItem();
        }
    }

    /**
     * 活动
     *
     * @param activityBeanList 活动列表
     */
    @Override
    public void setActivityList(List<ActionInfoBean> activityBeanList) {
        if (mActivityIv == null) {
            View view = mHomeActivityVs.inflate();
            mActivityIv = (HomeListItemView) view.findViewById(R.id.home_activity_list_iv);
            mActivityIv.configLinearLayoutManager(getSupportActivity());
            mActionListAdapter = new ActionListHomeAdapter(getSupportActivity());
            mActivityIv.setAdapter(mActionListAdapter);
            mActivityIv.setTitle("活动");
            mActionListAdapter.setOnItemClickListener(new OnRvItemClickListener<ActionInfoBean>() {
                @Override
                public void onItemClick(int position, ActionInfoBean data) {
                    if (data != null) {
                        ActionDetailsActivity.startActivityById(getSupportActivity(), data.mId);
                    }
                }
            });
            mActivityIv.setTitleListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityListActivity.startActivity(getActivity(), 0);
                }
            });
        } else {
            mActivityIv.showHomeListItem();
        }
        mActionListAdapter.clear();
        mActionListAdapter.addAll(activityBeanList);
    }

    @Override
    public void hideActivityList() {
        if (mActivityIv != null) {
            mActivityIv.hideHomeListItem();
        }
    }

    /**
     * 设置资讯列表
     *
     * @param informationBeanList 资讯列表
     */
    @Override
    public void setInformationList(List<InformationBean> informationBeanList) {
        if (mNewsIv == null) {
            View view = mHomeNewsVs.inflate();
            mNewsIv = (HomeListItemView) view.findViewById(R.id.home_news_list_iv);
            mNewsIv.configLinearLayoutManager(getSupportActivity());

            mInformationAdapter = new InformationHomeAdapter(getSupportActivity());
            mNewsIv.setAdapter(mInformationAdapter);
            mNewsIv.setTitle("资讯");
            mInformationAdapter.setOnItemClickListener(new OnRvItemClickListener<InformationBean>() {
                @Override
                public void onItemClick(int position, InformationBean data) {
                    if (data != null) {
                        InformationDetailDiscussActivity.startActivity(getActivity(), data.mId);
                    }
                }
            });
            mNewsIv.setTitleListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InformationActivity.startActivity(getActivity());
                }
            });
        } else {
            mNewsIv.showHomeListItem();
        }
        mInformationAdapter.clear();
        mInformationAdapter.addAll(informationBeanList);
    }

    @Override
    public void hideInformationList() {
        if (mNewsIv != null) {
            mNewsIv.hideHomeListItem();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1000) {
            mPresenter.getHomeInfoList();
        }
    }

    @Override
    public void setDistrictText(String districtText) {
        if (null != districtText && districtText.length() > 5) {
            districtText = districtText.substring(0, 5) + "...";
        }
        mHomeLocationTv.setText(districtText);
    }

    @Override
    public void setMenuVisibility(boolean menuVisibile) {
        super.setMenuVisibility(menuVisibile);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisibile ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        UmengHelper.setPageStart("TabHomeFragment");
        if (mBannerView != null) {
            mBannerView.restoreLoopPicHandler();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            if (mBannerView != null) {
                mBannerView.restoreLoopPicHandler();
            }
        } else {
            if (mBannerView != null) {
                mBannerView.pauseLoopPicHandler();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        UmengHelper.setPageEnd("TabHomeFragment");
        if (mBannerView != null) {
            mBannerView.pauseLoopPicHandler();
        }
    }

    @Override
    public void onDestroyView() {
        mBannerView.releaseBanner();
        super.onDestroyView();
        if (mLibraryAdapter != null) {
            mLibraryAdapter.clear();
        }
        if (mBookListAdapter != null) {
            mBookListAdapter.clear();
        }
        if (mEBookAdapter != null) {
            mEBookAdapter.clear();
        }
        if (mVideoListAdapter != null) {
            mVideoListAdapter.clear();
        }
        if (mActionListAdapter != null) {
            mActionListAdapter.clear();
        }
        if (mBannerAdapter != null) {
            mBannerAdapter.clearBannerData();
        }
        if (mModelListAdapter != null) {
            mModelListAdapter.clear();
        }
        if (mInformationAdapter != null) {
            mInformationAdapter.clear();
        }
        mPresenter.detachView();
    }

}
