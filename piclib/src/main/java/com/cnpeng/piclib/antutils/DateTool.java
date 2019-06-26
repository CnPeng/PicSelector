package com.cnpeng.piclib.antutils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTool {
    public static final String DEF_DATETIME_FORMAT     = "yyyy-MM-dd HH:mm";
    public static final String GET_DEF_DATETIME_FORMAT = "yyyyMMddHHmm";

    /**
     * 获取日期
     */
    public static String getDate() {//获取日期
        return new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
    }

    public static String dateFormat(String date, String desc) {
        String time = "";
        if (date.length() > 11) {
            time = new StringBuilder().append(date.substring(0, 4)).append(".").append(date.substring(4, 6)).append
                    ("" + ".").append(date.substring(6, 8)).append(" ").append(date.substring(8, 10)).append(":")
                    .append(date.substring(10, 12)).append(desc).toString();
            return time;
        } else {
            return date;
        }
    }

    public static String dateYearMonth(String date) {
        String time = "";
        if (date != null && date.length() == 14) {
            time = new StringBuilder().append(date.substring(0, 4)).append("年").append(date.substring(4, 6)).append
                    ("月").toString();
            return time;
        } else {
            return "";
        }
    }

    public static String dateFormat14(String date) {//组织14位的时间格式
        String time = "";
        if (date != null && date.length() > 13) {
            time = new StringBuilder().append(date.substring(0, 4)).append(".").append(date.substring(4, 6)).append
                    ("" + ".").append(date.substring(6, 8)).append(" ").append(date.substring(8, 10)).append(":")
                    .append(date.substring(10, 12)).append(":").append(date.substring(12, 14)).toString();
            return time;
        } else {
            return date == null ? "" : date;
        }
    }

    public static String actReplyDateFormat(String data) {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        DateFormat df1 = new SimpleDateFormat("MM/dd HH:mm");
        DateFormat df2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date = null;
        try {
            date = df.parse(data);

            int year = Integer.parseInt(data.substring(0, 4));
            int nowYear = Calendar.getInstance().get(Calendar.YEAR);
            if (nowYear > year) {
                return df2.format(date);
            } else {
                return df1.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String dateFormatToShow(String date) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        //用来显示数据库里的时间 并且进行修改，显示今天 昨天 等等
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        DateFormat df2 = new SimpleDateFormat("HH:mm");
        DateFormat dfGetDay = new SimpleDateFormat("yyyyMMdd");
        Date d1, d2, day;
        try {
            day = dfGetDay.parse(date.substring(0, 8));
            //数据库里的时间
            d1 = df.parse(date);
            //获得当前时间
            d2 = dfGetDay.parse(getDate().substring(0, 8));
        } catch (ParseException e) {
            return "";
        }
        long diff = (d2.getTime() - day.getTime()) / (1000 * 60 * 60 * 24);
        String d1year = new SimpleDateFormat("yyyy").format(d1);
        String d2year = new SimpleDateFormat("yyyy").format(d2);
        String time = "";
        if ((!d1year.equals(d2year)) || (diff < 0)) {
            time = new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(d1);
        } else if (diff == 0) {
            time = df2.format(d1);
        } else if (diff == 1) {
            time = "昨天 " + df2.format(d1);
        } else if (diff == 2) {
            time = "前天 " + df2.format(d1);
        } else {
            time = new SimpleDateFormat("MM月dd日 HH:mm").format(d1);
        }
        return time;
    }

    /**
     * 返回 XXXX年XX月XX日 XX:XX 的格式化日期
     */
    public static String dateFormatToShow_yearMonthDayHourMinute(String date) {
        return dateFormatToShow_yearMonthDayHourMinute(date, false);
    }

    public static String dateFormatToShow_yearMonthDayHourMinute(String date, boolean showSecond) {

        if (TextUtils.isEmpty(date)) {
            return "";
        }
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date d1;
        try {
            d1 = df.parse(date);
        } catch (ParseException e) {
            return "";
        }

        if (showSecond) {
            return new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(d1);
        } else {
            return new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(d1);
        }
    }

    public static boolean getIsOneDay(String signTime) {
        if (TextUtils.isEmpty(signTime)) {
            return false;
        }
        //用来显示数据库里的时间 并且进行修改，显示今天 昨天 等等
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        DateFormat dfGetDay = new SimpleDateFormat("yyyyMMdd");
        Date d1, d2, day;
        String curDate = getDate();
        try {
            day = dfGetDay.parse(signTime.substring(0, 8));
            d1 = df.parse(signTime);
            d2 = dfGetDay.parse(curDate.substring(0, 8));
        } catch (ParseException e) {
            return true;
        }
        long diff = (d2.getTime() - day.getTime()) / (1000 * 60 * 60 * 24);
        String d1year = new SimpleDateFormat("yyyy").format(d1);
        String d2year = new SimpleDateFormat("yyyy").format(d2);
        if (d1year.equals(d2year) && diff == 0) {
            return true;
        }
        return false;
    }

    public static String noticeDateFormat(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }

        StringBuffer stringBuffer = new StringBuffer();
        try {
            String systime = DateTool.getDate();
            String sysdate = systime.substring(6, 8);
            String date = time.substring(6, 8);
            if (date.equals(sysdate)) {
                // 日期是同一天
                stringBuffer.append("今天").append("  ").append(time.substring(8, 10)).append(":").append(time
                        .substring(10, 12));
            } else if (Integer.valueOf(sysdate) - (Integer.valueOf(date)) == 1) {
                // 日期是昨天
                stringBuffer.append("昨天").append("  ").append(time.substring(8, 10)).append(":").append(time
                        .substring(10, 12));
            } else {
                stringBuffer.append(time.substring(4, 6)).append("-").append(time.substring(6, 8)).append("  ")
                        .append(time.substring(8, 10)).append(":").append(time.substring(10, 12));
            }
        } catch (Exception e) {
        }
        return stringBuffer.toString();
    }

    public static String surveyDateFormat(String time) {
        StringBuffer stringBuffer = new StringBuffer();
        if (time != null) {
            stringBuffer.append("截止时间:").append(time.substring(4, 6)).append("-").append(time.substring(6, 8)).append
                    ("  ").append(time.substring(8, 10)).append(":").append(time.substring(10, 12));
        }
        return stringBuffer.toString();
    }

    /**
     * 得到当前时间的延长时间
     */
    public static String getExtendDateString(String timeString, long extendTime) {
        DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        try {
            Date tempDate = fmt.parse(timeString);
            date.setTime(tempDate.getTime() + extendTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String dateString = fmt.format(date);

        return dateString;
    }


    /**
     * 功用：根据指定的正则格式，格式化时间
     * 说明：
     *
     * @param time    时间字符串
     * @param pattern 时间格式
     */
    public static String getFormatDate(String time, String pattern) {

        if (TextUtils.isEmpty(time)) {
            return "";
        }

        if (time.length() > 12) {
            time = time.substring(0, 12);
        }
        DateFormat df = new SimpleDateFormat(GET_DEF_DATETIME_FORMAT, Locale.CHINA);
        Date date = null;
        try {
            date = df.parse(time);
            DateFormat dd = new SimpleDateFormat(pattern, Locale.CHINA);
            return dd.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCurDay() {//获取当前时间最晚的时间
        return new SimpleDateFormat("yyyyMMdd000000").format(Calendar.getInstance().getTime());
    }

    //待办事项的时间显示
    public static String todoDate(String pubTime, String endTime) {
        String defaultString = "表丢了，不知道时间";
        if (pubTime == null || "".equals(pubTime) || pubTime.length() < 14) {
            return defaultString;
        }
        if (endTime != null && endTime.length() < 14 && endTime.length() > 0) {
            return defaultString;
        }
        DateFormat dfGetDay = new SimpleDateFormat("yyyyMMdd");
        DateFormat mMddGetDay = new SimpleDateFormat("MM月dd日");
        DateFormat allDay = new SimpleDateFormat("yyyyMMddHHmmss");
        DateFormat mMddHHmmGetDay = new SimpleDateFormat("MM月dd日 HH:mm");
        Date endTimeDay, curDay, pubTimeDay;
        try {
            pubTimeDay = dfGetDay.parse(pubTime.substring(0, 8));
            curDay = dfGetDay.parse(getDate().substring(0, 8));//获得当前时间
        } catch (ParseException e) {
            return defaultString;
        }
        if (TextUtils.isEmpty(endTime)) {
            long diff = (curDay.getTime() - pubTimeDay.getTime()) / (1000 * 60 * 60 * 24);
            if ((diff < 0)) {
                return defaultString;
            } else {
                return "已提醒" + (diff + 1) + "天";//需要加上当天
            }
        } else {
            try {
                endTimeDay = dfGetDay.parse(endTime.substring(0, 8));
                if (pubTimeDay.equals(endTimeDay)) {
                    Date time = allDay.parse(pubTime);
                    if (endTime.endsWith("000000")) {
                        return "今日(" + mMddGetDay.format(time) + ")";
                    } else {
                        return "今日(" + mMddHHmmGetDay.format(time) + ")";
                    }
                } else {
                    long endDiff = (endTimeDay.getTime() - curDay.getTime()) / (1000 * 60 * 60 * 24);
                    if ((endDiff < 0)) {
                        return defaultString;
                    } else {
                        Date time = allDay.parse(endTime);
                        endDiff += 1;
                        if (endTime.endsWith("000000")) {
                            return endDiff + "天后到期(" + mMddGetDay.format(time) + ")";
                        } else {
                            return endDiff + "天后到期(" + mMddHHmmGetDay.format(time) + ")";
                        }
                    }
                }
            } catch (ParseException e) {
                return defaultString;
            }
        }
    }


    /**
     * CnPeng 2018/11/13 3:14 PM
     * 功用：判断指定日期是否为今天
     * 说明：注意：5.0系统中没有LocalDate类，会报错，所以，暂时放弃使用该方法，后期不再兼容5.0时可以直接使用. API 26 才增加LocalDate
     *
     * @param specifiedTimeStr 指定的日期。格式必须为： 2018-11-13，否则无法解析
     */
    public static boolean isCurDay(String specifiedTimeStr) {
        if (TextUtils.isEmpty(specifiedTimeStr)) {
            return false;
        }

        //CnPeng 2018/11/13 3:03 PM 当前日期，如 2018-11-13
        LocalDate today = LocalDate.now();
        LocalDate specifiedDate = LocalDate.parse(specifiedTimeStr);

        return 0L == specifiedDate.until(today, ChronoUnit.DAYS);
    }
}
