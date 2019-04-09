package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.UpdateAppBean;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.local.SharedPreferencesUtil;
import com.tzpt.cloundlibrary.manager.modle.remote.downloadmanager.YTAppDownloadManager;
import com.tzpt.cloundlibrary.manager.modle.remote.downloadmanager.YTAppUpdateUtils;
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ApiException;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.UpdateAppRemarkVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.UpdateAppVo;
import com.tzpt.cloundlibrary.manager.ui.contract.LoginContract;
import com.tzpt.cloundlibrary.manager.utils.MD5Util;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * login
 * Created by Administrator on 2017/6/21.
 */
public class LoginPresenter extends RxPresenter<LoginContract.View> implements LoginContract.Presenter,
        BaseResponseCode {

    @Override
    public void getLoginInfo() {
        if (DataRepository.getInstance().getLibraryInfo() != null) {
            mView.showLoginInfo(DataRepository.getInstance().getLibraryInfo());
        }
    }

    @Override
    public void login(final String libName, String userName, String pwd) {
        if (mView != null) {
            if (TextUtils.isEmpty(libName)) {
                mView.loginFailed("馆号不能为空！");
                return;
            }
            if (TextUtils.isEmpty(userName)) {
                mView.loginFailed("用户名不能为空！");
                return;
            }
            if (TextUtils.isEmpty(pwd)) {
                mView.loginFailed("密码不能为空！");
                return;
            }
            mView.showLoadingDialog("登录中...");
            //登录后执行获取图书馆信息
            Subscription subscription = DataRepository.getInstance().loginAdmin(libName, userName, MD5Util.MD5(pwd))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.dismissLoadingDialog();
                                if (e instanceof ApiException) {
                                    switch (((ApiException) e).getCode()) {
                                        case ERROR_CODE_KICK_OUT:
                                            mView.loginFailed(R.string.kicked_offline);
                                            break;
                                        case ERROR_CODE_1006:
                                            mView.loginFailed(R.string.operate_timeout);
                                            break;
                                        case ERROR_CODE_1001:
                                            mView.loginFailed(R.string.library_is_not_exists);
                                            break;
                                        case ERROR_CODE_1002:
                                            mView.loginFailed(R.string.account_is_not_exists);
                                            break;
                                        case ERROR_CODE_1004:
                                            mView.loginFailed(R.string.validation_fails);
                                            break;
                                        case ERROR_CODE_1003:
                                            mView.loginFailed(R.string.psw_error);
                                            break;
                                        case ERROR_CODE_1014:
                                            mView.loginFailed(R.string.account_is_lost);
                                            break;
                                        case CODE_SERVICE_ERROR:
                                            mView.loginFailed(R.string.error_code_500);
                                            break;
                                        case ERROR_CODE_10007:
                                            mView.setFirstLoginOperatorPswTip();
                                            break;
                                        case ERROR_CODE_UNKNOWN:
                                        case ERROR_CODE_PARSE:
                                        case ERROR_CODE_NETWORK:
                                        case ERROR_CODE_HTTP:
                                            mView.loginFailed(R.string.network_fault);
                                            break;
                                        default:
                                            mView.loginFailed(R.string.validation_fails);
                                            break;
                                    }
                                } else {
                                    mView.loginFailed(R.string.network_fault);
                                }
                            }
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (mView != null) {
                                mView.dismissLoadingDialog();
                                mView.loginSuccess();
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    /**
     * 更新APP
     */
    @Override
    public void updateAppInfo(Context context) {
        if (null != mView) {
            String versionName = getVersionName(context);
            removeDownloadId(context);
            Subscription subscription = DataRepository.getInstance().updateApp(versionName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<UpdateAppVo>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(UpdateAppVo updateAppVo) {
                            if (null != mView) {
                                if (updateAppVo.status == CODE_SUCCESS) {
                                    if (null != updateAppVo.data) {
                                        UpdateAppBean updateAppBean = new UpdateAppBean();
                                        updateAppBean.href = updateAppVo.data.href;
                                        updateAppBean.updateDate = updateAppVo.data.updateDate;
                                        updateAppBean.version = updateAppVo.data.version;
                                        updateAppBean.forceUpdate = updateAppVo.data.forceUpdate;
                                        if (null != updateAppVo.data.remark && !TextUtils.isEmpty(updateAppVo.data.remark)) {
                                            JSONObject updateAppObj = null;
                                            try {
                                                updateAppObj = new JSONObject(updateAppVo.data.remark);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            if (null != updateAppObj) {
                                                Gson gson = new Gson();
                                                UpdateAppRemarkVo updateAppRemarkVo = gson.fromJson(updateAppObj.toString(), UpdateAppRemarkVo.class);
                                                updateAppBean.updateTitle = updateAppRemarkVo.title;
                                                updateAppBean.updateSubTitle = updateAppRemarkVo.subtitle;
                                                updateAppBean.updateContentList = updateAppRemarkVo.content;
                                            }
                                        }
                                        mView.showUpdateAppInfo(updateAppBean);
                                    }
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    /**
     * 1.获取本地版本号
     */
    private String getVersionName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            return "1.0";
        }
    }

    /**
     * 2.移除下载器之前的ID
     *
     * @param context
     */
    private void removeDownloadId(Context context) {
        //remove download id
        long downloadApkId = SharedPreferencesUtil.getInstance().getLong("downloadId", -1);
        if (downloadApkId != -1) {
            YTAppDownloadManager.getInstance(context).removeIds(downloadApkId);
            SharedPreferencesUtil.getInstance().putLong("downloadId", -1);
        }
    }

    /**
     * 开始下载
     *
     * @param url
     */
    public void download(Context context, String url) {
        if (null != mView) {
            if (!canDownloadState(context)) {
                mView.loginFailed("下载管理不可用！");
                return;
            }
            if (TextUtils.isEmpty(url)) {
                mView.loginFailed("下载地址错误！");
                return;
            }
            if (!TextUtils.isEmpty(url) && !url.endsWith(".apk")) {
                mView.loginFailed("下载地址错误！");
                return;
            }
            YTAppUpdateUtils.download(context, url, context.getResources().getString(R.string.app_name));
        }
    }

    /**
     * 下载管理是否可用
     *
     * @param context
     * @return
     */
    private boolean canDownloadState(Context context) {
        try {
            int state = context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
