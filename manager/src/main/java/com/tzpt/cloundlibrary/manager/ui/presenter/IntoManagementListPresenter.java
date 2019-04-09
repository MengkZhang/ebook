package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.IntoManagementListBean;
import com.tzpt.cloundlibrary.manager.bean.StatisticsConditionBean;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.IntoManageListVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.IntoManagementListVo;
import com.tzpt.cloundlibrary.manager.ui.contract.IntoManagementListContract;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 流入管理列表操作
 * Created by ZhiqiangJia on 2017-07-14.
 */
public class IntoManagementListPresenter extends RxPresenter<IntoManagementListContract.View> implements
        IntoManagementListContract.Presenter,
        BaseResponseCode {

    @Override
    public void getIntoManageList(final int pageNumber) {
        StatisticsConditionBean condition = DataRepository.getInstance().getStatisticsCondition();
        if (condition == null) {
            mView.setIntoManagementListEmpty(pageNumber == 1);
            return;
        }
        String hallCode = DataRepository.getInstance().getLibraryInfo().mHallCode;
        if (TextUtils.isEmpty(hallCode)) {
            mView.setIntoManagementListEmpty(pageNumber == 1);
            return;
        }
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("inHallCode", hallCode);
        map.put("pageNo", String.valueOf(pageNumber));
        map.put("pageCount", String.valueOf(20));

        Subscription subscription = DataRepository.getInstance().getIntoManageSingleList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<IntoManagementListVo>() {
                    @Override
                    public void onCompleted() {
                        if (null != mView) {
                            mView.complete();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.showError(pageNumber == 1);
                        }
                    }

                    @Override
                    public void onNext(IntoManagementListVo intoManagementListVo) {
                        if (null != mView) {
                            if (intoManagementListVo.status == CODE_SUCCESS) {
                                if (null != intoManagementListVo.data) {
                                    List<IntoManagementListBean> intoManagementListBeanList = new ArrayList<>();
                                    List<IntoManageListVo> intoManageList = intoManagementListVo.data.resultList;
                                    for (IntoManageListVo intoManage : intoManageList) {
                                        IntoManagementListBean bean = new IntoManagementListBean();
                                        bean.conperson = intoManage.conperson;
                                        bean.id = intoManage.id;
                                        bean.name = intoManage.outLibraryName;
                                        bean.outCode = intoManage.outCode;
                                        bean.outDate = intoManage.outDate;
                                        bean.outHallCode = intoManage.outHallCode;
                                        bean.phone = intoManage.inLibraryPhone;
                                        bean.signPerson = intoManage.signPerson;
                                        bean.totalPrice = intoManage.sumPrice;
                                        bean.totalSum = intoManage.bookNum;

                                        if (null != intoManage.circulateStatus) {
                                            if (intoManage.isReturn == 1) {
                                                bean.inState = -1;
                                            } else {
                                                bean.inState = intoManage.circulateStatus.index;
                                            }
                                        }
                                        //如果当前状态为未审核或者已审核，则不显示操作员名称
                                        bean.userName = (bean.inState == -1 || bean.inState == 6) ? intoManage.signUserName : "";
                                        intoManagementListBeanList.add(bean);
                                    }
                                    if (intoManagementListBeanList.size() > 0) {
                                        mView.setIntoManageTotalInfo(intoManagementListVo.data.totalCount, intoManagementListVo.data.totalSumPrice);
                                        mView.setIntoManagementList(intoManagementListBeanList, intoManagementListVo.data.totalCount, pageNumber == 1);
                                    } else {
                                        mView.setIntoManagementListEmpty(pageNumber == 1);
                                    }
                                } else {
                                    mView.setIntoManagementListEmpty(pageNumber == 1);
                                }
                            } else {

                                if (intoManagementListVo.data != null) {
                                    if (intoManagementListVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                        mView.setNoLoginPermission(R.string.kicked_offline);
                                    } else if (intoManagementListVo.data.errorCode == ERROR_CODE_1006) {
                                        mView.setNoLoginPermission(R.string.operate_timeout);
                                    } else {
                                        mView.showError(pageNumber == 1);
                                    }
                                } else {
                                    mView.showError(pageNumber == 1);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);

    }

    @Override
    public void delStatisticsCondition() {
        DataRepository.getInstance().delStatisticsCondition();
    }
}
