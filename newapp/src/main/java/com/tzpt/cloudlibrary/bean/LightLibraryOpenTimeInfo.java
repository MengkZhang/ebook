package com.tzpt.cloudlibrary.bean;

import java.io.Serializable;

/**
 * 点亮书屋信息
 * Created by ZhiqiangJia on 2016-11-23.
 */
public class LightLibraryOpenTimeInfo implements Serializable {

    public Integer[] week;
    public DayTime dayTime;
    public WeekTime weekTime;

    /**
     * 今日开放时间
     */
    public class DayTime implements Serializable {
        public AM am;
        public PM pm;
        public String date;
    }

    /**
     * 周期开放时间
     */
    public class WeekTime implements Serializable {
        public AM am;
        public PM pm;
    }

    /**
     * am
     */
    public class AM implements Serializable {
        public String begin;
        public String end;
    }

    /**
     * pm
     */
    public class PM implements Serializable {
        public String begin;
        public String end;
    }
}
