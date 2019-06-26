package com.cnpeng.piclib.tools;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * author：luck
 * project：PictureSelector
 * email：893855882@qq.com
 * data：2017/5/25
 */

public class DateUtils {
    private static final String TAG = "DateUtils";

    /**
     * MS turn every minute
     *
     * @param duration Millisecond
     * @return Every minute
     */
    public static String timeParse(long duration) {
//        Log.e(TAG, "接收到的视频时长" + duration);
        String time = "";
        if (duration > 1000) {
            time = timeParseMinute(duration);
        } else {
            long minute = duration / 60000;
            long seconds = duration % 60000;
            long second = Math.round((float) seconds / 1000);
            if (minute < 10) {
                time += "0";
            }
            time += minute + ":";
            if (second < 10) {
                time += "0";
            }
            time += second;
        }
//        Log.e(TAG, "转换后的视频时长" + time);

        return time;
    }

    /**
     * MS turn every minute
     *
     * @param duration Millisecond
     * @return Every minute
     *
     *         CnPeng 2018/9/5 下午5:28 增加是否超过一小时的判断
     */
    public static String timeParseMinute(long duration) {
        try {
            SimpleDateFormat msFormat = null;
            long hourDuration = 1000 * 60 * 60;
            if (duration < hourDuration) {
                //如果没有超过小时
                msFormat = new SimpleDateFormat("mm:ss");
            } else {
                //如果超过了小时
                msFormat = new SimpleDateFormat("HH:mm:ss");
                // 设置格式化器的时区为格林威治时区，否则格式化的结果不对，中国的时间比格林威治时间早8小时，比如0点会被格式化为8:00
                msFormat.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
            }
            return msFormat.format(duration);
        } catch (Exception e) {
            e.printStackTrace();
            return "00:00";
        }
    }

    /**
     * 判断两个时间戳相差多少秒
     */
    public static int dateDiffer(long d) {
        try {
            long l1 = Long.parseLong(String.valueOf(System.currentTimeMillis()).substring(0, 10));
            long interval = l1 - d;
            return (int) Math.abs(interval);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 计算两个时间间隔
     */
    public static String cdTime(long sTime, long eTime) {
        long diff = eTime - sTime;
        return diff > 1000 ? diff / 1000 + "秒" : diff + "毫秒";
    }
}
