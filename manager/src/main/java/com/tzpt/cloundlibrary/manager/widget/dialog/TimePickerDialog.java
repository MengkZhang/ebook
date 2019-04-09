package com.tzpt.cloundlibrary.manager.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import com.tzpt.cloundlibrary.manager.R;
import com.tzpt.cloundlibrary.manager.utils.DateUtils;
import com.tzpt.cloundlibrary.manager.widget.PickerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 时间选择控件
 * Created by Administrator on 2017/6/28.
 */
public class TimePickerDialog extends AlertDialog {
    private static final int TODAY_AM = 10;
    private static final int TODAY_PM = 11;
    private static final int LONG_AM = 12;
    private static final int LONG_PM = 13;

    private static final String[] HOUR_AM = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private static final String[] HOUR_PM = {"12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
    private static final String[] MINUTE = {"00", "10", "20", "30", "40", "50"};
    private static final String[] MINUTE_ZERO = {"00"};

    private PickerView mPickerViewHour;
    private PickerView mPickerViewMinute;
    private String mChooseHour = "00";
    private String mChooseMinute = "00";
    private int mChooseType;
    private List<String> mHourList = new ArrayList<>();
    private List<String> mMinuteList = new ArrayList<>();
    private String mNowTime; //今日日期
    private boolean mTimeIsAm = true; //当前时间是否上午

    public TimePickerDialog(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public TimePickerDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    private void initView(Context context) {
        View v = View.inflate(context, R.layout.view_time_picker, null);
        setView(v);
        mPickerViewHour = (PickerView) v.findViewById(R.id.hour_pv);
        mPickerViewMinute = (PickerView) v.findViewById(R.id.minute_pv);
        setButton(BUTTON_POSITIVE, "确定", mOnClickListener);
    }

    private DialogInterface.OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (null != mListener) {
                mListener.onChange(mChooseType, mChooseHour + ":" + mChooseMinute);
            }
        }
    };

