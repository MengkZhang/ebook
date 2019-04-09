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
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ApiException;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.UpdateAppRemarkVo;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.UpdateAppVo;
import com.tzpt.cloundlibrary.manager.ui.contract.MainContract;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/9/26.
 */

public class MainPresenter extends RxPresenter<MainContract.View> implements MainContract.Presenter,
        BaseResponseCode {

    @Override
    public void getLoginUserInfo(Context context) {
        mView.showLoadingDialog();
        String versionName = getVersionName(context);
        removeDownloadId(context);

        Subscription subscription = DataRepository.getInstance().getLoginUserInfoAndCheckUpdate(versionName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateAppVo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.hideLoadingDialog();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case ERROR_CODE_KICK_OUT:
                                        mView.showDialogTip(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.showDialogTip(R.string.operate_timeout);
                                        break;
                                    case ERROR_CODE_1001:
                                        mView.showDialogTip(R.string.library_is_not_exists);
                                        break;
                                    case ERROR_CODE_1002:
                                        mView.showDialogTip(R.string.account_is_not_exists);
                                        break;
                                    case ERROR_CODE_1004:
                                        mView.showDialogTip(R.string.validation_fails);
                                        break;
                                    case ERROR_CODE_1003:
                                        mView.showDialogTip(R.string.psw_error);
                                        break;
                                    case ERROR_CODE_1014:
                                        mView.showDialogTip(R.string.account_is_lost);
                                        break;
                                    case 4001://最新版本不做任何处理
                                        break;
                                    default:
                                        mView.showDialogTip(R.string.error_code_500);
                                        break;
                                }
                            } else {
                                mView.showDialogTip(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(UpdateAppVo updateAppVo) {
                        if (mView != null) {
                            mView.hideLoadingDialog();
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
     */
    private void removeDownloadId(Context context) {
        //remove download id
        long downloadApkId = SharedPreferencesUtil.getInstance().getLong("downloadId", -1);
        if (downloadApkId != -1) {
            YTAppDownloadManager.getInstance(context).removeIds(downloadApkId);
            SharedPreferencesUtil.getInstance().putLong("downloadId", -1);
        }
    }
}
