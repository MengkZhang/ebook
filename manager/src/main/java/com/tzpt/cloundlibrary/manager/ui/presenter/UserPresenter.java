package com.tzpt.cloundlibrary.manager.ui.presenter;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.NoReadMsgVo;
import com.tzpt.cloundlibrary.manager.ui.contract.UserContract;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 用户
 * Created by Administrator on 2017/7/27.
 */
public class UserPresenter extends RxPresenter<UserContract.View> implements
        UserContract.Presenter,
        BaseResponseCode {
    private Subscription mSubscription;

    //检查是否有押金权限
    @Override
    public boolean checkDepositPermission() {
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        if (null != libraryInfo) {
            return libraryInfo.mAppDepositPermission;//押金权限
        }
        return false;
    }

    //检查是否有密码管理权限
    @Override
    public boolean checkPswManagePermission() {
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        if (null != libraryInfo) {
            return libraryInfo.mPasswordManagePermission;
        }
        return false;
    }

    @Override
    public void getMsgNoReadCount() {
        if (null != mSubscription && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        mSubscription = DataRepository.getInstance().getUnReadCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NoReadMsgVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.setMsgNoReadCount(0);
                        }
                    }

                    @Override
                    public void onNext(NoReadMsgVo msgVo) {
                        if (null != mView) {
                            if (msgVo.status == CODE_SUCCESS) {
                                if (null != msgVo.data) {
                                    mView.setMsgNoReadCount(msgVo.data.value);
                                }
                            } else {
                                if (null != msgVo.data) {
                                    if (msgVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                        mView.noPermissionPrompt(R.string.kicked_offline);
                                    } else if (msgVo.data.errorCode == ERROR_CODE_1006) {
                                        mView.noPermissionPrompt(R.string.operate_timeout);
                                    } else {
                                        mView.setMsgNoReadCount(0);
                                    }
                                } else {
                                    mView.setMsgNoReadCount(0);
                                }
                            }
                        }
                    }
                });
        addSubscrebe(mSubscription);
    }

    //清除图书馆对象
    @Override
    public void delLibraryInfo() {
        DataRepository.getInstance().clearToken();
        DataRepository.getInstance().delLibraryInfo();
    }
}
