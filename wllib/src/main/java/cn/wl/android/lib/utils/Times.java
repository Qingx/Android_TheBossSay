package cn.wl.android.lib.utils;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.TimeUtils;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by JustBlue on 2019-08-28.
 *
 * @email: bo.li@cdxzhi.com
 * @desc: 时间管理, 添加时间偏移
 */
public class Times {

    private static AtomicLong mTimeOffset = new AtomicLong();

    /**
     * 获取添加时间偏移后的系统时间
     *
     * @return
     */
    public static long current() {
        for (; ; ) {
            long offset = mTimeOffset.get();

            long time = System.currentTimeMillis();
            long actual = time + offset;

            if (offset == mTimeOffset.get()) {
                return actual;
            }
        }
    }

    /**
     * 判断时间是否为昨天
     *
     * @param millis
     * @return
     */
    public static boolean isTomorrow(long millis) {
        long wee = getWeeOfToday() - TimeConstants.DAY;
        return millis >= wee && millis < wee + TimeConstants.DAY;
    }

    private static long getWeeOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

}