    /**
     * 初始化数据
     *
     * @param am
     */
    public void initDatas(int am, int mChooseType, String mChooseHour, String mChooseMinute, long nowTime) {
        this.mChooseType = mChooseType;
        this.mChooseHour = mChooseHour;
        this.mChooseMinute = mChooseMinute;
        //初始化数据
        List<String> allAMHoursList = new ArrayList<>();
        List<String> allPMHoursList = new ArrayList<>();
        List<String> allMinutesList = new ArrayList<>();
        allAMHoursList.addAll(Arrays.asList(HOUR_AM));
        allPMHoursList.addAll(Arrays.asList(HOUR_PM));
        allMinutesList.addAll(Arrays.asList(MINUTE));
        if (nowTime > 0) {
            mNowTime = DateUtils.formatTimeToHHmm(nowTime);
            mTimeIsAm = DateUtils.TimeIsAM(nowTime);
        } else {
            mNowTime = DateUtils.formatTimeToHHmm(System.currentTimeMillis());
            mTimeIsAm = DateUtils.TimeIsAM(System.currentTimeMillis());
        }
        mHourList.clear();
        mMinuteList.clear();
        switch (am) {
            //今天上午
            case TODAY_AM:
                chooseTimeForTodayAM(allAMHoursList, allMinutesList);
                break;
            //今天下午
            case TODAY_PM:
                chooseTimeForTodayPM(allPMHoursList, allMinutesList);
                break;
            //定期上午
            case LONG_AM:
                chooseTimeForLongDayAM(allAMHoursList, allMinutesList);
                break;
            //定期下午
            case LONG_PM:
                chooseTimeForLongDayPM(allPMHoursList, allMinutesList);
                break;
        }
        mPickerViewHour.setData(mHourList, mChooseType, am);
        if (mMinuteList.size() == 0) {
            mMinuteList.addAll(Arrays.asList(MINUTE));
        }
        mPickerViewMinute.setData(mMinuteList, mChooseType, am);
        initHasHourMinute();
        initListeners();
    }
    //检查如果有小时或者分钟的值，则设置位置
    private void initHasHourMinute() {
        //init hour
        int dataSize = mPickerViewHour.getDataSize();
        List<String> hourList = mPickerViewHour.getData();
        if (dataSize > 0 && !TextUtils.isEmpty(mChooseHour)) {
            int index = 0;
            for (int i = 0; i < dataSize; i++) {
                if (hourList.get(i).equals(mChooseHour)) {
                    index = i;
                    break;
                }
            }
            if (index < dataSize) {
                mPickerViewHour.setSelected(index);
            }
        }
        //init minute
        int minuteDataSize = mPickerViewMinute.getDataSize();
        List<String> menuList = mPickerViewMinute.getData();
        if (minuteDataSize > 0 && !TextUtils.isEmpty(mChooseMinute)) {
            int index = 0;
            for (int i = 0; i < minuteDataSize; i++) {
                if (menuList.get(i).equals(mChooseMinute)) {
                    index = i;
                    break;
                }
            }
            if (index < minuteDataSize) {
                mPickerViewMinute.setSelected(index);
            }
        }
    }
    /**
     * 今日上午时间
     * @param allAMHoursList
     * @param allMinutesList
     */
    private void chooseTimeForTodayAM(List<String> allAMHoursList, List<String> allMinutesList){
        //当前小时
        String nowHour = mNowTime.split(":")[0];
        //当前分钟
        String nowMinute = mNowTime.split(":")[1];
        if (mChooseHour.equals("12")) {
            //12点时候设置分钟数
            this.mChooseMinute = "00";
            allMinutesList.clear();
            allMinutesList.addAll(Arrays.asList(MINUTE_ZERO));

            List<String> tempHourList = new ArrayList<>();
            tempHourList.clear();
            int size = allAMHoursList.size();
            //设置当前时间小时的范围,如果分钟>50,则不包含当前小时
            for (int i = 0; i < size; i++) {
                if (Integer.parseInt(nowMinute) > 50) {
                    if (Integer.parseInt(nowHour) < Integer.parseInt(allAMHoursList.get(i))) {
                        tempHourList.add(allAMHoursList.get(i));
                        mChooseHour = allAMHoursList.get(0);
                    }
                } else {
                    if (Integer.parseInt(nowHour) <= Integer.parseInt(allAMHoursList.get(i))) {
                        tempHourList.add(allAMHoursList.get(i));
                    }
                }
            }
            //填充临时小时数据
            allAMHoursList.clear();
            if (tempHourList.size() > 0) {
                allAMHoursList.addAll(tempHourList);
            } else {
                allAMHoursList.addAll(Arrays.asList(HOUR_AM));
            }
        } else {
            //如果当前时间大于选择时间,则显示为选择时间变为当前时间
            if (Integer.parseInt(mChooseHour) <= Integer.parseInt(nowHour)) {
                mChooseHour = nowHour;
            }
            List<String> tempHourList = new ArrayList<>();
            tempHourList.clear();
            int size = allAMHoursList.size();
            //设置当前时间小时的范围,如果分钟>50,则不包含当前小时
            for (int i = 0; i < size; i++) {
                if (Integer.parseInt(nowMinute) > 50) {
                    if (Integer.parseInt(nowHour) < Integer.parseInt(allAMHoursList.get(i))) {
                        tempHourList.add(allAMHoursList.get(i));
                        mChooseHour = tempHourList.get(0);
                    }
                } else {
                    if (Integer.parseInt(nowHour) <= Integer.parseInt(allAMHoursList.get(i))) {
                        tempHourList.add(allAMHoursList.get(i));
                    }
                }
            }
            //填充临时小时数据
            allAMHoursList.clear();
            if (tempHourList.size() > 0) {
                allAMHoursList.addAll(tempHourList);
            } else {
                allAMHoursList.addAll(Arrays.asList(HOUR_AM));
            }
            //如果当前时间与选中的小时相等，设置分钟在当前分钟之后
            if (nowHour.equals(mChooseHour)) {
                List<String> tempMinuteList = new ArrayList<>();
                tempMinuteList.clear();
                int tempMinuteListSize = allMinutesList.size();
                for (int i = 0; i < tempMinuteListSize; i++) {
                    if (Integer.parseInt(nowMinute) <= Integer.parseInt(allMinutesList.get(i))) {
                        tempMinuteList.add(allMinutesList.get(i));
                    }
                }
                allMinutesList.clear();
                if (tempMinuteList.size() > 0) {
                    allMinutesList.addAll(tempMinuteList);
                } else {
                    allMinutesList.addAll(Arrays.asList(MINUTE));
                }
            }
        }

        if (mChooseHour.equals("00")) {
            mChooseHour = allAMHoursList.get(0);
        }
        if (mChooseMinute.equals("00")) {
            if (allMinutesList.size() > 0) {
                mChooseMinute = allMinutesList.get(0);
            } else {
                mChooseMinute = "00";
                allMinutesList.addAll(Arrays.asList(MINUTE));
            }
        }
        if (allAMHoursList.size() > 0) {
            mHourList.addAll(allAMHoursList);
        }
        if (mChooseHour.equals("12")) {
            //12点时候设置分钟数
            this.mChooseMinute = "00";
            allMinutesList.clear();
            allMinutesList.addAll(Arrays.asList(MINUTE_ZERO));
        }
        mMinuteList.addAll((allMinutesList.size() > 0) ? allMinutesList : Arrays.asList(MINUTE));
    }
    /**
     * 今日下午时间
     * @param allPMHoursList
     * @param allMinutesList
     */
    private void chooseTimeForTodayPM(List<String> allPMHoursList, List<String> allMinutesList) {
        //如果当前时间是今天上午,则显示下午全部时间
        if (mTimeIsAm) {
            mChooseHour = "12";
            mChooseMinute = "00";
            mHourList.addAll(Arrays.asList(HOUR_PM));
            mMinuteList.addAll(Arrays.asList(MINUTE));
            return;
        }
        //当前小时
        String nowHour = mNowTime.split(":")[0];
        //当前分钟
        String nowMinute = mNowTime.split(":")[1];
        //如果当前时间大于选择时间,则显示为选择时间变为当前时间
        if (Integer.parseInt(mChooseHour) <= Integer.parseInt(nowHour)) {
            mChooseHour = nowHour;
        }
        //添加临时小时列表
        List<String> tempHourList = new ArrayList<>();
        tempHourList.clear();
        int size = allPMHoursList.size();
        for (int i = 0; i < size; i++) {
            //设置当前时间小时的范围,如果分钟>50,则不包含当前小时
            if (Integer.parseInt(nowMinute) > 50) {
                if (Integer.parseInt(nowHour) < Integer.parseInt(allPMHoursList.get(i))) {
                    tempHourList.add(allPMHoursList.get(i));
                    mChooseHour = tempHourList.get(0);
                }
            } else {
                if (Integer.parseInt(nowHour) <= Integer.parseInt(allPMHoursList.get(i))) {
                    tempHourList.add(allPMHoursList.get(i));
                }
            }
        }
        allPMHoursList.clear();
        if (tempHourList.size() > 0) {
            allPMHoursList.addAll(tempHourList);
        } else {
            allPMHoursList.addAll(Arrays.asList(HOUR_PM));
        }
        //如果选择时间与当前时间小时吻合,设置分钟数
        if (mChooseHour.equals(nowHour)) {
            List<String> tempMinuteList = new ArrayList<>();
            tempMinuteList.clear();
            int tempMinuteListSize = allMinutesList.size();
            for (int i = 0; i < tempMinuteListSize; i++) {
                if (Integer.parseInt(nowMinute) <= Integer.parseInt(allMinutesList.get(i))) {
                    tempMinuteList.add(allMinutesList.get(i));
                }
            }
            allMinutesList.clear();
            if (tempMinuteList.size() > 0) {
                allMinutesList.addAll(tempMinuteList);
            } else {
                allMinutesList.addAll(Arrays.asList(MINUTE));
            }
        }
        if (mChooseHour.equals("00")) {
            if (allPMHoursList.size() > 0) {
                mChooseHour = allPMHoursList.get(0);
            } else {
                mChooseHour = "00";
            }
        }
        if (mChooseMinute.equals("00")) {
            if (allMinutesList.size() > 0) {
                mChooseMinute = allMinutesList.get(0);
            } else {
                mChooseMinute = "00";
                allMinutesList.addAll(Arrays.asList(MINUTE));
            }
        }
        mHourList.addAll(allPMHoursList);
        mMinuteList.addAll((allMinutesList.size() > 0) ? allMinutesList : Arrays.asList(MINUTE));
    }


