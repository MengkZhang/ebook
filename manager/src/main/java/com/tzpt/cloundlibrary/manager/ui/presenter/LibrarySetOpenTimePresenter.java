package com.tzpt.cloundlibrary.manager.ui.presenter;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.base.BaseResponseCode;
import com.tzpt.cloundlibrary.manager.base.RxPresenter;
import com.tzpt.cloundlibrary.manager.bean.LibraryInfo;
import com.tzpt.cloundlibrary.manager.bean.LightLibraryOpenTimeInfo;
import com.tzpt.cloundlibrary.manager.bean.SearchAddressBean;
import com.tzpt.cloundlibrary.manager.modle.DataRepository;
import com.tzpt.cloundlibrary.manager.modle.remote.exception.ApiException;
import com.tzpt.cloundlibrary.manager.modle.remote.pojo.LightSelectVo;
import com.tzpt.cloundlibrary.manager.ui.contract.LibrarySetOpenTimeContract;
import com.tzpt.cloundlibrary.manager.utils.DateUtils;
import com.tzpt.cloundlibrary.manager.utils.MD5Util;
import com.tzpt.cloundlibrary.manager.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 开放时间
 * Created by Administrator on 2017/7/16.
 */
public class LibrarySetOpenTimePresenter extends RxPresenter<LibrarySetOpenTimeContract.View>
        implements LibrarySetOpenTimeContract.Presenter,
        BaseResponseCode {

    private String mAddress;
    private String mId;
    private double mLatitude;
    private double mLongitude;
    private boolean mIsChangeValidLngLat = false; //设置是否精确定位

    /**
     * 判断手机号码
     *
     * @param phone 手机号码
     */
    @Override
    public void verifyOpenTimePhoneNumber(String phone, String todayAMStart, String todayAMEnd, String todayPMStart, String todayPMEnd,
                                          String regularAMStart, String regularAMEnd) {
        if (!todayAMStart.equals("00:00") && todayAMEnd.equals("00:00")) {
            mView.showDialogMsg(R.string.today_open_time_error);
            return;
        }
        if (!todayPMStart.equals("00:00") && todayPMEnd.equals("00:00")) {
            mView.showDialogMsg(R.string.today_open_time_error);
            return;
        }
        if (todayPMStart.equals("00:00") && !todayPMEnd.equals("00:00")) {
            mView.showDialogMsg(R.string.today_open_time_error);
            return;
        }
        if (!regularAMStart.equals("00:00") && regularAMEnd.equals("00:00")) {
            mView.showDialogMsg(R.string.long_open_time_error);
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            mView.showDialogMsg(R.string.phone_number_cannot_empty);
            return;
        }
        if (!StringUtils.petternPhoneNumber(phone)) {
            mView.showDialogMsg(R.string.phone_number_error);
            return;
        }
        mView.showVerifyPasswordDialog();
    }

    /**
     * 设置图书馆开放标题
     */
    @Override
    public void getLibraryInfo() {
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        if (libraryInfo == null) {
            mView.setTitle("开放设置");
        } else {
            String libraryLevel = libraryInfo.mLibraryLev;
            String libraryName = libraryInfo.mName;
            mView.setLibraryName(libraryName);
            if (!TextUtils.isEmpty(libraryLevel) && (libraryLevel.equals("社区书屋") || libraryLevel.equals("农家书屋"))) {
                mView.setTitle("点亮设置");
            } else {
                mView.setTitle("开放设置");
            }
        }
    }

    /**
     * 获取图书馆开放信息
     */
    @Override
    public void getLibraryOpenTimeInfo() {
        if (mView != null) {
            mView.showProgressLoading();
        }
        LibraryInfo libraryInfo = DataRepository.getInstance().getLibraryInfo();
        if (libraryInfo != null) {
            Subscription subscription = DataRepository.getInstance().getLightSelect(libraryInfo.mHallCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<LightSelectVo>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.hideProgressLoading();
                                mView.showError(R.string.network_fault);
                            }
                        }

                        @Override
                        public void onNext(LightSelectVo lightSelectVo) {
                            if (mView != null) {
                                mView.hideProgressLoading();
                                if (lightSelectVo.status == CODE_SUCCESS) {
                                    if (null != lightSelectVo.data) {
                                        mId = lightSelectVo.data.id;
                                        if (!TextUtils.isEmpty(lightSelectVo.data.phone)) {
                                            mView.setContactTel(lightSelectVo.data.phone);
                                        }
                                        long nowTime = lightSelectVo.data.currentTime;
                                        if (nowTime == 0) {
                                            nowTime = System.currentTimeMillis();
                                        }
                                        boolean isAm = DateUtils.TimeIsAM(nowTime);
                                        mView.setNowTime(nowTime);
                                        mView.setTodayAmAvailable(isAm);
                                        startTimer();//开始计时
                                        if (null != lightSelectVo.data.lightOption) {
                                            Gson gson = new Gson();
                                            JSONObject objectLightOption = null;
                                            try {
                                                objectLightOption = new JSONObject(lightSelectVo.data.lightOption);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            if (objectLightOption != null) {//长期时间
                                                LightLibraryOpenTimeInfo mLightLibraryOpenTimeInfo = gson.fromJson(objectLightOption.toString(), LightLibraryOpenTimeInfo.class);
                                                mView.setWeekInfo(mLightLibraryOpenTimeInfo.week);
                                                if (mLightLibraryOpenTimeInfo.weekTime != null) {
                                                    LightLibraryOpenTimeInfo.TimeArea am = mLightLibraryOpenTimeInfo.weekTime.am;
                                                    if (null != am) {
                                                        mView.setLongAMOpenTimeStart(TextUtils.isEmpty(am.begin) ? "" : am.begin);
                                                        mView.setLongAMOpenTimeEnd(TextUtils.isEmpty(am.end) ? "" : am.end);
                                                    }
                                                    LightLibraryOpenTimeInfo.TimeArea pm = mLightLibraryOpenTimeInfo.weekTime.pm;
                                                    if (null != pm) {
                                                        mView.setLongPMTimeStart(TextUtils.isEmpty(pm.begin) ? "" : pm.begin);
                                                        mView.setLongPMTimeEnd(TextUtils.isEmpty(pm.end) ? "" : pm.end);
                                                    }
                                                } else {
                                                    mView.setLongAMOpenTimeStart("09:00");
                                                    mView.setLongAMOpenTimeEnd("12:00");
                                                    mView.setLongPMTimeStart("14:00");
                                                    mView.setLongPMTimeEnd("17:00");
                                                }
                                                boolean todayAlreadyChanged = DateUtils.isShouldChangeDate(mLightLibraryOpenTimeInfo.date, nowTime);
                                                if (todayAlreadyChanged && mLightLibraryOpenTimeInfo.dayTime != null) {
                                                    LightLibraryOpenTimeInfo.TimeArea am = mLightLibraryOpenTimeInfo.dayTime.am;
                                                    if (null != am) {
                                                        mView.setTodayAMOpenTimeStart(TextUtils.isEmpty(am.begin) ? "00:00" : am.begin);
                                                        mView.setTodayAMOpenTimeEnd(TextUtils.isEmpty(am.end) ? "00:00" : am.end);
                                                    }
                                                    LightLibraryOpenTimeInfo.TimeArea pm = mLightLibraryOpenTimeInfo.dayTime.pm;
                                                    if (null != pm) {
                                                        mView.setTodayPMOpenTimeStart(TextUtils.isEmpty(pm.begin) ? "00:00" : pm.begin);
                                                        mView.setTodayPMOpenTimeEnd(TextUtils.isEmpty(pm.end) ? "00:00" : pm.end);
                                                    }
                                                } else {
                                                    //当前时间没有设置
                                                    boolean useLongTimeForTodayTime = DateUtils.isShowUseLongTimeForDayTime(mLightLibraryOpenTimeInfo.week);
                                                    if (useLongTimeForTodayTime) {
                                                        if (mLightLibraryOpenTimeInfo.weekTime != null) {
                                                            LightLibraryOpenTimeInfo.TimeArea am = mLightLibraryOpenTimeInfo.weekTime.am;
                                                            if (null != am) {
                                                                mView.setTodayAMOpenTimeStart(TextUtils.isEmpty(am.begin) ? "" : am.begin);
                                                                mView.setTodayAMOpenTimeEnd(TextUtils.isEmpty(am.end) ? "" : am.end);
                                                            }
                                                            LightLibraryOpenTimeInfo.TimeArea pm = mLightLibraryOpenTimeInfo.weekTime.pm;
                                                            if (null != pm) {
                                                                mView.setTodayPMOpenTimeStart(TextUtils.isEmpty(pm.begin) ? "" : pm.begin);
                                                                mView.setTodayPMOpenTimeEnd(TextUtils.isEmpty(pm.end) ? "" : pm.end);
                                                            }
                                                        } else {
                                                            mView.setTodayAMOpenTimeStart("09:00");
                                                            mView.setTodayAMOpenTimeEnd("12:00");
                                                            mView.setTodayPMOpenTimeStart("14:00");
                                                            mView.setTodayPMOpenTimeEnd("17:00");
                                                        }
                                                    } else {
                                                        mView.setTodayAMOpenTimeStart("00:00");
                                                        mView.setTodayAMOpenTimeEnd("00:00");
                                                        mView.setTodayPMOpenTimeStart("00:00");
                                                        mView.setTodayPMOpenTimeEnd("00:00");
                                                    }
                                                }
                                            } else {
                                                //设置默认时间
                                                setDefaultDateValue(nowTime);
                                            }
                                        } else {
                                            //设置默认时间
                                            setDefaultDateValue(nowTime);
                                        }
                                        //解析图书馆定位地址
                                        if (lightSelectVo.data.address != null && !TextUtils.isEmpty(lightSelectVo.data.address)) {
                                            mAddress = lightSelectVo.data.address;
                                            mView.setLocationAddress(lightSelectVo.data.address);
                                        }
                                        //解析图书馆定位地址
                                        if (lightSelectVo.data.houseNumber != null && !TextUtils.isEmpty(lightSelectVo.data.houseNumber)) {
                                            mView.setLocationAddressNumber(lightSelectVo.data.houseNumber);
                                        }
                                        mLatitude = lightSelectVo.data.latitude;
                                        mLongitude = lightSelectVo.data.longitude;
                                    } else {
                                        //设置默认时间
                                        setDefaultDateValue(0);
                                    }
                                } else {
                                    if (null != lightSelectVo.data) {
                                        if (lightSelectVo.data.errorCode == ERROR_CODE_KICK_OUT) {
                                            mView.noPermissionPrompt(R.string.kicked_offline);
                                        } else if (lightSelectVo.data.errorCode == ERROR_CODE_1006) {
                                            mView.noPermissionPrompt(R.string.operate_timeout);
                                        } else {
                                            mView.showDialogMsg(R.string.error_code_500);
                                        }
                                    } else {
                                        mView.showDialogMsg(R.string.error_code_500);
                                    }
                                }
                            }
                        }
                    });
            addSubscrebe(subscription);
        }
    }

    /**
     * 设置搜索地址信息
     *
     * @param bean 地址对象
     */
    @Override
    public void setSearchAddressBean(SearchAddressBean bean) {
        mIsChangeValidLngLat = true;
        mLatitude = bean.mLatitude;
        mLongitude = bean.mLongitude;
        mAddress = bean.mAddressLocation;
        if (null != mView && !TextUtils.isEmpty(mAddress)) {
            mView.setLocationAddress(mAddress);
        }
    }

    /**
     * 设置默认时间
     *
     * @param nowTime 当前服务器时间
     */
    private void setDefaultDateValue(long nowTime) {
        if (nowTime == 0) {
            nowTime = System.currentTimeMillis();
        }
        boolean isAm = DateUtils.TimeIsAM(nowTime);
        mView.setNowTime(nowTime);
        mView.setTodayAmAvailable(isAm);
        mView.setTodayAMOpenTimeStart("00:00");
        mView.setTodayAMOpenTimeEnd("00:00");
        mView.setTodayPMOpenTimeStart("00:00");
        mView.setTodayPMOpenTimeEnd("00:00");

        mView.setLongAMOpenTimeStart("09:00");
        mView.setLongAMOpenTimeEnd("12:00");
        mView.setLongPMTimeStart("14:00");
        mView.setLongPMTimeEnd("17:00");
    }

    /**
     * 提交开放时间信息
     *
     * @param pwd            密码
     * @param phone          电话号码
     * @param todayAMStart   今日上午开始时间
     * @param todayAMEnd     今日上午结束时间
     * @param todayPMStart   今日下午开始时间
     * @param todayPMEnd     今日下午结束时间
     * @param regularAMStart 长期上午开始时间
     * @param regularAMEnd   长期上午结束时间
     * @param regularPMStart 长期下午开始时间
     * @param regularPMEnd   长期下午结束时间
     * @param weeks          星期集合
     * @param houseNumber    门牌号码
     */
    @Override
    public void setLightSelect(String pwd, String phone, String todayAMStart, String todayAMEnd, String todayPMStart, String todayPMEnd,
                               String regularAMStart, String regularAMEnd, String regularPMStart, String regularPMEnd, List<String> weeks,
                               String houseNumber) {
        if (TextUtils.isEmpty(pwd)) {
            mView.showDialogMsg(R.string.password_cannot_be_empty);
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            mView.showDialogMsg(R.string.phone_number_cannot_empty);
            return;
        }

        if (!StringUtils.petternPhoneNumber(phone)) {
            mView.showDialogMsg(R.string.phone_number_error);
            return;
        }

        String requestData = null;
        try {
            JSONObject object = new JSONObject();
            object.put("week", weeks);
            JSONObject jsonDayTimeAM = new JSONObject();
            jsonDayTimeAM.put("begin", todayAMStart);
            jsonDayTimeAM.put("end", todayAMEnd);
            JSONObject jsonDayTimePM = new JSONObject();
            jsonDayTimePM.put("begin", todayPMStart);
            jsonDayTimePM.put("end", todayPMEnd);
            JSONObject jsonDayTime = new JSONObject();
            jsonDayTime.put("am", jsonDayTimeAM);
            jsonDayTime.put("pm", jsonDayTimePM);
            object.put("dayTime", jsonDayTime);

            JSONObject jsonWeekTimeAM = new JSONObject();
            jsonWeekTimeAM.put("begin", regularAMStart);
            jsonWeekTimeAM.put("end", regularAMEnd);
            JSONObject jsonWeekTimePM = new JSONObject();
            jsonWeekTimePM.put("begin", regularPMStart);
            jsonWeekTimePM.put("end", regularPMEnd);
            JSONObject jsonWeekTime = new JSONObject();
            jsonWeekTime.put("am", jsonWeekTimeAM);
            jsonWeekTime.put("pm", jsonWeekTimePM);
            object.put("weekTime", jsonWeekTime);

            requestData = object.toString().replaceAll("]\"", "]").replace(":\"[", ":[");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JSONObject objectData = new JSONObject();
        try {
            if (null != mId && !TextUtils.isEmpty(mId)) {
                objectData.put("id", mId);
            }
            //传递经纬度
            if (mLatitude != 0 && mLongitude != 0) {
                objectData.put("latitude", mLatitude);
                objectData.put("longitude", mLongitude);
            }
            if (null != mAddress && !TextUtils.isEmpty(mAddress)) {
                objectData.put("address", mAddress);
            }
            if (!TextUtils.isEmpty(houseNumber)) {
                objectData.put("houseNumber", houseNumber);
            }
            objectData.put("lightOption", requestData);
            objectData.put("phone", phone);
            if (mIsChangeValidLngLat) {
                objectData.put("isValidLngLat", 1);
            }
        } catch (Exception e) {
            mView.showDialogMsg(R.string.network_fault);
        }
        mView.showLoading();
        Subscription subscription = DataRepository.getInstance().setLightSelect(MD5Util.MD5(pwd), objectData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView != null) {
                            mView.hideLoading();
                            if (e instanceof ApiException) {
                                switch (((ApiException) e).getCode()) {
                                    case ERROR_CODE_KICK_OUT:
                                        mView.noPermissionPrompt(R.string.kicked_offline);
                                        break;
                                    case ERROR_CODE_1006:
                                        mView.noPermissionPrompt(R.string.operate_timeout);
                                        break;
                                    case ERROR_CODE_3001:
                                        mView.showDialogMsg(R.string.error_code_3001);
                                        break;
                                    case ERROR_CODE_3002:
                                        mView.showDialogMsg(R.string.error_code_3002);
                                        break;
                                    case ERROR_CODE_1003://针对验证密码接口-密码错误
                                        mView.showDialogMsg(R.string.psw_error);
                                        break;
                                    case ERROR_CODE_3103:
                                        mView.showDialogMsg(R.string.psw_error);
                                        break;
                                    default:
                                        mView.showDialogMsg(R.string.setup_failed);
                                        break;
                                }
                            } else {
                                mView.showDialogMsg(R.string.network_fault);
                            }
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView != null) {
                            mView.hideLoading();
                            if (aBoolean) {
                                mView.showDialogSetSuccess(R.string.set_the_success);
                            } else {
                                mView.showDialogMsg(R.string.setup_failed);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private static final int sCount = 60 * 10;//默认定时10分钟

    //设置定时器
    private void startTimer() {
        Subscription subscription = Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(sCount + 1)
                .map(new Func1<Long, Long>() {
                    @Override
                    public Long call(Long aLong) {
                        return sCount - aLong;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        if (null != mView) {
                            mView.showTimeoutDialog();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                    }
                });
        addSubscrebe(subscription);
    }
}
