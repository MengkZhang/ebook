package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ApiException;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.ReaderLoginVo;
import com.tzpt.cloundlibrary.manager.ui.contract.ReaderLoginContract;
import com.tzpt.cloundlibrary.manager.utils.MD5Util;

import org.json.JSONObject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 用户登录信息
 * Created by Administrator on 2017/7/6.
 */
public class ReaderLoginPresenter extends RxPresenter<ReaderLoginContract.View> implements
        ReaderLoginContract.Presenter,
        BaseResponseCode {

    @Override
    public void readerLogin(String account, String pwd, final boolean dealPenalty) {
        if (TextUtils.isEmpty(account)) {
            mView.showToastMsg("账号不能为空！");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            mView.showToastMsg("密码不能为空！");
            return;
        }
        mView.showLoading("登录中...");
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("condition", account);
            requestData.put("password", MD5Util.MD5(pwd));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Subscription subscription = DataRepository.getInstance().readerLogin(requestData.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReaderLoginVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.dismissLoading();
                            mView.readerLoginFailed(R.string.login_fails);
                        }
                    }

                    @Override
                    public void onNext(ReaderLoginVo readerLoginVo) {
                        if (mView != null) {
                            if (readerLoginVo.status == CODE_SUCCESS) {
                                if (null != readerLoginVo.data) {
                                    if (dealPenalty) {
                                        autoDealPenalty(readerLoginVo.data.id);
                                    } else {
                                        mView.dismissLoading();
                                        mView.readerLoginSuccess(readerLoginVo.data.id);
                                    }
                                }
                            } else {
                                mView.dismissLoading();
                                if (null != readerLoginVo.data) {
                                    switch (readerLoginVo.data.errorCode) {
                                        case ERROR_CODE_KICK_OUT:
                                            mView.noPermissionDialog(R.string.kicked_offline);
                                            break;
                                        case ERROR_CODE_1006:
                                            mView.noPermissionDialog(R.string.operate_timeout);
                                            break;
                                        case ERROR_CODE_3103:
                                            mView.readerLoginFailed(R.string.psw_error);
                                            break;
                                        case ERROR_CODE_3108:
                                        case ERROR_CODE_3111:
                                        case ERROR_CODE_3112:
                                            //0 非限制馆 1 限制馆
                                            int readerLimit = DataRepository.getInstance().getLibraryInfo().mReaderLimit;
                                            if (readerLimit == 1) {
                                                mView.showDialogForFinish(R.string.not_library_reader);
                                            } else {
                                                mView.showDialogForFinish(R.string.reader_does_not_exist);
                                            }
                                            break;
                                        case ERROR_CODE_3113:
                                        case ERROR_CODE_3115:
                                            mView.showDialogForFinish(R.string.refresh_idcard_for_register);
                                            break;
                                        default:
                                            mView.readerLoginFailed(R.string.login_fails);
                                            break;
                                    }
                                } else {
                                    mView.readerLoginFailed(R.string.network_fault);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    /**
     * 自动触发押金处理罚金问题
     */
    private void autoDealPenalty(final String readerId) {
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
                                        mView.noPermissionDialog(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.noPermissionDialog(R.string.operate_timeout);
                                        break;
                                    case ERROR_CODE_3108:
                                        mView.showDialogForFinish(R.string.reader_does_not_exist);
                                        break;
                                    case ERROR_CODE_3400://3400:余额不足
                                        mView.readerLoginSuccess(readerId);
                                        break;
                                    default:
                                        mView.readerLoginFailed(R.string.network_fault);
                                        break;
                                }
                            } else {
                                mView.readerLoginFailed(R.string.network_fault);
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
                                mView.readerLoginSuccess(readerId);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);

    }
}
