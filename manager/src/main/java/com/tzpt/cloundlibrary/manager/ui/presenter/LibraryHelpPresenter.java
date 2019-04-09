package com.tzpt.cloundlibrary.manager.ui.presenter;

import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.HelpInfoBean;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.HelpInfoVo;
import com.tzpt.cloundlibrary.manager.ui.contract.LibraryHelpContract;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 帮助
 * Created by ZhiqiangJia on 2018-01-08.
 */
public class LibraryHelpPresenter extends RxPresenter<LibraryHelpContract.View> implements
        LibraryHelpContract.Presenter,
        BaseResponseCode {

    @Override
    public void getLibraryHelpList() {
        Subscription subscription = DataRepository.getInstance().getHelpList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HelpInfoVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setNetError();
                        }
                    }

                    @Override
                    public void onNext(HelpInfoVo helpInfoVo) {
                        if (null != mView) {
                            if (helpInfoVo.status == CODE_SUCCESS) {
                                if (null != helpInfoVo.data && null != helpInfoVo.data.menus) {
                                    List<HelpInfoBean> helpInfoBeanList = findHelpItem(helpInfoVo.data);
                                    if (null != helpInfoBeanList && helpInfoBeanList.size() > 0) {
                                        mView.setLibraryHelpList(helpInfoBeanList, helpInfoVo.data.title);
                                    } else {
                                        mView.setLibraryHelpListEmpty();
                                    }
                                }
                            } else {
                                mView.setNetError();
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    //递归查询所有列表
    private List<HelpInfoBean> findHelpItem(HelpInfoVo.ResponseData data) {
        if (null != data && null != data.menus && data.menus.size() > 0) {
            List<HelpInfoBean> helpInfoBeans = new ArrayList<>();
            for (HelpInfoVo.ResponseData.MenuBeanVo item : data.menus) {
                HelpInfoBean infoBean = new HelpInfoBean();
                infoBean.mTitle = data.title;
                infoBean.mItemName = item.name;
                infoBean.mUrl = item.url;
                infoBean.mHasChildren = item.hasChildren;
                if (item.hasChildren && item.childs.menus.size() > 0) {
                    infoBean.mChildren = findHelpItem(item.childs);
                }
                helpInfoBeans.add(infoBean);
            }
            return helpInfoBeans;
        }
        return null;
    }
}