    /**
     * 长期上午时间
     * @param allAMHoursList
     * @param allMinutesList
     */
    private void chooseTimeForLongDayAM(List<String> allAMHoursList, List<String> allMinutesList) {
        if (mChooseHour.equals("12")) {
            this.mChooseMinute = "00";
            allMinutesList.clear();
            allMinutesList.addAll(Arrays.asList(MINUTE_ZERO));
        } else {
            allMinutesList.clear();
            allMinutesList.addAll(Arrays.asList(MINUTE));
        }
        //定期上午有值，则显示该值，没有则显示默认列表第一个值
        if (mChooseHour.equals("00")) {
            mChooseHour = allAMHoursList.get(0);
        }
        if (mChooseMinute.equals("00")) {
            if (allMinutesList.size() > 0) {
                mChooseMinute = allMinutesList.get(0);
            } else {
                mChooseMinute = "00";
                allMinutesList.addAll(Arrays.asList(MINUTE));
            }
        }
        mHourList.addAll(allAMHoursList);
        mMinuteList.addAll(allMinutesList);
    }
    /**
     * 长期下午时间
     * @param allPMHoursList
     * @param allMinutesList
     */
    private void chooseTimeForLongDayPM(List<String> allPMHoursList, List<String> allMinutesList) {
        //定期定期下午有值，则显示该值，没有则显示默认列表第一个值
        if (mChooseHour.equals("00")) {
            mChooseHour = allPMHoursList.get(0);
        }
        if (mChooseMinute.equals("00")) {
            if (allMinutesList.size() > 0) {
                mChooseMinute = allMinutesList.get(0);
            } else {
                mChooseMinute = "00";
                allMinutesList.addAll(Arrays.asList(MINUTE));
            }
        }
        mHourList.addAll(allPMHoursList);
        mMinuteList.addAll(allMinutesList);
    }


