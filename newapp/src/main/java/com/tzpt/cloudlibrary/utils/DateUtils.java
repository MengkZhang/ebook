package com.tzpt.cloudlibrary.utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具
 * Created by ZhiqiangJia on 2017-08-17.
 */
public class DateUtils {

    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    public static String formatDate(String time) {
        if (null == time) return "";
        try {
            long newTime = Long.parseLong(time);
            Date d = new Date(newTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm");
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
        if (0 == time) return "";
        try {
            Date d = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm");
            return sdf.format(d);

        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 格式化时间精确到秒
     * @param time
     * @return
     */
    public static String formatDateSureToSecond(long time) {
        if (0 == time) return "";
        try {
            Date d = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(d);

        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 计算两段格式化后的时间相差多少小时
     * @param startTime
     * @param endTime
     * @return
     */
    public static long betweenTwoTimes(String startTime,String endTime) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = df.parse(startTime);
            Date d2 = df.parse(endTime);
            long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
            System.out.println(""+days+"天"+hours+"小时"+minutes+"分");
            return hours;
        } catch (Exception e) {
            return 0;
        }
    }

    public static String formatNewsDate(long time) {
        try {
            Date d = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.format(d);

        } catch (Exception e) {
            return "";
        }
    }

    public static String formatNowDate(long time) {
        try {
            Date d = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            return sdf.format(d);

        } catch (Exception e) {
            return "";
        }
    }

    /***
     * 获取几天之后的时间
     *
     * @param days
     * @param day
     * @return
     * @throws Exception
     */
    public static String getDateAfter(String days, int day) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date daysMinites = sdf.parse(days);
            Calendar now = Calendar.getInstance();
            now.setTime(daysMinites);
            now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
            String afterDay = sdf.format(now.getTime());
            return afterDay;
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * 判断是否已逾期
     *
     * @return
     */
    public static boolean isWithinTimeLimit(String afterDays) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date afterDate = sdf.parse(afterDays);
            long afterDateTime = afterDate.getTime();
            String times = sdf.format(new Date());
            Date date = sdf.parse(times);
            long nowTime = date.getTime();
            return afterDateTime < nowTime;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断还书日期与今天
     *
     * @param afterAay
     * @return
     * @throws Exception
     */
    public static int compareAfterTimeAboutToday(String afterAay) {
        if (TextUtils.isEmpty(afterAay)) {
            return -1;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date afterDate = sdf.parse(afterAay);
            double nowTime = (double) new Date().getTime();
            double intervalMilli = (double) afterDate.getTime() - nowTime;
            double sss = (intervalMilli / (24 * 60 * 60 * 1000));
            return (int) Math.ceil(sss);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 格式化时间-图书馆列表时间
     *
     * @param time
     * @return
     */
    public static String formatTime(String time) {
        if (null == time || TextUtils.isEmpty(time)) {
            return "";
        }
        try {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateTime = sd.parse(time);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");//2016-11-24 16:40:54
            return sdf.format(dateTime);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 时间是否上午
     *
     * @param time
     * @return
     */
    public static boolean timeIsAM(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = sdf.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int AM = calendar.get(Calendar.AM_PM);
            return (AM == 0);
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * 判断某一时间是否在一个区间内
     *
     * @param sourceTime 时间区间,半闭合,如[10:00-20:00)
     * @param curTime    需要判断的时间 如10:00
     * @return
     * @throws
     */
    public static boolean isInTime(String sourceTime, String curTime) {
        if (sourceTime == null || !sourceTime.contains("-") || !sourceTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }
        if (curTime == null || !curTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + curTime);
        }
        String[] args = sourceTime.split("-");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            long now = sdf.parse(curTime).getTime();
            long start = sdf.parse(args[0]).getTime();
            long end = sdf.parse(args[1]).getTime();
            if (args[1].equals("00:00")) {
                args[1] = "24:00";
            }
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
     * 获取今天日期
     *
     * @return
     */
    public static String getTodayDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String today = sdf.format(new Date());
        if (null == today) {
            return null;
        }
        return today;
    }

    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;

    private static final String ONE_SECOND_AGO = "秒前";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String ONE_HOUR_AGO = "小时前";
    private static final String ONE_DAY_AGO = "天前";

    public static String format(long endTime, long startTime) {
        long delta = endTime - startTime;
        if (delta < ONE_MINUTE) {
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        }
        if (delta < 60L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }
        return formatDate(startTime);
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    /**
     * 格式化资讯时间
     *
     * @param currentTime 当前时间
     * @param startTime   创建时间
     * @return
     */
    public static String formatNewsTime(long currentTime, long startTime) {
        long delta = currentTime - startTime;
        if (delta < 60L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        return formatNewsDate(startTime);
    }

    /**
     * 判断当前时间是否在15天内（包含15天）
     *
     * @param storageTime
     * @return
     */
    public static boolean isWithinTimeLimit30Days(String storageTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date createDate = sdf.parse(storageTime);
            long createDateTime = createDate.getTime();
            String times = sdf.format(new Date());
            Date date = sdf.parse(times);
            long nowTime = date.getTime();

            long limitSeconds = Math.abs(ONE_DAY * 30);
            return (nowTime - createDateTime) < limitSeconds;
        } catch (Exception e) {
            return false;
        }
    }

}
