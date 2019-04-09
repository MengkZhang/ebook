package com.tzpt.cloudlibrary.ui.bookstore;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;

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
import com.tzpt.cloudlibrary.ui.account.AccountMessage;
import com.tzpt.cloudlibrary.ui.account.LoginActivity;
import com.tzpt.cloudlibrary.ui.account.deposit.UserDepositAgreementActivity;
import com.tzpt.cloudlibrary.ui.ebook.EBookDetailActivity;
import com.tzpt.cloudlibrary.ui.ebook.EBookGridListAdapter;
import com.tzpt.cloudlibrary.ui.ebook.EBookTabActivity;
import com.tzpt.cloudlibrary.ui.information.InformationActivity;
import com.tzpt.cloudlibrary.ui.information.InformationDetailDiscussActivity;
import com.tzpt.cloudlibrary.ui.information.InformationHomeAdapter;
import com.tzpt.cloudlibrary.ui.library.LibraryIntroduceView;
import com.tzpt.cloudlibrary.ui.library.LibraryMessageBoardActivity;
import com.tzpt.cloudlibrary.ui.main.BannerAdapter;
import com.tzpt.cloudlibrary.ui.map.MapNavigationActivity;
import com.tzpt.cloudlibrary.ui.paperbook.BookDetailActivity;
import com.tzpt.cloudlibrary.ui.paperbook.BookHomeListAdapter;
import com.tzpt.cloudlibrary.ui.paperbook.BookTabActivity;
import com.tzpt.cloudlibrary.ui.readers.ActionDetailsActivity;
import com.tzpt.cloudlibrary.ui.readers.ActionListHomeAdapter;
import com.tzpt.cloudlibrary.ui.readers.ActivityListActivity;
import com.tzpt.cloudlibrary.ui.video.VideoDetailActivity;
import com.tzpt.cloudlibrary.ui.video.VideoListHomeAdapter;
import com.tzpt.cloudlibrary.ui.video.VideoTabActivity;
import com.tzpt.cloudlibrary.utils.DpPxUtils;
import com.tzpt.cloudlibrary.utils.ToastUtils;
import com.tzpt.cloudlibrary.utils.glide.GlideApp;
import com.tzpt.cloudlibrary.widget.CustomDialog;
import com.tzpt.cloudlibrary.widget.DrawableCenterTextView;
import com.tzpt.cloudlibrary.widget.HomeListItemView;
import com.tzpt.cloudlibrary.widget.bannerview.BannerView;
import com.tzpt.cloudlibrary.widget.menupager.ModelMenuLayout;
import com.tzpt.cloudlibrary.widget.multistatelayout.MultiStateLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 书店详情
 * Created by tonyjia on 2018/8/27.
 */
