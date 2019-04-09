package com.tzpt.cloudlibrary.ui.main;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.BaseFragment;
import com.tzpt.cloudlibrary.base.adapter.glide.RoundedCornersTransformation;
import com.tzpt.cloudlibrary.base.adapter.recyclerview.OnRvItemClickListener;
import com.tzpt.cloudlibrary.bean.ActionInfoBean;
import com.tzpt.cloudlibrary.bean.BannerInfo;
import com.tzpt.cloudlibrary.bean.InformationBean;
import com.tzpt.cloudlibrary.bean.ModelMenu;
import com.tzpt.cloudlibrary.bean.VideoSetBean;
import com.tzpt.cloudlibrary.business_bean.BookBean;
import com.tzpt.cloudlibrary.business_bean.EBookBean;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.rxbus.event.AttentionLibEvent;
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.LoginActivity;
import com.tzpt.cloudlibrary.ui.account.deposit.UserDepositAgreementActivity;
import com.tzpt.cloudlibrary.ui.bookstore.BookStoreIntroduceActivity;
import com.tzpt.cloudlibrary.ui.ebook.EBookDetailActivity;
import com.tzpt.cloudlibrary.ui.ebook.EBookGridListAdapter;
import com.tzpt.cloudlibrary.ui.ebook.EBookTabActivity;
import com.tzpt.cloudlibrary.ui.information.InformationActivity;
import com.tzpt.cloudlibrary.ui.information.InformationDetailDiscussActivity;
import com.tzpt.cloudlibrary.ui.information.InformationHomeAdapter;
import com.tzpt.cloudlibrary.ui.library.BorrowingIntroduceActivity;
import com.tzpt.cloudlibrary.ui.library.LibraryActivity;
import com.tzpt.cloudlibrary.ui.library.LibraryIntroduceActivity;
import com.tzpt.cloudlibrary.ui.library.LibraryIntroduceView;
import com.tzpt.cloudlibrary.ui.library.LibraryMainBranchActivity;
import com.tzpt.cloudlibrary.ui.library.LibraryMessageBoardActivity;
import com.tzpt.cloudlibrary.ui.map.MapNavigationActivity;
import com.tzpt.cloudlibrary.ui.paperbook.BookDetailActivity;
import com.tzpt.cloudlibrary.ui.paperbook.BookHomeListAdapter;
import com.tzpt.cloudlibrary.ui.paperbook.BookTabActivity;
import com.tzpt.cloudlibrary.ui.readers.ActionDetailsActivity;
import com.tzpt.cloudlibrary.ui.readers.ActionListHomeAdapter;
import com.tzpt.cloudlibrary.ui.readers.ActivityListActivity;
import com.tzpt.cloudlibrary.ui.search.SearchActivity;
import com.tzpt.cloudlibrary.ui.video.VideoDetailActivity;
import com.tzpt.cloudlibrary.ui.video.VideoListHomeAdapter;
import com.tzpt.cloudlibrary.ui.video.VideoTabActivity;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.HomeListItemView;
import com.tzpt.cloudlibrary.widget.bannerview.BannerView;
import com.tzpt.cloudlibrary.widget.menupager.ModelMenuLayout;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;
import com.tzpt.cloudlibrary.widget.titlebar.TitleBarView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 关注馆
 * Created by Administrator on 2018/8/28.
 */
