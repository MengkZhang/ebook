package com.tzpt.cloundlibrary.manager.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/6/27.
 */

public class DateUtils {
    /**
     * 时间是否上午
     *
     * @param time
     * @return
     */
    public static boolean TimeIsAM(long time) {
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int AM = calendar.get(Calendar.AM_PM);
        return (AM == 0);
    }


    /**
     * 判断某一时间是否在一个区间内
     *
     * @param startTime 起始时间
     * @param endTime   结束事件
     * @param curTime   需要判断的时间 如10:00
     * @return
     * @throws
     */
    public static boolean isInTime(String startTime, String endTime, String curTime) {
        if (startTime == null || !startTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + curTime);
        }
        if (endTime == null || !endTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + curTime);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            long now = sdf.parse(curTime).getTime();
            long start = sdf.parse(startTime).getTime();
            long end = sdf.parse(endTime).getTime();

            if (end < start) {
                if (now >= end && now < start) {
                    return false;
                } else {
                    return true;
                }
            } else {
                if (now >= start && now < end) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    public static String formatTimeToHHmm(long time) {
        try {
            Date d = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(d);
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    public static String formatDate(long time) {
        try {
            Date d = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.format(d);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    public static String formatDateToYYMMDDHHMM(long time) {
        try {
            Date d = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.format(d);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 格式化日期
     *
     * @param time
     * @return
     */
    public static String formatToDate(String time) {
        if (null == time || TextUtils.isEmpty(time)) {
            return "";
        }
        try {
            long newTime = Long.parseLong(time);
            Date d = new Date(newTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            return sdf.format(d);

        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    public static String formatToDate2(String time) {
        if (null == time || TextUtils.isEmpty(time)) {
            return "";
        }
        try {
            long newTime = Long.parseLong(time);
            Date d = new Date(newTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.format(d);

        } catch (Exception e) {
            return time;
        }
    }

    /**
     * 比较两个时间大小
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean compareStartTimeAndEndTime(String startTime, String endTime) {
        if (null == startTime || null == endTime) {
            return false;
        }
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        try {
            Date dt1 = df.parse(startTime);
            Date dt2 = df.parse(endTime);
            return dt1.getTime() < dt2.getTime();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    //判断时间是否大于当前时间
    public static boolean compareEndDateThanTodayDate(String endDate) {
        if (null == endDate) {
            return false;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm");
        try {
            Date dt1 = df.parse(endDate);
            Date dt2 = new Date();
            return dt1.getTime() > dt2.getTime();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * 是否今日修改数据
     *
     * @param lastChangeDate 日期 "date":"2017-11-28 15:40
     * @return
     */
    public static boolean isShouldChangeDate(String lastChangeDate, long nowTime) {
        try {
            if (!TextUtils.isEmpty(lastChangeDate) && lastChangeDate.contains(" ")) {
                String dates[] = lastChangeDate.split(" ");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String todayDate = df.format(nowTime);
                return todayDate.equals(dates[0]);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    //设置是否使用
    public static boolean isShowUseLongTimeForDayTime(Integer[] week) {
        //当前时间没有设置
        if (null != week && week.length > 0) {
            List<Integer> weekList = Arrays.asList(week);
            //判断今日是星期几
            if (weekList.contains(getWeekDay())) {
                return true;
            }
        }
        return false;
    }

    /*获取星期几*/
    private static int getWeekDay() {
        Calendar cal = Calendar.getInstance();
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);
        switch (weekDay) {
            case Calendar.SUNDAY:
                return 7;
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
        }
        return 1;
    }
}
