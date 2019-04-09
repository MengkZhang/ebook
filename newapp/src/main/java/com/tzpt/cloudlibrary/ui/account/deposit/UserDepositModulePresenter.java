package com.tzpt.cloudlibrary.ui.account.deposit;

import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;

/**
 * 押金信息
 * Created by Administrator on 2017/11/18.
 */
public class UserDepositModulePresenter extends RxPresenter<UserDepositModuleContract.View>
        implements UserDepositModuleContract.Presenter {

//    @Override
//    public void getUserDeposit() {
//        String idCard = DataRepository.getInstance().getLoginUserIdCard();
//        if (TextUtils.isEmpty(idCard)) {
//            return;
//        }
//        mView.showProgressLoading();
//        ArrayMap<String, Object> map = new ArrayMap<>();
//        map.put("idCard", idCard);
//        map.put("isAll", 1);
//        Subscription subscription = DataRepository.getInstance().getDepositInfo(map)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<BaseResultEntityVo<UserDepositVo>>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (mView != null) {
//                            mView.dismissProgressLoading();
//                            mView.showDialogTip(R.string.network_fault, true);
//                        }
//                    }
//
//                    @Override
//                    public void onNext(BaseResultEntityVo<UserDepositVo> userDepositVoBaseResultEntityVo) {
//                        if (mView != null) {
//                            mView.dismissProgressLoading();
//                            if (userDepositVoBaseResultEntityVo.status == 200
//                                    && userDepositVoBaseResultEntityVo.data != null) {
//                                DataRepository.getInstance().setChargeable(userDepositVoBaseResultEntityVo.data.chargeable == 0);
//
//                                if (userDepositVoBaseResultEntityVo.data.totalPenalty > 0
//                                        && userDepositVoBaseResultEntityVo.data.depositInfo.get(0).activeDeposit >= userDepositVoBaseResultEntityVo.data.totalPenalty) {
//                                    dealPenalty();
//                                }
//                            } else if (userDepositVoBaseResultEntityVo.status == 401) {
//                                if (userDepositVoBaseResultEntityVo.data.errorCode == 30100) {
//                                    mView.pleaseLoginTip();
//                                }
//                            } else {
//                                mView.showDialogTip(R.string.network_fault, true);
//                            }
//                        }
//                    }
//                });
//        addSubscrebe(subscription);
//    }
//
//    @Override
//    public boolean getChargeable() {
//        return DataRepository.getInstance().getChargeable();
//    }

    @Override
    public String getReaderId() {
        return UserRepository.getInstance().getLoginReaderId();
    }

//    private void dealPenalty() {
//        String readerId = DataRepository.getInstance().getLoginReaderId();
//        if (TextUtils.isEmpty(readerId)) {
//            mView.showDialogTip(R.string.network_fault, true);
//            return;
//        }
//        mView.showProgressLoading();
//        Subscription subscription = DataRepository.getInstance().dealUserLostBookPenalty(readerId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<BaseResultEntityVo<BaseDataResultVo>>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (null != mView) {
//                            mView.dismissProgressLoading();
//                            mView.showDialogTip(R.string.network_fault, true);
//                        }
//                    }
//
//                    @Override
//                    public void onNext(BaseResultEntityVo<BaseDataResultVo> baseDataResultVoBaseResultEntityVo) {
//                        if (mView != null) {
//                            mView.dismissProgressLoading();
//                            if (baseDataResultVoBaseResultEntityVo.status == 200) {
//                                getUserDeposit();
//                            } else if (baseDataResultVoBaseResultEntityVo.status == 401) {
//                                mView.pleaseLoginTip();
//                            } else if (baseDataResultVoBaseResultEntityVo.status == 417) {
//                                switch (baseDataResultVoBaseResultEntityVo.data.errorCode) {
//                                    case 3400://余额不足
//                                        getUserDeposit();
//                                        break;
//                                    default:
//                                        mView.showDialogTip(R.string.network_fault, true);
//                                        break;
//                                }
//                            } else {
//                                mView.showDialogTip(R.string.network_fault, true);
//                            }
//                        }
//                    }
//                });
//        addSubscrebe(subscription);
//    }

}
