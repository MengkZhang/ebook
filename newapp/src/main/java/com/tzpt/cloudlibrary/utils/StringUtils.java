package com.tzpt.cloudlibrary.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.google.gson.Gson;
import com.tzpt.cloudlibrary.CloudLibraryApplication;
import com.tzpt.cloudlibrary.R;
import com.tzpt.cloudlibrary.modle.remote.pojo.LibraryOpenTimeVo;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZhiqiangJia on 2017-08-04.
 */
public class StringUtils {

    /**
     * m换算成km
     *
     * @param dist
     * @return
     */
    public static String mToKm(int dist) {
        /*StringBuilder distanceBuffer = new StringBuilder();
        if (dist > 1000) {
            double distance = Math.round(dist / 100d) / 10d;
            return distanceBuffer.append(distance).append("km").toString();
        } else {
            return distanceBuffer.append(dist).append("m").toString();
        }*/
        StringBuilder distanceBuffer = new StringBuilder();             //小于1KM时，显示实际距离，单位为M；例 29M
        if ((dist >= 0) && (dist <= 1000)) {
            return distanceBuffer.append(dist).append("m").toString();
        } else if ((dist > 1000) && (dist <= 100000)) {                 //大于1KM小于100KM时，四舍五入 保留一位小数，单位为KM；例29.7KM
            double distance = Math.round(dist / 100d) / 10d;
            return distanceBuffer.append(distance).append("km").toString();
        } else if ((dist > 100000) && (dist <= 9999000)) {              // 大于100KM时，小于9999KM时，四舍五入显示整数距离；例3400KM
            double distance = Math.round(dist / 100d) / 10d;
            int dis = (int) distance;
            return distanceBuffer.append(dis).append("km").toString();
        } else if (dist > 9999000) {                                    //超出9999KM后，均按9999KM显示
            double distance = 9999;
            return distanceBuffer.append(distance).append("km").toString();
        } else {
            double distance = 9999;
            return distanceBuffer.append(distance).append("km").toString();
        }
    }

    //格式化电子书简介详情
    public static String formatStringForEBook(String content) {
        if (content.contains("<p>")) {
            String text = content.replaceAll("<p>", "<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>").replaceAll("</p>", "<br/>").replaceAll("<br/><br/>", "<br/>").trim();
            return text.replace("<br/></html>", "</html>").replace("<html><br/>", "<html>");
        } else {
            return "<html><span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>" + content + "</html>";
        }
    }

    //格式化图书简介详情
    public static String formatString(String content) {
        StringBuffer bufferMenu = new StringBuffer();
        bufferMenu.append("<html>")
                .append("<body>")
                .append(content)
                .append("</body>")
                .append("</html>");
        String text = bufferMenu.toString()
                .replaceAll("<p>", "<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>")
                .replaceAll("</p>", "<br/>")
                .replaceAll("\n", "<br/>")
                .replaceAll("<br/><br/>", "<br/>").trim();
        return text.replace("<br/></html>", "</html>")
                .replace("<body><br/>", "<body>")
                .replace("<br/></body>", "</body>")
                .replace("<html><br/>", "<html>");
    }

    /**
     * 判断是否数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (null == str) {
            return false;
        }
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否是男士
     *
     * @param idCard 身份证号码
     * @return
     */
    public static boolean isMan(String idCard) {
        if (null == idCard || TextUtils.isEmpty(idCard)) {
            return true;
        }
        String sexNumber = idCard.substring(16, 17);
        if (isNumeric(sexNumber)) {
            int userSexNumber = Integer.parseInt(sexNumber);
            return userSexNumber % 2 != 0;
        }
        return true;
    }


