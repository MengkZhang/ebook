package com.tzpt.cloudlibrary.ui.account.setting;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.AppVersionBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.UserRepository;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VersionUpdateRemarkVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.VersionUpdateVo;
import com.tzpt.cloudlibrary.utils.VersionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/1/4.
 */

public class SettingPresenter extends RxPresenter<SettingContract.View> implements SettingContract.Presenter {
    @Override
    public void checkVersionInfo() {
        mView.showLoading();
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
                        if (mView != null) {
                            mView.dismissLoading();
                        }
                    }

                    @Override
                    public void onNext(BaseResultEntityVo<VersionUpdateVo> versionUpdateVoBaseResultEntityVo) {
                        if (null != mView) {
                            mView.dismissLoading();
                            if (versionUpdateVoBaseResultEntityVo.status == 200) {
                                if (versionUpdateVoBaseResultEntityVo.data != null) {
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
                                } else {
                                    mView.showDialogTip(R.string.network_fault);
                                }
                            } else if (versionUpdateVoBaseResultEntityVo.status == 417) {
                                if (versionUpdateVoBaseResultEntityVo.data.errorCode == 30801) {
                                    mView.showDialogTip(R.string.last_current_version_tip);
                                }
                            } else {
                                mView.showDialogTip(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    @Override
    public boolean isLogin() {
        return UserRepository.getInstance().isLogin();
    }
}
