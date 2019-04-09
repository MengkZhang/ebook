package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.IDCardBean;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ApiException;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.CheckRegisterVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.RegisterVo;
import com.tzpt.cloundlibrary.manager.ui.contract.ReaderAuthenticationContract;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by tonyjia on 2018/9/7.
 * 读者身份验证
 */
public class ReaderAuthenticationPresenter extends RxPresenter<ReaderAuthenticationContract.View> implements
        ReaderAuthenticationContract.Presenter, BaseResponseCode {

    /**
     * 检查读者身份信息
     *
     * @param defaultIdCardBean 身份信息
     */
    @Override
    public void checkReaderAuthenticationInfo(final IDCardBean defaultIdCardBean) {
        if (!StringUtils.verifyIdCardNumber(defaultIdCardBean.NUM)) {
            mView.showDialogScanLoginFailed("身份证号码错误！");
            return;
        }
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        if (null != libraryInfo && !TextUtils.isEmpty(libraryInfo.mHallCode)) {
            Subscription subscription = DataRepository.getInstance().checkReaderAccount(defaultIdCardBean.NUM, libraryInfo.mHallCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<CheckRegisterVo>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.showDialogScanLoginFailed(R.string.network_fault);
                            }
                        }

                        @Override
                        public void onNext(CheckRegisterVo checkRegisterVo) {
                            if (mView != null) {
                                if (checkRegisterVo.status == CODE_SUCCESS) {
                                    IDCardBean idCard = new IDCardBean();
                                    idCard.ID = checkRegisterVo.data.id;
                                    idCard.NUM = checkRegisterVo.data.idCard;
                                    idCard.NAME = checkRegisterVo.data.cardName;
                                    idCard.BIRTHDAY = defaultIdCardBean.BIRTHDAY;
                                    idCard.FOLK = !TextUtils.isEmpty(checkRegisterVo.data.nation) ? checkRegisterVo.data.nation : defaultIdCardBean.FOLK;
                                    if (null != checkRegisterVo.data.gender) {
                                        idCard.SEX = checkRegisterVo.data.gender.equals("MALE") ? "男" : "女";
                                    } else {
                                        idCard.SEX = defaultIdCardBean.SEX;
                                    }

                                    if (checkRegisterVo.data.isUpdateName == 1) {   //不可修改读者信息，读者已刷读卡器
                                        mView.setReaderAuthenticationInfo(idCard, false);
                                    } else {                                        //可修改读者信息
                                        mView.setReaderAuthenticationInfo(idCard, true);
                                    }
                                    mView.needRegisterReader(false);
                                } else {
                                    //返回code 3108，3111，3112，3114 需要返回读者默认信息
                                    if (null != checkRegisterVo.data) {
                                        switch (checkRegisterVo.data.errorCode) {
                                            case ERROR_CODE_KICK_OUT:
                                                mView.noPermissionPrompt(R.string.kicked_offline);
                                                break;
                                            case ERROR_CODE_1006:
                                                mView.noPermissionPrompt(R.string.operate_timeout);
                                                break;
                                            case ERROR_CODE_3108:
                                                setReaderIdCardInfo(checkRegisterVo.data.errorData, defaultIdCardBean);
                                                break;
                                            case ERROR_CODE_3111:
                                            case ERROR_CODE_3112:
                                                //该读者为限注册馆读者，需重新注册!
                                                int readerLimit = DataRepository.getInstance().getLibraryInfo().mReaderLimit;
                                                if (readerLimit == 1) {
                                                    mView.showDialogForFinish(R.string.not_library_reader);
                                                }
                                                setReaderIdCardInfo(checkRegisterVo.data.errorData, defaultIdCardBean);
                                                break;
                                            case ERROR_CODE_3114:
                                                setReaderIdCardInfo(checkRegisterVo.data.errorData, defaultIdCardBean);
                                                if (checkRegisterVo.data.errorData != null && !TextUtils.isEmpty(checkRegisterVo.data.errorData.phone)) {
                                                    mView.showDialogForFirstScanToCheckInfo(checkRegisterVo.data.errorData.phone);
                                                } else {
                                                    mView.showDialogForFirstScanToRegister();
                                                }
                                                break;
                                            default:
                                                setReaderIdCardInfo(checkRegisterVo.data.errorData, defaultIdCardBean);
                                                mView.showDialogScanLoginFailed(R.string.network_fault);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }


    /**
     * 设置读者身份信息
     *
     * @param errorData         身份信息
     * @param defaultIdCardBean 默认身份信息
     */
    private void setReaderIdCardInfo(CheckRegisterVo.ResponseData.ErrorDataVo errorData, IDCardBean defaultIdCardBean) {
        if (errorData != null) {
            IDCardBean idCardBean = new IDCardBean();
            idCardBean.NAME = errorData.cardName;
            idCardBean.NUM = errorData.idCard;
            idCardBean.FOLK = errorData.nation + "族";
            idCardBean.SEX = errorData.gender.equals("MALE") ? "男" : "女";
            idCardBean.smallHeadPath = defaultIdCardBean.smallHeadPath;
            idCardBean.BIRTHDAY = defaultIdCardBean.BIRTHDAY;
            mView.setReaderAuthenticationInfo(idCardBean, true);
        } else {
            mView.setReaderAuthenticationInfo(defaultIdCardBean, true);
        }
        mView.needRegisterReader(true);
    }

    @Override
    public void login(String readerId, boolean dealPenalty) {
        if (dealPenalty) {
            autoDealPenalty(readerId);
        } else {
            mView.checkResult(readerId);
        }
    }

//
//    /**
//     * 触发押金处理罚金问题
//     */
//    @Override
//    public void handleDepositPenalties(final IDCardBean idCardBean) {
//        if (idCardBean == null || TextUtils.isEmpty(idCardBean.ID)) {
//            return;
//        }
//        Subscription subscription = DataRepository.getInstance().automaticProcessingDepositPenalties(idCardBean.ID)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<AutoProcessDepositPenaltyVo>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (null != mView) {
//                            mView.showAutoProcessDepositPenaltyFailed(R.string.network_fault);
//                        }
//                    }
//
//                    @Override
//                    public void onNext(AutoProcessDepositPenaltyVo autoProcessDepositPenaltyVo) {
//                        if (null != mView) {
//                            //押金处理罚金成功
//                            if (autoProcessDepositPenaltyVo.status == CODE_SUCCESS) {
//                                mView.checkResult(idCardBean);
//                            } else {
//                                if (null != autoProcessDepositPenaltyVo.data) {
//                                    switch (autoProcessDepositPenaltyVo.data.errorCode) {
//                                        case ERROR_CODE_KICK_OUT:
//                                            mView.noPermissionPrompt(R.string.kicked_offline);
//                                            break;
//                                        case ERROR_CODE_1006:
//                                            mView.noPermissionPrompt(R.string.operate_timeout);
//                                            break;
//                                        case ERROR_CODE_3108:
//                                            mView.showDialogForFinish(R.string.reader_does_not_exist);
//                                            break;
//                                        case ERROR_CODE_3400://3400:余额不足
//                                            mView.checkResult(idCardBean);
//                                            break;
//                                        default:
//                                            mView.showAutoProcessDepositPenaltyFailed(R.string.network_fault);
//                                            break;
//                                    }
//                                } else {
//                                    mView.showAutoProcessDepositPenaltyFailed(R.string.network_fault);
//                                }
//                            }
//                        }
//                    }
//                });
//        addSubscrebe(subscription);
//    }

    /**
     * 更新用户信息
     *
     * @param idCardId     读者ID
     * @param idCardNumber 身份证号码
     * @param readerName   读者名称
     * @param readerSex    读者性别
     * @param nation       读者民族
     */
    @Override
    public void updateReaderInfo(final String idCardId, final String idCardNumber, final String readerName,
                                 final String readerSex, final String nation, final boolean dealPenalty) {
        if (TextUtils.isEmpty(readerName)) {
            mView.showDialogTips(R.string.name_not_empty);
            return;
        }
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        if (null != libraryInfo && !TextUtils.isEmpty(libraryInfo.mHallCode)) {
            ArrayMap<String, Object> map = new ArrayMap<>();
            map.put("cardName", readerName);
            map.put("hallCode", libraryInfo.mHallCode);
            map.put("idCard", idCardNumber);
            map.put("nation", nation);
            map.put("updateBorrowCard", 0);
            mView.showLoading("提交中...");
            final Subscription subscription = DataRepository.getInstance().updateReaderInfo(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<RegisterVo>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.showDialogTips(R.string.network_fault);
                                mView.dismissLoading();
                            }
                        }

                        @Override
                        public void onNext(RegisterVo registerVo) {
                            if (mView != null) {
                                mView.dismissLoading();
                                if (registerVo.status == 200) {
                                    if (dealPenalty) {
                                        autoDealPenalty(idCardId);
                                    } else {
                                        mView.checkResult(idCardId);
                                    }
                                } else {
                                    if (registerVo.data != null) {
                                        switch (registerVo.data.errorCode) {
                                            case ERROR_CODE_KICK_OUT:
                                                mView.noPermissionPrompt(R.string.kicked_offline);
                                                break;
                                            case ERROR_CODE_1006:
                                                mView.noPermissionPrompt(R.string.operate_timeout);
                                                break;
                                            default:
                                                mView.showDialogTips(R.string.error_code_3001);
                                                break;
                                        }
                                    } else {
                                        mView.showDialogTips(R.string.error_code_3001);
                                    }
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }


    /**
     * 自动触发押金处理罚金问题
     */
    private void autoDealPenalty(final String readerId) {
        mView.showLoading("");
        Subscription subscription = DataRepository.getInstance().autoDealAllPenalty(readerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Double>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissLoading();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case ERROR_CODE_KICK_OUT:
                                        mView.noPermissionPrompt(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.noPermissionPrompt(R.string.operate_timeout);
                                        break;
                                    case ERROR_CODE_3108:
                                        mView.showDialogForFinish(R.string.reader_does_not_exist);
                                        break;
                                    case ERROR_CODE_3400://3400:余额不足
                                        mView.checkResult(readerId);
                                        break;
                                    default:
                                        mView.showDialogScanLoginFailed(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.showDialogScanLoginFailed(R.string.network_fault);
                            }

                        }
                    }

                    @Override
                    public void onNext(Double aDouble) {
                        if (mView != null) {
                            mView.dismissLoading();
                            if (aDouble > 0) {
                                mView.turnToDealPenalty(readerId);
                            } else {
                                mView.checkResult(readerId);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);

    }
}
