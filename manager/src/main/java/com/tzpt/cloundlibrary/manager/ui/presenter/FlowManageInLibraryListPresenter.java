package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.SameRangeLibraryBean;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.SameRangeLibraryListVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.SameRangeLibraryVo;
import com.tzpt.cloundlibrary.manager.ui.contract.FlowManageInLibraryListContract;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 流出管理新增流向馆列表
 * Created by ZhiqiangJia on 2017-07-13.
 */
public class FlowManageInLibraryListPresenter extends RxPresenter<FlowManageInLibraryListContract.View>
        implements FlowManageInLibraryListContract.Presenter,
        BaseResponseCode {

    /**
     * 模糊搜索流通范围的图书馆列表-流入馆列表
     *
     * @param pageNumber 页码
     * @param grepValue  内容
     */
    @Override
    public void searchFlowManageInLibraryList(final int pageNumber, final String grepValue) {
        String hallCode = DataRepository.getInstance().getLibraryInfo().mHallCode;
        if (TextUtils.isEmpty(hallCode)) {
            return;
        }
        if (null != mView) {
            if (pageNumber == 1) {
                mView.showProgressLoading();
                if (TextUtils.isEmpty(grepValue)) {
                    List<SameRangeLibraryBean> libraryList = DataRepository.getInstance().getSameRangeLibraryList();
                    if (null != libraryList && libraryList.size() > 0) {
                        mView.setFlowManageInLibraryList(libraryList, DataRepository.getInstance().getInLibraryTotalCount(), true);
                        mView.dismissProgressLoading();
                        mView.complete();
                        return;
                    }
                }
            }
            Subscription subscription = DataRepository.getInstance().searchSameRangeLibraryListByCondition(pageNumber, 10, hallCode, grepValue)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SameRangeLibraryListVo>() {
                        @Override
                        public void onCompleted() {
                            if (null != mView) {
                                mView.complete();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (null != mView) {
                                mView.dismissProgressLoading();
                                mView.showErrorView(pageNumber == 1);
                            }
                        }

                        @Override
                        public void onNext(SameRangeLibraryListVo sameRangeLibraryListVo) {
                            if (null != mView) {
                                mView.dismissProgressLoading();
                                if (sameRangeLibraryListVo.status == CODE_SUCCESS) {
                                    if (null != sameRangeLibraryListVo.data) {
                                        List<SameRangeLibraryVo> sameRangeLibraryVoList = sameRangeLibraryListVo.data.resultList;
                                        List<SameRangeLibraryBean> sameRangeLibraryBeanList = new ArrayList<>();
                                        for (SameRangeLibraryVo sameRangeLibraryVo : sameRangeLibraryVoList) {
                                            SameRangeLibraryBean bean = new SameRangeLibraryBean();
                                            bean.hallCode = sameRangeLibraryVo.inHallCode;
                                            bean.name = sameRangeLibraryVo.inLibraryName;
                                            bean.conperson = sameRangeLibraryVo.inLibraryConperson;
                                            bean.phone = sameRangeLibraryVo.inLibraryPhone;
                                            sameRangeLibraryBeanList.add(bean);
                                        }
                                        if (sameRangeLibraryBeanList.size() > 0) {
                                            mView.setFlowManageInLibraryList(sameRangeLibraryBeanList, sameRangeLibraryListVo.data.totalCount, pageNumber == 1);
                                            if (pageNumber == 1 && TextUtils.isEmpty(grepValue)) {
                                                DataRepository.getInstance().addSameRangeLibraryList(sameRangeLibraryBeanList, sameRangeLibraryListVo.data.totalCount);
                                            }
                                        } else {
                                            mView.setFlowManageInLibraryListEmpty(pageNumber == 1);
                                        }
                                    } else {
                                        mView.setFlowManageInLibraryListEmpty(pageNumber == 1);
                                    }
                                } else {
                                    if (null != sameRangeLibraryListVo.data) {
                                        if (sameRangeLibraryListVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                            mView.noPermissionPrompt(R.string.kicked_offline);
                                        } else if (sameRangeLibraryListVo.data.errorCode == ERROR_CODE_1006) {
                                            mView.noPermissionPrompt(R.string.operate_timeout);
                                        } else {
                                            mView.showErrorView(pageNumber == 1);
                                        }
                                    } else {
                                        mView.showErrorView(pageNumber == 1);
                                    }
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    @Override
    public void clearTempInLibraryList() {
        DataRepository.getInstance().clearSameRangeLibraryList();
    }
}
