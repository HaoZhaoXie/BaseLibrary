/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: TimeUtils
 * Author: 星河
 * Date: 2021/3/28 20:23
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.cangjiedata.baselibrary.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName: TimeUtils
 * @Description:
 * @Author: 星河
 * @Date: 2021/3/28 20:23
 */
public class TimeUtils {

    //转换时间
    public static String getCountTimeByLong(long finishTime) {
        int totalTime = (int) (finishTime / 1000);//秒
        int hour = 0, minute = 0, second = 0;
        if (3600 <= totalTime) {
            hour = totalTime / 3600;
            totalTime = totalTime - 3600 * hour;
        }
        if (60 <= totalTime) {
            minute = totalTime / 60;
            totalTime = totalTime - 60 * minute;
        }
        if (0 <= totalTime) {
            second = totalTime;
        }
        StringBuilder sb = new StringBuilder();
        if (hour > 0) {
            if (hour < 10) {
                sb.append("0").append(hour).append("小时");
            } else {
                sb.append(hour).append("小时");
            }
        }
        if (minute > 0) {
            if (minute < 10) {
                sb.append("0").append(minute).append("分");
            } else {
                sb.append(minute).append("分");
            }
        }
        if (second >= 0) {
            if (second < 10) {
                sb.append("0").append(second).append("秒");
            } else {
                sb.append(second).append("秒");
            }
        }
        return sb.toString();
    }

    public static String allTime2YMD(String dateFormat) {
        if (TextUtils.isEmpty(dateFormat)) {
            return "";
        }

        String res = "";
        try {
            String time = dateToStamp(dateFormat);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            long lt = Long.parseLong(time);
            Date date = new Date(lt);
            res = simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String allTime2MD(String dateFormat) {
        if (TextUtils.isEmpty(dateFormat)) {
            return "";
        }

        String res = "";
        try {
            String time = dateToStamp(dateFormat);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日");
            long lt = Long.parseLong(time);
            Date date = new Date(lt);
            res = simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String allTime2HMS(String dateFormat) {
        if (TextUtils.isEmpty(dateFormat)) {
            return "";
        }

        String res = "";
        try {
            String time = dateToStamp(dateFormat);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            long lt = Long.parseLong(time);
            Date date = new Date(lt);
            res = simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    public static String dateToYMDHM(String dateFormat) throws ParseException {
        if (TextUtils.isEmpty(dateFormat)) {
            return "";
        }

        String res = "";
        try {
            String time = dateToStamp(dateFormat);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            long lt = Long.parseLong(time);
            Date date = new Date(lt);
            res = simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }
    public static String stampToTime(Long stamp) {
        String sd = "";
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sd = sdf.format(new Date(stamp)); // 时间戳转换日期
        return sd;
    }

}