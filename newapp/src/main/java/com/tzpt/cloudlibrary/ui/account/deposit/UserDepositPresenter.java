package com.tzpt.cloudlibrary.ui.account.deposit;

import android.text.TextUtils;

import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.DepositBalanceBean;
import com.tzpt.cloudlibrary.bean.UserDepositBean;
import com.tzpt.cloudlibrary.business_bean.BaseListResultData;
import com.tzpt.cloudlibrary.business_bean.BillMoreBean;
import com.tzpt.cloudlibrary.business_bean.DepositCategoryBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.exception.ApiException;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BillInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.DepositCategoryVo;
import com.tzpt.cloudlibrary.utils.DateUtils;
import com.tzpt.cloudlibrary.utils.MoneyUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 用户押金
 * Created by ZhiqiangJia on 2017-08-23.
 */
public class UserDepositPresenter extends RxPresenter<UserDepositContract.View> implements
        UserDepositContract.Presenter {


    @Override
    public void getUserDepositInfo(final int pageNum, final DepositCategoryBean depositCategory) {
        if (depositCategory == null) {
            getDepositCategory();
        } else {
            if (pageNum == 1) {
                dealPenalty(depositCategory);
            } else {
                String readerId = UserRepository.getInstance().getLoginReaderId();
                if (TextUtils.isEmpty(readerId)) {
                    return;
                }
                Subscription subscription = DataRepository.getInstance().getUserBill(readerId, depositCategory, pageNum, 10)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<BaseListResultData<UserDepositBean>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                if (mView != null) {
                                    if (e instanceof ApiException) {
                                        if (((ApiException) e).getCode() == 30100) {
                                            mView.pleaseLoginTip();
                                        } else {
                                            mView.setNetError();
                                        }
                                    } else {
                                        mView.setNetError();
                                    }
                                }
                            }

                            @Override
                            public void onNext(BaseListResultData<UserDepositBean> userDepositBeanBaseListResultData) {
                                if (mView != null) {
                                    if (userDepositBeanBaseListResultData != null
                                            && userDepositBeanBaseListResultData.mResultList.size() > 0) {
                                        mView.setUserDepositList(userDepositBeanBaseListResultData.mResultList,
                                                userDepositBeanBaseListResultData.mTotalCount,
                                                false);
                                    } else {
                                        mView.setUserDepositListEmpty(false);
                                    }
                                }
                            }
                        });
                addSubscrebe(subscription);
            }
        }
    }

    private void getBillInfo(final int pageNum, final DepositCategoryBean depositCategory) {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)) {
            return;
        }
        if (pageNum == 1) {
            Observable<BaseListResultData<UserDepositBean>> observable1 = DataRepository.getInstance().getUserBill(readerId, depositCategory, pageNum, 10);
            Observable<List<DepositBalanceBean>> observable2 = DataRepository.getInstance().getDepositInfo(readerId);

            Observable.zip(observable1, observable2, new Func2<BaseListResultData<UserDepositBean>, List<DepositBalanceBean>, BillMoreBean>() {
                @Override
                public BillMoreBean call(BaseListResultData<UserDepositBean> userDepositBeanBaseListResultData, List<DepositBalanceBean> depositBalanceBeans) {
                    return new BillMoreBean(userDepositBeanBaseListResultData, depositBalanceBeans);
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BillMoreBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                if (e instanceof ApiException) {
                                    if (((ApiException) e).getCode() == 30100) {
                                        mView.pleaseLoginTip();
                                    } else {
                                        mView.setNetError();
                                    }
                                } else {
                                    mView.setNetError();
                                }
                            }
                        }

                        @Override
                        public void onNext(BillMoreBean billMoreBean) {
                            if (mView != null) {
                                if (billMoreBean != null) {
                                    if (billMoreBean.mBillList != null
                                            && billMoreBean.mBillList.mResultList != null
                                            && billMoreBean.mBillList.mResultList.size() > 0) {
                                        mView.setUserDepositList(billMoreBean.mBillList.mResultList,
                                                billMoreBean.mBillList.mTotalCount,
                                                false);
                                    }

                                    //罚金和押金合计
                                    if (billMoreBean.mDepositList != null
                                            && billMoreBean.mDepositList.size() > 0) {
                                        List<DepositBalanceBean> list = new ArrayList<>();
                                        List<DepositBalanceBean> penaltyList = new ArrayList<>();
                                        double totalPenalty = 0;
                                        double activeDeposit = 0;
                                        double occupyDeposit = 0;
                                        for (DepositBalanceBean item : billMoreBean.mDepositList) {

                                            totalPenalty += item.mPenalty;
                                            activeDeposit += item.mUsableDeposit;
                                            occupyDeposit += item.mOccupyDeposit;

                                            if (item.mPenalty > 0) {
                                                penaltyList.add(item);
                                            }
                                            if (item.mDepositBalance > 0) {
                                                list.add(item);
                                            }
                                        }

                                        mView.setUserDepositBottomInfo(totalPenalty,
                                                activeDeposit,
                                                occupyDeposit,
                                                list,
                                                penaltyList);
                                    }
                                } else {
                                    mView.setNetError();
                                }
                            }
                        }
                    });
        }

    }