    private void initListeners() {
        mPickerViewHour.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String hour, int chooseType, int am) {
                mChooseHour = hour;
                // 当hour 数据修改以后，根据类型mChooseType 修改minute 数据
                changeMinuteDataByHourChange(hour, am);
            }
        });
        mPickerViewMinute.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String minute, int chooseType, int am) {
                mChooseMinute = minute;
            }
        });
    }

    /**
     * 当hour 数据修改以后，根据类型mChooseType 修改minute 数据
     * @param hour
     * @param am
     */
    private void changeMinuteDataByHourChange(String hour, int am) {
        switch (am) {
            case TODAY_AM:
            case LONG_AM:
                changeTimeFor12(hour, am);
                break;
            case TODAY_PM:
            case LONG_PM:
                timeChange(hour,am);
                break;
        }
        initHasHourMinute();
    }


    private void changeTimeFor12(String hour, int am){
        if (null != hour && hour.equals("12")) {
            mMinuteList.clear();
            mMinuteList.addAll(Arrays.asList(MINUTE_ZERO));
            mPickerViewMinute.setData(mMinuteList, mChooseType, am);
            this.mChooseMinute = "00";
        } else {
            timeChange(hour, am);
        }
        initHasHourMinute();
    }

    /**
     * 其他时间修改
     *
     * @param am
     */
    private void timeChange(String hour, int am) {
        switch (am) {
            case TODAY_AM:
            case TODAY_PM:
                changeTimeForToday(hour, am);
                break;
            case LONG_AM:
            case LONG_PM:
                setCommonTime(am);
                break;
        }
    }

    private void changeTimeForToday(String hour, int am) {
        //当前分钟
        String nowHour = mNowTime.split(":")[0];
        if (null != hour && hour.equals(nowHour)) {
            String nowMinute = mNowTime.split(":")[1];
            List<String> tempMinuteList = new ArrayList<>();
            tempMinuteList.clear();
            //当前时间对所有时间的筛选
            List<String> minuteList = Arrays.asList(MINUTE);
            int tempMinuteListSize = minuteList.size();
            for (int i = 0; i < tempMinuteListSize; i++) {
                if (Integer.parseInt(nowMinute) <= Integer.parseInt(minuteList.get(i))) {
                    tempMinuteList.add(minuteList.get(i));
                }
            }
            //填充选中的时间数据
            mMinuteList.clear();
            if (tempMinuteList.size() > 0) {
                mMinuteList.addAll(tempMinuteList);
                mPickerViewMinute.setData(mMinuteList, mChooseType, am);
                this.mChooseMinute = mMinuteList.get(0);
            } else {
                mMinuteList.addAll(minuteList);
                mPickerViewMinute.setData(mMinuteList, mChooseType, am);
                this.mChooseMinute = mMinuteList.get(0);
            }
        } else {
            setCommonTime(am);
        }
    }
    private void setCommonTime(int am){
        //如果设置分钟列表没有包含默认分钟列表
        if (!mMinuteList.containsAll(Arrays.asList(MINUTE))) {
            mMinuteList.clear();
            mMinuteList.addAll(Arrays.asList(MINUTE));
            mPickerViewMinute.setData(mMinuteList, mChooseType, am);
            this.mChooseMinute = mMinuteList.get(0);
        }
    }
    @Override
    public void dismiss() {
        super.dismiss();
        if (mHourList.size() > 0) {
            mHourList.clear();
        }
        if (mMinuteList.size() > 0) {
            mMinuteList.clear();
        }
        this.mChooseMinute = "00";
        this.mChooseHour = "00";
    }

    private OnTimeChangeListener mListener;

    public void setOnTimeChangeListener(OnTimeChangeListener listener) {
        this.mListener = listener;
    }

    public interface OnTimeChangeListener {
        void onChange(int type, String content);
    }

}