public class BookStoreDetailFragment extends BaseFragment implements
        BookStoreDetailContract.View {

    @BindView(R.id.multi_state_layout)
    MultiStateLayout mMultiStateLayout;
    @BindView(R.id.lib_banner_view)
    BannerView mBannerView;
    @BindView(R.id.lib_model_menu_layout)
    ModelMenuLayout mModelMenuLayout;
    @BindView(R.id.lib_attention_tv)
    DrawableCenterTextView mLibAttentionTv;

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

    private BookStoreDetailContract.Presenter mPresenter;
    private BannerAdapter mBannerAdapter;
    private boolean mIsFirstLoad = true;
    private boolean mIsPrepared;
    private LibraryBean mBookStoreLib;

    @OnClick({R.id.lib_attention_tv, R.id.lib_distance_ll, R.id.lib_introduce_fl,
            R.id.banner_default_img})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.lib_attention_tv:
                mPresenter.dealAttentionMenuItem();
                break;
            case R.id.lib_distance_ll:
                if (mBookStoreLib != null) {
                    MapNavigationActivity.startActivity(getSupportActivity(),
                            mBookStoreLib.mLibrary.mName,
                            mBookStoreLib.mLibrary.mAddress,
                            mBookStoreLib.mLibrary.mLngLat,
                            mBookStoreLib.mDistance,
                            String.valueOf(mBookStoreLib.mLibrary.mBookCount));
                }
                break;
            case R.id.banner_default_img:
                mPresenter.getStoreNewsList();
                break;
            case R.id.lib_introduce_fl:
                if (mBookStoreLib != null) {
                    BookStoreIntroduceActivity.startActivity(getSupportActivity(), mBookStoreLib.mLibrary, mBookStoreLib.mDistance, mBookStoreLib.mIsValidLngLat, mPresenter.getFromSearch());
                }
                break;
        }
    }

    private OnRvItemClickListener<ModelMenu> mModelItemClickListener = new OnRvItemClickListener<ModelMenu>() {
        @Override
        public void onItemClick(int position, ModelMenu data) {
            if (data.mIsBaseModel) {
                switch (data.mId) {
                    case 0://本馆介绍
                        BookStoreIntroduceActivity.startActivity(getSupportActivity(), mBookStoreLib.mLibrary, mBookStoreLib.mDistance, mBookStoreLib.mIsValidLngLat, mPresenter.getFromSearch());
                        break;
                    case 2://图书
                        BookTabActivity.startActivityFromLib(getSupportActivity(), mPresenter.getStoreCode(), "本店图书");
                        break;
                    case 3://电子书
                        EBookTabActivity.startActivityFromLib(getSupportActivity(), mPresenter.getStoreCode(), "本店电子书");
                        break;
                    case 4://视频
                        VideoTabActivity.startActivity(getSupportActivity(), mPresenter.getStoreCode(), "本店视频");
                        break;
                    case 5://活动
                        ActivityListActivity.startActivity(getSupportActivity(), mPresenter.getStoreCode(), "本店活动");
                        break;
                    case 6://资讯
                        InformationActivity.startActivity(getSupportActivity(), mPresenter.getStoreCode(), "本店资讯");
                        break;
                    case 7://读者留言
                        LibraryMessageBoardActivity.startActivity(getSupportActivity(), mPresenter.getStoreCode(), "顾客留言");
                        break;
                }
            } else {
                UserDepositAgreementActivity.startActivity(getSupportActivity(), data.mName, data.mUrl);
            }
        }
    };

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_book_store_detail;
    }

    @Override
    public void initDatas() {
        mIsPrepared = true;
        lazyLoad();
    }

    @Override
    public void configViews() {

    }

    @Override
    protected void lazyLoad() {
        if (!mIsVisible || !mIsPrepared) {
            return;
        }
        if (null != mPresenter && mIsFirstLoad) {
            this.mIsFirstLoad = false;
            mPresenter.getStoreMenuList();
        }
    }

    @Override
    public void setPresenter(BookStoreDetailContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showBannerInfoList(List<BannerInfo> bannerInfoList) {
        mBannerView.showBannerView();
        if (null == mBannerAdapter) {
            mBannerAdapter = new BannerAdapter(getSupportActivity());
        }
        mBannerAdapter.addAllData(bannerInfoList);
        mBannerView.setAdapter(mBannerAdapter);
        mBannerAdapter.setOnItemClickListener(new BannerAdapter.OnItemClickListener() {

            @Override
            public void onClick(BannerInfo item) {
                if (null != item && !TextUtils.isEmpty(item.mNewsId)) {
                    InformationDetailDiscussActivity.startActivity(getSupportActivity(), Long.valueOf(item.mNewsId));
                }
            }
        });

        mBannerView.setBannerTitle(mBannerAdapter.getItemData(0).mTitle);
    }

    @Override
    public void showBannerInfoError() {
        mBannerView.hideBannerView();
    }

    /**
     * 设置图书馆model数据
     *
     * @param modelList 动态model列表
     */
    @Override
    public void showLibModelList(List<ModelMenu> modelList) {
        mModelMenuLayout.initPagerAdapter(modelList, mModelItemClickListener);
    }

    @Override
    public void showLibModelError() {
        mMultiStateLayout.showError();
        mMultiStateLayout.showRetryError(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getStoreMenuList();
            }
        });
    }

    @Override
    public void showLibProgress() {
        mMultiStateLayout.showProgress();
    }

    @Override
    public void setContentView() {
        mMultiStateLayout.showContentView();
    }

    @Override
    public void needLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent, 1000);
    }

    @Override
    public void showChangeAttentionTip(String attentionLib) {
        final CustomDialog dialog = new CustomDialog(getActivity(), R.style.DialogTheme, getString(R.string.change_attention_lib_tip, attentionLib));
        dialog.setCancelable(false);
        dialog.hasNoCancel(true);
        dialog.setOkText("是");
        dialog.setCancelText("否");
        dialog.show();
        dialog.setOnClickBtnListener(new CustomDialog.OnClickBtnListener() {
            @Override
            public void onClickOk() {
                dialog.dismiss();
                mPresenter.attentionLib();
            }

            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void showToastTip(int msgId) {
        ToastUtils.showSingleToast(msgId);
    }

    @Override
    public void changeAttentionStatus(boolean isAttention) {
        Drawable flup;
        if (isAttention) {
            mLibAttentionTv.setText("取消关注");
            flup = mContext.getResources().getDrawable(R.mipmap.ic_library_cancel_attention);
        } else {
            flup = mContext.getResources().getDrawable(R.mipmap.ic_library_attention);
            mLibAttentionTv.setText("设为关注");
        }
        flup.setBounds(0, 0, flup.getMinimumWidth(), flup.getMinimumHeight());
        mLibAttentionTv.setCompoundDrawables(flup, null, null, null);

    }

    @Override
    public void setBookStoreInfo(LibraryBean libInfo) {
        this.mBookStoreLib = libInfo;
        mLibIntroduceView.setTitle("本店介绍");
        mLibIntroduceView.setLibDistance(mBookStoreLib.mDistance);
        mLibIntroduceView.setAddress(mBookStoreLib.mLibrary.mAddress);

        GlideApp.with(mContext)
                .load(libInfo.mLibrary.mLogo)
                .transform(new RoundedCornersTransformation(5, RoundedCornersTransformation.CornerType.ALL))
                .placeholder(R.drawable.bg_00000000)
                .error(R.mipmap.ic_default_lib_icon)
                .centerInside()
                .into(mLibIntroduceView.getLibLogoImageView());

    }

    /**
     * 设置营业时间
     */
    @Override
    public void showTodayOpenTime(String openInfo) {
        mLibIntroduceView.setOpenTimeToday(openInfo);
    }

    @Override
    public void showCancelAttentionTip() {
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

    }

    @Override
    public void setPaperBookList(List<BookBean> paperBookBeanList, int totalCount) {
        if (mLibPaperBookIv == null) {
            View view = mLibPaperBookVs.inflate();
            mLibPaperBookIv = (HomeListItemView) view.findViewById(R.id.lib_book_list_iv);
            mBookListAdapter = new BookHomeListAdapter(getSupportActivity());
            mLibPaperBookIv.setRecyclerViewMargin(DpPxUtils.dp2px(13.5f), 0, DpPxUtils.dp2px(13.5f), 0);

            mLibPaperBookIv.configGridLayoutManager(getSupportActivity(), 4);
            mLibPaperBookIv.setAdapter(mBookListAdapter);
            mLibPaperBookIv.setTitle("图书");

            mBookListAdapter.setOnItemClickListener(new OnRvItemClickListener<BookBean>() {
                @Override
                public void onItemClick(int position, BookBean bean) {
                    if (bean != null) {
                        BookDetailActivity.startActivity(getSupportActivity(), bean.mBook.mIsbn, mPresenter.getStoreCode(), null, 2);
                    }
                }
            });
            mLibPaperBookIv.setTitleListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookTabActivity.startActivityFromLib(getSupportActivity(), mPresenter.getStoreCode(), "本店图书");
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
                        EBookDetailActivity.startActivity(getSupportActivity(), String.valueOf(data.mEBook.mId), mPresenter.getStoreCode());
                    }
                }
            });
            mLibEBookIv.setTitleListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EBookTabActivity.startActivityFromLib(getSupportActivity(), mPresenter.getStoreCode(), "本店电子书");
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
                        VideoDetailActivity.startActivity(getSupportActivity(), data.getId(), data.getTitle(), mPresenter.getStoreCode());
                    }
                }
            });
            mLibVideoIv.setTitleListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoTabActivity.startActivity(getSupportActivity(), mPresenter.getStoreCode(), "本店视频");
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
                    ActivityListActivity.startActivity(getSupportActivity(), mPresenter.getStoreCode(), "本店活动");
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
                    InformationActivity.startActivity(getSupportActivity(), mPresenter.getStoreCode(), "本店资讯");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            mPresenter.checkAttentionStatus();
        }
    }

    @Override
    public void onDestroyView() {
        mBannerView.releaseBanner();
        super.onDestroyView();
        if (null != mBannerAdapter) {
            mBannerAdapter.clearBannerData();
        }

        mPresenter.clearCacheData();
        mPresenter.detachView();
        mPresenter = null;
    }
}