//
//    private void dealBillList(List<BillInfoVo.BillListItemVo> result, int totalCount, int pageNum) {
//        if (result != null && result.size() > 0) {
//            List<UserDepositBean> userDepositBeanList = new ArrayList<>();
//            for (BillInfoVo.BillListItemVo item : result) {
//                UserDepositBean userDepositBean = new UserDepositBean();
//
//                userDepositBean.mOperation = item.transName;
//
//                userDepositBean.mRemark = item.remark;
//
//                userDepositBean.mOperationDate = DateUtils.formatDate(item.transTime);
//
//                if (item.transMoney > 0) {
//                    userDepositBean.mMoney = "+" + MoneyUtils.formatMoney(item.transMoney);
//                } else {
//                    userDepositBean.mMoney = MoneyUtils.formatMoney(item.transMoney);
//                }
//
//                userDepositBean.mOrderNum = item.orderNumber;
//
//                userDepositBean.mOperateOrder = item.operOrder;
//
//                userDepositBean.mPayRemark = item.payRemark;
//
//                userDepositBean.mDeductionMoney = item.deductionMoney;
//
//                userDepositBean.mIsRefund = item.isRefund == 1;
//
//                userDepositBean.mComment = item.comment;
//
//                if (item.transStatus != null &&
//                        (item.transStatus.index == 2 || item.transStatus.index == 3)) {
//                    userDepositBean.mStatus = item.payStatus;
//                }
//
//                if (item.transType != null) {
//                    userDepositBean.mTransactionType = item.transType.index;
//                }
//
//                userDepositBeanList.add(userDepositBean);
//            }
//            mView.setUserDepositList(userDepositBeanList, totalCount, pageNum == 1);
//        } else {
//            mView.setUserDepositListEmpty(pageNum == 1);
//        }
//    }


    private void dealPenalty(final DepositCategoryBean depositCategory) {
        String readerId = UserRepository.getInstance().getLoginReaderId();
        if (TextUtils.isEmpty(readerId)) {
            mView.showDialogTip(R.string.network_fault, true);
            return;
        }
        Subscription subscription = DataRepository.getInstance().dealUserPenaltyAuto(readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Double>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof ApiException) {
                            getBillInfo(1, depositCategory);
                        }
                    }

                    @Override
                    public void onNext(Double aDouble) {
                        getBillInfo(1, depositCategory);
                    }
                });
        addSubscrebe(subscription);
    }

    private void getDepositCategory() {
        Subscription subscription = DataRepository.getInstance().getDepositCategory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<DepositCategoryVo>>() {
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
                    public void onNext(BaseResultEntityVo<DepositCategoryVo> depositCategoryVoBaseResultEntityVo) {
                        if (mView != null) {
                            if (depositCategoryVoBaseResultEntityVo.status == 200) {
                                List<DepositCategoryBean> depositCategoryList = new ArrayList<>();
                                DepositCategoryBean item0 = new DepositCategoryBean();
                                item0.mDesc = "全部";
                                item0.mName = "";
                                item0.mValue = "";
                                depositCategoryList.add(item0);
                                for (DepositCategoryVo.CategoryVo category : depositCategoryVoBaseResultEntityVo.data.categoryList) {
                                    DepositCategoryBean item = new DepositCategoryBean();
                                    item.mDesc = category.desc;
                                    item.mName = category.name;
                                    item.mValue = category.value;
                                    depositCategoryList.add(item);
                                }
                                mView.setDepositCategory(depositCategoryList);
                                getUserDepositInfo(1, depositCategoryList.get(0));
                            } else if (depositCategoryVoBaseResultEntityVo.status == 401) {
                                mView.pleaseLoginTip();
                            } else {
                                mView.showDialogTip(R.string.network_fault, true);
                            }

                        }
                    }
                });
        addSubscrebe(subscription);
    }
}
