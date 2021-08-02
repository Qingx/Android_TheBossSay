package cn.wl.android.lib.utils;

import com.blankj.utilcode.util.TimeUtils;

import java.util.Date;

import retrofit2.http.PUT;

/**
 * Created by JustBlue on 2019-09-03.
 *
 * @email: bo.li@cdxzhi.com
 * @desc: 时间格式
 */
public class DateFormat {

    public static final String DATEFORMAT_yyyy_MM_dd_HHmmss = "yyyy年MM月dd日 HH:mm:ss";
    public static final String DATEFORMAT_HHmmss = "HH:mm:ss";
    public static final String DATEFORMAT_yyyy_MM_dd_HHmm = "yyyy年MM月dd日 HH:mm";
    public static final String DATEFORMAT_yyyyMMdd_HHmm = "yyyyMMdd HH:mm";
    public static final String DATEFORMAT_yyyyzMMzddz_HHmm = "yyyy年MM月dd日 HH:mm";
    public static final String DATEFORMAT_yyyyzMMzddz = "yyyy年MM月dd日";
    public static final String DATEFORMAT_yyyy_MM_dd = "yyyy年MM月dd日";
    public static final String DATEFORMAT_yyyyMMdd = "yyyyMMdd";
    public static final String DATEFORMAT_yyyyMMdd2 = "yyyy.MM.dd";
    public static final String DATEFORMAT_yMd_Hm = "yyyy.MM.dd HH:mm";
    public static final String DATEFORMAT_MM_dd_HHmm = "yyyy-MM-dd HH:mm";
    public static final String DATEFORMAT_MMz_ddz_HHmm = "MM月dd日 HH:mm";
    public static final String DATEFORMAT_MMdd = "MMdd";
    public static final String DATEFORMAT_yyyy = "yyyy";
    public static final String DATEFORMAT_MM = "MM";
    public static final String DATEFORMAT_dd = "dd";
    public static final String DATEFORMAT_HHmm = "HH:mm";
    public static final String DATEFORMAT_MM_dd = "MM-dd";
    public static final String DATEFORMAT_MM_dd_HH_mm = "MM-dd HH:mm";
    public static final String DATEFORMAT_MMz_ddz = "MM月dd日";
    public static final String DATEFORMAT_YYMMDD = "yyyy/MM/dd";
    public static final String DATEFORMAT_YYMMDD2 = "yy/MM/dd";


    public final static long SCEND_1 = 1000;// 1秒钟
    public final static long MINUTE_1 = 60 * 1000;// 1分钟
    public final static long MINUTE_5 = 5 * MINUTE_1;// 5分钟
    public final static long HOUR_1 = 60 * MINUTE_1;// 1小时
    public final static long DAY_1 = 24 * HOUR_1;// 1天
    public final static long WEEK_1 = 7 * DAY_1; // 1周
    public final static long MONTH_1 = 31 * DAY_1;// 月
    public final static long YEAR_1 = 12 * MONTH_1;// 年

    /**
     * 获取中文模式下的时间格式
     *
     * @param time
     * @return
     */
    public static String date2YY_MM_dd_CN(long time) {
        return TimeUtils.date2String(new Date(time),
                DATEFORMAT_yyyyzMMzddz);
    }

    public static String date2MM_dd_hh_mm(long time) {
        return TimeUtils.date2String(new Date(time),
                DATEFORMAT_MMz_ddz_HHmm);
    }

    public static String getHHmm(long time) {
        return TimeUtils.date2String(new Date(time),
                DATEFORMAT_HHmm);
    }

    /**
     * 获取中文模式下的时间格式
     *
     * @param time
     * @return
     */
    public static String date2YY_MM_dd_HH_mm_ss_CN(long time) {
        return TimeUtils.date2String(new Date(time),
                DATEFORMAT_yyyy_MM_dd_HHmmss);
    }

    /**
     * 格式化时间为 yy/MM/dd
     *
     * @param time
     * @return
     */
    public static String date2yymmdd(long time) {
        return TimeUtils.date2String(new Date(time), DATEFORMAT_YYMMDD2);
    }

    /**
     * 格式化时间为 yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public static String date2YY_MM_dd_HH_mm_ss(long time) {
        if (time < 1000) return "";
        return TimeUtils.date2String(new Date(time),
                DATEFORMAT_yyyy_MM_dd_HHmmss);
    }

    /**
     * 格式化时间为 yyyy-MM-dd HH:mm
     *
     * @param time
     * @return
     */
    public static String date2YY_MM_dd_HH_mm(long time) {
        if (time < 1000) return "";
        return TimeUtils.date2String(new Date(time),
                DATEFORMAT_yyyy_MM_dd_HHmm);
    }

    /**
     * 转换时间格式: yyyy-MM-dd
     *
     * @param time
     * @return
     */
    public static String date2YY_MM_DD(long time) {
        if (time < 1000) return "";
        return TimeUtils.date2String(new Date(time),
                DATEFORMAT_yyyy_MM_dd);
    }

    /**
     * 解析时间到时间戳
     *
     * @param timeStr
     * @param format
     * @return
     */
    public static long parseTime(String timeStr, String format) {
        return TimeUtils.string2Millis(timeStr, format);
    }

    /**
     * 获取今天的 00:00:00:000的时间戳
     *
     * @param time
     * @return
     */
    public static long getTodayZero(long time) {
        String d = date2YY_MM_DD(time);

        try {
            return parseTime(d, DATEFORMAT_yyyy_MM_dd);
        } catch (Exception e) {
            return time;
        }
    }

    /**
     * 年份描述
     *
     * @param time
     * @return
     */
    public static String yearDesc(long time) {
        return TimeUtils.date2String(new Date(time), "yyyy年");
    }

    /**
     * 月份描述
     *
     * @param time
     * @return
     */
    public static String monthDesc(long time) {
        return TimeUtils.date2String(new Date(time), "MM月");
    }

    /**
     * 日期描述
     *
     * @param time
     * @return
     */
    public static String dayDesc(long time) {
        return TimeUtils.date2String(new Date(time), "dd日");
    }

}