public class TabAttentionLibFragment extends BaseFragment implements AttentionLibContract.View {
    @BindView(R.id.common_toolbar)
    TitleBarView mCommonTitleBar;
    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.no_attention_lib_ll)
    LinearLayout mNoAttentionLibLl;
    @BindView(R.id.attention_lib_sv)
    ScrollView mAttentionLibSv;
    @BindView(R.id.lib_banner_view)
    BannerView mLibBannerView;

    @BindView(R.id.lib_model_menu_layout)
    ModelMenuLayout mModelMenuLayout;
    @BindView(R.id.lib_introduce_view)
    LibraryIntroduceView mLibIntroduceView;
    @BindView(R.id.lib_paper_book_vs)
    ViewStub mLibPaperBookVs;
    @BindView(R.id.lib_ebook_vs)
    ViewStub mLibEBookVs;
    @BindView(R.id.lib_video_vs)
    ViewStub mLibVideoVs;
    @BindView(R.id.lib_news_vs)
    ViewStub mLibNewsVs;
    @BindView(R.id.lib_activity_vs)
    ViewStub mLibActivityVs;

    private HomeListItemView mLibPaperBookIv;
    private HomeListItemView mLibEBookIv;
    private HomeListItemView mLibVideoIv;
    private HomeListItemView mLibNewsIv;
    private HomeListItemView mLibActivityIv;

    private BookHomeListAdapter mBookListAdapter;
    private EBookGridListAdapter mEBookAdapter;
    private VideoListHomeAdapter mVideoListAdapter;
    private InformationHomeAdapter mInformationAdapter;
    private ActionListHomeAdapter mActionListAdapter;

    private AttentionLibPresenter mPresenter;
    private BannerAdapter mBannerAdapter;
    private String mLibCode;
    private LibraryBean mLibraryBean;
    private boolean mIsBookStore = false;


    @OnClick({R.id.titlebar_left_btn, R.id.set_attention_lib_tv, R.id.banner_default_img, R.id.titlebar_right_btn,
            R.id.lib_introduce_fl, R.id.lib_distance_ll})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                final CustomDialog dialog = new CustomDialog(getActivity(), R.style.DialogTheme, getString(R.string.cancel_attention_library));
                dialog.setCancelable(false);
                dialog.hasNoCancel(true);
                dialog.setOkText("是");
                dialog.setCancelText("否");
                dialog.show();
                dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
                    @Override
                    public void onClickOk() {
                        dialog.dismiss();
                        mPresenter.cancelAttentionLib();
                    }

                    @Override
                    public void onClickCancel() {
                        dialog.dismiss();
                    }
                });

                break;
            case R.id.set_attention_lib_tv:
                if (mPresenter.isLogin()) {
                    LibraryActivity.startActivity(getActivity(), 1);
                } else {
                    LoginActivity.startActivity(getActivity());
                }
                break;
            case R.id.banner_default_img:
                mPresenter.getAttentionLibBanner();
                break;
            case R.id.titlebar_right_btn:
                if (mIsBookStore) {
                    SearchActivity.startActivityFromBookStore(getActivity(), mLibCode);
                } else {
                    SearchActivity.startActivityFromLib(getActivity(), mLibCode, 0);
                }
                break;
            case R.id.lib_introduce_fl:
                toIntroduce();
                break;
            case R.id.lib_distance_ll:
                if (mLibraryBean != null) {
                    MapNavigationActivity.startActivity(getSupportActivity(),
                            mLibraryBean.mLibrary.mName,
                            mLibraryBean.mLibrary.mAddress,
                            mLibraryBean.mLibrary.mLngLat,
                            mLibraryBean.mDistance,
                            String.valueOf(mLibraryBean.mLibrary.mBookCount));
                }
                break;
        }
    }

    /**
     * 进入介绍界面
     */
    private void toIntroduce() {
        if (mIsBookStore) {
            //进入书店介绍
            BookStoreIntroduceActivity.startActivity(getSupportActivity(),
                    mLibraryBean.mLibrary,
                    mLibraryBean.mDistance,
                    mLibraryBean.mIsValidLngLat,
                    0);
        } else {
            //进入图书馆介绍
            LibraryIntroduceActivity.startActivity(getSupportActivity(),
                    mLibraryBean.mLibrary,
                    mLibraryBean.mDistance,
                    mLibraryBean.mIsValidLngLat,
                    0);
        }
    }

    private OnRvItemClickListener<ModelMenu> mModelItemClickListener = new OnRvItemClickListener<ModelMenu>() {
        @Override
        public void onItemClick(int position, ModelMenu data) {
            if (data.mIsBaseModel) {
                switch (data.mId) {
                    case 0://本馆介绍
                        toIntroduce();
                        break;
                    case 1://总分馆
                        LibraryMainBranchActivity.startActivity(getSupportActivity(), mLibCode);
                        break;
                    case 2://图书
                        BookTabActivity.startActivityFromLib(getSupportActivity(), mLibCode, mIsBookStore ? "本店图书" : "本馆图书");
                        break;
                    case 3://电子书
                        EBookTabActivity.startActivityFromLib(getSupportActivity(), mLibCode, mIsBookStore ? "本店电子书" : "本馆电子书");
                        break;
                    case 4://视频
                        VideoTabActivity.startActivity(getSupportActivity(), mLibCode, mIsBookStore ? "本店视频" : "本馆视频");
                        break;
                    case 5://活动
                        ActivityListActivity.startActivity(getSupportActivity(), mLibCode, mIsBookStore ? "本店活动" : "本馆活动");
                        break;
                    case 6://资讯
                        InformationActivity.startActivity(getSupportActivity(), mLibCode, mIsBookStore ? "本店资讯" : "本馆资讯");
                        break;
                    case 7://读者留言
                        LibraryMessageBoardActivity.startActivity(getSupportActivity(), mLibCode, mIsBookStore ? "顾客留言" : "读者留言");
                        break;
                    case 8://借阅须知
                        BorrowingIntroduceActivity.startActivity(getSupportActivity(), mLibCode);
                        break;
                }
            } else {
                UserDepositAgreementActivity.startActivity(getSupportActivity(), data.mName, data.mUrl);
            }
        }
    };

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_attention_lib;
    }

    @Override
    public void initDatas() {
        mPresenter = new AttentionLibPresenter();
        mPresenter.attachView(this);
        mPresenter.getAttentionLib();
        mPresenter.registerRxBus(AttentionLibEvent.class, new Action1<AttentionLibEvent>() {
            @Override
            public void call(AttentionLibEvent attentionLibEvent) {
                if (attentionLibEvent.mIsAttention) {
                    mPresenter.getAttentionLib();
                }
            }
        });
    }

    @Override
    public void configViews() {

    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void showNoAttentionLib() {
        mCommonTitleBar.setTitle("关注");
        mCommonTitleBar.clearRightBtnIcon();
        mNoAttentionLibLl.setVisibility(View.VISIBLE);
        mMultiStateLayout.showContentView();
        mAttentionLibSv.setVisibility(View.GONE);
        mCommonTitleBar.setLeftTxtBtnVisibility(View.GONE);
    }

    @Override
    public void setTitle(String title) {
        mCommonTitleBar.setTitle(title);
        mCommonTitleBar.setRightBtnIcon(R.drawable.bg_btn_search);
        mCommonTitleBar.setLeftBtnIcon(R.mipmap.ic_library_top_cancel_attention);
        mCommonTitleBar.setLeftTxtBtnVisibility(View.VISIBLE);
    }

    @Override
    public void showBannerInfoList(List<BannerInfo> bannerInfoList) {
        mLibBannerView.showBannerView();

        if (null == mBannerAdapter) {
            mBannerAdapter = new BannerAdapter(getSupportActivity());
        }
        mBannerAdapter.addAllData(bannerInfoList);
        mLibBannerView.setAdapter(mBannerAdapter);
        mBannerAdapter.setOnItemClickListener(new BannerAdapter.OnItemClickListener() {

            @Override
            public void onClick(BannerInfo item) {
                if (null != item && !TextUtils.isEmpty(item.mNewsId)) {
                    InformationDetailDiscussActivity.startActivity(getSupportActivity(), Long.valueOf(item.mNewsId));
                }
            }
        });

        mLibBannerView.setBannerTitle(mBannerAdapter.getItemData(0).mTitle);
    }

    @Override
    public void showBannerInfoError() {
        mLibBannerView.hideBannerView();
    }

    @Override
    public void showLibModelList(List<ModelMenu> modelList, final String libCode, final String libName) {
        mLibCode = libCode;
        mModelMenuLayout.initPagerAdapter(modelList, mModelItemClickListener);
    }

    @Override
    public void showLibModelError() {
        mMultiStateLayout.showError();
        mMultiStateLayout.showRetryError(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getAttentionLib();
            }
        });
    }

    @Override
    public void showLibProgress() {
        mMultiStateLayout.showProgress();
        mNoAttentionLibLl.setVisibility(View.GONE);
        mAttentionLibSv.setVisibility(View.GONE);
    }

    @Override
    public void setContentView() {
        mNoAttentionLibLl.setVisibility(View.GONE);
        mAttentionLibSv.setVisibility(View.VISIBLE);
        mMultiStateLayout.showContentView();
    }

    @Override
    public void showToastTip(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }

    /**
     * 设置本馆介绍信息
     *
     * @param libInfo 图书馆信息
     */
    @Override
    public void showLibraryInfo(LibraryBean libInfo) {
        this.mLibraryBean = libInfo;
        this.mIsBookStore = mLibraryBean.mIsBookStore;
        mLibIntroduceView.setLibDistance(libInfo.mDistance);
        mLibIntroduceView.setAddress(libInfo.mLibrary.mAddress);

        GlideApp.with(getSupportActivity())
                .load(libInfo.mLibrary.mLogo)
                .transform(new RoundedCornersTransformation(5, RoundedCornersTransformation.CornerType.ALL))
                .placeholder(R.drawable.bg_00000000)
                .error(R.mipmap.ic_default_lib_icon)
                .centerInside()
                .into(mLibIntroduceView.getLibLogoImageView());
    }

    @Override
    public void pleaseLoginTip() {
        AccountMessage accountMessage = new AccountMessage();
        accountMessage.mIsLoginOut = true;
        EventBus.getDefault().post(accountMessage);

        final CustomDialog dialog = new CustomDialog(getSupportActivity(), R.style.DialogTheme, getString(R.string.account_login_other_device));
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.setLoginAnewBtn();
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                LoginActivity.startActivity(getSupportActivity());
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    /**
     * 设置图书馆开放时间
     *
     * @param openInfo
     */
    @Override
    public void showTodayOpenTime(String openInfo) {
        mLibIntroduceView.setOpenToday(openInfo);
    }

    @Override
    public void showBusinessTime(String businessTime) {
        mLibIntroduceView.setOpenTimeToday(businessTime);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void setPaperBookList(List<BookBean> paperBookBeanList, int totalCount) {
        if (mLibPaperBookIv == null) {
            View view = mLibPaperBookVs.inflate();
            mLibPaperBookIv = (HomeListItemView) view.findViewById(R.id.lib_book_list_iv);
            mLibPaperBookIv.setRecyclerViewMargin(DpPxUtils.dp2px(13.5f), 0, DpPxUtils.dp2px(13.5f), 0);

            mBookListAdapter = new BookHomeListAdapter(getSupportActivity());
            mLibPaperBookIv.configGridLayoutManager(getSupportActivity(), 4);
            mLibPaperBookIv.setAdapter(mBookListAdapter);
            mLibPaperBookIv.setTitle("图书");
            mBookListAdapter.setOnItemClickListener(new OnRvItemClickListener<BookBean>() {
                @Override
                public void onItemClick(int position, BookBean bean) {
                    if (bean != null) {
                        BookDetailActivity.startActivity(getSupportActivity(), bean.mBook.mIsbn, mLibCode, null, 2);
                    }
                }
            });
            mLibPaperBookIv.setTitleListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookTabActivity.startActivityFromLib(getSupportActivity(), mLibCode, mIsBookStore ? "本店图书" : "本馆图书");
                }
            });
        } else {
            mLibPaperBookIv.showHomeListItem();
        }

        mBookListAdapter.clear();
        mBookListAdapter.addAll(paperBookBeanList);
    }

    @Override
    public void hidePaperBookList() {
        if (mLibPaperBookIv != null) {
            mLibPaperBookIv.hideHomeListItem();
        }
    }

    @Override
    public void setEBookList(List<EBookBean> eBookBeanList, int totalCount) {
        if (mLibEBookIv == null) {
            View view = mLibEBookVs.inflate();
            mLibEBookIv = (HomeListItemView) view.findViewById(R.id.lib_ebook_list_iv);
            mLibEBookIv.setRecyclerViewMargin(DpPxUtils.dp2px(13.5f), 0, DpPxUtils.dp2px(13.5f), 0);
            mLibEBookIv.configGridLayoutManager(getSupportActivity(), 4);

            mEBookAdapter = new EBookGridListAdapter(getSupportActivity(), false);
            mLibEBookIv.setAdapter(mEBookAdapter);
            mLibEBookIv.setTitle("电子书");
            mEBookAdapter.setOnItemClickListener(new OnRvItemClickListener<EBookBean>() {
                @Override
                public void onItemClick(int position, EBookBean data) {
                    if (data != null) {
                        EBookDetailActivity.startActivity(getSupportActivity(), String.valueOf(data.mEBook.mId), mLibCode);
                    }
                }
            });
            mLibEBookIv.setTitleListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EBookTabActivity.startActivityFromLib(getSupportActivity(), mLibCode, mIsBookStore ? "本店电子书" : "本馆电子书");
                }
            });
        } else {
            mLibEBookIv.showHomeListItem();
        }

        mEBookAdapter.clear();
        mEBookAdapter.addAll(eBookBeanList);
    }

    @Override
    public void hideEBookList() {
        if (mLibEBookIv != null) {
            mLibEBookIv.hideHomeListItem();
        }
    }

    @Override
    public void setVideoList(List<VideoSetBean> videoBeanList, int totalCount) {
        if (mLibVideoIv == null) {
            View view = mLibVideoVs.inflate();
            mLibVideoIv = (HomeListItemView) view.findViewById(R.id.lib_video_list_iv);
            mVideoListAdapter = new VideoListHomeAdapter(getSupportActivity());
            mLibVideoIv.configLinearLayoutManager(getSupportActivity());
            mLibVideoIv.setAdapter(mVideoListAdapter);
            mLibVideoIv.setTitle("视频");
            mVideoListAdapter.setOnItemClickListener(new OnRvItemClickListener<VideoSetBean>() {
                @Override
                public void onItemClick(int position, VideoSetBean data) {
                    if (data != null) {
                        VideoDetailActivity.startActivity(getSupportActivity(), data.getId(), data.getTitle(), mLibCode);
                    }
                }
            });
            mLibVideoIv.setTitleListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoTabActivity.startActivity(getSupportActivity(), mLibCode, mIsBookStore ? "本店视频" : "本馆视频");
                }
            });
        } else {
            mLibVideoIv.showHomeListItem();
        }

        mVideoListAdapter.clear();
        mVideoListAdapter.addAll(videoBeanList);
    }

    @Override
    public void hideVideoList() {
        if (mLibVideoIv != null) {
            mLibVideoIv.hideHomeListItem();
        }
    }

    @Override
    public void setActivityList(List<ActionInfoBean> activityBeanList, int totalCount) {
        if (mLibActivityIv == null) {
            View view = mLibActivityVs.inflate();
            mLibActivityIv = (HomeListItemView) view.findViewById(R.id.lib_activity_list_iv);
            mLibActivityIv.configLinearLayoutManager(getSupportActivity());
            mActionListAdapter = new ActionListHomeAdapter(getSupportActivity());
            mLibActivityIv.setAdapter(mActionListAdapter);
            mLibActivityIv.setTitle("活动");
            mActionListAdapter.setOnItemClickListener(new OnRvItemClickListener<ActionInfoBean>() {
                @Override
                public void onItemClick(int position, ActionInfoBean data) {
                    if (data != null) {
                        ActionDetailsActivity.startActivityById(getSupportActivity(), data.mId);
                    }
                }
            });
            mLibActivityIv.setTitleListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityListActivity.startActivity(getSupportActivity(), mLibCode, mIsBookStore ? "本店活动" : "本馆活动");
                }
            });
        } else {
            mLibActivityIv.showHomeListItem();
        }

        mActionListAdapter.clear();
        mActionListAdapter.addAll(activityBeanList);
    }

    @Override
    public void hideActivityList() {
        if (mLibActivityIv != null) {
            mLibActivityIv.hideHomeListItem();
        }
    }

    @Override
    public void setInformationList(List<InformationBean> informationBeanList, int totalCount) {
        if (mLibNewsIv == null) {
            View view = mLibNewsVs.inflate();
            mLibNewsIv = (HomeListItemView) view.findViewById(R.id.lib_news_list_iv);
            mLibNewsIv.configLinearLayoutManager(getSupportActivity());

            mInformationAdapter = new InformationHomeAdapter(getSupportActivity());
            mLibNewsIv.setAdapter(mInformationAdapter);
            mLibNewsIv.setTitle("资讯");
            mInformationAdapter.setOnItemClickListener(new OnRvItemClickListener<InformationBean>() {
                @Override
                public void onItemClick(int position, InformationBean data) {
                    if (data != null) {
                        InformationDetailDiscussActivity.startActivity(getActivity(), data.mId);
                    }
                }
            });
            mLibNewsIv.setTitleListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InformationActivity.startActivity(getSupportActivity(), mLibCode, mIsBookStore ? "本店资讯" : "本馆资讯");
                }
            });
        } else {
            mLibNewsIv.showHomeListItem();
        }

        mInformationAdapter.clear();
        mInformationAdapter.addAll(informationBeanList);
    }

    @Override
    public void hideInformationList() {
        if (mLibNewsIv != null) {
            mLibNewsIv.hideHomeListItem();
        }
    }

    @Override
    public void setAttentionIntroduceTitle(String title) {
        mLibIntroduceView.setTitle(title);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLibBannerView != null) {
            mLibBannerView.restoreLoopPicHandler();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            if (mLibBannerView != null) {
                mLibBannerView.restoreLoopPicHandler();
            }
        } else {
            if (mLibBannerView != null) {
                mLibBannerView.pauseLoopPicHandler();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLibBannerView != null) {
            mLibBannerView.pauseLoopPicHandler();
        }
    }

    @Override
    public void onDestroyView() {
        mLibBannerView.releaseBanner();
        super.onDestroyView();
        if (mBannerAdapter != null) {
            mBannerAdapter.clearBannerData();
        }
        if (mPresenter != null) {
            mPresenter.unregisterRxBus();
            mPresenter.detachView();
        }
    }

}
