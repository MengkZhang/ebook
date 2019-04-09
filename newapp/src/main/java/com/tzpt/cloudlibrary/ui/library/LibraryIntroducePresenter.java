package com.tzpt.cloudlibrary.ui.library;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.base.RxPresenter;
import com.tzpt.cloudlibrary.bean.LibraryIntroduceBean;
import com.tzpt.cloudlibrary.bean.LightLibraryOpenTimeInfo;
import com.tzpt.cloudlibrary.business_bean.LibraryBean;
import com.tzpt.cloudlibrary.modle.DataRepository;
import com.tzpt.cloudlibrary.modle.remote.newpojo.BaseResultEntityVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibInfoVo;
import com.tzpt.cloudlibrary.modle.remote.newpojo.LibIntroduceVo;
import com.tzpt.cloudlibrary.ui.map.LocationManager;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/11/8.
 */

public class LibraryIntroducePresenter extends RxPresenter<LibraryIntroduceContract.View> implements
        LibraryIntroduceContract.Presenter {
    private String mLibCode;
    private int mFromSearch;

    @Override
    public void setLibraryCode(String libCode, int fromSearch) {
        mLibCode = libCode;
        mFromSearch = fromSearch;
    }

    @Override
    public void getLibraryInfo() {
        if (null != mView) {
            mView.showProgressDialog();
        }
        Subscription subscription = Observable.zip(DataRepository.getInstance().getLibIntroduce(LocationManager.getInstance().getLngLat(), mLibCode, mFromSearch, 1),
                DataRepository.getInstance().getLibraryNumber(mLibCode),
                new Func2<BaseResultEntityVo<LibIntroduceVo>, BaseResultEntityVo<LibInfoVo>, LibraryIntroduceBean>() {
                    @Override
                    public LibraryIntroduceBean call(BaseResultEntityVo<LibIntroduceVo> libIntroduceVo, BaseResultEntityVo<LibInfoVo> libInfoVo) {
                        return new LibraryIntroduceBean(libIntroduceVo, libInfoVo);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LibraryIntroduceBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mView) {
                            mView.dismissProgressDialog();
                            mView.showErrorMsg(R.string.network_fault);
                        }
                    }

                    @Override
                    public void onNext(LibraryIntroduceBean libraryIntroduceBean) {
                        if (mView != null) {
                            mView.dismissProgressDialog();
                            BaseResultEntityVo<LibIntroduceVo> libIntroduceVoBaseResultEntityVo = libraryIntroduceBean.mLibIntroduceVo;
                            if (libIntroduceVoBaseResultEntityVo.status == 200
                                    && libIntroduceVoBaseResultEntityVo.data != null) {
                                LibraryBean libInfo = new LibraryBean();
                                libInfo.mLibrary.mId = libIntroduceVoBaseResultEntityVo.data.info.libId;
                                libInfo.mLibrary.mCode = libIntroduceVoBaseResultEntityVo.data.info.libCode;
                                libInfo.mLibrary.mName = libIntroduceVoBaseResultEntityVo.data.info.libName;
                                libInfo.mLibrary.mPhone = libIntroduceVoBaseResultEntityVo.data.info.phone;
                                libInfo.mLibrary.mMail = libIntroduceVoBaseResultEntityVo.data.info.eamil;
                                libInfo.mLibrary.mLngLat = libIntroduceVoBaseResultEntityVo.data.info.lngLat;
                                libInfo.mLibrary.mNet = libIntroduceVoBaseResultEntityVo.data.info.webUrl;
                                libInfo.mDistance = libIntroduceVoBaseResultEntityVo.data.info.distance;
                                libInfo.mIsValidLngLat = (libIntroduceVoBaseResultEntityVo.data.info.isValidLngLat == 1);
                                libInfo.mLibrary.mAddress = libIntroduceVoBaseResultEntityVo.data.info.address + (libInfo.mIsValidLngLat ? "" : "(未精准定位)");
                                int bookCount = 0;
                                if (null != libraryIntroduceBean.mLibInfoVo.data) {
                                    bookCount = libraryIntroduceBean.mLibInfoVo.data.bookNum;
                                }
                                libInfo.mLibrary.mBookCount = bookCount;
                                //设置开放时间
                                if (null != libIntroduceVoBaseResultEntityVo.data.openInfo && !TextUtils.isEmpty(libIntroduceVoBaseResultEntityVo.data.openInfo.lightTime)) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(libIntroduceVoBaseResultEntityVo.data.openInfo.lightTime);
                                        LightLibraryOpenTimeInfo lightLibraryOpenTimeInfo = new Gson().fromJson(jsonObject.toString(), LightLibraryOpenTimeInfo.class);
                                        if (null != lightLibraryOpenTimeInfo) {
                                            //设置今日工作时间
                                            setTodayInfo(lightLibraryOpenTimeInfo.dayTime, lightLibraryOpenTimeInfo.week);
                                            //设置长期工作时间
                                            setLongDayInfo(lightLibraryOpenTimeInfo.weekTime, lightLibraryOpenTimeInfo.week);
                                            //设置开放工作日
                                            setWeekInfo(lightLibraryOpenTimeInfo.week);
                                        } else {
                                            setLightLibraryOpenTimeEmpty();
                                        }
                                    } catch (Exception e) {
                                        setLightLibraryOpenTimeEmpty();
                                    }
                                } else {
                                    setLightLibraryOpenTimeEmpty();
                                }
                                if (libIntroduceVoBaseResultEntityVo.data.htmlInfo != null) {
                                    libInfo.mLibrary.mIntroduceUrl = libIntroduceVoBaseResultEntityVo.data.htmlInfo.url;
                                }
                                mView.showLibraryInfo(libInfo);
                            } else {
                                mView.showErrorMsg(R.string.network_fault);
                            }
                        }
                    }
                });
        addSubscrebe(subscription);
    }

    private void setLightLibraryOpenTimeEmpty() {
        setTodayInfo(null, null);
        setLongDayInfo(null, null);
        setWeekInfo(null);
    }

    /**
     * 设置今日工作时间
     *
     * @param mDayTime
     */
    private void setTodayInfo(LightLibraryOpenTimeInfo.DayTime mDayTime, Integer[] weeks) {
        if (null == mDayTime && (null == weeks || weeks.length == 0)) {
            mView.setLibraryTodayTime("未设置！", "00:00\r\r\r\r-\r\r\r\r00:00", "00:00\r\r\r\r-\r\r\r\r00:00");
        } else if (mDayTime != null) {
            String startTime = "";
            String endTime = "";
            LightLibraryOpenTimeInfo.AM tAm = mDayTime.am;
            if (null != tAm) {
                String begin = tAm.begin;
                String end = tAm.end;
                StringBuilder builder = new StringBuilder();
                startTime = ((TextUtils.isEmpty(begin) || TextUtils.isEmpty(end)) ? "00:00\r\r\r\r-\r\r\r\r00:00"
                        : builder.append(begin).append("\r\r\r\r-\r\r\r\r").append(end).toString());

            }
            LightLibraryOpenTimeInfo.PM tPm = mDayTime.pm;
            if (null != tPm) {
                String begin = tPm.begin;
                String end = tPm.end;
                StringBuilder builder = new StringBuilder();
                endTime = ((TextUtils.isEmpty(begin) || TextUtils.isEmpty(end)) ? "00:00\r\r\r\r-\r\r\r\r00:00"
                        : builder.append(begin).append("\r\r\r\r-\r\r\r\r").append(end).toString());
            }
            if (startTime.equals("00:00\r\r\r\r-\r\r\r\r00:00")
                    && endTime.equals("00:00\r\r\r\r-\r\r\r\r00:00")) {
                if (null == weeks || weeks.length == 0) {
                    mView.setLibraryTodayTime("未设置！", "00:00\r\r\r\r-\r\r\r\r00:00", "00:00\r\r\r\r-\r\r\r\r00:00");
                } else {
                    mView.setLibraryTodayTime("闭馆！", "00:00\r\r\r\r-\r\r\r\r00:00", "00:00\r\r\r\r-\r\r\r\r00:00");
                }
            } else {
                mView.setLibraryTodayTime(null, startTime, endTime);
            }

        } else {
            mView.setLibraryTodayTime("闭馆！", "00:00\r\r\r\r-\r\r\r\r00:00", "00:00\r\r\r\r-\r\r\r\r00:00");
        }


    }

    /**
     * 设置长期工作时间
     *
     * @param weekTime
     * @param weeks
     */
    private void setLongDayInfo(LightLibraryOpenTimeInfo.WeekTime weekTime, Integer[] weeks) {
        if (null == weeks || weeks.length == 0) {
            mView.setLibraryLongTime("00:00\r\r\r\r-\r\r\r\r00:00", "00:00\r\r\r\r-\r\r\r\r00:00");
            return;
        }
        if (null == weekTime) {
            mView.setLibraryLongTime("09:00\r\r\r\r-\r\r\r\r12:00", "14:00\r\r\r\r-\r\r\r\r17:00");
            return;
        }
        String startTime = "";
        String endTime = "";
        LightLibraryOpenTimeInfo.AM wAm = weekTime.am;
        if (null != wAm) {
            String begin = wAm.begin;
            String end = wAm.end;
            StringBuilder builder = new StringBuilder();
            startTime = ((TextUtils.isEmpty(begin) || TextUtils.isEmpty(end)) ? "09:00\r\r\r\r-\r\r\r\r12:00"
                    : builder.append(begin).append("\r\r\r\r-\r\r\r\r").append(end).toString());

        }
        LightLibraryOpenTimeInfo.PM wPm = weekTime.pm;
        if (null != wPm) {
            String begin = wPm.begin;
            String end = wPm.end;
            StringBuilder builder = new StringBuilder();
            endTime = ((TextUtils.isEmpty(begin) || TextUtils.isEmpty(end)) ? "14:00\r\r\r\r-\r\r\r\r17:00"
                    : builder.append(begin).append("\r\r\r\r-\r\r\r\r").append(end).toString());
        }
        mView.setLibraryLongTime(startTime, endTime);
    }

    /**
     * 设置开放工作日
     *
     * @param week
     */
    private void setWeekInfo(Integer[] week) {
        if (null != week && week.length > 0) {
            List<Integer> weekList = Arrays.asList(week);
            StringBuilder weekBuilder = new StringBuilder();
            weekBuilder.append("周");
            if (weekList.size() > 0) {
                if (weekList.contains(1)) {
                    weekBuilder.append("一\r\r");
                }
                if (weekList.contains(2)) {
                    weekBuilder.append("二\r\r");
                }
                if (weekList.contains(3)) {
                    weekBuilder.append("三\r\r");
                }
                if (weekList.contains(4)) {
                    weekBuilder.append("四\r\r");
                }
                if (weekList.contains(5)) {
                    weekBuilder.append("五\r\r");
                }
                if (weekList.contains(6)) {
                    weekBuilder.append("六\r\r");
                }
                if (weekList.contains(7)) {
                    weekBuilder.append("日");
                }
            }
            mView.setWeekInfo(weekList.size() == 0 ? "" : weekBuilder.toString());
        } else {
            mView.setWeekInfo("未设置！");
        }
    }

}
