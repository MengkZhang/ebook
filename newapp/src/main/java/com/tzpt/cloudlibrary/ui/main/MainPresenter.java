package com.tzpt.cloudlibrary.ui.main;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.AppVersionBean;
import com.tzpt.cloudlibrary.business_bean.UserInfoBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.LibraryRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.local.SharedPreferencesUtil;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VersionUpdateRemarkVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VersionUpdateVo;
import com.tzpt.cloudlibrary.utils.DateUtils;
import com.tzpt.cloudlibrary.utils.VersionUtils;
import com.tzpt.cloudlibrary.utils.badger.ShortcutBadger;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/12/10.
 */

public class MainPresenter extends RxPresenter<MainContract.View> implements MainContract.Presenter {

    private static final String NOTICE_BORROW_OVERDUE_TIME = "notice_borrow_overdue_time";

    MainPresenter() {
        UserRepository.getInstance().registerRxBus(UserInfoBean.class, new Action1<UserInfoBean>() {
            @Override
            public void call(UserInfoBean userInfoBean) {
                if (mView != null) {
                    //判断是否存在即将逾期，如果存在，则判断今天是否提示过,提示用户的即将逾期消息
                    if (userInfoBean.mIsBorrowOverdue && userInfoBean.mUpcomingOverdueCount > 0) {
                        String nowDate = DateUtils.formatNowDate(System.currentTimeMillis());
                        String noticeDate = SharedPreferencesUtil.getInstance().getString(NOTICE_BORROW_OVERDUE_TIME);
                        if (null == noticeDate || !noticeDate.equals(nowDate)) {
                            SharedPreferencesUtil.getInstance().putString(NOTICE_BORROW_OVERDUE_TIME, nowDate);
                            mView.setUserBorrowOverdueMsg(userInfoBean.mUpcomingOverdueCount);
                        }
                    }
                    mView.setUserRbStatus(userInfoBean.mOverdueUnReadSum + userInfoBean.mUnreadMsgCount + userInfoBean.mUnreadOverdueMsgCount);
                }
            }
        });
    }

    @Override
    public void checkAttentionLib() {
        if (!UserRepository.getInstance().isLogin()
                || TextUtils.isEmpty(LibraryRepository.getInstance().getAttentionLibCode())) {
            mView.showAttentionLib(false);
        } else {
            mView.showAttentionLib(true);
        }
    }

    @Override
    public void checkVersionInfo() {
        String versionName = VersionUtils.getVersionInfo();
        Subscription subscription = DataRepository.getInstance().checkVersion(versionName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResultEntityVo<VersionUpdateVo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResultEntityVo<VersionUpdateVo> versionUpdateVoBaseResultEntityVo) {
                        if (null != mView) {
                            if (versionUpdateVoBaseResultEntityVo.status == 200
                                    && versionUpdateVoBaseResultEntityVo.data != null) {
                                AppVersionBean bean = new AppVersionBean();
                                bean.mHref = versionUpdateVoBaseResultEntityVo.data.href;
                                if (!TextUtils.isEmpty(versionUpdateVoBaseResultEntityVo.data.remark)) {
                                    Gson gson = new Gson();
                                    JSONObject updateAppObj = null;
                                    try {
                                        updateAppObj = new JSONObject(versionUpdateVoBaseResultEntityVo.data.remark);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (null != updateAppObj) {
                                        VersionUpdateRemarkVo updateAppRemarkVo = gson.fromJson(updateAppObj.toString(), VersionUpdateRemarkVo.class);
                                        bean.mTitle = updateAppRemarkVo.title;
                                        bean.mSubTitle = updateAppRemarkVo.subtitle;
                                        bean.mContents = updateAppRemarkVo.content;
                                    }
                                }
                                bean.mUpdateTime = versionUpdateVoBaseResultEntityVo.data.updateDate;
                                bean.mForceUpdate = versionUpdateVoBaseResultEntityVo.data.forceUpdate;
                                bean.mVersion = versionUpdateVoBaseResultEntityVo.data.version;
                                bean.mId = versionUpdateVoBaseResultEntityVo.data.id;
                                mView.setAppVersionInfo(bean);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public void getUserInfo() {
        UserRepository.getInstance().refreshUserInfo();
    }

    @Override
    public void getLocalInfo() {
        int msgCount = UserRepository.getInstance().getLocalMsgCount();
        mView.setUserRbStatus(msgCount);
    }

    @Override
    public void handleShortCutBadger(int count) {
        if (count > 0) {
            ShortcutBadger.applyCount(CloudLibraryApplication.getAppContext(), count);
        } else {
            ShortcutBadger.removeCount(CloudLibraryApplication.getAppContext());
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        UserRepository.getInstance().unregisterRxBus();
    }
}
