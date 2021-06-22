package cn.wl.android.lib.utils;

import com.blankj.utilcode.util.DeviceUtils;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by JustBlue on 2019-09-24.
 *
 * @email: bo.li@cdxzhi.com
 * @desc: 生成全局唯一标识
 */
public final class WLID {

    private static Random mRandom = new Random();
    private static AtomicLong mCount = new AtomicLong();

    /**
     * 获取全局唯一id
     *
     * @return
     */
    public static String getId() {
        String nextInt = mRandom.nextInt(10000) + "";
//        String deviceId = DeviceUtils.getUniqueDeviceId();

        long current = Times.current();
        long maskInd = mCount.incrementAndGet();

        return new StringBuilder(nextInt)
//                .append('_')
//                .append(deviceId)
                .append('_')
                .append(current)
                .append('_')
                .append(maskInd)
                .toString();
    }

    /**
     * 获取全局唯一id
     *
     * @return
     */
    public static String getId(int index) {
        String nextInt = mRandom.nextInt(10000) + "";
        String deviceId = DeviceUtils.getUniqueDeviceId();

        long current = Times.current();

        return new StringBuilder(nextInt)
                .append('_')
                .append(deviceId)
                .append('_')
                .append(current)
                .append('_')
                .append(index)
                .toString();
    }

}
