package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.FlowManageListBean;
import com.tzpt.cloundlibrary.manager.bean.StatisticsConditionBean;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManageListVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.FlowManagementListVo;
import com.tzpt.cloundlibrary.manager.ui.contract.FlowManagementcontract;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 流出管理
 * Created by ZhiqiangJia on 2017-07-10.
 */
public class FlowManagementListPresenter extends RxPresenter<FlowManagementcontract.View> implements
        FlowManagementcontract.Presenter,
        BaseResponseCode {

    /**
     * 请求流出管理列表
     */
    @Override
    public void getFlowManagementList(final int pageNumber) {
        StatisticsConditionBean condition = DataRepository.getInstance().getStatisticsCondition();
        if (condition == null) {
            mView.setFlowManageListBeanListEmpty(pageNumber == 1);
            return;
        }
        String hallCode = DataRepository.getInstance().getLibraryInfo().mHallCode;
        if (TextUtils.isEmpty(hallCode)) {
            mView.setFlowManageListBeanListEmpty(pageNumber == 1);
            return;
        }
        ArrayMap<String, String> map = new ArrayMap<>();
        map.put("outHallCode", hallCode);
        map.put("pageNo", String.valueOf(pageNumber));
        map.put("pageCount", String.valueOf(20));

        if (condition.getConditionType() == StatisticsConditionBean.ConditionType.SingleSelection) {
            map.put(condition.getSingleConditionKey(), condition.getSingleValue());
        } else if (condition.getConditionType() == StatisticsConditionBean.ConditionType.SingleInput) {
            map.put(condition.getSingleConditionKey(), condition.getSingleValue());
        } else if (condition.getConditionType() == StatisticsConditionBean.ConditionType.DoubleInput) {
            map.put(condition.getStartConditionKey(), condition.getStartValue());
            map.put(condition.getEndConditionKey(), condition.getEndValue());
        } else if (condition.getConditionType() == StatisticsConditionBean.ConditionType.DateSelection) {
            map.put(condition.getStartConditionKey(), condition.getStartValue());
            map.put(condition.getEndConditionKey(), condition.getEndValue());
        }

        Subscription subscription = DataRepository.getInstance().getFlowManagementList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FlowManagementListVo>() {
                    @Override
                    public void onCompleted() {
                        if (mView != null) {
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
                    public void onNext(FlowManagementListVo flowManagementListVo) {
                        if (mView != null) {
                            if (flowManagementListVo.status == CODE_SUCCESS) {
                                if (null != flowManagementListVo.data) {
                                    List<FlowManageListBean> flowManageListBeanList = new ArrayList<>();
                                    List<FlowManageListVo> flowManageList = flowManagementListVo.data.resultList;
                                    String operatorId = DataRepository.getInstance().getLibraryInfo().mOperaterId;

                                    for (FlowManageListVo flowManageListVo : flowManageList) {
                                        FlowManageListBean bean = new FlowManageListBean();
                                        bean.id = flowManageListVo.id;
                                        bean.inHallCode = flowManageListVo.inHallCode;
                                        bean.name = flowManageListVo.inLibraryName;
                                        bean.outDate = flowManageListVo.outDate;
                                        bean.totalPrice = flowManageListVo.sumPrice;
                                        bean.totalSum = flowManageListVo.bookNum;
                                        bean.userName = flowManageListVo.outOperUserName;
                                        bean.auditDate = flowManageListVo.auditDate;
                                        bean.auditPerson = flowManageListVo.auditPerson;
                                        bean.conperson = flowManageListVo.inLibraryConperson;
                                        bean.outOperUserId = flowManageListVo.outOperUserId;
                                        bean.phone = flowManageListVo.inLibraryPhone;
                                        //设置是否只读属性
                                        if (!TextUtils.isEmpty(operatorId) && !TextUtils.isEmpty(flowManageListVo.outOperUserId)) {
                                            bean.onlyRead = (!operatorId.equals(flowManageListVo.outOperUserId));
                                        } else {
                                            bean.onlyRead = true;
                                        }
                                        //如果流出馆为null，则显示流出馆为当前馆
                                        if (!TextUtils.isEmpty(flowManageListVo.outHallCode)) {
                                            bean.outHallCode = flowManageListVo.outHallCode;
                                        } else {
                                            bean.outHallCode = DataRepository.getInstance().getLibraryInfo().mHallCode;
                                        }
                                        //设置流出状态
                                        if (null != flowManageListVo.circulateStatus) {
                                            if (flowManageListVo.isReturn == 1) {
                                                bean.outState = -1;//设置为通还
                                            } else {
                                                bean.outState = flowManageListVo.circulateStatus.index;
                                            }
                                        }
                                        flowManageListBeanList.add(bean);
                                    }
                                    if (flowManageListBeanList.size() > 0) {
                                        mView.setFlowManageTotalInfo(flowManagementListVo.data.totalCount, flowManagementListVo.data.totalSumPrice);
                                        mView.setFlowManageListBeanList(flowManageListBeanList, flowManagementListVo.data.totalCount, pageNumber == 1);
                                    } else {
                                        mView.setFlowManageListBeanListEmpty(pageNumber == 1);
                                    }
                                } else {
                                    mView.setFlowManageListBeanListEmpty(pageNumber == 1);
                                }
                            } else {
                                if (null != flowManagementListVo.data) {
                                    if (flowManagementListVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                        mView.setNoLoginPermission(R.string.kicked_offline);
                                    } else if (flowManagementListVo.data.errorCode == ERROR_CODE_1006) {
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