    public static String convertStorageNoB(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1fGB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0fMB" : "%.1fMB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0fKB" : "%.1fKB", f);
        } else
            return String.format("%dB", size);
    }


    /**
     * 从url中截取文件名
     */
    public static String clipFileName(String url) {
        int index = url.lastIndexOf("/");
        if (index != -1) {
            return url.substring(index + 1);
        }
        return null;
    }

    /**
     * 格式化人气热度
     * 若人气小于99999人时，显示实际数字，当人气大于100000时，显示100000+人
     * @param heatCount 人气热度
     * @return
     */
    public static String formatHeatCount(int heatCount) {
        if (heatCount >= 0 && heatCount <= 99999) {
            return String.valueOf(heatCount);
        } else if (heatCount >= 100000) {
            return "100000+";
        }
        return "100000+";
    }

    /**
     * 格式化藏书量
     * 当藏书量小于999999时，显示实际藏书数字，例：；当藏书量大于1000000时显示：1000000+册
     * @param bookCount 藏书量
     * @return
     */
    public static String formatBookCount(int bookCount) {
        if (bookCount >= 0 && bookCount <= 999999) {
            return String.valueOf(bookCount);
        } else if (bookCount >= 1000000) {
            return "1000000+";
        }
        return "1000000+";
    }

    /**
     * 格式化借阅人数
     *
     * @param borrowCount 借阅人数
     * @return
     */
    public static String formatBorrowCount(int borrowCount) {
        if (borrowCount < 10000) {
            return String.valueOf(borrowCount);
        }
        StringBuilder borrowCountBuffer = new StringBuilder();
        double resultCount = Math.round(borrowCount / 100d) / 100d;
        String newCount = new DecimalFormat("#.00").format(resultCount);
        return borrowCountBuffer.append(newCount).append("万").toString();
    }

    /**
     * 格式化数字，过万显示为万+
     *
     * @param count
     * @return
     */
    public static String formatCountPlus(int count) {
        if (count < 10000) {
            return String.valueOf(count);
        }
        StringBuilder borrowCountBuffer = new StringBuilder();
        double resultCount = Math.round(count / 100d) / 100d;
        String newCount = new DecimalFormat("#.00").format(resultCount);
        return borrowCountBuffer.append(newCount).append("万+").toString();
    }

    /**
     * 验证昵称
     *
     * @param nickName
     * @return
     */
    public static boolean isVerifyNickName(String nickName) {
        String regEx = "^[\\u4e00-\\u9fa5a-zA-Z0-9-_]+$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(nickName);
        return matcher.matches();
    }

    /**
     * 格式化姓名为昵称
     *
     * @param level     级别
     * @param replyType 回复类型
     * @param cardName  名称
     * @param isMan     是否男性
     * @return
     */
    public static String formatNickName(int level, int replyType, String cardName, boolean isMan) {
        if (TextUtils.isEmpty(cardName)) {
            return "用户名";
        }
        if (level == 0) {
            if (replyType == 3 || replyType == 4) { //如果是平台，则返回全名
                return cardName;
            }
        } else {
            if (replyType == 1) {                   //1:被回复的是平台 2:被回复的是读者
                return cardName;
            }
        }
        String name;
        if (cardName.length() >= 2) {
            List<String> surnameList = Arrays.asList(CloudLibraryApplication.getAppContext()
                    .getResources().getStringArray(R.array.compound_surnames));
            String lastName = cardName.substring(0, 2);
            if (surnameList.contains(lastName)) {
                name = lastName + (isMan ? "先生" : "女士");
            } else {
                name = setOneLastName(cardName, isMan);
            }
        } else {
            name = setOneLastName(cardName, isMan);
        }
        return name;
    }

    /**
     * 格式化读者姓名为昵称
     *
     * @param readerName 姓名
     * @param gender     性别
     * @return
     */
    public static String formatReaderNickName(String readerName, int gender) {
        if (null == readerName) {
            return "";
        }
        String title;
        if (readerName.length() >= 2) {
            //如果有复姓
            List<String> surnameList = Arrays.asList(CloudLibraryApplication.getAppContext().getResources().getStringArray(R.array.compound_surnames));
            String lastName = readerName.substring(0, 2);
            if (surnameList.contains(lastName)) {
                title = lastName + (gender == 1 ? "先生" : "女士");
            } else {
                //设置单姓
                title = setOneLastName(readerName, gender == 1);
            }
        } else {
            //设置单姓
            title = setOneLastName(readerName, gender == 1);
        }
        return title;
    }

    /**
     * 设置单姓
     *
     * @param userName 名称
     * @param isMan    身份证号
     * @return 单姓称谓
     */
    public static String setOneLastName(String userName, boolean isMan) {
        //获取姓氏及性别,设置先生女士-暂时取单字
        String newName = userName.substring(0, 1);
        return newName + (isMan ? "先生" : "女士");
    }

    /**
     * 格式化读者名称
     *
     * @param readerName
     * @return
     */
    public static SpannableString formatCommentTitle(String readerName) {
        SpannableString spannableReaderTitle = new SpannableString(readerName);
        spannableReaderTitle.setSpan(new ForegroundColorSpan(Color.parseColor("#3972be")), 0, readerName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableReaderTitle;
    }

    /**
     * 格式化读者回复信息
     *
     * @param replyName   回复读者名称
     * @param repliedName 被回复读者名称
     * @return
     */
    public static SpannableString formatCommentTitle(String replyName, String repliedName) {
        //	0:被回复的是评论本身
        String replyStr = replyName + (TextUtils.isEmpty(repliedName) ? "" : "回复" + repliedName);
        SpannableString spannableReaderTitle = new SpannableString(replyStr);
        spannableReaderTitle.setSpan(new ForegroundColorSpan(Color.parseColor("#3972be")), 0, replyName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (!TextUtils.isEmpty(repliedName)) {
            spannableReaderTitle.setSpan(new ForegroundColorSpan(Color.parseColor("#3972be")), replyName.length() + 2, replyName.length() + 2 + repliedName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableReaderTitle;
    }

    /**
     * 格式化读者回复信息 -评论带内容
     *
     * @param replyName   回复者名称
     * @param repliedName 被回复者名称
     * @param content     回复内容
     * @return
     */
    public static SpannableString formatCommentTitleContent(String replyName, String repliedName, String content) {
        String replyContentStr = replyName + (TextUtils.isEmpty(repliedName) ? "" : "回复" + repliedName) + ":" + content;
        SpannableString spannableContentString = new SpannableString(replyContentStr);
        spannableContentString.setSpan(new ForegroundColorSpan(Color.parseColor("#3972be")), 0, replyName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (!TextUtils.isEmpty(repliedName)) {
            spannableContentString.setSpan(new ForegroundColorSpan(Color.parseColor("#3972be")), replyName.length() + 2, replyName.length() + 2 + repliedName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableContentString;
    }

    public static SpannableString formatCommentChildContent(String replyName, String repliedName, String replyContent) {
        String replyStr = replyName + (TextUtils.isEmpty(repliedName) ? "" : "回复" + repliedName) + ": " + replyContent;
        SpannableString spannableString = new SpannableString(replyStr);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#3972be")), 0, replyName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (!TextUtils.isEmpty(repliedName)) {
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#3972be")), replyName.length() + 2, replyName.length() + 1 + repliedName.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    public static String formatTel(String tel) {
        if (!TextUtils.isEmpty(tel)) {
            String replaceStr = tel.substring(3, 9);
            return tel.replace(replaceStr, "******");
        }

        return tel;
    }

    /**
     * 是否含有emoji表情
     *
     * @param content 内容
     * @return true表示有；false表示没有
     */
    public static boolean isHaveEmoji(String content) {
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher m = emoji.matcher(content);
        return m.find();
    }


    /**
     * 判断图书馆是否开放
     *
     * @param lightTime 开放时间
     * @param time      当前时间
     * @return 图书馆是否开放
     */
    public static boolean libraryIsOpen(String lightTime, String time) {
        boolean isOpen = false;
        if (null != lightTime) {
            LibraryOpenTimeVo mLibraryOpenTimeVo = new Gson().fromJson(lightTime, LibraryOpenTimeVo.class);
            if (null != mLibraryOpenTimeVo) {
                LibraryOpenTimeVo.DayTime mDayTime = mLibraryOpenTimeVo.dayTime;
                LibraryOpenTimeVo.AM am = mDayTime.am;
                LibraryOpenTimeVo.PM pm = mDayTime.pm;
                if (null != time && !TextUtils.isEmpty(time)) {
                    boolean isAm = DateUtils.timeIsAM(time);
                    StringBuilder builder = new StringBuilder();
                    if (isAm) {
                        builder.append(am.begin).append("-").append(am.end);
                    } else {
                        builder.append(pm.begin).append("-").append(pm.end);
                    }
                    String sourceDate = builder.toString();
                    //判断系统当前时间是否在指定时间范围内
                    boolean isLight = DateUtils.isInTime(sourceDate, DateUtils.formatTime(time));
                    if (isLight) {
                        isOpen = true;
                    } else {
                        isOpen = false;
                    }
                } else {
                    isOpen = false;
                }
            } else {
                isOpen = false;
            }
        }
        return isOpen;
    }
}
